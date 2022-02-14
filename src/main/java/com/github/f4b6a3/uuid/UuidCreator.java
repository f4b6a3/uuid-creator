/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.StringCodec;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortPrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortSuffixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.SuffixCombFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.DceSecurityFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.NameBasedMd5Factory;
import com.github.f4b6a3.uuid.factory.rfc4122.NameBasedSha1Factory;
import com.github.f4b6a3.uuid.factory.rfc4122.RandomBasedFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeBasedFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedFactory;

/**
 * Facade to all the UUID generators.
 */
public final class UuidCreator {

	public static final UuidNamespace NAMESPACE_DNS = UuidNamespace.NAMESPACE_DNS;
	public static final UuidNamespace NAMESPACE_URL = UuidNamespace.NAMESPACE_URL;
	public static final UuidNamespace NAMESPACE_ISO_OID = UuidNamespace.NAMESPACE_ISO_OID;
	public static final UuidNamespace NAMESPACE_X500_DN = UuidNamespace.NAMESPACE_X500_DN;

	public static final UuidLocalDomain LOCAL_DOMAIN_PERSON = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
	public static final UuidLocalDomain LOCAL_DOMAIN_GROUP = UuidLocalDomain.LOCAL_DOMAIN_GROUP;
	public static final UuidLocalDomain LOCAL_DOMAIN_ORG = UuidLocalDomain.LOCAL_DOMAIN_ORG;

	private static final UUID UUID_NIL = new UUID(0L, 0L);

