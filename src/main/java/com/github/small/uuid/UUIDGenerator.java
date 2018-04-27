package com.github.small.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.BitSet;
import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {

	private static final UUIDClock clock = new UUIDClock();

	private static final Instant GREGORIAN_EPOCH = getGregorianCalendarBeginning();

	private static final char TIMESTAMP_VERSION = '1';
	private static final char RANDOM_VERSION = '4';

	private static final short TIMESTAMP_LENGH = 15;
	private static final char[] VARIANT_1_CHARS = "89ab".toCharArray();
	private static final char[] HEXADECIMAL_CHARS = "0123456789abcdef".toCharArray();

	private static Random random = new Random();
	private static String hardwareAddress = null;
	private static String lastClockSequence = null;

	private static Charset charsetUTF8 = null;
	private static MessageDigest messageDigest = null;

	// Constant used to generate clock sequence (3 nibbles)
	private static final int THREE_NIBBLES = 4096;

	// Constants used to avoid long data type overflow
	private static final long SECONDS_MULTIPLYER = (long) Math.pow(10, 7);
	private static final long NANOSECONDS_DIVISOR = (long) Math.pow(10, 2);

	/**
	 * @see {@link UUIDGenerator#getRandomUUIDString(boolean)}
	 * 
	 * @return
	 */
	public static UUID getRandomUUID() {
		return UUID.fromString(getRandomUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampPrivateUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getTimestampPrivateUUID() {
		return UUID.fromString(getTimestampPrivateUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getTimestampUUID() {
		return UUID.fromString(getTimestampUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialPrivateUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getSequentialPrivateUUID() {
		return UUID.fromString(getSequentialPrivateUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getSequentialUUID() {
		return UUID.fromString(getSequentialUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampPrivateUUIDString(Instant)}
	 * 
	 * @return
	 */
	protected static UUID getTimestampPrivateUUID(Instant instant) {
		return UUID.fromString(getTimestampPrivateUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	protected static UUID getTimestampUUID(Instant instant) {
		return UUID.fromString(getTimestampUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialPrivateUUIDString(Instant)}
	 * 
	 * @return
	 */
	protected static UUID getSequentialPrivateUUID(Instant instant) {
		return UUID.fromString(getSequentialPrivateUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialUUIDString(Instant)}
	 * 
	 * @return
	 */
	protected static UUID getSequentialUUID(Instant instant) {
		return UUID.fromString(getSequentialUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampPrivateUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getTimestampPrivateUUIDString() {
		return getTimestampPrivateUUIDString(getClockInstant());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getTimestampUUIDString() {
		return getTimestampUUIDString(getClockInstant());
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialPrivateUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getSequentialPrivateUUIDString() {
		return getSequentialPrivateUUIDString(getClockInstant());
	}

	/**
	 * @see {@link UUIDGenerator#getSequentialUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getSequentialUUIDString() {
		return getSequentialUUIDString(getClockInstant());
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
	protected static String getTimestampPrivateUUIDString(Instant instant) {
		return getUUIDString(instant, false, true);
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
	 * @param instant
	 * @return
	 */
	protected static String getTimestampUUIDString(Instant instant) {
		return getUUIDString(instant, true, true);
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
	protected static String getSequentialPrivateUUIDString(Instant instant) {
		return getUUIDString(instant, false, false);
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
	protected static String getSequentialUUIDString(Instant instant) {
		return getUUIDString(instant, true, false);
	}

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
	protected static String getRandomUUIDString() {

		String uuid = null;
		uuid = getFormattedRandomHash();
		uuid = replaceBlock(uuid, RANDOM_VERSION + getRandomHexadecimal(3), 3);
		uuid = replaceBlock(uuid, getRandomCharacters(VARIANT_1_CHARS, 1)[0] + getRandomHexadecimal(3), 4);

		return uuid;

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
	 * @param realHardwareAddress
	 * @param standardTimestamp
	 * @return
	 */
	protected static String getUUIDString(Instant instant, boolean realHardwareAddress, boolean standardTimestamp) {

		long timestamp = getGregorianCalendarTimestamp(instant);
		String timestampHex = toHexadecimal(timestamp, TIMESTAMP_LENGH);

		String[] blocks = new String[5];

		if (standardTimestamp) {
			blocks[0] = timestampHex.substring(7);
			blocks[1] = timestampHex.substring(3, 7);
			blocks[2] = TIMESTAMP_VERSION + timestampHex.substring(0, 3);
		} else {
			blocks[0] = timestampHex.substring(0, 8);
			blocks[1] = timestampHex.substring(8, 12);
			blocks[2] = RANDOM_VERSION + timestampHex.substring(12);
		}

		blocks[3] = getClockSequence(timestamp);
		blocks[4] = getHardwareAddress(realHardwareAddress);

		return String.join("-", blocks);
	}

	/**
	 * Returns a 4 char sequence in which the first char is between '8' an 'b.
	 * 
	 * All four chars are generated based on the timestamp's last bytes.
	 * 
	 * If the char sequence generated repeats, the new char sequence is
	 * incremented.
	 * 
	 * @param timestampHex
	 * @return
	 */
	protected static String getClockSequence(long timestamp) {

		// Get char from '8' to 'b', calculated from last byte of timestamp
		char variant = VARIANT_1_CHARS[(int) (timestamp % 16) / 4];

		// Get three chars from the last bytes of timestamp
		long hundredNanoseconds = (timestamp / NANOSECONDS_DIVISOR) % THREE_NIBBLES;
		String clockSequence = toHexadecimal(hundredNanoseconds, 3);

		// Increment if the clock sequence repeats
		if (lastClockSequence != null && lastClockSequence.equals(clockSequence)) {
			clockSequence = toHexadecimal(hundredNanoseconds + 1, 3);
		}

		return variant + clockSequence;

	}

	/**
	 * Returns a random array of chars chosen from within a list of chars.
	 * 
	 * @param chars
	 * @param length
	 * @return
	 */
	protected static char[] getRandomCharacters(char[] chars, int length) {

		char[] array = new char[length];

		for (int i = 0; i < length; i++) {
			array[i] = chars[random.nextInt(chars.length)];
		}

		return array;
	}

	/**
	 * Returns a random String of hexadecimal.
	 * 
	 * @param length
	 * @return
	 */
	protected static String getRandomHexadecimal(int length) {
		char[] array = getRandomCharacters(HEXADECIMAL_CHARS, length);
		return String.valueOf(array);
	}

	/**
	 * Returns a SHA-1 hash for de input string.
	 * 
	 * @param string
	 * @return
	 */
	protected static String getHash(String string) {

		if (messageDigest == null) {
			try {
				charsetUTF8 = Charset.forName("UTF-8");
				messageDigest = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
		}

		byte[] bytes = string.getBytes(charsetUTF8);
		byte[] hash = messageDigest.digest(bytes);
		return toHexadecimal(hash);
	}

	/**
	 * Returns a SHA-1 hash calculated from a randomly generated string.
	 * 
	 * @return
	 */
	protected static String getRandomHash() {
		return getHash(getRandomHexadecimal(64));
	}

	/**
	 * Get a number corresponding to a hexadecimal string.
	 * 
	 * @param hexadecimal
	 * @return
	 */
	protected static long toNumber(String hexadecimal) {
		return (long) Long.parseLong(hexadecimal, 16);
	}

	/**
	 * Get a hexadecimal string corresponding to a number.
	 * 
	 * The string is padded with zeros to fit its desired length.
	 * 
	 * @param number
	 * @param length
	 * @return
	 */
	protected static String toHexadecimal(long number, int length) {
		String format2 = "%" + length + "s";
		String string = Long.toHexString(number);
		return String.format(format2, string).replaceAll(" ", "0");
	}

	/**
	 * Get a block of chars corresponding to an index from 1 to 5.
	 * 
	 * @param uuid
	 * @param index
	 * @return
	 */
	protected static String getBlock(String uuid, int index) {
		String[] blocks = uuid.split("-");
		return blocks[index - 1];
	}

	/**
	 * Replace a block of chars corresponding to an index from 1 to 5.
	 * 
	 * @param uuid
	 * @param hex
	 * @param index
	 * @return
	 */
	protected static String replaceBlock(String uuid, String hex, int index) {
		String[] blocks = uuid.split("-");
		blocks[index - 1] = hex;
		String result = String.join("-", blocks);
		return result;
	}

	/**
	 * Returns a string of the hardware address (MAC).
	 * 
	 * It returns the first MAC found.
	 * 
	 * It returns NULL if no MAC is found or something wrong happens.
	 * 
	 * @return
	 */
	protected static String getHardwareAddress(boolean realHardwareAddress) {

		if (hardwareAddress != null) {
			return hardwareAddress;
		}

		if (!realHardwareAddress) {
			hardwareAddress = setMultiCastBit(getRandomHexadecimal(12));
			return hardwareAddress;
		}

		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] mac = nic.getHardwareAddress();
			hardwareAddress = toHexadecimal(mac);
			return hardwareAddress;
		} catch (SocketException | NullPointerException e) {
			return null;
		}
	}

	/**
	 * Returns a hexadecimal string from a array of bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toHexadecimal(byte[] bytes) {
		char[] hexadecimal = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexadecimal[j * 2] = HEXADECIMAL_CHARS[v >>> 4];
			hexadecimal[j * 2 + 1] = HEXADECIMAL_CHARS[v & 0x0F];
		}
		return new String(hexadecimal);
	}

	/**
	 * Returns a array of bytes from a hexadecimal string.
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
	 * Returns an instant based on {@link UUIDClock#instant()}.
	 * 
	 * This method should be used in this class instead of "Instant.now()".
	 * 
	 * @return
	 */
	protected static Instant getClockInstant() {
		return Instant.now(clock);
	}

	/**
	 * Returns the date of adpotion the Gregorian Calendar: 1582-10-15T00:00:00Z
	 * 
	 * It used the UTC timezone.
	 * 
	 * @return
	 */
	protected static Instant getGregorianCalendarBeginning() {
		LocalDate localDate = LocalDate.parse("1582-10-15");
		return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
	}

	/**
	 * Returns a timestamp since 1582-10-15T00:00:00Z with of resolution 100
	 * nanoseconds.
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
	 * Returns the timestamp from the UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	protected static Instant extractInstant(UUID uuid) {

		String uuidString = uuid.toString();

		String timeLow = getBlock(uuidString, 1);
		String timeMid = getBlock(uuidString, 2);
		String timeHi = getBlock(uuidString, 3);

		char version = timeHi.charAt(0);

		String timestampHex = null;
		if (version == TIMESTAMP_VERSION) {
			timestampHex = timeHi.substring(1) + timeMid + timeLow;
		} else if (version == RANDOM_VERSION) {
			timestampHex = timeLow + timeMid + timeHi.substring(1);
		} else {
			return null;
		}

		long timestamp = toNumber(timestampHex);
		long nanoseconds = timestamp % SECONDS_MULTIPLYER;
		long seconds = timestamp - nanoseconds;

		Instant instant = GREGORIAN_EPOCH.plus(seconds / SECONDS_MULTIPLYER, ChronoUnit.SECONDS);
		return instant.plus(nanoseconds * NANOSECONDS_DIVISOR, ChronoUnit.NANOS);

	}

	/**
	 * Returns the hardware address if UUIDs version is 1 and the hardware
	 * addres is unicast.
	 * 
	 * @param uuid
	 * @return
	 */
	public static String extractHardwareAddress(UUID uuid) {

		String uuidString = uuid.toString();

		String timeHi = getBlock(uuidString, 3);
		String hardwareAddress = getBlock(uuidString, 5);

		char version = timeHi.charAt(0);

		if (version == TIMESTAMP_VERSION && !isMulticastHardwareAddress(hardwareAddress)) {
			return hardwareAddress;
		}

		return null;
	}

	/**
	 * Returns a random SHA-1 hash formatted in the UUID format.
	 *
	 * Some chars must be changed to comply standards, like version and variant
	 * chars.
	 * 
	 * @return
	 */
	protected static String getFormattedRandomHash() {
		return formatString(getRandomHash());
	}

	/**
	 * Returns a formatted string like this:
	 * 00000000-0000-0000-0000-000000000000
	 * 
	 * @return
	 */
	protected static String formatString(String string) {
		StringBuffer buffer = new StringBuffer(string.substring(0, 32));
		buffer.insert(8, '-');
		buffer.insert(13, '-');
		buffer.insert(18, '-');
		buffer.insert(23, '-');
		return buffer.toString();
	}

	/**
	 * Sets the least significant bit of the first byte of the hardware address
	 * to 1.
	 * 
	 * @param hardwareAddress
	 * @return
	 */
	protected static String setMultiCastBit(String hardwareAddress) {
		byte[] bytes = toByteArray(hardwareAddress);
		BitSet bits = BitSet.valueOf(bytes);
		bits.set(0, true);
		bytes = bits.toByteArray();
		return toHexadecimal(bytes);
	}

	/**
	 * Checks if a hardware address has the multicast set to 1.
	 * 
	 * Otherwise, the addres is unicast.
	 * 
	 * @param hardwareAddress
	 * @return
	 */
	protected static boolean isMulticastHardwareAddress(String hardwareAddress) {
		byte[] bytes = toByteArray(hardwareAddress);
		BitSet bits = BitSet.valueOf(bytes);
		return bits.get(0);
	}

	/**
	 * This is just a template used during implementations to compare speed of
	 * to versions of the same method.
	 */
	protected static void speedTest() {

		long max = (long) Math.pow(10, 1);
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
			UUIDGenerator.getRandomUUID(); // example
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
