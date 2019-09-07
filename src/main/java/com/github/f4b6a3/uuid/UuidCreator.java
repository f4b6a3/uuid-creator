/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 */

package com.github.f4b6a3.uuid;

import java.security.SecureRandom;
import java.util.UUID;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.factory.CombGuidCreator;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.LexicalOrderGuidCreator;
import com.github.f4b6a3.uuid.factory.MssqlGuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.RandomUuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSha256UuidCreator;
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
	 * Returns a random UUID.
	 * 
	 * The random generator used is {@link java.security.SecureRandom} with
	 * SHA1PRNG algorithm.
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
		try {
			return SequentialCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return SequentialCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return SequentialWithMacCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return SequentialWithMacCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return SequentialWithFingerprintCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return SequentialWithFingerprintCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return TimeBasedCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return TimeBasedCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return TimeBasedWithMacCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return TimeBasedWithMacCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return TimeBasedWithFingerprintCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return TimeBasedWithFingerprintCreatorLazyHolder.INSTANCE.create();
		}
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
		try {
			return DceSecurityCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return DceSecurityCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		}
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
		try {
			return DceSecurityWithMacCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return DceSecurityWithMacCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		}
	}
	
	/**
	 * Returns a DCE Security UUID with fingerprint based on a local domain
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
		try {
			return DceSecurityWithFingerprintCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return DceSecurityWithFingerprintCreatorLazyHolder.INSTANCE.create(localDomain, localIdentifier);
		}
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
	 * Returns a UUID based on a name, using SHA256.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 4 
	 * - Variant number: 1 
	 * - Hash Algorithm: SHA256 
	 * - Name Space: none
	 * </pre>
	 * 
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha256(String name) {
		return NameBasedSha256CreatorLazyHolder.INSTANCE.create(name);
	}

	/**
	 * Returns a UUID based on a name space and a name, using SHA256.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 4 
	 * - Variant number: 1 
	 * - Hash Algorithm: SHA256 
	 * - Name Space: informed by user
	 * </pre>
	 * 
	 * @param namespace
	 *            a name space UUID
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha256(UUID namespace, String name) {
		return NameBasedSha256CreatorLazyHolder.INSTANCE.create(namespace, name);
	}

	/**
	 * Returns a time-based GUID for MSSQL Server.
	 * 
	 * @return a MSSQL GUID
	 */
	public static UUID getMssqlGuid() {
		try {
			return MssqlGuidCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overrun exception and trust the clock sequence
			return MssqlGuidCreatorLazyHolder.INSTANCE.create();
		}
	}

	/**
	 * Returns a COMB GUID for MS SQL Server.
	 * 
	 * This implementation is derived from the ULID specification.
	 * 
	 * @return a COMB GUID
	 */
	public static UUID getCombGuid() {
		try {
			return CombGuidCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overflow exception and trust that there's a lot of
			// room to increment after reset o zero
			return CombGuidCreatorLazyHolder.INSTANCE.create();
		}
	}

	/**
	 * Returns a Lexical Order GUID based on the ULID specification.
	 * 
	 * @return a Lexical Order GUID
	 */
	public static UUID getLexicalOrderGuid() {
		try {
			return LexicalOrderCreatorLazyHolder.INSTANCE.create();
		} catch (UuidCreatorException e) {
			// Ignore the overflow exception and trust that there's a lot of
			// room to increment after reset o zero
			return LexicalOrderCreatorLazyHolder.INSTANCE.create();
		}
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
	 * Returns a {@link NameBasedSha256UuidCreator} that creates UUID version 4.
	 * 
	 * @return {@link NameBasedSha256UuidCreator}
	 */
	public static NameBasedSha256UuidCreator getNameBasedSha256Creator() {
		return new NameBasedSha256UuidCreator();
	}

	/**
	 * Returns a {@link MssqlGuidCreator}.
	 * 
	 * @return {@link MssqlGuidCreator}
	 */
	public static MssqlGuidCreator getMssqlGuidCreator() {
		return new MssqlGuidCreator();
	}

	/**
	 * Returns a {@link CombGuidCreator}.
	 * 
	 * @return {@link CombGuidCreator}
	 */
	public static CombGuidCreator getCombGuidCreator() {
		return new CombGuidCreator();
	}

	/**
	 * Returns a {@link LexicalOrderGuidCreator}.
	 * 
	 * @return {@link LexicalOrderGuidCreator}
	 */
	public static LexicalOrderGuidCreator getLexicalOrderCreator() {
		return new LexicalOrderGuidCreator();
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
		static final SequentialUuidCreator INSTANCE = getSequentialCreator();
	}

	private static class SequentialWithMacCreatorLazyHolder {
		static final SequentialUuidCreator INSTANCE = getSequentialCreator().withHardwareAddressNodeIdentifier();
	}

	private static class SequentialWithFingerprintCreatorLazyHolder {
		static final SequentialUuidCreator INSTANCE = getSequentialCreator().withFingerprintNodeIdentifier();
	}

	private static class TimeBasedCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator();
	}

	private static class TimeBasedWithMacCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withHardwareAddressNodeIdentifier();
	}

	private static class TimeBasedWithFingerprintCreatorLazyHolder {
		static final TimeBasedUuidCreator INSTANCE = getTimeBasedCreator().withFingerprintNodeIdentifier();
	}

	private static class NameBasedMd5CreatorLazyHolder {
		static final NameBasedMd5UuidCreator INSTANCE = getNameBasedMd5Creator();
	}

	private static class NameBasedSha1CreatorLazyHolder {
		static final NameBasedSha1UuidCreator INSTANCE = getNameBasedSha1Creator();
	}

	private static class NameBasedSha256CreatorLazyHolder {
		static final NameBasedSha256UuidCreator INSTANCE = getNameBasedSha256Creator();
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

	private static class MssqlGuidCreatorLazyHolder {
		static final MssqlGuidCreator INSTANCE = getMssqlGuidCreator();
	}

	private static class CombGuidCreatorLazyHolder {
		static final CombGuidCreator INSTANCE = getCombGuidCreator();
	}

	private static class LexicalOrderCreatorLazyHolder {
		static final LexicalOrderGuidCreator INSTANCE = getLexicalOrderCreator();
	}
}
