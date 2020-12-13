/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.UuidStringCodec;
import com.github.f4b6a3.uuid.creator.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.nonstandard.PrefixCombCreator;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortPrefixCombCreator;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortSuffixCombCreator;
import com.github.f4b6a3.uuid.creator.nonstandard.SuffixCombCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

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
		return UuidBytesCodecLazyHolder.CODEC.encode(uuid);
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
		return UuidBytesCodecLazyHolder.CODEC.decode(uuid);
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
		return UuidStringCodecLazyHolder.CODEC.encode(uuid);
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
		return UuidStringCodecLazyHolder.CODEC.decode(uuid);
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
		return RandomCreatorHolder.INSTANCE.create();
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
		return TimeBasedCreatorHolder.INSTANCE.create();
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
		return TimeBasedWithMacCreatorHolder.INSTANCE.create();
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
		return TimeBasedWithHashCreatorHolder.INSTANCE.create();
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @param nodeid   an alternate node (0 to 2^48-1)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBased(Instant instant, Integer clockseq, Long nodeid) {
		return TimeBasedCreatorHolder.INSTANCE.create(instant, clockseq, nodeid);
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithMac(Instant instant, Integer clockseq) {
		return TimeBasedWithMacCreatorHolder.INSTANCE.create(instant, clockseq, null);
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 1 UUID
	 */
	public static UUID getTimeBasedWithHash(Instant instant, Integer clockseq) {
		return TimeBasedWithHashCreatorHolder.INSTANCE.create(instant, clockseq, null);
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
		return TimeOrderedCreatorHolder.INSTANCE.create();
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
		return TimeOrderedWithMacCreatorHolder.INSTANCE.create();
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
		return TimeOrderedWithHashCreatorHolder.INSTANCE.create();
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @param nodeid   an alternate node (0 to 2^48-1)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrdered(Instant instant, Integer clockseq, Long nodeid) {
		return TimeOrderedCreatorHolder.INSTANCE.create(instant, clockseq, nodeid);
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithMac(Instant instant, Integer clockseq) {
		return TimeOrderedWithMacCreatorHolder.INSTANCE.create(instant, clockseq, null);
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
	 * @see {@link AbstractTimeBasedUuidCreator#create(Instant, Integer, Long)}
	 * 
	 * @param instant  an alternate instant
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return a version 6 UUID
	 */
	public static UUID getTimeOrderedWithHash(Instant instant, Integer clockseq) {
		return TimeOrderedWithHashCreatorHolder.INSTANCE.create(instant, clockseq, null);
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
	 * @param name a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(name);
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
		return NameBasedMd5CreatorHolder.INSTANCE.create(name);
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
	 * @param namespace a custom name space UUID
	 * @param name      a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
	 * @param namespace a custom name space UUID in string format
	 * @param name      a name string
	 * @return a version 3 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedMd5(String namespace, String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * @param namespace a predefined name space enumeration
	 * @param name      a name string
	 * @return a version 3 UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, String name) {
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedMd5CreatorHolder.INSTANCE.create(namespace, name);
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
	 * @param name a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(name);
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
		return NameBasedSha1CreatorHolder.INSTANCE.create(name);
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
	 * @param namespace a custom name space UUID
	 * @param name      a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
	 * @param namespace a custom name space UUID in string format
	 * @param name      a name string
	 * @return a version 5 UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public static UUID getNameBasedSha1(String namespace, String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
	 * @param namespace a predefined name space enumeration
	 * @param name      a name string
	 * @return a version 5 UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, String name) {
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
		return NameBasedSha1CreatorHolder.INSTANCE.create(namespace, name);
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
		return DceSecurityCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return DceSecurityWithMacCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return DceSecurityWithHashCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return DceSecurityCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return DceSecurityWithMacCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return DceSecurityWithHashCreatorHolder.INSTANCE.create(localDomain, localIdentifier);
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
		return PrefixCombCreatorHolder.INSTANCE.create();
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
		return SuffixCombCreatorHolder.INSTANCE.create();
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
		return ShortPrefixCombCreatorHolder.INSTANCE.create();
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
		return ShortSuffixCombCreatorHolder.INSTANCE.create();
	}

	/*
	 * Public static methods for creating FACTORIES of UUIDs
	 */

	/**
	 * Returns a {@link RandomBasedUuidCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomBasedUuidCreator}
	 */
	public static RandomBasedUuidCreator getRandomBasedCreator() {
		return new RandomBasedUuidCreator();
	}

	/**
	 * Returns a {@link TimeBasedUuidCreator} that creates UUID version 1.
	 * 
	 * @return {@link TimeBasedUuidCreator}
	 */
	public static TimeBasedUuidCreator getTimeBasedCreator() {
		return new TimeBasedUuidCreator();
	}

	/**
	 * Returns a {@link TimeOrderedUuidCreator} that creates UUID version 6.
	 * 
	 * @return {@link TimeOrderedUuidCreator}
	 */
	public static TimeOrderedUuidCreator getTimeOrderedCreator() {
		return new TimeOrderedUuidCreator();
	}

	/**
	 * Returns a {@link NameBasedMd5UuidCreator} that creates UUID version 3.
	 * 
	 * @return {@link NameBasedMd5UuidCreator}
	 */
	public static NameBasedMd5UuidCreator getNameBasedMd5Creator() {
		return new NameBasedMd5UuidCreator();
	}

	/**
	 * Returns a {@link NameBasedSha1UuidCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSha1UuidCreator}
	 */
	public static NameBasedSha1UuidCreator getNameBasedSha1Creator() {
		return new NameBasedSha1UuidCreator();
	}

	/**
	 * Returns a {@link DceSecurityUuidCreator} that creates UUID version 2.
	 * 
	 * @return {@link DceSecurityUuidCreator}
	 */
	public static DceSecurityUuidCreator getDceSecurityCreator() {
		return new DceSecurityUuidCreator();
	}

	/**
	 * Returns a {@link SuffixCombCreator}.
	 * 
	 * Read: The Cost of GUIDs as Primary Keys
	 * http://www.informit.com/articles/article.aspx?p=25862
	 * 
	 * @return {@link SuffixCombCreator}
	 */
	public static SuffixCombCreator getSuffixCombCreator() {
		return new SuffixCombCreator();
	}

	/**
	 * Returns a {@link PrefixCombCreator}.
	 * 
	 * Read: The Cost of GUIDs as Primary Keys
	 * http://www.informit.com/articles/article.aspx?p=25862
	 * 
	 * @return {@link PrefixCombCreator}
	 */
	public static PrefixCombCreator getPrefixCombCreator() {
		return new PrefixCombCreator();
	}

	/**
	 * Returns a {@link ShortPrefixCombCreator}.
	 * 
	 * Read: Sequential UUID Generators
	 * https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/
	 * 
	 * @return {@link ShortPrefixCombCreator}
	 */
	public static ShortPrefixCombCreator getShortPrefixCombCreator() {
		return new ShortPrefixCombCreator();
	}

	/**
	 * Returns a {@link ShortSuffixCombCreator}.
	 * 
	 * Read: Sequential UUID Generators
	 * https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/
	 * 
	 * @return {@link ShortSuffixCombCreator}
	 */
	public static ShortSuffixCombCreator getShortSuffixCombCreator() {
		return new ShortSuffixCombCreator();
	}

	/*
	 * Private classes for lazy holders
	 */

	private static class UuidBytesCodecLazyHolder {
		private static final UuidCodec<byte[]> CODEC = new UuidBytesCodec();
	}

	private static class UuidStringCodecLazyHolder {
		private static final UuidCodec<String> CODEC = new UuidStringCodec();
	}

	private static class RandomCreatorHolder {
		static final RandomBasedUuidCreator INSTANCE = getRandomBasedCreator();
	}

	private static class TimeOrderedCreatorHolder {
		static final TimeOrderedUuidCreator INSTANCE = getTimeOrderedCreator();
	}

	private static class TimeOrderedWithMacCreatorHolder {
		static final TimeOrderedUuidCreator INSTANCE = getTimeOrderedCreator().withMacNodeIdentifier();
	}

	private static class TimeOrderedWithHashCreatorHolder {
		static final TimeOrderedUuidCreator INSTANCE = getTimeOrderedCreator().withHashNodeIdentifier();
	}

	private static class TimeBasedCreatorHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator();
	}

	private static class TimeBasedWithMacCreatorHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withMacNodeIdentifier();
	}

	private static class TimeBasedWithHashCreatorHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withHashNodeIdentifier();
	}

	private static class NameBasedMd5CreatorHolder {
		static final NameBasedMd5UuidCreator INSTANCE = getNameBasedMd5Creator();
	}

	private static class NameBasedSha1CreatorHolder {
		static final NameBasedSha1UuidCreator INSTANCE = getNameBasedSha1Creator();
	}

	private static class DceSecurityCreatorHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator();
	}

	private static class DceSecurityWithMacCreatorHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator().withMacNodeIdentifier();
	}

	private static class DceSecurityWithHashCreatorHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator().withHashNodeIdentifier();
	}

	private static class SuffixCombCreatorHolder {
		static final SuffixCombCreator INSTANCE = getSuffixCombCreator();
	}

	private static class PrefixCombCreatorHolder {
		static final PrefixCombCreator INSTANCE = getPrefixCombCreator();
	}

	private static class ShortPrefixCombCreatorHolder {
		static final ShortPrefixCombCreator INSTANCE = getShortPrefixCombCreator();
	}

	private static class ShortSuffixCombCreatorHolder {
		static final ShortSuffixCombCreator INSTANCE = getShortSuffixCombCreator();
	}
}
