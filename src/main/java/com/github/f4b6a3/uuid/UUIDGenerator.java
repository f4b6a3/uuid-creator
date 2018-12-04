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

import com.github.f4b6a3.uuid.factory.DCESecurityUUIDCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMD5UUIDCreator;
import com.github.f4b6a3.uuid.factory.RandomUUIDCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSHA1UUIDCreator;
import com.github.f4b6a3.uuid.factory.OrderedUUIDCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUUIDCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

/**
 * Facade to the UUID factories.
 * 
 * @author fabiolimace
 *
 */
public class UUIDGenerator {

	private static OrderedUUIDCreator orderedCreator;
	private static OrderedUUIDCreator orderedWithNodeIdCreator;
	private static OrderedUUIDCreator orderedWithMACCreator;
	private static TimeBasedUUIDCreator timeBasedCreator;
	private static TimeBasedUUIDCreator timeBasedWithNodeIdCreator;
	private static TimeBasedUUIDCreator timeBasedWithMACCreator;
	private static AbstractNameBasedUUIDCreator nameBasedMD5Creator;
	private static AbstractNameBasedUUIDCreator nameBasedSHA1Creator;
	private static RandomUUIDCreator randomCreator;
	private static RandomUUIDCreator fastRandomCreator;
	private static DCESecurityUUIDCreator dceSecurityCreator;
	private static DCESecurityUUIDCreator dceSecurityWithNodeIdCreator;
	private static DCESecurityUUIDCreator dceSecurityWithMACCreator;

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
			fastRandomCreator = getFastRandomCreator();
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
	public static UUID getOrdered() {
		if (orderedCreator == null) {
			orderedCreator = getOrderedCreator();
		}
		return orderedCreator.create();
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
	public static UUID getOrderedWithMAC() {
		if (orderedWithMACCreator == null) {
			orderedWithMACCreator = getOrderedCreator().withHardwareAddress();
		}
		return orderedWithMACCreator.create();
	}

	/**
	 * Returns a UUID with timestamp and without machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order.
	 *
	 * Details: <br/>
	 * - Version number: 0 (zero) <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: informed by user <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: NO <br/>
	 *
	 * @param nodeIdentifier
	 * @return
	 */
	public static UUID getOrdered(long nodeIdentifier) {
		if (orderedWithNodeIdCreator == null) {
			orderedWithNodeIdCreator = getOrderedCreator().withNodeIdentifier(nodeIdentifier);
		}
		return orderedWithNodeIdCreator.create();
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
	public static UUID getTimeBasedWithMAC() {
		if (timeBasedWithMACCreator == null) {
			timeBasedWithMACCreator = getTimeBasedCreator().withHardwareAddress();
		}
		return timeBasedWithMACCreator.create();
	}

	/**
	 * Returns a UUID with timestamp and without machine address.
	 *
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: informed by user <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @param nodeIdentifier
	 * @return
	 */
	public static UUID getTimeBased(long nodeIdentifier) {
		if (timeBasedWithNodeIdCreator == null) {
			timeBasedWithNodeIdCreator = getTimeBasedCreator().withNodeIdentifier(nodeIdentifier);
		}
		return timeBasedWithNodeIdCreator.create();
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
	public static UUID getDCESecurity(byte localDomain, int localIdentifier) {
		if (dceSecurityCreator == null) {
			dceSecurityCreator = getDCESecurityCreator();
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
	public static UUID getDCESecurityWithMAC(byte localDomain, int localIdentifier) {
		if (dceSecurityWithMACCreator == null) {
			dceSecurityWithMACCreator = getDCESecurityCreator().withHardwareAddress();
		}
		return dceSecurityCreator.create(localDomain, localIdentifier);
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
	 * - Has hardware address (MAC)?: informed by user <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @param localDomain
	 * @param localIdentifier
	 * @param nodeIdentifier
	 * @return
	 */
	public static UUID getDCESecurity(byte localDomain, int localIdentifier, long nodeIdentifier) {
		if (dceSecurityWithNodeIdCreator == null) {
			dceSecurityWithNodeIdCreator = getDCESecurityCreator().withNodeIdentifier(nodeIdentifier);
		}
		return dceSecurityWithNodeIdCreator.create(localDomain, localIdentifier);
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
	public static UUID getNameBasedMD5(String name) {
		if (nameBasedMD5Creator == null) {
			nameBasedMD5Creator = getNameBasedMD5Creator();
		}
		return nameBasedMD5Creator.create(name);
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
	public static UUID getNameBasedMD5(UUID namespace, String name) {
		if (nameBasedMD5Creator == null) {
			nameBasedMD5Creator = getNameBasedMD5Creator();
		}
		return nameBasedMD5Creator.create(namespace, name);
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
	public static UUID getNameBasedSHA1(String name) {
		if (nameBasedSHA1Creator == null) {
			nameBasedSHA1Creator = getNameBasedSHA1Creator();
		}
		return nameBasedSHA1Creator.create(name);
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
	public static UUID getNameBasedSHA1(UUID namespace, String name) {
		if (nameBasedSHA1Creator == null) {
			nameBasedSHA1Creator = getNameBasedSHA1Creator();
		}
		return nameBasedSHA1Creator.create(namespace, name);
	}

	/*
	 * Public static methods for creating FACTORIES of UUIDs
	 */

	/**
	 * Returns a {@link OrderedUUIDCreator} that creates UUID version 0.
	 * 
	 * @return {@link OrderedUUIDCreator}
	 */
	public static OrderedUUIDCreator getOrderedCreator() {
		return new OrderedUUIDCreator();
	}

	/**
	 * Returns a {@link TimeBasedUUIDCreator} that creates UUID version 1.
	 * 
	 * @return {@link TimeBasedUUIDCreator}
	 */
	public static TimeBasedUUIDCreator getTimeBasedCreator() {
		return new TimeBasedUUIDCreator();
	}

	/**
	 * Returns a {@link DCESecurityUUIDCreator} that creates UUID version 2.
	 * 
	 * @return {@link DCESecurityUUIDCreator}
	 */
	public static DCESecurityUUIDCreator getDCESecurityCreator() {
		return new DCESecurityUUIDCreator();
	}

	/**
	 * Returns a {@link NameBasedMD5UUIDCreator} that creates UUID version 3.
	 * 
	 * @return {@link NameBasedMD5UUIDCreator}
	 */
	public static NameBasedMD5UUIDCreator getNameBasedMD5Creator() {
		return new NameBasedMD5UUIDCreator();
	}

	/**
	 * Returns a {@link RandomUUIDCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUUIDCreator}
	 */
	public static RandomUUIDCreator getRandomCreator() {
		return new RandomUUIDCreator();
	}

	/**
	 * Returns a {@link RandomUUIDCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUUIDCreator}
	 */
	public static RandomUUIDCreator getFastRandomCreator() {
		return new RandomUUIDCreator().withFastRandomGenerator();
	}

	/**
	 * Returns a {@link NameBasedSHA1UUIDCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSHA1UUIDCreator}
	 */
	public static NameBasedSHA1UUIDCreator getNameBasedSHA1Creator() {
		return new NameBasedSHA1UUIDCreator();
	}
}
