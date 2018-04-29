package com.github.small.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {

	protected static final UUIDClock clock = new UUIDClock();

	private static final Instant GREGORIAN_EPOCH = getGregorianCalendarBeginning();

	private static final char[] HEXADECIMAL_CHARS = "0123456789abcdef".toCharArray();

	private static Random random = new Random();
	private static byte[] hardwareAddress = null;
	private static byte[] lastClockSequence = null;

	// Used to generate a MD5 hash
	private static MessageDigest messageDigest = null;

	// Constant used to generate clock sequence (2^14 or 14 bits)
	private static final int CLOCK_SEQUENCE_DIVISOR = (int) Math.pow(2, 14);

	// Constants used to avoid long data type overflow
	private static final long SECONDS_MULTIPLYER = (long) Math.pow(10, 7);
	private static final long NANOSECONDS_DIVISOR = (long) Math.pow(10, 2);

	// UUID in this format: 00000000-0000-0000-0000-000000000000
	private static final byte[] NIL_UUID = array(16, (byte) 0x00);

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
	 * @param instant
	 * @return
	 */
	public static UUID getRandomUUID() {
		return UUID.fromString(getRandomUUIDString());
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
	public static UUID getTimestampUUID() {
		return UUID.fromString(getTimestampUUIDString(getClockInstant()));
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
	 * @param instant
	 * @return
	 */
	public static UUID getTimestampPrivateUUID() {
		return UUID.fromString(getTimestampPrivateUUIDString(getClockInstant()));
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
	 * @param instant
	 * @return
	 */
	public static UUID getSequentialUUID() {
		return UUID.fromString(getSequentialUUIDString(getClockInstant()));
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
		return UUID.fromString(getSequentialPrivateUUIDString(getClockInstant()));
	}

	/* ### PROTECTED UUID STRING GENERATORS */

	protected static String getTimestampUUIDString(Instant instant) {
		return getUUIDString(instant, true, true);
	}

	protected static String getTimestampPrivateUUIDString(Instant instant) {
		return getUUIDString(instant, false, true);
	}

	protected static String getSequentialUUIDString(Instant instant) {
		return getUUIDString(instant, true, false);
	}

	protected static String getSequentialPrivateUUIDString(Instant instant) {
		return getUUIDString(instant, false, false);
	}

	/* ### PROTECTED ACTUAL UUID STRING GENERATORS */

	protected static String getRandomUUIDString() {

		byte[] uuid = getRandomHash();

		uuid = replaceFragment(uuid, getRandomBytes(2), 3);
		uuid = replaceFragment(uuid, getRandomBytes(2), 4);

		uuid[6] = (byte) (uuid[6] & 0x0f | 0x40); // version 4
		uuid[8] = (byte) (uuid[8] & 0x3f | 0x80); // variant 1

		return formatString(toHexadecimal(uuid));

	}

	/**
	 * Returns a time based UUID with to options: to include or not hardware
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
	protected static String getUUIDString(Instant instant, boolean standardTimestamp, boolean realHardwareAddress) {

		long timestamp = getGregorianCalendarTimestamp(instant);

		byte[] bytes = toBytes(timestamp);

		byte[] uuid = copy(NIL_UUID);

		byte[] fragment1;
		byte[] fragment2;
		byte[] fragment3;
		byte[] fragment4;
		byte[] fragment5;

		if (standardTimestamp) {
			fragment1 = copy(bytes, 4, 8);
			fragment2 = copy(bytes, 2, 4);
			fragment3 = copy(bytes, 0, 2);
		} else {
			fragment1 = copy(bytes, 0, 4);
			fragment2 = copy(bytes, 4, 6);
			fragment3 = copy(bytes, 6, 8);
		}

		fragment4 = getClockSequence(instant);
		fragment5 = getHardwareAddress(realHardwareAddress);

		uuid = replaceFragment(uuid, fragment1, 1);
		uuid = replaceFragment(uuid, fragment2, 2);
		uuid = replaceFragment(uuid, fragment3, 3);
		uuid = replaceFragment(uuid, fragment4, 4);
		uuid = replaceFragment(uuid, fragment5, 5);

		if (standardTimestamp) {
			uuid[6] = (byte) (uuid[6] & 0x0f | 0x10); // version 1
		} else {
			uuid[6] = (byte) (uuid[6] & 0x0f | 0x40); // version 4
		}

		uuid[8] = (byte) (uuid[8] & 0x3f | 0x80); // variant 1

		return formatString(toHexadecimal(uuid));
	}

	/* ### PROTECTED AUXILIARY METHODS */

	/**
	 * Get the clock instance used to get timestamps.
	 * 
	 * @return
	 */
	protected static Instant getClockInstant() {
		return Instant.now(clock);
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
		long seconds = GREGORIAN_EPOCH.until(instant, ChronoUnit.SECONDS);
		long nanoseconds = instant.getLong(ChronoField.NANO_OF_SECOND);
		return ((seconds * SECONDS_MULTIPLYER) + (nanoseconds / NANOSECONDS_DIVISOR));
	}

	/**
	 * Get the Instant associated with the given timestamp.
	 * 
	 * @param timestamp
	 * @return
	 */
	protected static Instant getGregorianCalendarInstant(long timestamp) {
		long nanoseconds = timestamp % SECONDS_MULTIPLYER;
		long seconds = timestamp - nanoseconds;
		Instant instant = GREGORIAN_EPOCH.plus(seconds / SECONDS_MULTIPLYER, ChronoUnit.SECONDS);
		return instant.plus(nanoseconds * NANOSECONDS_DIVISOR, ChronoUnit.NANOS);
	}

	/**
	 * Get a clock sequence extracted from a given instant.
	 * 
	 * Currently the clock sequence is calculated from the instant nanoseconds.
	 * 
	 * It receives an Instant as parameter, then extracts its nanoseconds and
	 * calculates the clock sequence based on these nanoseconds.
	 * 
	 * If the current clock sequence is equal to the last clock sequence, the
	 * current clock sequence is incremented by 1 nanosecond to avoid
	 * repetitions.
	 * 
	 * @param timestamp
	 * @return
	 */
	protected static byte[] getClockSequence(Instant instant) {
		byte[] clockSequence = incrementClockSequence(instant, 0);

		if (lastClockSequence != null && equals(lastClockSequence, clockSequence)) {
			clockSequence = incrementClockSequence(instant, 1);
		}
		lastClockSequence = clockSequence;
		return clockSequence;
	}

	/**
	 * Increment clock sequence extracted from a given instant.
	 * 
	 * If you just want to get the current nanoseconsd, let the increment
	 * parameter set to zero.
	 * 
	 * @param timestamp
	 * @param nanosecondsIncrement
	 * @return
	 */
	protected static byte[] incrementClockSequence(Instant instant, long nanosecondsIncrement) {
		long nanoseconds = instant.getLong(ChronoField.NANO_OF_SECOND);
		long nanosecondsRest = (nanoseconds + nanosecondsIncrement) % CLOCK_SEQUENCE_DIVISOR;
		byte[] bytes = toBytes(nanosecondsRest);
		byte[] clockSequence = copy(bytes, 6, 8);
		return clockSequence;
	}

	/**
	 * Get the instant contained in the UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	protected static Instant extractInstant(UUID uuid) {

		byte[] bytes = toByteArray(uuid.toString().replaceAll("-", ""));

		byte[] fragment3 = getFragment(bytes, 3);
		boolean version1 = (fragment3[0] >> 4 & 0x01) == 1;
		boolean version4 = (fragment3[0] >> 4 & 0x04) == 4;

		byte[] time1;
		byte[] time2;
		byte[] time3;

		if (version1) {
			time1 = getFragment(bytes, 1);
			time2 = getFragment(bytes, 2);
			time3 = getFragment(bytes, 3);
			time3[0] = (byte) (time3[0] & 0x0F);
		} else if (version4) {
			time3 = getFragment(bytes, 1);
			time2 = getFragment(bytes, 2);
			time1 = getFragment(bytes, 3);
		} else {
			return null;
		}

		byte[] timestampBytes = array(8, (byte) 0x00);
		if (version1) {
			timestampBytes = replace(timestampBytes, time3, 0);
			timestampBytes = replace(timestampBytes, time2, 2);
			timestampBytes = replace(timestampBytes, time1, 4);
		} else if (version4) {
			timestampBytes = replace(timestampBytes, time3, 0);
			timestampBytes = replace(timestampBytes, time2, 4);
			timestampBytes = replace(timestampBytes, time1, 6);
		} else {
			return null;
		}

		long timestamp = toNumber(toHexadecimal(timestampBytes));
		return getGregorianCalendarInstant(timestamp);
	}

	/**
	 * Get hardware address from host machine.
	 * 
	 * It tries to get the first MAC, otherwise, returns null.
	 * 
	 * @param realHardwareAddress
	 * @return
	 */
	protected static byte[] getHardwareAddress(boolean realHardwareAddress) {

		if (hardwareAddress != null) {
			return hardwareAddress;
		}

		if (!realHardwareAddress) {
			hardwareAddress = setMulticastHardwareAddress(getRandomBytes(6));
			return hardwareAddress;
		}

		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			hardwareAddress = nic.getHardwareAddress();
			return hardwareAddress;
		} catch (SocketException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Set a hardware address as multicast.
	 * 
	 * @param hardwareAddress
	 * @return
	 */
	protected static byte[] setMulticastHardwareAddress(final byte[] hardwareAddress) {
		byte[] result = copy(hardwareAddress);
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

		byte[] bytes = toByteArray(uuid.toString().replaceAll("-", ""));

		byte[] hardwareAddress = getFragment(bytes, 5);

		if (!isMulticastHardwareAddress(hardwareAddress)) {
			return hardwareAddress;
		}

		return null;
	}

	/**
	 * Get a MD5 hash from a given array of bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	protected static byte[] getHash(byte[] bytes) {

		if (messageDigest == null) {
			try {
				messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}
		return messageDigest.digest(bytes);
	}

	/**
	 * Get a random hash.
	 * 
	 * It works in two steps: 1. get an array of 32 bytes using
	 * java.util.Random(); 2. get and return a MD5 hash from the array of bytes.
	 * 
	 * @return
	 */
	protected static byte[] getRandomHash() {
		return getHash(getRandomBytes(32));
	}

	/**
	 * Get a random array of bytes.
	 * 
	 * @param length
	 * @return
	 */
	protected static byte[] getRandomBytes(int length) {
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Get a formatted random hash.
	 * 
	 * @return
	 */
	protected static String getFormattedRandomHash() {
		return formatString(toHexadecimal(getRandomHash()));
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
	 * Get a fragment of a given UUID.
	 * 
	 * @param uuid
	 * @param index
	 * @return
	 */
	protected static byte[] getFragment(byte[] uuid, int index) {
		switch (index) {
		case 1:
			return copy(uuid, 0, 4);
		case 2:
			return copy(uuid, 4, 6);
		case 3:
			return copy(uuid, 6, 8);
		case 4:
			return copy(uuid, 8, 10);
		case 5:
			return copy(uuid, 10, 16);
		default:
			return null;
		}
	}

	/**
	 * Replace a fragment of a given UUID.
	 * 
	 * @param uuid
	 * @param replacement
	 * @param index
	 * @return
	 */
	protected static byte[] replaceFragment(final byte[] uuid, final byte[] replacement, int index) {
		switch (index) {
		case 1:
			return replace(uuid, replacement, 0);
		case 2:
			return replace(uuid, replacement, 4);
		case 3:
			return replace(uuid, replacement, 6);
		case 4:
			return replace(uuid, replacement, 8);
		case 5:
			return replace(uuid, replacement, 10);
		default:
			return null;
		}
	}

	/**
	 * Get a number from a given hexadevimal string.
	 * 
	 * @param hexadecimal
	 * @return
	 */
	protected static long toNumber(String hexadecimal) {
		return (long) Long.parseLong(hexadecimal, 16);
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
	 * Get a hexadecimal string from given array of bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexadecimal(byte[] bytes) {
		char[] hexadecimal = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			hexadecimal[i * 2] = HEXADECIMAL_CHARS[v >>> 4];
			hexadecimal[i * 2 + 1] = HEXADECIMAL_CHARS[v & 0x0F];
		}
		return new String(hexadecimal);
	}

	/**
	 * Get an array of bytes from a given hexadecimal string.
	 * 
	 * @param hexadecimal
	 * @return
	 */
	public static byte[] toByteArray(String hexadecimal) {
		int len = hexadecimal.length();
		byte[] bytes = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hexadecimal.charAt(i), 16) << 4)
					+ Character.digit(hexadecimal.charAt(i + 1), 16));
		}
		return bytes;
	}

	/**
	 * Get a new array with a specific lenth and filled with a byte value.
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	private static byte[] array(int length, byte value) {
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
	private static byte[] copy(byte[] bytes) {
		byte[] result = copy(bytes, 0, bytes.length);
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
	private static byte[] copy(byte[] bytes, int start, int end) {

		byte[] result = new byte[end - start];
		for (int i = 0; i < result.length; i++) {
			result[i] = bytes[start + i];
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
	private static byte[] replace(final byte[] bytes, final byte[] replacement, int index) {
		byte[] result = copy(bytes);
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
	 * This is just a template used during implementations to compare speed of
	 * to versions of the same method.
	 */
	protected static void speedTest() {

		long max = (long) Math.pow(10, 6);
		Instant start = null;
		Instant end = null;

		start = getClockInstant();
		for (int i = 0; i < max; i++) {
			UUID.randomUUID(); // example
		}
		end = getClockInstant();
		long miliseconds1 = (end.toEpochMilli() - start.toEpochMilli());

		start = getClockInstant();
		for (int i = 0; i < max; i++) {
			UUIDGenerator.getRandomUUIDString(); // example
		}
		end = getClockInstant();
		long miliseconds2 = (end.toEpochMilli() - start.toEpochMilli());

		System.out.println("Method 1: " + miliseconds1);
		System.out.println("Method 2: " + miliseconds2);

	}

	public static void main(String[] args) {

		speedTest();

	}
}
