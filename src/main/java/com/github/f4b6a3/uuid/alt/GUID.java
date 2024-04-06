/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.alt;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that represents and generates GUIDs/UUIDs.
 * <p>
 * It serves as an alternative to the classic JDK's {@link UUID}.
 * <p>
 * It also serves as a self-contained generator, independent of the rest of the
 * library. This can result in fewer classes being loaded.
 * <p>
 * This generator was designed to be an alternative to {@code UuidCreator} with
 * three primary objectives in mind: clean interface, simple implementation, and
 * high performance. It was inspired by popular libraries for Javascript and
 * Python.
 * <p>
 * The name GUID was chosen to avoid confusion with the classic JDK's UUID
 * class. This naming choice was made so that when you see the word GUID in the
 * source code, you can be sure it's not the JDK's built-in UUID.
 * <p>
 * Instances of this class are <b>immutable</b> and static methods of this class
 * are <b>thread safe</b>.
 */
public final class GUID implements Serializable, Comparable<GUID> {

	private static final long serialVersionUID = -6082258105369032877L;

	/**
	 * The most significant bits.
	 */
	private final long msb;
	/**
	 * The least significant bits.
	 */
	private final long lsb;

	/**
	 * A special GUID that has all 128 bits set to ZERO.
	 */
	public static final GUID NIL = new GUID(0x0000000000000000L, 0x0000000000000000L);
	/**
	 * A special GUID that has all 128 bits set to ONE.
	 */
	public static final GUID MAX = new GUID(0xffffffffffffffffL, 0xffffffffffffffffL);

	/**
	 * Name space to be used when the name string is a fully-qualified domain name.
	 */
	public static final GUID NAMESPACE_DNS = new GUID(0x6ba7b8109dad11d1L, 0x80b400c04fd430c8L);
	/**
	 * Name space to be used when the name string is a URL.
	 */
	public static final GUID NAMESPACE_URL = new GUID(0x6ba7b8119dad11d1L, 0x80b400c04fd430c8L);
	/**
	 * Name space to be used when the name string is an ISO OID.
	 */
	public static final GUID NAMESPACE_OID = new GUID(0x6ba7b8129dad11d1L, 0x80b400c04fd430c8L);
	/**
	 * Name space to be used when the name string is an X.500 DN (DER or text).
	 */
	public static final GUID NAMESPACE_X500 = new GUID(0x6ba7b8149dad11d1L, 0x80b400c04fd430c8L);

	/**
	 * The principal domain, interpreted as POSIX UID domain on POSIX systems.
	 */
	public static final byte LOCAL_DOMAIN_PERSON = (byte) 0x00;
	/**
	 * The group domain, interpreted as POSIX GID domain on POSIX systems.
	 */
	public static final byte LOCAL_DOMAIN_GROUP = (byte) 0x01;
	/**
	 * The organization domain, site-defined.
	 */
	public static final byte LOCAL_DOMAIN_ORG = (byte) 0x02;

	/**
	 * Number of characters of a GUID.
	 */
	public static final int GUID_CHARS = 36;
	/**
	 * Number of bytes of a GUID.
	 */
	public static final int GUID_BYTES = 16;

	private static final long MASK_04 = 0x0000_0000_0000_000fL;
	private static final long MASK_08 = 0x0000_0000_0000_00ffL;
	private static final long MASK_12 = 0x0000_0000_0000_0fffL;
	private static final long MASK_16 = 0x0000_0000_0000_ffffL;
	private static final long MASK_32 = 0x0000_0000_ffff_ffffL;

	private static final long MULTICAST = 0x0000_0100_0000_0000L;

	/**
	 * Creates a new GUID.
	 * <p>
	 * Useful to make copies of GUIDs.
	 * 
	 * @param guid a GUID
	 * @throws IllegalArgumentException if the input is null
	 */
	public GUID(GUID guid) {
		if (guid == null) {
			throw new IllegalArgumentException("Null GUID");
		}
		this.msb = guid.msb;
		this.lsb = guid.lsb;
	}

	/**
	 * Creates a new GUID.
	 * <p>
	 * Useful to make copies of JDK's UUIDs.
	 * 
	 * @param uuid a JDK's UUID
	 * @throws IllegalArgumentException if the input is null
	 */
	public GUID(UUID uuid) {
		if (uuid == null) {
			throw new IllegalArgumentException("Null UUID");
		}
		this.msb = uuid.getMostSignificantBits();
		this.lsb = uuid.getLeastSignificantBits();
	}

