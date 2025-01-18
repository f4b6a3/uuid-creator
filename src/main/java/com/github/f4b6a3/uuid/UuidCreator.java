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

package com.github.f4b6a3.uuid;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import com.github.f4b6a3.uuid.codec.StandardBinaryCodec;
import com.github.f4b6a3.uuid.codec.StandardStringCodec;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.factory.UuidFactory;
import com.github.f4b6a3.uuid.factory.UuidFactory.Parameters;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortPrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortSuffixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.SuffixCombFactory;
import com.github.f4b6a3.uuid.factory.standard.DceSecurityFactory;
import com.github.f4b6a3.uuid.factory.standard.NameBasedMd5Factory;
import com.github.f4b6a3.uuid.factory.standard.NameBasedSha1Factory;
import com.github.f4b6a3.uuid.factory.standard.RandomBasedFactory;
import com.github.f4b6a3.uuid.factory.standard.TimeBasedFactory;
import com.github.f4b6a3.uuid.factory.standard.TimeOrderedEpochFactory;
import com.github.f4b6a3.uuid.factory.standard.TimeOrderedFactory;
import com.github.f4b6a3.uuid.util.MachineId;

/**
 * Facade for everything.
 * <p>
 * All UUID types can be generated from this entry point.
 */
public final class UuidCreator {

	/**
	 * Name space to be used when the name string is a fully-qualified domain name.
	 */
	public static final UuidNamespace NAMESPACE_DNS = UuidNamespace.NAMESPACE_DNS;
	/**
	 * Name space to be used when the name string is a URL.
	 */
	public static final UuidNamespace NAMESPACE_URL = UuidNamespace.NAMESPACE_URL;
	/**
	 * Name space to be used when the name string is an ISO OID.
	 */
	public static final UuidNamespace NAMESPACE_OID = UuidNamespace.NAMESPACE_OID;
	/**
	 * Name space to be used when the name string is an X.500 DN (DER or text).
	 */
	public static final UuidNamespace NAMESPACE_X500 = UuidNamespace.NAMESPACE_X500;

	/**
	 * The principal domain, interpreted as POSIX UID domain on POSIX systems.
	 */
	public static final UuidLocalDomain LOCAL_DOMAIN_PERSON = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
	/**
	 * The group domain, interpreted as POSIX GID domain on POSIX systems.
	 */
	public static final UuidLocalDomain LOCAL_DOMAIN_GROUP = UuidLocalDomain.LOCAL_DOMAIN_GROUP;
	/**
	 * The organization domain, site-defined.
	 */
	public static final UuidLocalDomain LOCAL_DOMAIN_ORG = UuidLocalDomain.LOCAL_DOMAIN_ORG;

	private static final UUID UUID_NIL = new UUID(0x0000000000000000L, 0x0000000000000000L);
	private static final UUID UUID_MAX = new UUID(0xffffffffffffffffL, 0xffffffffffffffffL);

	private UuidCreator() {
	}

	/**
	 * Returns a Nil UUID.
	 * <p>
	 * Nil UUID is a special UUID that has all 128 bits set to ZERO.
	 * <p>
	 * The canonical string of Nil UUID is
	 * <code>00000000-0000-0000-0000-000000000000</code>.
	 * 
	 * @return a Nil UUID
	 */
	public static UUID getNil() {
		return UUID_NIL;
	}

	/**
	 * Returns a Max UUID.
	 * <p>
	 * Max UUID is a special UUID that has all 128 bits set to ONE.
	 * <p>
	 * The canonical string of Max UUID is
	 * <code>FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF</code>.
	 * 
	 * @return a Max UUID
	 * @since 5.0.0
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getMax() {
		return UUID_MAX;
	}

	/**
	 * Returns an array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static byte[] toBytes(final UUID uuid) {
		return StandardBinaryCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Returns a UUID from a byte array.
	 * <p>
	 * It also checks if the input byte array is valid.
	 * 
	 * @param uuid a byte array
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static UUID fromBytes(byte[] uuid) {
		return StandardBinaryCodec.INSTANCE.decode(uuid);
	}

	/**
	 * Returns a string from a UUID.
	 * <p>
	 * It can be much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static String toString(UUID uuid) {
		return StandardStringCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Returns a UUID from a string.
	 * <p>
	 * It accepts strings:
	 * <ul>
	 * <li>With URN prefix: "urn:uuid:";
	 * <li>With curly braces: '{' and '}';
	 * <li>With upper or lower case;
	 * <li>With or without hyphens.
	 * </ul>
	 * <p>
	 * It can be much faster than {@link UUID#fromString(String)} in JDK 8.
	 * <p>
	 * It also can be twice as fast as {@link UUID#fromString(String)} in JDK 11.
	 * 
	 * @param uuid a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static UUID fromString(String uuid) {
		return StandardStringCodec.INSTANCE.decode(uuid);
	}

	/**
	 * Returns a random-based unique identifier (UUIDv4).
	 * 
	 * @return a UUIDv4
	 * @see RandomBasedFactory
	 */
	public static UUID getRandomBased() {
		return UUID4.create();
	}

