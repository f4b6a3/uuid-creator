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

import com.github.f4b6a3.uuid.factory.CombGuidCreator;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.MssqlGuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.RandomUuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.factory.SequentialUuidCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUuidCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

/**
 * Facade to the UUID factories.
 * 
 * @author fabiolimace
 *
 */
public class UuidCreator {

	private static SequentialUuidCreator sequentialCreator;
	private static SequentialUuidCreator sequentialWithMacCreator;
	private static TimeBasedUuidCreator timeBasedCreator;
	private static TimeBasedUuidCreator timeBasedWithMacCreator;
	private static AbstractNameBasedUuidCreator nameBasedMd5Creator;
	private static AbstractNameBasedUuidCreator nameBasedSha1Creator;
	private static RandomUuidCreator randomCreator;
	private static RandomUuidCreator fastRandomCreator;
	private static DceSecurityUuidCreator dceSecurityCreator;
	private static DceSecurityUuidCreator dceSecurityWithMacCreator;
	private static MssqlGuidCreator mssqlGuidCreator;
	private static CombGuidCreator combGuidCreator;

	/*
	 * Public static methods for creating UUIDs
	 */

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
		if (randomCreator == null) {
			randomCreator = getRandomCreator();
		}
		return randomCreator.create();
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
		if (fastRandomCreator == null) {
			fastRandomCreator = getRandomCreator().withFastRandomGenerator();
		}
		return fastRandomCreator.create();
	}

	/**
	 * Returns a UUID with timestamp and without machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 0 (zero) 
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: NO 
	 * - Timestamp bytes are in the RFC-4122 order?: NO 
	 * </pre>
	 * 
	 * @return a sequential UUID
	 */
	public static UUID getSequential() {
		if (sequentialCreator == null) {
			sequentialCreator = getSequentialCreator();
		}
		return sequentialCreator.create();
	}

	/**
	 * Returns a UUID with timestamp and machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * <pre>
	 * Details: 
	 * - Version number: 0 (zero) 
	 * - Variant number: 1 
	 * - Has timestamp?: YES 
	 * - Has hardware address (MAC)?: YES 
	 * - Timestamp bytes are in the RFC-4122 order?: NO 
	 * </pre>
	 * 
	 * @return a sequential UUID with MAC
	 */
	public static UUID getSequentialWithMac() {
		if (sequentialWithMacCreator == null) {
			sequentialWithMacCreator = getSequentialCreator().withHardwareAddress();
		}
		return sequentialWithMacCreator.create();
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
		if (timeBasedCreator == null) {
			timeBasedCreator = getTimeBasedCreator();
		}
		return timeBasedCreator.create();
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
	 * @return a time-based UUID
	 */
	public static UUID getTimeBasedWithMac() {
		if (timeBasedWithMacCreator == null) {
			timeBasedWithMacCreator = getTimeBasedCreator().withHardwareAddress();
		}
		return timeBasedWithMacCreator.create();
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
	 * @param localDomain a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public static UUID getDceSecurity(byte localDomain, int localIdentifier) {
		if (dceSecurityCreator == null) {
			dceSecurityCreator = getDceSecurityCreator();
		}
		return dceSecurityCreator.create(localDomain, localIdentifier);
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
	 * @param localDomain a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public static UUID getDceSecurityWithMac(byte localDomain, int localIdentifier) {
		if (dceSecurityWithMacCreator == null) {
			dceSecurityWithMacCreator = getDceSecurityCreator().withHardwareAddress();
		}
		return dceSecurityCreator.create(localDomain, localIdentifier);
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
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedMd5(String name) {
		if (nameBasedMd5Creator == null) {
			nameBasedMd5Creator = getNameBasedMd5Creator();
		}
		return nameBasedMd5Creator.create(name);
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
	 * @param namespace a name space UUID
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedMd5(UUID namespace, String name) {
		if (nameBasedMd5Creator == null) {
			nameBasedMd5Creator = getNameBasedMd5Creator();
		}
		return nameBasedMd5Creator.create(namespace, name);
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
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha1(String name) {
		if (nameBasedSha1Creator == null) {
			nameBasedSha1Creator = getNameBasedSha1Creator();
		}
		return nameBasedSha1Creator.create(name);
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
	 * @param namespace a name space UUID
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		if (nameBasedSha1Creator == null) {
			nameBasedSha1Creator = getNameBasedSha1Creator();
		}
		return nameBasedSha1Creator.create(namespace, name);
	}
	
	/**
	 * Returns a time-based GUID for MS SQL Server.
	 * 
	 * @return a MSSQL GUID
	 */
	public static UUID getMssqlGuid() {
		if (mssqlGuidCreator == null) {
			mssqlGuidCreator = getMssqlCreator();
		}
		return mssqlGuidCreator.create();
	}
	
	/**
	 * Returns a COMB GUID for MS SQL Server.
	 * 
	 * @return a COMB GUID
	 */
	public static UUID getCombGuid() {
		if (combGuidCreator == null) {
			combGuidCreator = getCombCreator();
		}
		return combGuidCreator.create();
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
	 * Returns a {@link NameBasedSha1UuidCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSha1UuidCreator}
	 */
	public static NameBasedSha1UuidCreator getNameBasedSha1Creator() {
		return new NameBasedSha1UuidCreator();
	}
	
	/**
	 * Returns a {@link MssqlGuidCreator}.
	 * 
	 * @return {@link MssqlGuidCreator}
	 */
	public static MssqlGuidCreator getMssqlCreator() {
		return new MssqlGuidCreator();
	}
	
	/**
	 * Returns a {@link CombGuidCreator}.
	 * 
	 * @return {@link CombGuidCreator}
	 */
	public static CombGuidCreator getCombCreator() {
		return new CombGuidCreator();
	}
}
