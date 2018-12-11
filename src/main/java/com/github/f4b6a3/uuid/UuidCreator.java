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
 *
 */

package com.github.f4b6a3.uuid;

import java.security.SecureRandom;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.MssqlUuidCreator;
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

	/*
	 * Public static methods for creating UUIDs
	 */

	/**
	 * Returns a random UUID.
	 * 
	 * The random generator used is {@link java.security.SecureRandom} with
	 * SHA1PRNG algorithm.
	 * 
	 * It uses the Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Random generator: {@link SecureRandom}
	 *
	 * @return
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
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Random generator: {@link Xorshift128PlusRandom}
	 * 
	 * @return
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
	 * Details: <br/>
	 * - Version number: 0 (zero) <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: NO <br/>
	 *
	 * @return
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
	 * Details: <br/>
	 * - Version number: 0 (zero) <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: NO <br/>
	 *
	 * @return
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
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @return
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
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @return
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
	 * Domain identifiers listed in the RFC-4122: <br/>
	 * - Local Domain Person (POSIX UserID) = 0;<br/>
	 * - Local Domain Group (POSIX GroupID) = 1;<br/>
	 * - Local Domain Org = 2.<br/>
	 *
	 * Details: <br/>
	 * - Version number: 2 <br/>
	 * - Variant number: 1 <br/>
	 * - Local domain: informed by user <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @param localDomain
	 * @param localIdentifier
	 * @return
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
	 * Domain identifiers listed in the RFC-4122: <br/>
	 * - Local Domain Person (POSIX UserID) = 0;<br/>
	 * - Local Domain Group (POSIX GroupID) = 1;<br/>
	 * - Local Domain Org = 2.<br/>
	 *
	 * Details: <br/>
	 * - Version number: 2 <br/>
	 * - Variant number: 1 <br/>
	 * - Local domain: informed by user <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @return
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
	 * Details: <br/>
	 * - Version number: 3 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: MD5 <br/>
	 * - Name Space: none <br/>
	 *
	 * @param name
	 * @return
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
	 * Details: <br/>
	 * - Version number: 3 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: MD5 <br/>
	 * - Name Space: informed by user <br/>
	 *
	 * @param namespace
	 * @param name
	 * @return
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
	 * Details: <br/>
	 * - Version number: 5 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: SHA1 <br/>
	 * - Name Space: none <br/>
	 *
	 * @param name
	 * @return
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
	 * Details: <br/>
	 * - Version number: 5 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: SHA1 <br/>
	 * - Name Space: informed by user <br/>
	 *
	 * @param namespace
	 * @param name
	 * @return
	 */
	public static UUID getNameBasedSha1(UUID namespace, String name) {
		if (nameBasedSha1Creator == null) {
			nameBasedSha1Creator = getNameBasedSha1Creator();
		}
		return nameBasedSha1Creator.create(namespace, name);
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
	 * Returns a {@link NameBasedSha1UuidCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSha1UuidCreator}
	 */
	public static MssqlUuidCreator getMssqlCreator() {
		return new MssqlUuidCreator();
	}
}