	/**
	 * Creates a new GUID.
	 * 
	 * @param mostSignificantBits  the first 8 bytes as a long value
	 * @param leastSignificantBits the last 8 bytes as a long value
	 */
	public GUID(long mostSignificantBits, long leastSignificantBits) {
		this.msb = mostSignificantBits;
		this.lsb = leastSignificantBits;
	}

	/**
	 * Creates a new GUID.
	 * 
	 * @param bytes an array of 16 bytes
	 * @throws IllegalArgumentException if bytes are null or its length is not 16
	 */
	public GUID(byte[] bytes) {
		if (bytes == null || bytes.length != GUID_BYTES) {
			throw new IllegalArgumentException("Invalid GUID bytes"); // null or wrong length!
		}

		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		this.msb = buffer.getLong();
		this.lsb = buffer.getLong();
	}

	/**
	 * Creates a new GUID.
	 * 
	 * @param string a canonical string
	 * @throws IllegalArgumentException if the input string is invalid
	 */
	public GUID(String string) {
		this(Parser.parse(string));
	}

	/**
	 * Returns a gregorian time-based unique identifier (UUIDv1).
	 * <p>
	 * The clock sequence and node bits are reset to a pseudo-random value for each
	 * new UUIDv1 generated.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v1();
	 * }</pre>
	 * 
	 * @return a GUID
	 */
	public static GUID v1() {
		final long time = gregorian();
		final long msb = (time << 32) | ((time >>> 16) & (MASK_16 << 16)) | ((time >>> 48) & MASK_12);
		final long lsb = ThreadLocalRandom.current().nextLong() | MULTICAST;
		return version(msb, lsb, 1);
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v2(Uuid.LOCAL_DOMAIN_PERSON, 1234567890);
	 * }</pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a GUID
	 */
	public static GUID v2(byte localDomain, int localIdentifier) {
		GUID uuid = v1();
		final long msb = (uuid.msb & MASK_32) | ((localIdentifier & MASK_32) << 32);
		final long lsb = (uuid.lsb & 0x3f00_ffff_ffff_ffffL) | ((localDomain & MASK_08) << 48);
		return version(msb, lsb, 2);
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v3(Uuid.NAMESPACE_DNS, "www.example.com");
	 * }</pre>
	 * 
	 * @param namespace a GUID (optional)
	 * @param name      a string
	 * @return a GUID
	 * @throws NullPointerException if the name is null
	 */
	public static GUID v3(GUID namespace, String name) {
		return hash(3, "MD5", namespace, name);
	}

