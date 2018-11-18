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
import com.github.f4b6a3.uuid.factory.SequentialUUIDCreator;
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

	private static SequentialUUIDCreator sequentialUUIDCreator;
	private static SequentialUUIDCreator sequentialWithHardwarAddressUUIDCreator;
	private static TimeBasedUUIDCreator timeBasedUUIDCreator;
	private static TimeBasedUUIDCreator timeBasedWithHardwarAddressUUIDCreator;
	private static AbstractNameBasedUUIDCreator md5NameBasedUUIDCreator;
	private static AbstractNameBasedUUIDCreator sha1NameBasedUUIDCreator;
	private static RandomUUIDCreator randomUUIDCreator;
	private static RandomUUIDCreator fastRandomUUIDCreator;
	private static DCESecurityUUIDCreator dceSecurityUUIDCreator;

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
	public static UUID getRandomUUID() {
		if (randomUUIDCreator == null) {
			randomUUIDCreator = getRandomUUIDCreator();
		}
		return randomUUIDCreator.create();
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
	public static UUID getFastRandomUUID() {
		if (fastRandomUUIDCreator == null) {
			fastRandomUUIDCreator = getFastRandomUUIDCreator();
		}
		return fastRandomUUIDCreator.create();
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
	 * @param instant
	 * @return
	 */
	public static UUID getSequentialUUID() {
		if (sequentialUUIDCreator == null) {
			sequentialUUIDCreator = getSequentialUUIDCreator();
		}
		return sequentialUUIDCreator.create();
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
	public static UUID getSequentialWithHardwareAddressUUID() {
		if (sequentialWithHardwarAddressUUIDCreator == null) {
			sequentialWithHardwarAddressUUIDCreator = getSequentialUUIDCreator();
			sequentialWithHardwarAddressUUIDCreator.withHardwareAddressNodeIdentifier();
		}
		return sequentialWithHardwarAddressUUIDCreator.create();
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
	public static UUID getTimeBasedUUID() {
		if (timeBasedUUIDCreator == null) {
			timeBasedUUIDCreator = getTimeBasedUUIDCreator();
		}
		return timeBasedUUIDCreator.create();
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
	public static UUID getTimeBasedWithHardwareAddressUUID() {
		if (timeBasedWithHardwarAddressUUIDCreator == null) {
			timeBasedWithHardwarAddressUUIDCreator = getTimeBasedUUIDCreator();
			timeBasedWithHardwarAddressUUIDCreator.withHardwareAddressNodeIdentifier();
		}
		return timeBasedWithHardwarAddressUUIDCreator.create();
	}

	/**
	 * Returns a DCE Security UUID based on a local identifier.
	 *
	 * Details: <br/>
	 * - Version number: 2 <br/>
	 * - Variant number: 1 <br/>
	 * - Local domain: POSIX UserID <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @param localDomain
	 * @param localIdentification
	 * @return
	 */
	public static UUID getDCESecurityUUID(int localIdentification) {
		if (dceSecurityUUIDCreator == null) {
			dceSecurityUUIDCreator = new DCESecurityUUIDCreator();
		}
		return dceSecurityUUIDCreator.create(localIdentification);
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
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in the RFC-4122 order?: YES <br/>
	 *
	 * @param localDomain
	 * @param localIdentification
	 * @return
	 */
	public static UUID getDCESecurityUUID(byte localDomain, int localIdentification) {
		if (dceSecurityUUIDCreator == null) {
			dceSecurityUUIDCreator = new DCESecurityUUIDCreator();
		}
		return dceSecurityUUIDCreator.create(localDomain, localIdentification);
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
	public static UUID getNameBasedMD5UUID(String name) {
		if (md5NameBasedUUIDCreator == null) {
			md5NameBasedUUIDCreator = getNameBasedMD5UUIDCreator();
		}
		return md5NameBasedUUIDCreator.create(name);
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
	public static UUID getNameBasedMD5UUID(UUID namespace, String name) {
		if (md5NameBasedUUIDCreator == null) {
			md5NameBasedUUIDCreator = getNameBasedMD5UUIDCreator();
		}
		return md5NameBasedUUIDCreator.create(namespace, name);
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
	public static UUID getNameBasedSHA1UUID(String name) {
		if (sha1NameBasedUUIDCreator == null) {
			sha1NameBasedUUIDCreator = getNameBasedSHA1UUIDCreator();
		}
		return sha1NameBasedUUIDCreator.create(name);
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
	public static UUID getNameBasedSHA1UUID(UUID namespace, String name) {
		if (sha1NameBasedUUIDCreator == null) {
			sha1NameBasedUUIDCreator = getNameBasedSHA1UUIDCreator();
		}
		return sha1NameBasedUUIDCreator.create(namespace, name);
	}

	/*
	 * Public static methods for creating FACTORIES of UUIDs
	 */

	/**
	 * Returns a {@link SequentialUUIDCreator} that creates UUID version 0.
	 * 
	 * @return {@link SequentialUUIDCreator}
	 */
	public static SequentialUUIDCreator getSequentialUUIDCreator() {
		return new SequentialUUIDCreator();
	}

	/**
	 * Returns a {@link TimeBasedUUIDCreator} that creates UUID version 1.
	 * 
	 * @return {@link TimeBasedUUIDCreator}
	 */
	public static TimeBasedUUIDCreator getTimeBasedUUIDCreator() {
		return new TimeBasedUUIDCreator();
	}

	/**
	 * Returns a {@link DCESecurityUUIDCreator} that creates UUID version 2.
	 * 
	 * @return {@link DCESecurityUUIDCreator}
	 */
	public static DCESecurityUUIDCreator getDCESecurityUUIDCreator() {
		return new DCESecurityUUIDCreator();
	}

	/**
	 * Returns a {@link NameBasedMD5UUIDCreator} that creates UUID version 3.
	 * 
	 * @return {@link NameBasedMD5UUIDCreator}
	 */
	public static NameBasedMD5UUIDCreator getNameBasedMD5UUIDCreator() {
		return new NameBasedMD5UUIDCreator();
	}

	/**
	 * Returns a {@link RandomUUIDCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUUIDCreator}
	 */
	public static RandomUUIDCreator getRandomUUIDCreator() {
		return new RandomUUIDCreator();
	}

	/**
	 * Returns a {@link RandomUUIDCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUUIDCreator}
	 */
	public static RandomUUIDCreator getFastRandomUUIDCreator() {
		return new RandomUUIDCreator().withFastRandomGenerator();
	}

	/**
	 * Returns a {@link NameBasedSHA1UUIDCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedSHA1UUIDCreator}
	 */
	public static NameBasedSHA1UUIDCreator getNameBasedSHA1UUIDCreator() {
		return new NameBasedSHA1UUIDCreator();
	}
}
