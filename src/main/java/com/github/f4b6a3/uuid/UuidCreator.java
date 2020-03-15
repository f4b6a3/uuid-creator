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

import java.security.SecureRandom;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.factory.CombGuidCreator;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.UlidBasedGuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.RandomUuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.factory.SequentialUuidCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

/**
 * Facade to the UUID factories.
 */
public class UuidCreator {

	private UuidCreator() {
	}

	/**
	 * Returns a Nil UUID
	 * 
	 * The nil UUID is special UUID that has all 128 bits set to zero.
	 * 
	 * @return a Nil UUID
	 */
	public static UUID getNil() {
		return new UUID(0L, 0L);
	}

	/**
	 * Returns a random UUID.
	 * 
	 * The random generator used is {@link java.security.SecureRandom}.
	 * 
	 * <pre>
	 * It uses the Details: 
	 * - Version number: 4
	 * - Variant number: 1
	 * - Random generator: {@link SecureRandom}
	 * </pre>
	 * 
	 * @return a random UUID
	 */
	public static UUID getRandom() {
		return RandomCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a fast random UUID.
	 *
	 * The random generator used is {@link Xorshift128PlusRandom}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 4 
	 * - Variant number: 1 
	 * - Random generator: {@link Xorshift128PlusRandom}
	 * </pre>
	 * 
	 * @return a random UUID
	 */
	public static UUID getFastRandom() {
		return FastRandomCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and without machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 0 (non-standard)
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: NO
	 * </pre>
	 * 
	 * @return a sequential UUID
	 */
	public static UUID getSequential() {
		return SequentialCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 0 (non-standard)
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: YES 
	 * - Timestamp bytes are in the RFC-4122 order?: NO
	 * </pre>
	 * 
	 * @return a sequential UUID with MAC
	 */
	public static UUID getSequentialWithMac() {
		return SequentialWithMacCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and fingerprint, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 0 (non-standard)
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: NO
	 * </pre>
	 * 
	 * @return a sequential UUID with fingerprint
	 */
	public static UUID getSequentialWithFingerprint() {
		return SequentialWithFingerprintCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and without machine address.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @return a time-based UUID
	 */
	public static UUID getTimeBased() {
		return TimeBasedCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and machine address.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: YES 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @return a time-based UUID with a MAC
	 */
	public static UUID getTimeBasedWithMac() {
		return TimeBasedWithMacCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a UUID with timestamp and fingerprint.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 1 
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @return a time-based UUID with a fingerprint
	 */
	public static UUID getTimeBasedWithFingerprint() {
		return TimeBasedWithFingerprintCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a DCE Security UUID based on a local domain and a local
	 * identifier.
	 *
	 * <pre>
	 * Domain identifiers listed in the RFC-4122: 
	 * - Local Domain Person (POSIX UserID) = 0;
	 * - Local Domain Group (POSIX GroupID) = 1;
	 * - Local Domain Org = 2.
	 *
	 * Details: 
	 * - Version number: 2 
	 * - Variant number: 1 
	 * - Local domain: informed by user 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @param localDomain
	 *            a local domain
	 * @param localIdentifier
	 *            a local identifier
	 * @return a DCE Security UUID
	 */
	public static UUID getDceSecurity(byte localDomain, int localIdentifier) {
		return DceSecurityCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with machine address based on a local domain
	 * and a local identifier.
	 *
	 * <pre>
	 * Domain identifiers listed in the RFC-4122: 
	 * - Local Domain Person (POSIX UserID) = 0;
	 * - Local Domain Group (POSIX GroupID) = 1;
	 * - Local Domain Org = 2.
	 *
	 * Details: 
	 * - Version number: 2 
	 * - Variant number: 1 
	 * - Local domain: informed by user 
	 * - Has hardware address (MAC)?: YES 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @param localDomain
	 *            a local domain
	 * @param localIdentifier
	 *            a local identifier
	 * @return a DCE Security UUID
	 */
	public static UUID getDceSecurityWithMac(byte localDomain, int localIdentifier) {
		return DceSecurityWithMacCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID with fingerprint based on a local domain and
	 * a local identifier.
	 *
	 * <pre>
	 * Domain identifiers listed in the RFC-4122: 
	 * - Local Domain Person (POSIX UserID) = 0;
	 * - Local Domain Group (POSIX GroupID) = 1;
	 * - Local Domain Org = 2.
	 *
	 * Details: 
	 * - Version number: 2 
	 * - Variant number: 1 
	 * - Local domain: informed by user 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: YES
	 * </pre>
	 * 
	 * @param localDomain
	 *            a local domain
	 * @param localIdentifier
	 *            a local identifier
	 * @return a DCE Security UUID
	 */
	public static UUID getDceSecurityWithFingerprint(byte localDomain, int localIdentifier) {
		return DceSecurityWithFingerprintCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
	}

	/**
	 * Returns a UUID based on a name, using MD5.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Variant number: 1 
	 * - Hash Algorithm: MD5 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedMd5(String name) {
		return NameBasedMd5CreatorLazyHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a UUID based on a name space and a name, using MD5.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Variant number: 1 
	 * - Hash Algorithm: MD5 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace
	 *            a name space UUID
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, String name) {
		return NameBasedMd5CreatorLazyHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a UUID based on a name space and a name, using MD5.
	 *
	 * See {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 3 
	 * - Variant number: 1 
	 * - Hash Algorithm: MD5 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace
	 *            a name space enumeration
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedMd5(UuidNamespace namespace, String name) {
		return NameBasedMd5CreatorLazyHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a UUID based on a name, using SHA1.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Variant number: 1 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha1(String name) {
		return NameBasedSha1CreatorLazyHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a UUID based on a name space and a name, using SHA1.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Variant number: 1 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace
	 *            a name space UUID
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		return NameBasedSha1CreatorLazyHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a UUID based on a name space and a name, using SHA1.
	 *
	 * See {@link UuidNamespace}.
	 * 
	 * <pre>
	 * Details: 
	 * - Version number: 5 
	 * - Variant number: 1 
	 * - Hash Algorithm: SHA1 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace
	 *            a name space enumeration
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha1(UuidNamespace namespace, String name) {
		return NameBasedSha1CreatorLazyHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a COMB GUID for MS SQL Server.
	 * 
	 * This implementation is derived from the ULID specification.
	 * 
	 * @return a COMB GUID
	 */
	public static UUID getCombGuid() {
		return CombCreatorLazyHolder.INSTANCE.create();
	}

	/**
	 * Returns a GUID based on the ULID specification.
	 * 
	 * @return a ULID-based GUID
	 */
	public static UUID getUlidBasedGuid() {
		return UlidBasedCreatorLazyHolder.INSTANCE.create();
	}

	/*
	 * Public static methods for creating FACTORIES of UUIDs
	 */

	/**
	 * Returns a {@link SequentialUuidCreator} that creates UUID version 0.
	 * 
	 * @return {@link SequentialUuidCreator}
	 */
	public static SequentialUuidCreator getSequentialCreator() {
		return new SequentialUuidCreator();
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
	 * Returns a {@link DceSecurityUuidCreator} that creates UUID version 2.
	 * 
	 * @return {@link DceSecurityUuidCreator}
	 */
	public static DceSecurityUuidCreator getDceSecurityCreator() {
		return new DceSecurityUuidCreator();
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
	 * Returns a {@link RandomUuidCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	public static RandomUuidCreator getRandomCreator() {
		return new RandomUuidCreator();
	}

	/**
	 * Returns a {@link RandomUuidCreator} that creates UUID version 4.
	 * 
	 * The random generator used is {@link Xorshift128PlusRandom}.
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	public static RandomUuidCreator getFastRandomCreator() {
		return new RandomUuidCreator().withFastRandomGenerator();
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
	 * Returns a {@link CombGuidCreator}.
	 * 
	 * @return {@link CombGuidCreator}
	 */
	public static CombGuidCreator getCombCreator() {
		return new CombGuidCreator();
	}

	/**
	 * Returns a {@link UlidBasedGuidCreator}.
	 * 
	 * @return {@link UlidBasedGuidCreator}
	 */
	public static UlidBasedGuidCreator getUlidBasedCreator() {
		return new UlidBasedGuidCreator();
	}

	/*
	 * Private classes for lazy holders
	 */

	private static class RandomCreatorLazyHolder {
		static final RandomUuidCreator INSTANCE = getRandomCreator();
	}

	private static class FastRandomCreatorLazyHolder {
		static final RandomUuidCreator INSTANCE = getFastRandomCreator();
	}

	private static class SequentialCreatorLazyHolder {
		static final SequentialUuidCreator INSTANCE = getSequentialCreator().withoutOverrunException();
	}

	private static class SequentialWithMacCreatorLazyHolder {
		static final SequentialUuidCreator INSTANCE = getSequentialCreator().withoutOverrunException()
				.withHardwareAddressNodeIdentifier();
	}

	private static class SequentialWithFingerprintCreatorLazyHolder {
		static final SequentialUuidCreator INSTANCE = getSequentialCreator().withoutOverrunException()
				.withFingerprintNodeIdentifier();
	}

	private static class TimeBasedCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withoutOverrunException();
	}

	private static class TimeBasedWithMacCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withoutOverrunException()
				.withHardwareAddressNodeIdentifier();
	}

	private static class TimeBasedWithFingerprintCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withoutOverrunException()
				.withFingerprintNodeIdentifier();
	}

	private static class NameBasedMd5CreatorLazyHolder {
		static final NameBasedMd5UuidCreator INSTANCE = getNameBasedMd5Creator();
	}

	private static class NameBasedSha1CreatorLazyHolder {
		static final NameBasedSha1UuidCreator INSTANCE = getNameBasedSha1Creator();
	}

	private static class DceSecurityCreatorLazyHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator();
	}

	private static class DceSecurityWithMacCreatorLazyHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator().withHardwareAddressNodeIdentifier();
	}

	private static class DceSecurityWithFingerprintCreatorLazyHolder {
		static final DceSecurityUuidCreator INSTANCE = getDceSecurityCreator().withFingerprintNodeIdentifier();
	}

	private static class CombCreatorLazyHolder {
		static final CombGuidCreator INSTANCE = getCombCreator();
	}

	private static class UlidBasedCreatorLazyHolder {
		static final UlidBasedGuidCreator INSTANCE = getUlidBasedCreator();
	}
}