	/**
	 * Returns a fast random-based unique identifier (UUIDv4).
	 * <p>
	 * It employs {@link ThreadLocalRandom} which works very well, although not
	 * cryptographically strong. It can be useful, for example, for logging.
	 * <p>
	 * Security-sensitive applications that require a cryptographically secure
	 * pseudo-random generator should use {@link UuidCreator#getRandomBased()}.
	 * 
	 * @return a UUIDv4
	 * @see RandomBasedFactory
	 * @see ThreadLocalRandom
	 * @since 5.2.0
	 */
	public static UUID getRandomBasedFast() {
		return UUID4_FAST.create();
	}

	/**
	 * Returns a time-based unique identifier (UUIDv1).
	 * <p>
	 * The default node identifier is a random number that is generated once at
	 * initialization.
	 * <p>
	 * A custom node identifier can be provided by the system property
	 * 'uuidcreator.node' or the environment variable 'UUIDCREATOR_NODE'.
	 * 
	 * @return a UUIDv1
	 * @see TimeBasedFactory
	 */
	public static UUID getTimeBased() {
		return UUID1.create();
	}

	/**
	 * Returns a time-based unique identifier (UUIDv1).
	 * <p>
	 * The node identifier is a MAC address that is obtained once at initialization.
	 * 
	 * @return a UUIDv1
	 * @see TimeBasedFactory
	 */
	public static UUID getTimeBasedWithMac() {
		return UUID1_MAC.create();
	}

	/**
	 * Returns a time-based unique identifier (UUIDv1).
	 * <p>
	 * The node identifier is a hash that is calculated once at initialization.
	 * <p>
	 * The hash input is a string containing host name, MAC and IP.
	 * 
	 * @return a UUIDv1
	 * @see TimeBasedFactory
	 * @see MachineId
	 */
	public static UUID getTimeBasedWithHash() {
		return UUID1_HASH.create();
	}

	/**
	 * Returns a time-based unique identifier (UUIDv1).
	 * <p>
	 * The node identifier is a random number that is generated with each method
	 * invocation.
	 * 
	 * @return a UUIDv1
	 * @see TimeBasedFactory
	 */
	public static UUID getTimeBasedWithRandom() {
		return UUID1_RANDOM.create();
	}

	/**
	 * Returns a time-based unique identifier (UUIDv1).
	 * <p>
	 * {@link Instant} accuracy is be limited to 1 millisecond on Linux with JDK 8.
	 * On Windows, its accuracy may be limited to 15.625ms (64hz).
	 * <p>
	 * The clock sequence is a number between 0 and 16383 (2^14 - 1). If the value
	 * passed as an argument is out of range, the result of MOD 2^14 will be used.
	 * <p>
	 * The node identifier is a number between 0 and 281474976710655 (2^48 - 1). If
	 * the value passed as an argument is out of range, the result of MOD 2^48 will
	 * be used.
	 * <p>
	 * Null arguments are ignored. If all arguments are null, this method works just
	 * like method {@link UuidCreator#getTimeBased()}.
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence between 0 and 2^14-1
	 * @param nodeid   an alternate node identifier between 0 and 2^48-1
	 * @return a UUIDv1
	 * @see TimeBasedFactory
	 */
	public static UUID getTimeBased(Instant instant, Integer clockseq, Long nodeid) {
		TimeBasedFactory.Builder builder = TimeBasedFactory.builder();
		if (instant != null) {
			builder.withInstant(instant);
		}
		if (clockseq != null) {
			builder.withClockSeq(clockseq);
		}
		if (nodeid != null) {
			builder.withNodeId(nodeid);
		}
		return builder.build().create();
	}