	private UuidCreator() {
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 */
	public static byte[] toBytes(final UUID uuid) {
		return BinaryCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Returns a UUID from a byte array.
	 * 
	 * It validates the input byte array.
	 * 
	 * @param uuid a byte array
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromBytes(byte[] uuid) {
		return BinaryCodec.INSTANCE.decode(uuid);
	}

	/**
	 * Get a string from a UUID.
	 * 
	 * It may be much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * In JDK9+ it uses {@link UUID#toString()}.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 */
	public static String toString(UUID uuid) {
		return StringCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Returns a UUID from a string.
	 * 
	 * It accepts strings:
	 * 
	 * - With URN prefix: "urn:uuid:";
	 * 
	 * - With curly braces: '{' and '}';
	 * 
	 * - With upper or lower case;
	 * 
	 * - With or without hyphens.
	 * 
	 * It may be much faster than {@link UUID#fromString(String)} in JDK 8.
	 * 
	 * In JDK9+ it may be slightly faster.
	 * 
	 * @param uuid a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static UUID fromString(String uuid) {
		return StringCodec.INSTANCE.decode(uuid);
	}

	/**
	 * Returns a Nil UUID.
	 * 
	 * The Nil UUID is special UUID that has all 128 bits set to zero.
	 * 
	 * @return a Nil UUID
	 */
	public static UUID getNil() {
		return UUID_NIL;
	}

	/**
	 * Returns a random UUID.
	 * 
	 * The random generator is {@link java.security.SecureRandom}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 4
	 * </pre>
	 * 
	 * @return a version 4 UUID
	 */
	public static UUID getRandomBased() {
		return RandomBasedHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Node identifier: random (default) or user defined
	 * </pre>
	 * 
	 * A user defined node identifier can be provided by the system property
	 * 'uuidcreator.node' or the environment variable 'UUIDCREATOR_NODE'.
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBased() {
		return TimeBasedHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID with hardware address as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithMac() {
		return TimeBasedWithMacHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID with system data hash as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithHash() {
		return TimeBasedWithHashHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID with random node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithRandom() {
		return TimeBasedWithRandomHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-based UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Node identifier: random
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @param nodeid   an alternate node (0 to 2^48-1)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBased(Instant instant, Integer clockseq, Long nodeid) {
		return TimeBasedHolder.INSTANCE.create(instant, clockseq, nodeid);
	}

	/**
	 * Returns a time-based UUID with hardware address as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithMac(Instant instant, Integer clockseq) {
		return TimeBasedWithMacHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a time-based UUID with system data hash as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithHash(Instant instant, Integer clockseq) {
		return TimeBasedWithHashHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a time-based UUID with random node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithRandom(Instant instant, Integer clockseq) {
		return TimeBasedWithRandomHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a time-ordered UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: random (default) or user defined
	 * </pre>
	 * 
	 * A user defined node identifier can be provided by the system property
	 * 'uuidcreator.node' or the environment variable 'UUIDCREATOR_NODE'.
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrdered() {
		return TimeOrderedHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID with hardware address as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithMac() {
		return TimeOrderedWithMacHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID with system data hash as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithHash() {
		return TimeOrderedWithHashHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID with random node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithRandom() {
		return TimeOrderedWithRandomHolder.INSTANCE.create();
	}

	/**
	 * Returns a time-ordered UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: random
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @param nodeid   an alternate node (0 to 2^48-1)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrdered(Instant instant, Integer clockseq, Long nodeid) {
		return TimeOrderedHolder.INSTANCE.create(instant, clockseq, nodeid);
	}

	/**
	 * Returns a time-ordered UUID with hardware address as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithMac(Instant instant, Integer clockseq) {
		return TimeOrderedWithMacHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a time-ordered UUID with system data hash as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithHash(Instant instant, Integer clockseq) {
		return TimeOrderedWithHashHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a time-ordered UUID with random node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 6
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithRandom(Instant instant, Integer clockseq) {
		return TimeOrderedWithRandomHolder.INSTANCE.create(instant, clockseq, null);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: NO
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedMd5(byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param name a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(String name) {
		return NameBasedMd5Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: NO
	 * </pre>
	 * 
	 * @param name a byte array
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(byte[] name) {
		return NameBasedMd5Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: NO
	 * </pre>
	 * 
	 * @param name a UUID
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UUID name) {
		return NameBasedMd5Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedMd5(UUID, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, String name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a byte array
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, byte[] name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a UUID
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, UUID name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedMd5(String, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a name string
	 * @return a version 3 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedMd5(String namespace, String name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a byte array
	 * @return a version 3 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedMd5(String namespace, byte[] name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a UUID
	 * @return a version 3 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedMd5(String namespace, UUID name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedMd5(UuidNamespace, byte[])} instead.
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, String name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a byte array
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, byte[] name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (MD5).
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Hash Algorithm: MD5 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a UUID
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, UUID name) {
		return NameBasedMd5Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: NO
	 * </pre>
	 *
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedSha1(byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param name a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(String name) {
		return NameBasedSha1Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: NO
	 * </pre>
	 * 
	 * @param name a byte array
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(byte[] name) {
		return NameBasedSha1Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: NO
	 * </pre>
	 * 
	 * @param name a UUID
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UUID name) {
		return NameBasedSha1Holder.INSTANCE.create(name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedSha1(UUID, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a byte array
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, byte[] name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID
	 * @param name      a UUID
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, UUID name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedSha1(String, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a name string
	 * @return a version 5 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedSha1(String namespace, String name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a byte array
	 * @return a version 5 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedSha1(String namespace, byte[] name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (custom)
	 * </pre>
	 * 
	 * @param namespace a custom name space UUID in string format
	 * @param name      a UUID
	 * @return a version 5 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedSha1(String namespace, UUID name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #getNameBasedSha1(UuidNamespace, byte[])} instead.
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, String name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a byte array
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, byte[] name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a name-based UUID (SHA1).
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: YES (predefined)
	 * </pre>
	 * 
	 * <pre>
	 * Name spaces predefined by RFC-4122 (Appendix C):
	 * 
	 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
	 * - NAMESPACE_URL: Name string is a URL;
	 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
	 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
	 * </pre>
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a UUID
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, UUID name) {
		return NameBasedSha1Holder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a DCE Security UUID.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: random
	 * </pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurity(byte localDomain, int localIdentifier) {
		return DceSecurityHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with hardware address as node identifier.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithMac(byte localDomain, int localIdentifier) {
		return DceSecurityWithMacHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with system data hash as node identifier.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithHash(byte localDomain, int localIdentifier) {
		return DceSecurityWithHashHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with random node identifier.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @param localDomain     a custom local domain byte
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithRandom(byte localDomain, int localIdentifier) {
		return DceSecurityWithRandomHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: random
	 * </pre>
	 * 
	 * <pre>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services (Chapter 11):
	 * 
	 * - LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * - LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * - LOCAL_DOMAIN_ORG: 2.
	 * </pre>
	 * 
	 * See: {@link UuidLocalDomain}.
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurity(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with hardware address as node identifier.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: MAC
	 * </pre>
	 * 
	 * <pre>
	 * Local domains predefined by DCE 1.1 Authentication and Security Services (Chapter 11):
	 * 
	 * - LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
	 * - LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
	 * - LOCAL_DOMAIN_ORG: 2.
	 * </pre>
	 * 
	 * See: {@link UuidLocalDomain}.
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithMac(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityWithMacHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with system data hash as node identifier.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: system data hash
	 * </pre>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithHash(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityWithHashHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with random node identifier.
	 *
	 * See: {@link UuidLocalDomain}.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 2 
	 * - Node identifier: random (always changing)
	 * </pre>
	 * 
	 * @param localDomain     a predefined local domain enumeration
	 * @param localIdentifier a local identifier
	 * @return a version 2 UUID
	 */
	public static UUID getDceSecurityWithRandom(UuidLocalDomain localDomain, int localIdentifier) {
		return DceSecurityWithRandomHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a Prefix COMB.
	 * 
	 * The creation millisecond is a 6 bytes PREFIX is at the MOST significant bits.
	 * 
	 * Read: The Cost of GUIDs as Primary Keys
	 * http://www.informit.com/articles/article.aspx?p=25862
	 * 
	 * @return a GUID
	 */
	public static UUID getPrefixComb() {
		return PrefixCombHolder.INSTANCE.create();
	}

	/**
	 * Returns a Suffix COMB.
	 * 
	 * The creation millisecond is a 6 bytes SUFFIX is at the LEAST significant
	 * bits.
	 * 
	 * Read: The Cost of GUIDs as Primary Keys
	 * http://www.informit.com/articles/article.aspx?p=25862
	 * 
	 * @return a GUID
	 */
	public static UUID getSuffixComb() {
		return SuffixCombHolder.INSTANCE.create();
	}

	/**
	 * Returns n Short Prefix COMB.
	 * 
	 * The creation minute is a 2 bytes PREFIX is at the MOST significant bits.
	 * 
	 * The prefix wraps around every ~45 days (2^16/60/24 = ~45).
	 * 
	 * Read: Sequential UUID Generators
	 * https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/
	 * 
	 * @return a GUID
	 */
	public static UUID getShortPrefixComb() {
		return ShortPrefixCombHolder.INSTANCE.create();
	}

	/**
	 * Returns a Short Suffix COMB.
	 * 
	 * The creation minute is a 2 bytes SUFFIX is at the LEAST significant bits.
	 * 
	 * The suffix wraps around every ~45 days (2^16/60/24 = ~45).
	 * 
	 * Read: Sequential UUID Generators
	 * https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/
	 * 
	 * @return a GUID
	 */
	public static UUID getShortSuffixComb() {
		return ShortSuffixCombHolder.INSTANCE.create();
	}

	/*
	 * Private classes for lazy holders
	 */

	private static class RandomBasedHolder {
		static final RandomBasedFactory INSTANCE = new RandomBasedFactory();
	}

	private static class TimeOrderedHolder {
		static final TimeOrderedFactory INSTANCE = new TimeOrderedFactory();
	}

	private static class TimeOrderedWithMacHolder {
		static final TimeOrderedFactory INSTANCE = new TimeOrderedFactory.Builder().withMacNodeId().build();
	}

	private static class TimeOrderedWithHashHolder {
		static final TimeOrderedFactory INSTANCE = new TimeOrderedFactory.Builder().withHashNodeId().build();
	}

	private static class TimeOrderedWithRandomHolder {
		static final TimeOrderedFactory INSTANCE = new TimeOrderedFactory.Builder().withRandomNodeId().build();
	}

	private static class TimeBasedHolder {
		static final TimeBasedFactory INSTANCE = new TimeBasedFactory();
	}

	private static class TimeBasedWithMacHolder {
		static final TimeBasedFactory INSTANCE = new TimeBasedFactory.Builder().withMacNodeId().build();
	}

	private static class TimeBasedWithHashHolder {
		static final TimeBasedFactory INSTANCE = new TimeBasedFactory.Builder().withHashNodeId().build();
	}

	private static class TimeBasedWithRandomHolder {
		static final TimeBasedFactory INSTANCE = new TimeBasedFactory.Builder().withRandomNodeId().build();
	}

	private static class NameBasedMd5Holder {
		static final NameBasedMd5Factory INSTANCE = new NameBasedMd5Factory();
	}

	private static class NameBasedSha1Holder {
		static final NameBasedSha1Factory INSTANCE = new NameBasedSha1Factory();
	}

	private static class DceSecurityHolder {
		static final DceSecurityFactory INSTANCE = new DceSecurityFactory();
	}

	private static class DceSecurityWithMacHolder {
		static final DceSecurityFactory INSTANCE = new DceSecurityFactory.Builder().withMacNodeId().build();
	}

	private static class DceSecurityWithHashHolder {
		static final DceSecurityFactory INSTANCE = new DceSecurityFactory.Builder().withHashNodeId().build();
	}

	private static class DceSecurityWithRandomHolder {
		static final DceSecurityFactory INSTANCE = new DceSecurityFactory.Builder().withRandomNodeId().build();
	}

	private static class SuffixCombHolder {
		static final SuffixCombFactory INSTANCE = new SuffixCombFactory();
	}

	private static class PrefixCombHolder {
		static final PrefixCombFactory INSTANCE = new PrefixCombFactory();
	}

	private static class ShortPrefixCombHolder {
		static final ShortPrefixCombFactory INSTANCE = new ShortPrefixCombFactory();
	}

	private static class ShortSuffixCombHolder {
		static final ShortSuffixCombFactory INSTANCE = new ShortSuffixCombFactory();
	}
}
