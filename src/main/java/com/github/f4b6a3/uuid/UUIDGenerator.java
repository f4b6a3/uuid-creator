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

import java.util.UUID;

import com.github.f4b6a3.uuid.factory.NameBasedUUIDCreator;
import com.github.f4b6a3.uuid.factory.RandomUUIDCreator;
import com.github.f4b6a3.uuid.factory.SequentialUUIDCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.factory.UUIDCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

/**
 * Facade to the UUID factories.
 * 
 * @author fabiolimace
 *
 */
public class UUIDGenerator {
	
	private static TimeBasedUUIDCreator sequentialUUIDCreator;
	private static TimeBasedUUIDCreator sequentialWithHardwarAddressUUIDCreator;
	private static TimeBasedUUIDCreator timeBasedUUIDCreator;
	private static TimeBasedUUIDCreator timeBasedWithHardwarAddressUUIDCreator;
	private static NameBasedUUIDCreator md5NameBasedUUIDCreator;
	private static NameBasedUUIDCreator sha1NameBasedUUIDCreator;
	private static RandomUUIDCreator randomUUIDCreator;
	private static RandomUUIDCreator randomFastUUIDCreator;

	/* 
	 * ----------------------------------------------------------------------
	 * Public static methods for creating UUIDs
	 * ----------------------------------------------------------------------
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
	 *
	 * @return
	 */
	public static UUID getRandomFastUUID() {
		if (randomFastUUIDCreator == null) {
			randomFastUUIDCreator = getRandomUUIDCreator().withRandomGenerator(new Xorshift128PlusRandom());
		}
		return randomFastUUIDCreator.create();
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
	 * - Timestamp bytes are in the RFC-4122 order: NO <br/>
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
	 * - Timestamp bytes are in the RFC-4122 order: NO <br/>
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
	 * - Timestamp bytes are in the RFC-4122 order: YES <br/>
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
	 * - Timestamp bytes are in the RFC-4122 order: YES <br/>
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
	 * @return
	 */
	public static UUID getNameBasedMD5UUID(String name) {
		if (md5NameBasedUUIDCreator == null) {
			md5NameBasedUUIDCreator = getMD5NameBasedUUIDCreator();
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
			md5NameBasedUUIDCreator = getMD5NameBasedUUIDCreator();
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
			sha1NameBasedUUIDCreator = getSHA1NameBasedUUIDCreator();
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
			sha1NameBasedUUIDCreator = getSHA1NameBasedUUIDCreator();
		}
		return sha1NameBasedUUIDCreator.create(namespace, name);
	}
	
	/* 
	 * ----------------------------------------------------------------------
	 * Public static methods for creating FACTORIES of UUIDs
	 * ----------------------------------------------------------------------
	 */
	
	/**
	 * Returns a {@link RandomUUIDCreator} that creates UUID version 4.
	 * 
	 * @return {@link RandomUUIDCreator}
	 */
	public static RandomUUIDCreator getRandomUUIDCreator() {
		return new RandomUUIDCreator();
	}
	
	/**
	 * Returns a {@link TimeBasedUUIDCreator} that creates UUID version 0.
	 * 
	 * @return {@link TimeBasedUUIDCreator}
	 */
	public static TimeBasedUUIDCreator getSequentialUUIDCreator() {
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
	 * Returns a {@link NameBasedUUIDCreator} that creates UUID version 3.
	 * 
	 * @return {@link NameBasedUUIDCreator}
	 */
	public static NameBasedUUIDCreator getMD5NameBasedUUIDCreator() {
		return new NameBasedUUIDCreator(UUIDCreator.VERSION_3);
	}
	
	/**
	 * Returns a {@link NameBasedUUIDCreator} that creates UUID version 5.
	 * 
	 * @return {@link NameBasedUUIDCreator}
	 */
	public static NameBasedUUIDCreator getSHA1NameBasedUUIDCreator() {
		return new NameBasedUUIDCreator(UUIDCreator.VERSION_5);
	}
}