	/**
	 * Returns the minimum UUIDv1 for a given instant.
	 * <p>
	 * The 60 bits of the timestamp are filled with the bits of the given instant
	 * and the other 62 bits are all set to ZERO.
	 * <p>
	 * For example, the minimum UUIDv1 for 2022-02-22 22:22:22.222 is
	 * `{@code e7a1c2e0-942d-11ec-8000-000000000000}`, where
	 * `{@code e7a1c2e0-942d-_1ec}` is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv1
	 */
	public static UUID getTimeBasedMin(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		TimeBasedFactory.Builder builder = TimeBasedFactory.builder();
		return builder.withInstant(instant).withClockSeq(0x0000L).withNodeId(0x000000000000L).build().create();
	}

	/**
	 * Returns the maximum UUIDv1 for a given instant.
	 * <p>
	 * The 60 bits of the timestamp are filled with the bits of the given instant
	 * and the other 62 bits are all set to ONE.
	 * <p>
	 * For example, the maximum UUIDv1 for 2022-02-22 22:22:22.222 is
	 * `{@code e7a1c2e0-942d-11ec-bfff-ffffffffffff}`, where
	 * `{@code e7a1c2e0-942d-_1ec}` is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv1
	 */
	public static UUID getTimeBasedMax(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		TimeBasedFactory.Builder builder = TimeBasedFactory.builder();
		return builder.withInstant(instant).withClockSeq(0xffffL).withNodeId(0xffffffffffffL).build().create();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv6).
	 * <p>
	 * The default node identifier is a random number that is generated once at
	 * initialization.
	 * <p>
	 * A custom node identifier can be provided by the system property
	 * 'uuidcreator.node' or the environment variable 'UUIDCREATOR_NODE'.
	 * 
	 * @return a UUIDv6
	 * @see TimeOrderedFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrdered() {
		return UUID6.create();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv6).
	 * <p>
	 * The node identifier is a MAC address that is obtained once at initialization.
	 * 
	 * @return a UUIDv6
	 * @see TimeOrderedFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedWithMac() {
		return UUID6_MAC.create();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv6).
	 * <p>
	 * The node identifier is a hash that is calculated once at initialization.
	 * <p>
	 * The hash input is a string containing host name, MAC and IP.
	 * 
	 * @return a UUIDv6
	 * @see TimeOrderedFactory
	 * @see MachineId
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedWithHash() {
		return UUID6_HASH.create();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv6).
	 * <p>
	 * The node identifier is a random number that is generated with each method
	 * invocation.
	 * 
	 * @return a UUIDv6
	 * @see TimeOrderedFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedWithRandom() {
		return UUID6_RANDOM.create();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv6).
	 * <p>
	 * {@link Instant} accuracy is be limited to 1 millisecond on Linux with JDK 8.
	 * On Windows, its accuracy may be limited to 15.625ms (64hz).
	 * <p>
	 * The clock sequence is a number between 0 and 16383 (2^14 - 1). If the value
	 * passed as an argument is out of range, the result of MOD 2^14 will be used.
	 * <p>
	 * The node identifier is a number between 0 and 281474976710655 (2^48 - 1). If
	 * the value passed as an argument is out of range, the result of MOD 2^48 will
	 * be used.
	 * <p>
	 * Null arguments are ignored. If all arguments are null, this method works just
	 * like method {@link UuidCreator#getTimeOrdered()}.
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence between 0 and 2^14-1
	 * @param nodeid   an alternate node identifier between 0 and 2^48-1
	 * @return a UUIDv6
	 * @see TimeOrderedFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrdered(Instant instant, Integer clockseq, Long nodeid) {
		TimeOrderedFactory.Builder builder = TimeOrderedFactory.builder();
		if (instant != null) {
			builder.withInstant(instant);
		}
		if (clockseq != null) {
			builder.withClockSeq(clockseq);
		}
		if (nodeid != null) {
			builder.withNodeId(nodeid);
		}
		return builder.build().create();
	}

	/**
	 * Returns the minimum UUIDv6 for a given instant.
	 * <p>
	 * The 60 bits of the timestamp are filled with the bits of the given instant
	 * and the other 62 bits are all set to ZERO.
	 * <p>
	 * For example, the minimum UUIDv6 for 2022-02-22 22:22:22.222 is
	 * `{@code 1ec942de-7a1c-62e0-8000-000000000000}`, where
	 * `{@code 1ec942de-7a1c-_2e0}` is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv6
	 */
	public static UUID getTimeOrderedMin(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		TimeOrderedFactory.Builder builder = TimeOrderedFactory.builder();
		return builder.withInstant(instant).withClockSeq(0x0000L).withNodeId(0x000000000000L).build().create();
	}

	/**
	 * Returns the maximum UUIDv6 for a given instant.
	 * <p>
	 * The 60 bits of the timestamp are filled with the bits of the given instant
	 * and the other 62 bits are all set to ONE.
	 * <p>
	 * For example, the maximum UUIDv6 for 2022-02-22 22:22:22.222 is
	 * `{@code 1ec942de-7a1c-62e0-bfff-ffffffffffff}`, where
	 * `{@code 1ec942de-7a1c-_2e0}` is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv6
	 */
	public static UUID getTimeOrderedMax(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		TimeOrderedFactory.Builder builder = TimeOrderedFactory.builder();
		return builder.withInstant(instant).withClockSeq(0xffffL).withNodeId(0xffffffffffffL).build().create();
	}

	/**
	 * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
	 * <p>
	 * This method produces identifiers with 3 parts: time, counter and random.
	 * <p>
	 * The counter bits are incremented by 1 when the time repeats.
	 * <p>
	 * The random bits are generated with each method invocation.
	 * 
	 * @return a UUIDv7
	 * @since 5.0.0
	 * @see TimeOrderedEpochFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedEpoch() {
		return UUID7.create();
	}

	/**
	 * Returns a fast time-ordered unique identifier that uses Unix Epoch (UUIDv7).
	 * <p>
	 * This method produces identifiers with 3 parts: time, counter and random.
	 * <p>
	 * The counter bits are incremented by 1 when the time repeats.
	 * <p>
	 * The random bits are generated with each method invocation.
	 * <p>
	 * It employs {@link ThreadLocalRandom} which works very well, although not
	 * cryptographically strong. It can be useful, for example, for logging.
	 * <p>
	 * Security-sensitive applications that require a cryptographically secure
	 * pseudo-random generator should use {@link UuidCreator#getTimeOrderedEpoch()}.
	 * 
	 * @return a UUIDv7
	 * @since 6.0.0
	 * @see TimeOrderedEpochFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedEpochFast() {
		return UUID7_FAST.create();
	}

	/**
	 * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
	 * <p>
	 * This method produces identifiers with 2 parts: time and monotonic random.
	 * <p>
	 * The monotonic random bits are incremented by 1 when the time repeats.
	 * 
	 * @return a UUIDv7
	 * @since 5.0.0
	 * @see TimeOrderedEpochFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedEpochPlus1() {
		return UUID7_PLUS_1.create();
	}

	/**
	 * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7).
	 * <p>
	 * This method produces identifiers with 2 parts: time and monotonic random.
	 * <p>
	 * The monotonic random bits are incremented by a random number between 1 and
	 * 2^32 when the time repeats.
	 * 
	 * @return a UUIDv7
	 * @since 5.0.0
	 * @see TimeOrderedEpochFactory
	 * @see <a href=
	 *      "https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html">New
	 *      UUID Formats</a>
	 */
	public static UUID getTimeOrderedEpochPlusN() {
		return UUID7_PLUS_N.create();
	}

	/**
	 * Returns a time-ordered unique identifier that uses Unix Epoch (UUIDv7) for a
	 * given instant.
	 * <p>
	 * This method produces identifiers with 2 parts: time and secure random.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are random.
	 * <p>
	 * For example, the maximum UUIDv7 for 2022-02-22 22:22:22.222 is
	 * `{@code 017f2387-460e-7012-b345-6789abcdef01}`, where `{@code 017f2387-460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * The random bits are generated with each method invocation.
	 * <p>
	 * You can use this method to produce UUIDs with any instant you want, for
	 * example to obfuscate the actual generation instant in a simple way. Example:
	 * <p>
	 * 
	 * <pre>{@code
	 * // Shift the generation instant 1 day ahead of system clock
	 * Instant instant = Instant.now().plus(Duration.ofDays(1));
	 * UUID uuid = UuidCreator.getTimeOrderedEpoch(instant);
	 * }</pre>
	 *
	 * @param instant a given instant
	 * @return a UUIDv7
	 * @since 5.3.3
	 */
	public static UUID getTimeOrderedEpoch(Instant instant) {
		return UUID7.create(Parameters.builder().withInstant(instant).build());
	}

	/**
	 * Returns the minimum UUIDv7 for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ZERO.
	 * <p>
	 * For example, the minimum UUIDv7 for 2022-02-22 22:22:22.222 is
	 * `{@code 017f2387-460e-7000-8000-000000000000}`, where `{@code 017f2387-460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv7
	 */
	public static UUID getTimeOrderedEpochMin(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID((time << 16) | 0x7000L, 0x8000000000000000L);
	}

	/**
	 * Returns the maximum UUIDv7 for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ONE.
	 * <p>
	 * For example, the maximum UUIDv7 for 2022-02-22 22:22:22.222 is
	 * `{@code 017f2387-460e-7fff-bfff-ffffffffffff}`, where `{@code 017f2387-460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a UUIDv7
	 */
	public static UUID getTimeOrderedEpochMax(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID((time << 16) | 0x7fffL, 0xbfffffffffffffffL);
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param name a string
	 * @return a UUIDv3
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(String name) {
		return UUID3.create(Parameters.builder().withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * 
	 * @param name a byte array
	 * @return a UUIDv3
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(byte[] name) {
		return UUID3.create(Parameters.builder().withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a string
	 * @return a UUIDv3
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(UUID namespace, String name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a byte array
	 * @return a UUIDv3
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(UUID namespace, byte[] name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a string
	 * @return a UUIDv3
	 * @throws InvalidUuidException if namespace is invalid
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(String namespace, String name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a byte array
	 * @return a UUIDv3
	 * @throws InvalidUuidException if namespace is invalid
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(String namespace, byte[] name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * <p>
	 * Name spaces predefined by RFC 9562 (Appendix C):
	 * <ul>
	 * <li>NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * <li>NAMESPACE_URL: Name string is a URL;
	 * <li>NAMESPACE_OID: Name string is an ISO OID;
	 * <li>NAMESPACE_X500: Name string is an X.500 DN (in DER or text format).
	 * </ul>
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a string
	 * @return a UUIDv3
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, String name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses MD5 hashing (UUIDv3).
	 * <p>
	 * Name spaces predefined by RFC 9562 (Appendix C):
	 * <ul>
	 * <li>NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * <li>NAMESPACE_URL: Name string is a URL;
	 * <li>NAMESPACE_OID: Name string is an ISO OID;
	 * <li>NAMESPACE_X500: Name string is an X.500 DN (in DER or text format).
	 * </ul>
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a byte array
	 * @return a UUIDv3
	 * @see UuidNamespace
	 * @see NameBasedMd5Factory
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, byte[] name) {
		return UUID3.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param name a string
	 * @return a UUIDv5
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(String name) {
		return UUID5.create(Parameters.builder().withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * 
	 * @param name a byte array
	 * @return a UUIDv5
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(byte[] name) {
		return UUID5.create(Parameters.builder().withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a string
	 * @return a UUIDv5
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a byte array
	 * @return a UUIDv5
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(UUID namespace, byte[] name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a string
	 * @return a UUIDv5
	 * @throws InvalidUuidException if namespace is invalid
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(String namespace, String name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a byte array
	 * @return a UUIDv5
	 * @throws InvalidUuidException if namespace is invalid
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(String namespace, byte[] name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * <p>
	 * Name spaces predefined by RFC 9562 (Appendix C):
	 * <ul>
	 * <li>NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * <li>NAMESPACE_URL: Name string is a URL;
	 * <li>NAMESPACE_OID: Name string is an ISO OID;
	 * <li>NAMESPACE_X500: Name string is an X.500 DN (in DER or text format).
	 * </ul>
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a string
	 * @return a UUIDv5
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, String name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a name-based unique identifier that uses SHA-1 hashing (UUIDv5).
	 * <p>
	 * Name spaces predefined by RFC 9562 (Appendix C):
	 * <ul>
	 * <li>NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * <li>NAMESPACE_URL: Name string is a URL;
	 * <li>NAMESPACE_OID: Name string is an ISO OID;
	 * <li>NAMESPACE_X500: Name string is an X.500 DN (in DER or text format).
	 * </ul>
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a byte array
	 * @return a UUIDv5
	 * @see UuidNamespace
	 * @see NameBasedSha1Factory
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, byte[] name) {
		return UUID5.create(Parameters.builder().withNamespace(namespace).withName(name).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurity(byte localDomain, int localIdentifier) {
		return UUID2
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithMac(byte localDomain, int localIdentifier) {
		return UUID2_MAC
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithHash(byte localDomain, int localIdentifier) {
		return UUID2_HASH
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 *
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithRandom(byte localDomain, int localIdentifier) {
		return UUID2_RANDOM
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services
	 * (Chapter 11):
	 * <ul>
	 * <li>LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * <li>LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * <li>LOCAL_DOMAIN_ORG: 2.
	 * </ul>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurity(UuidLocalDomain localDomain, int localIdentifier) {
		return UUID2
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services
	 * (Chapter 11):
	 * <ul>
	 * <li>LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * <li>LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * <li>LOCAL_DOMAIN_ORG: 2.
	 * </ul>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithMac(UuidLocalDomain localDomain, int localIdentifier) {
		return UUID2_MAC
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services
	 * (Chapter 11):
	 * <ul>
	 * <li>LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * <li>LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * <li>LOCAL_DOMAIN_ORG: 2.
	 * </ul>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithHash(UuidLocalDomain localDomain, int localIdentifier) {
		return UUID2_HASH
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services
	 * (Chapter 11):
	 * <ul>
	 * <li>LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * <li>LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * <li>LOCAL_DOMAIN_ORG: 2.
	 * </ul>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 * @see UuidLocalDomain
	 * @see DceSecurityFactory
	 */
	public static UUID getDceSecurityWithRandom(UuidLocalDomain localDomain, int localIdentifier) {
		return UUID2_RANDOM
				.create(Parameters.builder().withLocalDomain(localDomain).withLocalIdentifier(localIdentifier).build());
	}

	/**
	 * Returns a Prefix COMB GUID.
	 * <p>
	 * The creation millisecond is a 6 bytes PREFIX is at the MOST significant bits.
	 * 
	 * @return a GUID
	 * @see PrefixCombFactory
	 * @see <a href="http://www.informit.com/articles/article.aspx?p=25862">The Cost
	 *      of GUIDs as Primary Keys</a>
	 */
	public static UUID getPrefixComb() {
		return COMB_PREFIX.create();
	}

	/**
	 * Returns the minimum Prefix COMB GUID for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ZERO.
	 * <p>
	 * For example, the minimum GUID for 2022-02-22 22:22:22.222 is
	 * `{@code 017f2387-460e-4000-8000-000000000000}`, where `{@code 017f2387-460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a GUID
	 */
	public static UUID getPrefixCombMin(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID((time << 16) | 0x4000L, 0x8000000000000000L);
	}

	/**
	 * Returns the maximum Prefix COMB GUID for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ONE.
	 * <p>
	 * For example, the maximum GUID for 2022-02-22 22:22:22.222 is
	 * `{@code 017f2387-460e-4fff-bfff-ffffffffffff}`, where `{@code 017f2387-460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a GUID
	 */
	public static UUID getPrefixCombMax(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID((time << 16) | 0x4fffL, 0xbfffffffffffffffL);
	}

	/**
	 * Returns a Suffix COMB GUID.
	 * 
	 * The creation millisecond is a 6 bytes SUFFIX is at the LEAST significant
	 * bits.
	 * 
	 * @return a GUID
	 * @see SuffixCombFactory
	 * @see <a href="http://www.informit.com/articles/article.aspx?p=25862">The Cost
	 *      of GUIDs as Primary Keys</a>
	 */
	public static UUID getSuffixComb() {
		return COMB_SUFFIX.create();
	}

	/**
	 * Returns the minimum Suffix COMB GUID for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ZERO.
	 * <p>
	 * For example, the minimum GUID for 2022-02-22 22:22:22.222 is
	 * `{@code 00000000-0000-4000-8000-017f2387460e}`, where `{@code 017f2387460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a GUID
	 */
	public static UUID getSuffixCombMin(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID(0x0000000000004000L, 0x8000000000000000L | (time & 0x0000ffffffffffffL));
	}

	/**
	 * Returns the maximum Suffix COMB GUID for a given instant.
	 * <p>
	 * The 48 bits of the time component are filled with the bits of the given
	 * instant and the other 74 bits are all set to ONE.
	 * <p>
	 * For example, the maximum GUID for 2022-02-22 22:22:22.222 is
	 * `{@code ffffffff-ffff-4fff-bfff-017f2387460e}`, where `{@code 017f2387460e}`
	 * is the timestamp in hexadecimal.
	 * <p>
	 * It can be useful to find all records before or after a specific timestamp in
	 * a table without a `{@code created_at}` field.
	 * 
	 * @param instant a given instant
	 * @return a GUID
	 */
	public static UUID getSuffixCombMax(Instant instant) {
		Objects.requireNonNull(instant, "Null instant");
		final long time = instant.toEpochMilli();
		return new UUID(0xffffffffffff4fffL, 0xbfff000000000000L | (time & 0x0000ffffffffffffL));
	}

	/**
	 * Returns n Short Prefix COMB GUID.
	 * <p>
	 * The creation minute is a 2 bytes PREFIX is at the MOST significant bits.
	 * <p>
	 * The prefix wraps around every ~45 days (2^16/60/24 = ~45).
	 * 
	 * @return a GUID
	 * @see ShortPrefixCombFactory
	 * @see <a href=
	 *      "https://www.2ndquadrant.com/en/blog/sequential-uuid-generators">Sequential
	 *      UUID Generators</a>
	 */
	public static UUID getShortPrefixComb() {
		return COMB_SHORT_PREFIX.create();
	}

	/**
	 * Returns a Short Suffix COMB GUID.
	 * <p>
	 * The creation minute is a 2 bytes SUFFIX is at the LEAST significant bits.
	 * <p>
	 * The suffix wraps around every ~45 days (2^16/60/24 = ~45).
	 * 
	 * @return a GUID
	 * @see ShortSuffixCombFactory
	 * @see <a href=
	 *      "https://www.2ndquadrant.com/en/blog/sequential-uuid-generators">Sequential
	 *      UUID Generators</a>
	 */
	public static UUID getShortSuffixComb() {
		return COMB_SHORT_SUFFIX.create();
	}

	// ***************************************
	// Lazy holders
	// ***************************************

	private static final Proxy UUID1 = new Proxy(Proxy.UUID1);
	private static final Proxy UUID1_MAC = new Proxy(Proxy.UUID1_MAC);
	private static final Proxy UUID1_HASH = new Proxy(Proxy.UUID1_HASH);
	private static final Proxy UUID1_RANDOM = new Proxy(Proxy.UUID1_RANDOM);
	private static final Proxy UUID2 = new Proxy(Proxy.UUID2);
	private static final Proxy UUID2_MAC = new Proxy(Proxy.UUID2_MAC);
	private static final Proxy UUID2_HASH = new Proxy(Proxy.UUID2_HASH);
	private static final Proxy UUID2_RANDOM = new Proxy(Proxy.UUID2_RANDOM);
	private static final Proxy UUID3 = new Proxy(Proxy.UUID3);
	private static final Proxy UUID4 = new Proxy(Proxy.UUID4);
	private static final Proxy UUID4_FAST = new Proxy(Proxy.UUID4_FAST);
	private static final Proxy UUID5 = new Proxy(Proxy.UUID5);
	private static final Proxy UUID6 = new Proxy(Proxy.UUID6);
	private static final Proxy UUID6_MAC = new Proxy(Proxy.UUID6_MAC);
	private static final Proxy UUID6_HASH = new Proxy(Proxy.UUID6_HASH);
	private static final Proxy UUID6_RANDOM = new Proxy(Proxy.UUID6_RANDOM);
	private static final Proxy UUID7 = new Proxy(Proxy.UUID7);
	private static final Proxy UUID7_FAST = new Proxy(Proxy.UUID7_FAST);
	private static final Proxy UUID7_PLUS_1 = new Proxy(Proxy.UUID7_PLUS_1);
	private static final Proxy UUID7_PLUS_N = new Proxy(Proxy.UUID7_PLUS_N);
	private static final Proxy COMB_PREFIX = new Proxy(Proxy.COMB_PREFIX);
	private static final Proxy COMB_SUFFIX = new Proxy(Proxy.COMB_SUFFIX);
	private static final Proxy COMB_SHORT_PREFIX = new Proxy(Proxy.COMB_SHORT_PREFIX);
	private static final Proxy COMB_SHORT_SUFFIX = new Proxy(Proxy.COMB_SHORT_SUFFIX);

	private static class Proxy extends UuidFactory {

		private UuidFactory factory = null;
		private Supplier<UuidFactory> supplier;
		private static final ReentrantLock lock = new ReentrantLock();

		// @formatter:off
		static final Supplier<UuidFactory> UUID1 = TimeBasedFactory::new;
		static final Supplier<UuidFactory> UUID1_MAC = () -> TimeBasedFactory.builder().withMacNodeId().build();
		static final Supplier<UuidFactory> UUID1_HASH = () -> TimeBasedFactory.builder().withHashNodeId().build();
		static final Supplier<UuidFactory> UUID1_RANDOM = () -> TimeBasedFactory.builder().withRandomNodeId().build();
		static final Supplier<UuidFactory> UUID2 = DceSecurityFactory::new;
		static final Supplier<UuidFactory> UUID2_MAC = () -> DceSecurityFactory.builder().withMacNodeId().build();
		static final Supplier<UuidFactory> UUID2_HASH = () -> DceSecurityFactory.builder().withHashNodeId().build();
		static final Supplier<UuidFactory> UUID2_RANDOM = () -> DceSecurityFactory.builder().withRandomNodeId().build();
		static final Supplier<UuidFactory> UUID3 = NameBasedMd5Factory::new;
		static final Supplier<UuidFactory> UUID4 = RandomBasedFactory::new;
		static final Supplier<UuidFactory> UUID4_FAST = () -> RandomBasedFactory.builder().withFastRandom().build();
		static final Supplier<UuidFactory> UUID5 = NameBasedSha1Factory::new;
		static final Supplier<UuidFactory> UUID6 = TimeOrderedFactory::new;
		static final Supplier<UuidFactory> UUID6_MAC = () -> TimeOrderedFactory.builder().withMacNodeId().build();
		static final Supplier<UuidFactory> UUID6_HASH = () -> TimeOrderedFactory.builder().withHashNodeId().build();
		static final Supplier<UuidFactory> UUID6_RANDOM = () -> TimeOrderedFactory.builder().withRandomNodeId().build();
		static final Supplier<UuidFactory> UUID7 = TimeOrderedEpochFactory::new;
		static final Supplier<UuidFactory> UUID7_FAST = () -> TimeOrderedEpochFactory.builder().withFastRandom().build();
		static final Supplier<UuidFactory> UUID7_PLUS_1 = () -> TimeOrderedEpochFactory.builder().withIncrementPlus1().build();
		static final Supplier<UuidFactory> UUID7_PLUS_N = () -> TimeOrderedEpochFactory.builder().withIncrementPlusN().build();
		static final Supplier<UuidFactory> COMB_PREFIX = PrefixCombFactory::new;
		static final Supplier<UuidFactory> COMB_SUFFIX = SuffixCombFactory::new;
		static final Supplier<UuidFactory> COMB_SHORT_PREFIX = ShortPrefixCombFactory::new;
		static final Supplier<UuidFactory> COMB_SHORT_SUFFIX = ShortSuffixCombFactory::new;
		// @formatter:on

		public Proxy(Supplier<UuidFactory> supplier) {
			this.supplier = supplier;
		}

		private UuidFactory get() {

			if (factory != null) {
				return factory;
			}

			lock.lock();
			try {
				if (factory == null) {
					this.factory = supplier.get();
				}
				return this.factory;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public UUID create() {
			return get().create();
		}

		@Override
		public UUID create(Parameters parameters) {
			return get().create(parameters);
		}
	}
}
