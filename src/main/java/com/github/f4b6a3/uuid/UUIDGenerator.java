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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class UUIDGenerator {

	private static final UUIDClock clock = new UUIDClock();

	private static final Instant GREGORIAN_EPOCH = UUIDGenerator.getGregorianCalendarBeginning();

	private static final char[] HEXADECIMAL_CHARS = "0123456789abcdef".toCharArray();

	private static SecureRandom random = UUIDGenerator.getSecureRandom();
	private static byte[] lastHardwareAddress = null;
	private static long lastTimestamp = 0;
	private static long lastClockSequence = 0;
	
	// Constants used to avoid long data type overflow
	private static final long SECONDS_MULTIPLYER = (long) Math.pow(10, 7);
	private static final long NANOSECONDS_DIVISOR = (long) Math.pow(10, 2);

	// UUID in this format: 00000000-0000-0000-0000-000000000000
	private static final byte[] UUID_NIL = UUIDGenerator.array(16, (byte) 0x00);
	
	// UUID for name spaces defined in RFC-4122
	public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_ISO_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
	public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");
	
	/* ### PUBLIC UUID GENERATORS */

	/**
	 * Returns a random UUID with no timestamp and no machine address.
	 *
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: NO <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in standard order: NO <br/>
	 *
	 * @return
	 */
	public static UUID getRandomUUID() {
		return UUIDGenerator.toUUID(UUIDGenerator.getRandomUUIDBytes());
	}

	/**
	 * Returns a UUID with timestamp and machine address.
	 *
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in standard order: YES <br/>
	 *
	 * @return
	 */
	public static UUID getTimeBasedUUID() {
		return UUIDGenerator.toUUID(UUIDGenerator.getTimeBasedUUIDBytes(UUIDGenerator.getClockInstant(), true, true));
	}

	/**
	 * Returns a UUID with timestamp and without machine address.
	 *
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in standard order: YES <br/>
	 *
	 * @return
	 */
	public static UUID getTimeBasedPrivateUUID() {
		return UUIDGenerator.toUUID(UUIDGenerator.getTimeBasedUUIDBytes(UUIDGenerator.getClockInstant(), true, false));
	}

	/**
	 * Returns a UUID with timestamp and machine adress, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order, that is
	 * not compatible with the version 1. For that reason it's returned as a
	 * version 4 UUID.
	 *
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Timestamp bytes are in standard order: NO <br/>
	 *
	 * @return
	 */
	public static UUID getSequentialUUID() {
		return UUIDGenerator.toUUID(UUIDGenerator.getTimeBasedUUIDBytes(UUIDGenerator.getClockInstant(), false, true));
	}

	/**
	 * Returns a UUID with timestamp and without machine address, but the bytes
	 * corresponding to timestamp are arranged in the "natural" order, that is
	 * not compatible with the version 1. For that reason it's returned as a
	 * version 4 UUID.
	 *
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Timestamp bytes are in standard order: NO <br/>
	 *
	 * @param instant
	 * @return
	 */
	public static UUID getSequentialPrivateUUID() {
		return UUIDGenerator.toUUID(UUIDGenerator.getTimeBasedUUIDBytes(UUIDGenerator.getClockInstant(), false, false));
	}

	/**
	 * Returns a UUID with based on a name, using MD5, but without a name space.
	 *
	 * It uses the NIL UUID as name space internally.
	 * 
	 * Details: <br/>
	 * - Version number: 3 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: MD5 <br/>
	 * - Name Space: NIL UUID (default) <br/>
	 *
	 * @param name
	 * @return
	 * @return
	 */
	public static UUID getNameBasedUUID(String name) {
		return UUIDGenerator.getNameBasedUUID(null, name, false);
	}
	
	/**
	 * Returns a UUID with based on a name space and a name, using MD5.
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
	public static UUID getNameBasedUUID(UUID namespace, String name) {
		return UUIDGenerator.getNameBasedUUID(namespace, name, false);
	}

	/**
	 * Returns a UUID with based on a name, using SHA1, but without a name space.
	 *
	 * It uses the NIL UUID as name space internally.
	 *
	 * Details: <br/>
	 * - Version number: 5 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: SHA1 <br/>
	 * - Name Space: NIL UUID (default) <br/>
	 *
	 * @param name
	 * @return
	 */
	public static UUID getNameBasedSHA1UUID(String name) {
		return UUIDGenerator.getNameBasedUUID(null, name, true);
	}
	
	/**
	 * Returns a UUID with based on a name space and a name, using SHA1.
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
		return UUIDGenerator.getNameBasedUUID(null, name, true);
	}
	
	protected static String getTimeBasedUUIDString(Instant instant) {
		return UUIDGenerator.getTimeBasedUUIDString(instant, true, true);
	}

	protected static String getTimeBasedPrivateUUIDString(Instant instant) {
		return UUIDGenerator.getTimeBasedUUIDString(instant, true, false);
	}

	protected static String getSequentialUUIDString(Instant instant) {
		return UUIDGenerator.getTimeBasedUUIDString(instant, false, true);
	}

	protected static String getSequentialPrivateUUIDString(Instant instant) {
		return UUIDGenerator.getTimeBasedUUIDString(instant, false, false);
	}

	protected static String getRandomUUIDString() {
		return UUIDGenerator.formatString(UUIDGenerator.toHexadecimal(UUIDGenerator.getRandomUUIDBytes()));
	}
	
	protected static String getNameBasedUUIDString(String name) {
		return UUIDGenerator.getNameBasedUUID(null, name, false).toString();
	}
	
	protected static String getNameBasedSHA1UUIDString(String name) {
		return UUIDGenerator.getNameBasedUUID(null, name, true).toString();
	}

	protected static String getNameBasedUUIDString(UUID namespace, String name) {
		return UUIDGenerator.getNameBasedUUID(namespace, name, false).toString();
	}
	
	protected static String getNameBasedSHA1UUIDString(UUID namespace, String name) {
		return UUIDGenerator.getNameBasedUUID(namespace, name, true).toString();
	}
	
	protected static byte[] getRandomUUIDBytes() {

		byte[] uuid = UUIDGenerator.getRandomBytes(16);
		uuid = setUUIDVersion(uuid, 4);
		return uuid;

	}

	/**
	 * Returns a time-based UUID with to options: to include or not hardware
	 * address and to use or not the standard bytes order for timestamps.
	 *
	 * Details: <br/>
	 * - Version number: 1 or 4<br/>
	 * - Variant number: 1 or 4 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES or NO <br/>
	 * - Timestamp bytes are in standard order: YES or NO <br/>
	 *
	 * @param instant
	 * @param standardTimestamp
	 * @param realHardwareAddress
	 * @return
	 */
	protected static byte[] getTimeBasedUUIDBytes(Instant instant, boolean standardTimestamp, boolean realHardwareAddress) {
		
		long timestamp = 0;
		long clockSequence = 0;
		
		byte[] uuid;
		byte[] timestampBytes;
		byte[] clockSequenceBytes;
		byte[] hardwareAddressBytes;

		if(UUIDGenerator.lastTimestamp == 0) {
			UUIDGenerator.lastTimestamp = UUIDGenerator.getGregorianCalendarTimestamp(instant);
		}

		timestamp = UUIDGenerator.getGregorianCalendarTimestamp(instant);
		timestampBytes = UUIDGenerator.getUUIDTimestampBytes(timestamp, standardTimestamp);
		
		boolean hasTimestampChanged = (timestamp > UUIDGenerator.lastTimestamp);
		boolean incrementClockSequence = !hasTimestampChanged; 
		UUIDGenerator.lastTimestamp = timestamp;
		
		clockSequence = getClockSequence(incrementClockSequence);
		clockSequenceBytes = UUIDGenerator.copy(UUIDGenerator.copy(UUIDGenerator.toBytes(clockSequence)), 6, 8);
		
		if (hasTimestampChanged) {
			hardwareAddressBytes = getHardwareAddress(realHardwareAddress);
		} else {
			hardwareAddressBytes = getRandomHardwareAddress();
		}
		
		uuid = UUIDGenerator.copy(UUIDGenerator.UUID_NIL);
		uuid = UUIDGenerator.replaceField(uuid, UUIDGenerator.copy(timestampBytes, 0, 4), 1);
		uuid = UUIDGenerator.replaceField(uuid, UUIDGenerator.copy(timestampBytes, 4, 6), 2);
		uuid = UUIDGenerator.replaceField(uuid, UUIDGenerator.copy(timestampBytes, 6, 8), 3);
		uuid = UUIDGenerator.replaceField(uuid, clockSequenceBytes, 4);
		
		uuid = UUIDGenerator.replaceField(uuid, hardwareAddressBytes, 5);
		
		return uuid;
	}
	
	protected static String getTimeBasedUUIDString(Instant instant, boolean standardTimestamp, boolean realHardwareAddress) {
		return UUIDGenerator.formatString(UUIDGenerator
				.toHexadecimal(UUIDGenerator.getTimeBasedUUIDBytes(instant, standardTimestamp, realHardwareAddress)));
	}

	/**
	 * Get a name-based UUID using name space, a name and a specific hash algorithm.
	 * It uses the NIL UUID as name space internally.
	 * 
	 * Details: <br/>
	 * - Version number: 3 or 5 <br/>
	 * - Variant number: 1 <br/>
	 * - Hash Algorithm: MD5 or SHA1 <br/>
	 * - Name Space: NIL UUID (default) or other informed by user <br/>
	 * 
	 * @param namespace
	 * @param name
	 * @param useSHA1
	 * @return
	 */
	protected static UUID getNameBasedUUID(final UUID namespace, final String name, boolean useSHA1) {
		
		byte[] namespaceBytes = null;
		byte[] nameBytes = null;
		byte[] bytes = null;
		
		if(namespace != null) {
			namespaceBytes = UUIDGenerator.toBytes(namespace.toString().replaceAll("[^0-9a-fA-F]", ""));
		} else {
			namespaceBytes = UUID_NIL;
		}
		
		nameBytes = name.getBytes();
		bytes = UUIDGenerator.concat(namespaceBytes, nameBytes);
		
		return UUIDGenerator.getNameBasedUUID(bytes, useSHA1);
	}
	
	private static UUID getNameBasedUUID(byte[] bytes, boolean useSHA1) {
		byte[] uuid = null;
        MessageDigest md;
        try {
        	if(useSHA1) {
        		md = MessageDigest.getInstance("SHA1");
        	} else {
        		md = MessageDigest.getInstance("MD5");
        	}
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("Message digest algorithm not supported.", e);
        }
        
        byte[] hash = md.digest(bytes);
        
		if(useSHA1) {
			uuid = setUUIDVersion(hash, 5);
		} else {
			uuid = setUUIDVersion(hash, 3);
		}
		return toUUID(uuid);
	}
	
	/**
	 * Get the clock instance used to get timestamps.
	 *
	 * @return
	 */
	protected static Instant getClockInstant() {
		return Instant.now(UUIDGenerator.clock);
	}

	/**
	 * Get the beggining of the Gregorian Calendar: 1582-10-15 00:00:00Z.
	 *
	 * @return
	 */
	protected static Instant getGregorianCalendarBeginning() {
		LocalDate localDate = LocalDate.parse("1582-10-15");
		return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
	}

	/**
	 * Get the timestamp associated with the given instant.
	 *
	 * @param instant
	 * @return
	 */
	protected static long getGregorianCalendarTimestamp(Instant instant) {
		long seconds = UUIDGenerator.GREGORIAN_EPOCH.until(instant, ChronoUnit.SECONDS);
		long nanoseconds = instant.getLong(ChronoField.NANO_OF_SECOND);
		long hundredNanoseconds = ((seconds * UUIDGenerator.SECONDS_MULTIPLYER)
				+ ((nanoseconds) / UUIDGenerator.NANOSECONDS_DIVISOR));
		return hundredNanoseconds; // Discard the last 4 bits;
	}

	/**
	 * Get the Instant associated with the given timestamp.
	 *
	 * @param timestamp
	 * @return
	 */
	protected static Instant getGregorianCalendarInstant(long timestamp) {
		long nanoseconds = timestamp % UUIDGenerator.SECONDS_MULTIPLYER;
		long seconds = timestamp - nanoseconds;
		Instant instant = UUIDGenerator.GREGORIAN_EPOCH.plus(seconds / UUIDGenerator.SECONDS_MULTIPLYER,
				ChronoUnit.SECONDS);
		return instant.plus(nanoseconds * UUIDGenerator.NANOSECONDS_DIVISOR, ChronoUnit.NANOS);
	}

	/**
	 * Get a byte array that contains the timestamp that will be embedded in the
	 * UUID.
	 *
	 * @param timestamp
	 * @param standardTimestamp
	 * @return
	 */
	protected static byte[] getUUIDTimestampBytes(final long timestamp, boolean standardTimestamp) {

		long part1;
		long part2;
		long part3;
		long timestampBytes;

		if (standardTimestamp) {
			part1 = (timestamp & 0x00000000FFFFFFFFL) << 32;
			part2 = (timestamp & 0x0000FFFF00000000L) >>> 16;
			part3 = ((timestamp & 0x0FFF000000000000L) | 0x1000000000000000L) >>> 48;
		} else {
			part1 = (timestamp & 0x0FFFFFFFF0000000L) << 4;
			part2 = (timestamp & 0x000000000FFFF000L) << 4;
			part3 = ((timestamp & 0x0000000000000FFFL) | 0x0000000000004000L);
		}

		timestampBytes = part1 | part2 | part3;

		return UUIDGenerator.toBytes(timestampBytes);
	}

	/**
	 * Get the instant that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return
	 */
	public static Instant extractInstant(UUID uuid) {

		long part1;
		long part2;
		long part3;
		long timestamp;

		byte[] timestampBytes = UUIDGenerator.toBytes(uuid.toString().replaceAll("-", ""));
		long embededTimestamp = UUIDGenerator.toNumber(UUIDGenerator.copy(timestampBytes, 0, 8));

		long version = (embededTimestamp & 0x000000000000F000) >>> 12;

		if (version == 1) {
			part1 = (embededTimestamp & 0xFFFFFFFF00000000L) >>> 32;
			part2 = (embededTimestamp & 0x00000000FFFF0000L) << 16;
			part3 = (embededTimestamp & 0x0000000000000FFFL) << 48;
		} else if (version == 4) {
			part1 = (embededTimestamp & 0xFFFFFFFF00000000L) >>> 4;
			part2 = (embededTimestamp & 0x00000000FFFF0000L) >>> 4;
			part3 = (embededTimestamp & 0x0000000000000FFFL);
		} else {
			return null;
		}

		timestamp = part1 | part2 | part3;

		return UUIDGenerator.getGregorianCalendarInstant(timestamp);
	}
	
	/**
	 * Get the clock sequence.
	 * 
	 * @return
	 */
	protected static long getClockSequence(boolean increment) {
		
		long clockSequence = 0;
		
		if(!increment && UUIDGenerator.lastClockSequence != 0) {
			return UUIDGenerator.lastClockSequence;
		}
		
		if(increment) {
			clockSequence = UUIDGenerator.lastClockSequence + 1;
		} else {
			clockSequence = UUIDGenerator.getRandomNumber();
		}
		
		clockSequence &= 0x0000000000003FFFL;
		clockSequence |= 0x0000000000008000L;
		UUIDGenerator.lastClockSequence = clockSequence;
		return UUIDGenerator.lastClockSequence;
	}
	
	/**
	 * Get hardware address from host machine.
	 *
	 * If 'realHardwareAddress' is true, it tries to get the first MAC,
	 * otherwise, a multicast machine address is returned.
	 *
	 * @param realHardwareAddress
	 * @return
	 */
	protected static byte[] getHardwareAddress(boolean realHardwareAddress) {

		if (UUIDGenerator.lastHardwareAddress != null) {
			
			boolean isMulticast = UUIDGenerator.isMulticastHardwareAddress(UUIDGenerator.lastHardwareAddress);
			
			if(realHardwareAddress && !isMulticast) {
				return UUIDGenerator.lastHardwareAddress;
			} else if(!realHardwareAddress && isMulticast) {
				return UUIDGenerator.lastHardwareAddress;
			}
		}
		
		if (realHardwareAddress) {
			UUIDGenerator.lastHardwareAddress = getRealHardwareAddress();
			return UUIDGenerator.lastHardwareAddress;
		}

		return getRandomHardwareAddress(); 
	}
	
	/**
	 * Get real hardware address.
	 * 
	 * If no real hardware address is found, a multicast machine address is
	 * returned.
	 * 
	 * @return
	 */
	protected static byte[] getRealHardwareAddress() {
		
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				UUIDGenerator.lastHardwareAddress = realHardwareAddress; 
				return UUIDGenerator.lastHardwareAddress;
			}
		} catch (SocketException | NullPointerException e) {
			// If exception occurs, return a random hardware address.
		}
		
		if (UUIDGenerator.lastHardwareAddress != null) {
			return UUIDGenerator.lastHardwareAddress;
		}
		
		return getRandomHardwareAddress();
	}
	
	/**
	 * Get a random multicast hardware address.
	 * 
	 * @return
	 */
	protected static byte[] getRandomHardwareAddress() {
		byte[] ramdomHardwareAddress = UUIDGenerator.getRandomBytes(6);
		UUIDGenerator.lastHardwareAddress = UUIDGenerator.setMulticastHardwareAddress(ramdomHardwareAddress);
		return UUIDGenerator.lastHardwareAddress;
	}
	
	/**
	 * Set a hardware address as multicast.
	 *
	 * @param hardwareAddress
	 * @return
	 */
	protected static byte[] setMulticastHardwareAddress(final byte[] hardwareAddress) {
		byte[] result = UUIDGenerator.copy(hardwareAddress);
		result[0] = (byte) (result[0] | 0x01);
		return result;
	}

	/**
	 * Returns true if the hardware address is a multicast address.
	 *
	 * @param hardwareAddress
	 * @return
	 */
	protected static boolean isMulticastHardwareAddress(final byte[] hardwareAddress) {
		return (hardwareAddress[0] & 0x01) == 1;
	}

	/**
	 * Get hardware address contained in the UUID.
	 *
	 * @param uuid
	 * @return
	 */
	public static byte[] extractHardwareAddress(UUID uuid) {

		byte[] bytes = UUIDGenerator.toBytes(uuid.toString().replaceAll("-", ""));

		byte[] hardwareAddress = UUIDGenerator.getField(bytes, 5);

		if (!UUIDGenerator.isMulticastHardwareAddress(hardwareAddress)) {
			return hardwareAddress;
		}

		return null;
	}

	/**
	 * Get a random array of bytes.
	 *
	 * @param length
	 * @return
	 */
	protected static byte[] getRandomBytes(int length) {
		byte[] bytes = new byte[length];
		UUIDGenerator.random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Get a random number.
	 *
	 * @return
	 */
	protected static long getRandomNumber() {
		return UUIDGenerator.random.nextLong();
	}

	/**
	 * Initiate a secure random instance with SHA1PRNG algorithm.
	 *
	 * If this algorithm is not present, it uses JVM's default.
	 */
	protected static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}

	/**
	 * Format a string to UUID format.
	 *
	 * @param uuid
	 * @return
	 */
	protected static String formatString(String uuid) {
		StringBuffer buffer = new StringBuffer(uuid.substring(0, 32));
		buffer.insert(8, '-');
		buffer.insert(13, '-');
		buffer.insert(18, '-');
		buffer.insert(23, '-');
		return buffer.toString();
	}

	/**
	 * Set uuid version.
	 * 
	 * @param uuid
	 * @param type
	 * @return
	 */
	protected static byte[] setUUIDVersion(final byte[] uuid, long type) {
		
		byte[] bytes = copy(uuid);
		
		if(type == 1) {
			bytes[6] = (byte) ((bytes[6] & 0x0f) | 0x10); // version 1
		} else if (type == 2) {
			bytes[6] = (byte) ((bytes[6] & 0x0f) | 0x20); // version 2
		} else if (type == 3) {
			bytes[6] = (byte) ((bytes[6] & 0x0f) | 0x30); // version 3
		} else if (type == 4) {
			bytes[6] = (byte) ((bytes[6] & 0x0f) | 0x40); // version 4
		} else if (type == 5) {
			bytes[6] = (byte) ((bytes[6] & 0x0f) | 0x50); // version 5
		} else {
			throw new RuntimeException("No such UUID type.");
		}
		
		bytes[8] = (byte) ((bytes[8] & 0x3f) | 0x80); // variant 1		
		
		return bytes;
	}
	
	/**
	 * Get a field of a given UUID.
	 *
	 * @param uuid
	 * @param index
	 * @return
	 */
	protected static byte[] getField(byte[] uuid, int index) {
		switch (index) {
		case 1:
			return UUIDGenerator.copy(uuid, 0, 4);
		case 2:
			return UUIDGenerator.copy(uuid, 4, 6);
		case 3:
			return UUIDGenerator.copy(uuid, 6, 8);
		case 4:
			return UUIDGenerator.copy(uuid, 8, 10);
		case 5:
			return UUIDGenerator.copy(uuid, 10, 16);
		default:
			return null;
		}
	}

	/**
	 * Replace a field of a given UUID.
	 *
	 * @param uuid
	 * @param replacement
	 * @param index
	 * @return
	 */
	protected static byte[] replaceField(final byte[] uuid, final byte[] replacement, int index) {
		switch (index) {
		case 1:
			return UUIDGenerator.replace(uuid, replacement, 0);
		case 2:
			return UUIDGenerator.replace(uuid, replacement, 4);
		case 3:
			return UUIDGenerator.replace(uuid, replacement, 6);
		case 4:
			return UUIDGenerator.replace(uuid, replacement, 8);
		case 5:
			return UUIDGenerator.replace(uuid, replacement, 10);
		default:
			return null;
		}
	}

	/**
	 * Returns a java.util.UUID from a given byte array.
	 *
	 * @param bytes
	 * @return
	 */
	protected static UUID toUUID(byte[] bytes) {

		long mostSigBits = UUIDGenerator.toNumber(UUIDGenerator.copy(bytes, 0, 8));
		long leastSigBits = UUIDGenerator.toNumber(UUIDGenerator.copy(bytes, 8, 16));

		return new UUID(mostSigBits, leastSigBits);
	}

	/**
	 * Get a number from a given hexadevimal string.
	 *
	 * @param hexadecimal
	 * @return
	 */
	protected static long toNumber(String hexadecimal) {
		return Long.parseLong(hexadecimal, 16);
	}

	protected static long toNumber(byte[] bytes) {
		byte[] b = bytes;

		if (bytes.length < 8) {
			b = UUIDGenerator.array(8, (byte) 0x00);
			b = UUIDGenerator.replace(b, bytes, 8 - bytes.length);
		}

		long result = 0;
		for (int i = 0; i < 8; i++) {
			result <<= 8;
			result |= (b[i] & 0xFF);
		}
		return result;
	}

	/**
	 * Get an array of bytes from a given number.
	 *
	 * @param number
	 * @return
	 */
	protected static byte[] toBytes(long number) {
		byte[] bytes = new byte[8];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (number >>> (8 * ((bytes.length - 1) - i)));
		}
		return bytes;
	}

	/**
	 * Get an array of bytes from a given hexadecimal string.
	 *
	 * @param hexadecimal
	 * @return
	 */
	protected static byte[] toBytes(String hexadecimal) {
		int len = hexadecimal.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hexadecimal.charAt(i), 16) << 4)
					+ Character.digit(hexadecimal.charAt(i + 1), 16));
		}
		return bytes;
	}

	/**
	 * Get a hexadecimal string from given array of bytes.
	 *
	 * @param bytes
	 * @return
	 */
	protected static String toHexadecimal(byte[] bytes) {
		char[] hexadecimal = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			hexadecimal[i * 2] = UUIDGenerator.HEXADECIMAL_CHARS[v >>> 4];
			hexadecimal[(i * 2) + 1] = UUIDGenerator.HEXADECIMAL_CHARS[v & 0x0F];
		}
		return new String(hexadecimal);
	}

	/**
	 * Get a new array with a specific lenth and filled with a byte value.
	 *
	 * @param length
	 * @param value
	 * @return
	 */
	protected static byte[] array(int length, byte value) {
		byte[] result = new byte[length];
		for (int i = 0; i < length; i++) {
			result[i] = value;
		}
		return result;
	}

	/**
	 * Copy an entire array.
	 *
	 * @param bytes
	 * @return
	 */
	protected static byte[] copy(final byte[] bytes) {
		byte[] result = UUIDGenerator.copy(bytes, 0, bytes.length);
		return result;
	}

	/**
	 * Copy part of an array.
	 *
	 * @param bytes
	 * @param start
	 * @param end
	 * @return
	 */
	protected static byte[] copy(byte[] bytes, int start, int end) {

		byte[] result = new byte[end - start];
		for (int i = 0; i < result.length; i++) {
			result[i] = bytes[start + i];
		}
		return result;
	}
	
	protected static byte[] concat(byte[] bytes1, byte[] bytes2) {
		
		int length = bytes1.length + bytes2.length;
		byte[] result = new byte[length];

		for (int i = 0; i < bytes1.length; i++) {
			result[i] = bytes1[i];
		}
		for (int j = 0; j < bytes2.length; j++) {
			result[bytes1.length + j] = bytes2[j];
		}
		return result;
	}
	/**
	 * Replace part of an array of bytes with another subarray of bytes and
	 * starting from a given index.
	 *
	 * @param bytes
	 * @param replacement
	 * @param index
	 * @return
	 */
	protected static byte[] replace(final byte[] bytes, final byte[] replacement, int index) {
		byte[] result = UUIDGenerator.copy(bytes);
		for (int i = 0; i < replacement.length; i++) {
			result[index + i] = replacement[i];
		}
		return result;
	}

	/**
	 * Check if two arrays of bytes are equal.
	 *
	 * @param bytes1
	 * @param bytes2
	 * @return
	 */
	protected static boolean equals(byte[] bytes1, byte[] bytes2) {
		if (bytes1.length != bytes2.length) {
			return false;
		}

		for (int i = 0; i < bytes1.length; i++) {
			if (bytes1[i] != bytes2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Clock class responsible to create instants with nanoseconds.
	 */
	public static class UUIDClock extends Clock {

		private final Clock clock;

		private Instant initialInstant;
		private long initialNanoseconds;
		private long lastNanoseconds;

		public UUIDClock() {
			this(Clock.systemUTC());
		}

		public UUIDClock(final Clock clock) {
			this.clock = clock;
			this.reset();
		}

		@Override
		public ZoneId getZone() {
			return this.clock.getZone();
		}

		@Override
		public Instant instant() {
			return this.initialInstant.plusNanos(this.getSystemNanos() - this.initialNanoseconds);
		}

		@Override
		public Clock withZone(final ZoneId zone) {
			return new UUIDClock(this.clock.withZone(zone));
		}

		protected long getSystemNanos() {
			long currentNanoseconds = System.nanoTime();

			if (currentNanoseconds > this.lastNanoseconds) {
				this.lastNanoseconds = currentNanoseconds;
			} else {
				this.reset(); // You can't go back!
			}

			return currentNanoseconds;
		}

		protected void reset() {
			this.initialInstant = this.clock.instant();
			this.initialNanoseconds = System.nanoTime();
			this.lastNanoseconds = this.initialNanoseconds;
		}
	}
}