	/**
	 * Returns a random-based unique identifier (UUIDv4).
	 * <p>
	 * It is an extremely fast and non-blocking alternative to
	 * {@link UUID#randomUUID()}.
	 * <p>
	 * It employs {@link ThreadLocalRandom} which works very well, although not
	 * cryptographically strong.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v4();
	 * }</pre>
	 * 
	 * @return a GUID
	 */
	public static GUID v4() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		final long msb = random.nextLong();
		final long lsb = random.nextLong();
		return version(msb, lsb, 4);
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v5(Uuid.NAMESPACE_DNS, "www.example.com");
	 * }</pre>
	 * 
	 * @param namespace a GUID (optional)
	 * @param name      a string
	 * @return a GUID
	 * @throws NullPointerException if the name is null
	 */
	public static GUID v5(GUID namespace, String name) {
		return hash(5, "SHA-1", namespace, name);
	}

	/**
	 * Returns a reordered gregorian time-based unique identifier (UUIDv6).
	 * <p>
	 * The clock sequence and node bits are reset to a pseudo-random value for each
	 * new UUIDv6 generated.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v6();
	 * }</pre>
	 * 
	 * @return a GUID
	 */
	public static GUID v6() {
		final long time = gregorian();
		final long msb = ((time & ~MASK_12) << 4) | (time & MASK_12);
		final long lsb = ThreadLocalRandom.current().nextLong() | MULTICAST;
		return version(msb, lsb, 6);
	}

	/**
	 * Returns a Unix epoch time-based unique identifier (UUIDv7).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v7();
	 * }</pre>
	 * 
	 * @return a GUID
	 */
	public static GUID v7() {
		final long time = System.currentTimeMillis();
		ThreadLocalRandom random = ThreadLocalRandom.current();
		final long msb = (time << 16) | (random.nextLong() & MASK_16);
		final long lsb = random.nextLong();
		return version(msb, lsb, 7);
	}

	/**
	 * Checks if the GUID string is valid.
	 * 
	 * @param string a GUID string
	 * @return true if valid, false if invalid
	 */
	public static boolean valid(String string) {
		return Parser.valid(string);
	}

	/**
	 * Converts the GUID into a byte array.
	 * 
	 * @return an byte array.
	 */
	public byte[] toBytes() {
		return ByteBuffer.allocate(GUID_BYTES).putLong(msb).putLong(lsb).array();
	}

	/**
	 * Converts the GUID into a canonical string.
	 */
	@Override
	public String toString() {
		return toUUID().toString();
	}

	/**
	 * Converts the GUID into a JDK's UUID.
	 * <p>
	 * It simply copies all 128 bits into a new JDK's UUID.
	 * <p>
	 * You can think of the GUID class as a JDK's UUID wrapper. This method unwraps
	 * a JDK's UUID instance so that you can store it in the built-in format or
	 * access its classic interface.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * UUID uuid = GUID.v4().toUUID();
	 * }</pre>
	 * 
	 * @return a JDK's UUID.
	 */
	public UUID toUUID() {
		return new UUID(this.msb, this.lsb);
	}

	/**
	 * Returns the version number of this GUID.
	 * 
	 * @return a version number
	 */
	public int version() {
		return toUUID().version();
	}

	/**
	 * Returns a hash code value for the GUID.
	 */
	@Override
	public int hashCode() {
		final long bits = msb ^ lsb;
		return (int) (bits ^ (bits >>> 32));
	}

	/**
	 * Checks if some other GUID is equal to this one.
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other.getClass() != GUID.class)
			return false;
		GUID that = (GUID) other;
		if (lsb != that.lsb)
			return false;
		else if (msb != that.msb)
			return false;
		return true;
	}

	/**
	 * Compares two GUIDs as unsigned 128-bit integers.
	 * <p>
	 * The first of two GUIDs is greater than the second if the most significant
	 * byte in which they differ is greater for the first GUID.
	 * <p>
	 * If the second GUID is {@code null}, then it is treated as a {@link GUID#NIL}
	 * GUID, which has all its bits set to ZERO, instead of throwing a
	 * {@link NullPointerException}.
	 * <p>
	 * This method differs from JDK's {@link UUID#compareTo(UUID)} as this method
	 * compares two GUIDs as <b>unsigned</b> 128-bit integers.
	 * <p>
	 * It can be useful because JDK's {@link UUID#compareTo(UUID)} can lead to
	 * unexpected behavior due to its <b>signed</b> 64-bit comparison. Another
	 * reason is that JDK's {@link UUID#compareTo(UUID)} throws
	 * {@link NullPointerException} if it receives a {@code null} UUID.
	 * 
	 * @param other a second GUID to be compared with
	 * @return -1, 0 or 1 as {@code this} is less than, equal to, or greater than
	 *         {@code that}
	 */
	@Override
	public int compareTo(GUID other) {

		GUID that = other != null ? other : GUID.NIL;

		// used to compare as UNSIGNED longs
		final long min = 0x8000000000000000L;

		final long a = this.msb + min;
		final long b = that.msb + min;

		if (a > b)
			return 1;
		else if (a < b)
			return -1;

		final long c = this.lsb + min;
		final long d = that.lsb + min;

		if (c > d)
			return 1;
		else if (c < d)
			return -1;

		return 0;
	}

	private static long gregorian() {
		// 1582-10-15T00:00:00.000Z
		Instant now = Instant.now();
		final long greg = 12219292800L;
		final long nano = now.getNano();
		final long secs = now.getEpochSecond() + greg;
		final long time = (secs * 10_000_000L) + (nano / 100L);
		return time;
	}

	private static GUID hash(int version, String algorithm, GUID namespace, String name) {

		Objects.requireNonNull(name, "Null name");

		MessageDigest hasher = null;
		try {
			hasher = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		if (namespace != null) {
			ByteBuffer ns = ByteBuffer.allocate(16);
			ns.putLong(namespace.msb);
			ns.putLong(namespace.lsb);
			hasher.update(ns.array());
		}

		hasher.update(name.getBytes(StandardCharsets.UTF_8));
		ByteBuffer hash = ByteBuffer.wrap(hasher.digest());

		final long msb = hash.getLong();
		final long lsb = hash.getLong();

		return version(msb, lsb, version);
	}

	private static GUID version(long hi, long lo, int version) {

		// set the 4 most significant bits of the 7th byte
		final long msb = (hi & 0xffff_ffff_ffff_0fffL) | (version & MASK_04) << 12; // RFC-4122 version
		// set the 2 most significant bits of the 9th byte to 1 and 0
		final long lsb = (lo & 0x3fff_ffff_ffff_ffffL) | 0x8000_0000_0000_0000L; // RFC-4122 variant

		return new GUID(msb, lsb);
	}

	long getMostSignificantBits() {
		return this.msb;
	}

	long getLeastSignificantBits() {
		return this.lsb;
	}

	static final class Parser {

		private static final byte[] VALUES = new byte[256];
		static {
			Arrays.fill(VALUES, (byte) -1);
			VALUES['0'] = 0;
			VALUES['1'] = 1;
			VALUES['2'] = 2;
			VALUES['3'] = 3;
			VALUES['4'] = 4;
			VALUES['5'] = 5;
			VALUES['6'] = 6;
			VALUES['7'] = 7;
			VALUES['8'] = 8;
			VALUES['9'] = 9;
			VALUES['A'] = 10;
			VALUES['B'] = 11;
			VALUES['C'] = 12;
			VALUES['D'] = 13;
			VALUES['E'] = 14;
			VALUES['F'] = 15;
			VALUES['a'] = 10;
			VALUES['b'] = 11;
			VALUES['c'] = 12;
			VALUES['d'] = 13;
			VALUES['e'] = 14;
			VALUES['f'] = 15;
		}

		public static GUID parse(final String string) {

			if (!valid(string)) {
				throw new IllegalArgumentException("Invalid GUID string: " + string);
			}

			long msb = 0;
			long lsb = 0;

			// UUID string WITH hyphen
			msb |= (long) VALUES[string.charAt(0x00)] << 60;
			msb |= (long) VALUES[string.charAt(0x01)] << 56;
			msb |= (long) VALUES[string.charAt(0x02)] << 52;
			msb |= (long) VALUES[string.charAt(0x03)] << 48;
			msb |= (long) VALUES[string.charAt(0x04)] << 44;
			msb |= (long) VALUES[string.charAt(0x05)] << 40;
			msb |= (long) VALUES[string.charAt(0x06)] << 36;
			msb |= (long) VALUES[string.charAt(0x07)] << 32;
			// input[8] = '-'
			msb |= (long) VALUES[string.charAt(0x09)] << 28;
			msb |= (long) VALUES[string.charAt(0x0a)] << 24;
			msb |= (long) VALUES[string.charAt(0x0b)] << 20;
			msb |= (long) VALUES[string.charAt(0x0c)] << 16;
			// input[13] = '-'
			msb |= (long) VALUES[string.charAt(0x0e)] << 12;
			msb |= (long) VALUES[string.charAt(0x0f)] << 8;
			msb |= (long) VALUES[string.charAt(0x10)] << 4;
			msb |= (long) VALUES[string.charAt(0x11)];
			// input[18] = '-'
			lsb |= (long) VALUES[string.charAt(0x13)] << 60;
			lsb |= (long) VALUES[string.charAt(0x14)] << 56;
			lsb |= (long) VALUES[string.charAt(0x15)] << 52;
			lsb |= (long) VALUES[string.charAt(0x16)] << 48;
			// input[23] = '-'
			lsb |= (long) VALUES[string.charAt(0x18)] << 44;
			lsb |= (long) VALUES[string.charAt(0x19)] << 40;
			lsb |= (long) VALUES[string.charAt(0x1a)] << 36;
			lsb |= (long) VALUES[string.charAt(0x1b)] << 32;
			lsb |= (long) VALUES[string.charAt(0x1c)] << 28;
			lsb |= (long) VALUES[string.charAt(0x1d)] << 24;
			lsb |= (long) VALUES[string.charAt(0x1e)] << 20;
			lsb |= (long) VALUES[string.charAt(0x1f)] << 16;
			lsb |= (long) VALUES[string.charAt(0x20)] << 12;
			lsb |= (long) VALUES[string.charAt(0x21)] << 8;
			lsb |= (long) VALUES[string.charAt(0x22)] << 4;
			lsb |= (long) VALUES[string.charAt(0x23)];

			return new GUID(msb, lsb);
		}

		private static final int[] DASH_POSITIONS = { 8, 13, 18, 23 };
		private static final int DASH_REQUIRED_COUNT = 4;

		private static boolean isCharValid(char chr) {
			return chr >= 0 && chr < VALUES.length && VALUES[chr] >= 0;
		}

		private static boolean isDashPosition(int index) {
			for (int dashPosition : DASH_POSITIONS) {
				if (index == dashPosition) {
					return true;
				}
			}
			return false;
		}

		public static boolean valid(final String guid) {
			if (guid == null || guid.length() != GUID_CHARS) {
				return false; // null or wrong length
			}

			int dashesCount = 0;
			for (int i = 0; i < GUID_CHARS; i++) {
				char chr = guid.charAt(i);
				if (isCharValid(chr)) {
					continue; // character is valid
				}
				if (chr == '-' && isDashPosition(i)) {
					dashesCount++;
					continue;
				}
				return false; // invalid character
			}

			return dashesCount == DASH_REQUIRED_COUNT;
		}
	}
}
