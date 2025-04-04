/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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
import java.security.SecureRandom;
import java.util.Random;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongSupplier;

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
	 * It uses {@link ThreadLocalRandom} as random number generator.
	 * <p>
	 * 
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v1();
	 * }</pre>
	 * 
	 * @return a GUID
	 */
	public static GUID v1() {
		return v1(System::currentTimeMillis, TLRandom::nextLong);
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
	 * SecureRandom random = new SecureRandom();
	 * GUID guid = GUID.v1(Instant.now(), random);
	 * }</pre>
	 * 
	 * @param instant an instant (optional)
	 * @param random  a random generator (optional)
	 * @return a GUID
	 */
	public static GUID v1(Instant instant, Random random) {
		return v1(optional(instant), optional(random));
	}

	private static GUID v1(LongSupplier msec, LongSupplier random) {
		final long time = gregorian(msec.getAsLong());
		final long msb = (time << 32) | ((time >>> 16) & (MASK_16 << 16)) | ((time >>> 48) & MASK_12);
		final long lsb = random.getAsLong() | MULTICAST;
		return version(msb, lsb, 1);
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * It uses {@link ThreadLocalRandom} as random number generator.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v2(Uuid.LOCAL_DOMAIN_PERSON, 1234);
	 * }</pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a GUID
	 */
	public static GUID v2(byte localDomain, int localIdentifier) {
		return v2(localDomain, localIdentifier, v1());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * SecureRandom random = new SecureRandom();
	 * GUID guid = GUID.v2(Uuid.LOCAL_DOMAIN_PERSON, 1234, Instant.now(), random);
	 * }</pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @param instant         an instant (optional)
	 * @param random          a random generator (optional)
	 * @return a GUID
	 */
	public static GUID v2(byte localDomain, int localIdentifier, Instant instant, Random random) {
		return v2(localDomain, localIdentifier, v1(instant, random));
	}

	private static GUID v2(byte localDomain, int localIdentifier, GUID guid) {
		final long msb = (guid.msb & MASK_32) | ((localIdentifier & MASK_32) << 32);
		final long lsb = (guid.lsb & 0x3f00_ffff_ffff_ffffL) | ((localDomain & MASK_08) << 48);
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
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v3(myNameSpace, myBytes);
	 * }</pre>
	 * 
	 * @param namespace a GUID (optional)
	 * @param bytes     a byte array
	 * @return a GUID
	 * @throws NullPointerException if the byte array is null
	 */
	public static GUID v3(GUID namespace, byte[] bytes) {
		return hash(3, "MD5", namespace, bytes);
	}

	/**
	 * Returns a random-based unique identifier (UUIDv4).
	 * <p>
	 * It is an extremely fast and non-blocking alternative to
	 * {@link UUID#randomUUID()}.
	 * <p>
	 * It uses {@link ThreadLocalRandom} as random number generator.
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
		return version(TLRandom.nextLong(), TLRandom.nextLong(), 4);
	}

	/**
	 * Returns a random-based unique identifier (UUIDv4).
	 * <p>
	 * It is equivalent to {@link UUID#randomUUID()}.
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * SecureRandom random = new SecureRandom();
	 * GUID guid = GUID.v4(random);
	 * }</pre>
	 * 
	 * @param random a random generator
	 * @return a GUID
	 * @throws NullPointerException if the random is null
	 */
	public static GUID v4(Random random) {
		Objects.requireNonNull(random, "Null random");
		return version(random.nextLong(), random.nextLong(), 4);
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
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * GUID guid = GUID.v5(myNameSpace, myBytes);
	 * }</pre>
	 * 
	 * @param namespace a GUID (optional)
	 * @param bytes     a byte array
	 * @return a GUID
	 * @throws NullPointerException if the byte array is null
	 */
	public static GUID v5(GUID namespace, byte[] bytes) {
		return hash(5, "SHA-1", namespace, bytes);
	}

	/**
	 * Returns a reordered gregorian time-based unique identifier (UUIDv6).
	 * <p>
	 * The clock sequence and node bits are reset to a pseudo-random value for each
	 * new UUIDv6 generated.
	 * <p>
	 * It uses {@link ThreadLocalRandom} as random number generator.
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
		return v6(System::currentTimeMillis, TLRandom::nextLong);
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
	 * SecureRandom random = new SecureRandom();
	 * GUID guid = GUID.v6(Instant.now(), random);
	 * }</pre>
	 * 
	 * @param instant an instant (optional)
	 * @param random  a random generator (optional)
	 * @return a GUID
	 */
	public static GUID v6(Instant instant, Random random) {
		return v6(optional(instant), optional(random));
	}

	private static GUID v6(LongSupplier msec, LongSupplier random) {
		final long time = gregorian(msec.getAsLong());
		final long msb = ((time & ~MASK_12) << 4) | (time & MASK_12);
		final long lsb = random.getAsLong() | MULTICAST;
		return version(msb, lsb, 6);
	}

	/**
	 * Returns a Unix epoch time-based unique identifier (UUIDv7).
	 * <p>
	 * It uses {@link ThreadLocalRandom} as random number generator.
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
		return v7(System::currentTimeMillis, TLRandom::nextLong);
	}

	/**
	 * Returns a Unix epoch time-based unique identifier (UUIDv7).
	 * <p>
	 * Usage:
	 * 
	 * <pre>{@code
	 * SecureRandom random = new SecureRandom();
	 * GUID guid = GUID.v7(Instant.now(), random);
	 * }</pre>
	 * 
	 * @param instant an instant (optional)
	 * @param random  a random generator (optional)
	 * @return a GUID
	 */
	public static GUID v7(Instant instant, Random random) {
		return v7(optional(instant), optional(random));
	}

	private static GUID v7(LongSupplier msec, LongSupplier random) {
		final long time = msec.getAsLong();
		final long msb = (time << 16) | (TLRandom.nextLong() & MASK_12);
		final long lsb = random.getAsLong();
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

	static LongSupplier optional(Instant instant) {
		return instant == null ? System::currentTimeMillis : instant::toEpochMilli;
	}

	static LongSupplier optional(Random random) {
		return random == null ? TLRandom::nextLong : random::nextLong;
	}

	static long gregorian(final long millisecons) {
		// 1582-10-15T00:00:00Z
		final long factor = 10_000L;
		final long offset = 12219292800000L;
		return ((millisecons + offset) * factor);
	}

	static GUID hash(int version, String algorithm, GUID namespace, String name) {
		Objects.requireNonNull(name, "Null name");
		return hash(version, algorithm, namespace, name.getBytes(StandardCharsets.UTF_8));
	}

	static GUID hash(int version, String algorithm, GUID namespace, byte[] bytes) {

		Objects.requireNonNull(bytes, "Null bytes");
		MessageDigest hasher = hasher(algorithm);

		if (namespace != null) {
			ByteBuffer ns = ByteBuffer.allocate(16);
			ns.putLong(namespace.msb);
			ns.putLong(namespace.lsb);
			hasher.update(ns.array());
		}

		hasher.update(bytes);
		ByteBuffer hash = ByteBuffer.wrap(hasher.digest());

		final long msb = hash.getLong();
		final long lsb = hash.getLong();

		return version(msb, lsb, version);
	}

	static MessageDigest hasher(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	static GUID version(long hi, long lo, int version) {
		// set the 4 most significant bits of the 7th byte
		final long msb = (hi & 0xffff_ffff_ffff_0fffL) | (version & 0xfL) << 12; // RFC 9562 version
		// set the 2 most significant bits of the 9th byte to 1 and 0
		final long lsb = (lo & 0x3fff_ffff_ffff_ffffL) | 0x8000_0000_0000_0000L; // RFC 9562 variant
		return new GUID(msb, lsb);
	}

	long getMostSignificantBits() {
		return this.msb;
	}

	long getLeastSignificantBits() {
		return this.lsb;
	}

	static final class Parser {

		private static final byte[] MAP;
		static {
			byte[] mapping = new byte[256];
			Arrays.fill(mapping, (byte) -1);
			mapping['0'] = 0;
			mapping['1'] = 1;
			mapping['2'] = 2;
			mapping['3'] = 3;
			mapping['4'] = 4;
			mapping['5'] = 5;
			mapping['6'] = 6;
			mapping['7'] = 7;
			mapping['8'] = 8;
			mapping['9'] = 9;
			mapping['a'] = 10;
			mapping['b'] = 11;
			mapping['c'] = 12;
			mapping['d'] = 13;
			mapping['e'] = 14;
			mapping['f'] = 15;
			mapping['A'] = 10;
			mapping['B'] = 11;
			mapping['C'] = 12;
			mapping['D'] = 13;
			mapping['E'] = 14;
			mapping['F'] = 15;
			MAP = mapping;
		}

		private static final int DASH_POSITION_1 = 8;
		private static final int DASH_POSITION_2 = 13;
		private static final int DASH_POSITION_3 = 18;
		private static final int DASH_POSITION_4 = 23;

		public static GUID parse(final String string) {

			validate(string);

			long msb = 0;
			long lsb = 0;

			for (int i = 0; i < 8; i++) {
				msb = (msb << 4) | get(string, i);
			}

			for (int i = 9; i < 13; i++) {
				msb = (msb << 4) | get(string, i);
			}

			for (int i = 14; i < 18; i++) {
				msb = (msb << 4) | get(string, i);
			}

			for (int i = 19; i < 23; i++) {
				lsb = (lsb << 4) | get(string, i);
			}

			for (int i = 24; i < 36; i++) {
				lsb = (lsb << 4) | get(string, i);
			}

			return new GUID(msb, lsb);
		}

		public static boolean valid(final String guid) {
			try {
				parse(guid);
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		private static long get(final String string, int i) {

			final int chr = string.charAt(i);
			if (chr > 255) {
				throw exception(string);
			}

			final byte value = MAP[chr];
			if (value < 0) {
				throw exception(string);
			}
			return value & 0xffL;
		}

		private static RuntimeException exception(final String str) {
			return new IllegalArgumentException("Invalid UUID: " + str);
		}

		private static void validate(final String string) {
			if (string == null || string.length() != GUID_CHARS) {
				throw exception(string);
			}
			if (string.charAt(DASH_POSITION_1) != '-' || string.charAt(DASH_POSITION_2) != '-'
					|| string.charAt(DASH_POSITION_3) != '-' || string.charAt(DASH_POSITION_4) != '-') {
				throw exception(string);
			}
		}
	}

	private static class TLRandom {

		// The JVM unique number tries to mitigate the fact that the thread
		// local random is not seeded with a secure random seed by default.
		// Their seeds are based on temporal data and predefined constants.
		// Although the seeds are unique per JVM, they are not across JVMs.
		// It helps to generate different sequences of numbers even if two
		// ThreadLocalRandom are by chance instantiated with the same seed.
		// Of course it doesn't better the output, but doesn't hurt either.
		static final long JVM_UNIQUE_NUMBER = new SecureRandom().nextLong();

		private static long nextLong() {
			return ThreadLocalRandom.current().nextLong() ^ JVM_UNIQUE_NUMBER;
		}
	}
}
