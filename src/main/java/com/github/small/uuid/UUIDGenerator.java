package com.github.small.uuid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class UUIDGenerator {

	private static final char TIMESTAMP_VERSION = '1';
	private static final char RANDOM_VERSION = '4';
	private static final Instant START_INSTANT = getGregorianCalendarBeginning();
	private static final long SECONDS_MULTIPLYER = (long) Math.pow(10, 7);
	private static final long NANOSECONDS_DIVISOR = (long) Math.pow(10, 2);
	private static final short TIMESTAMP_LENGH = 15;
	private static final char[] VARIANT_ONE_CHARS = "89ab".toCharArray();
	private static final char[] HEXADECIMAL_CHARS = "0123456789abcdef".toCharArray();

	/**
	 * @see {@link UUIDGenerator#getRandomUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getRandomUUID() {
		return UUID.fromString(getRandomUUIDString());
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
	 * @see {@link UUIDGenerator#getTimestampAndHardwareAddressUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getTimestampAndHardwareAddressUUID() {
		return UUID.fromString(getTimestampAndHardwareAddressUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getNaturalTimestampUUID() {
		return UUID.fromString(getNaturalTimestampUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampAndHardwareAddressUUIDString()}
	 * 
	 * @return
	 */
	public static UUID getNaturalTimestampAndHardwareAddressUUID() {
		return UUID.fromString(getNaturalTimestampAndHardwareAddressUUIDString());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static UUID getTimestampUUID(Instant instant) {
		return UUID.fromString(getTimestampUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampAndHardwareAddressUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static UUID getTimestampAndHardwareAddressUUID(Instant instant) {
		return UUID.fromString(getTimestampAndHardwareAddressUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static UUID getNaturalTimestampUUID(Instant instant) {
		return UUID.fromString(getNaturalTimestampUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampAndHardwareAddressUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static UUID getNaturalTimestampAndHardwareAddressUUID(Instant instant) {
		return UUID.fromString(getNaturalTimestampAndHardwareAddressUUIDString(instant));
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getTimestampUUIDString() {
		return getTimestampUUIDString(Instant.now());
	}

	/**
	 * @see {@link UUIDGenerator#getTimestampAndHardwareAddressUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getTimestampAndHardwareAddressUUIDString() {
		return getTimestampAndHardwareAddressUUIDString(Instant.now());
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getNaturalTimestampUUIDString() {
		return getNaturalTimestampUUIDString(Instant.now());
	}

	/**
	 * @see {@link UUIDGenerator#getNaturalTimestampAndHardwareAddressUUIDString(Instant)}
	 * 
	 * @return
	 */
	public static String getNaturalTimestampAndHardwareAddressUUIDString() {
		return getNaturalTimestampAndHardwareAddressUUIDString(Instant.now());
	}

	/**
	 * Returns a random UUID with no timestamp and no machine address.
	 * 
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: NO <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Has sequential clock: NO <br/>
	 * 
	 * @param instant
	 * @return
	 */
	public static String getRandomUUIDString() {

		String timeLow = getRandomHexadecimal(8);
		String timeMid = getRandomHexadecimal(4);
		String timeHi = RANDOM_VERSION + getRandomHexadecimal(3);
		String clodkSeq = getRandomVariantOne() + getRandomHexadecimal(3);
		String node = getRandomHexadecimal(12);

		return timeLow + "-" + timeMid + "-" + timeHi + "-" + clodkSeq + "-" + node;
	}

	/**
	 * Returns a UUID with timestamp and no machine address.
	 * 
	 * TODO: set multicast bit for machine address to 1.
	 * 
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Has sequential clock: NO <br/>
	 * 
	 * @param instant
	 * @return
	 */
	public static String getTimestampUUIDString(Instant instant) {

		long timestamp = getTimestamp(instant);
		String hexTimestamp = toHexadecimal(timestamp, TIMESTAMP_LENGH);

		String timeLow = hexTimestamp.substring(7);
		String timeMid = hexTimestamp.substring(3, 7);
		String timeHi = TIMESTAMP_VERSION + hexTimestamp.substring(0, 3);
		String clodkSeq = getRandomVariantOne() + getRandomHexadecimal(3);
		String node = getRandomHexadecimal(12);

		return timeLow + "-" + timeMid + "-" + timeHi + "-" + clodkSeq + "-" + node;
	}

	/**
	 * Returns a UUID with timestamp and machine adress.
	 * 
	 * Details: <br/>
	 * - Version number: 1 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Has sequential clock: NO <br/>
	 * 
	 * @param instant
	 * @return
	 */
	public static String getTimestampAndHardwareAddressUUIDString(Instant instant) {

		String node = getHardwareAddress();
		if (node == null) {
			return getNaturalTimestampUUIDString(instant);
		}

		long timestamp = getTimestamp(instant);
		String hexTimestamp = toHexadecimal(timestamp, TIMESTAMP_LENGH);

		String timeLow = hexTimestamp.substring(7);
		String timeMid = hexTimestamp.substring(3, 7);
		String timeHi = TIMESTAMP_VERSION + hexTimestamp.substring(0, 3);
		String clodkSeq = getRandomVariantOne() + getRandomHexadecimal(3);

		return timeLow + "-" + timeMid + "-" + timeHi + "-" + clodkSeq + "-" + node;
	}

	/**
	 * Returns a UUID with timestamp and no machine address, but the bytes
	 * corresponding to timestamp are arranged in "natural" order, that is not
	 * compliant the standard.
	 * 
	 * TODO: set multicast bit for machine address to 1.
	 * 
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: NO <br/>
	 * - Has sequential clock: NO <br/>
	 * 
	 * @param instant
	 * @return
	 */
	public static String getNaturalTimestampUUIDString(Instant instant) {

		long timestamp = getTimestamp(instant);
		String hexTimestamp = toHexadecimal(timestamp, TIMESTAMP_LENGH);

		String timeLow = hexTimestamp.substring(0, 8);
		String timeMid = hexTimestamp.substring(8, 12);
		String timeHi = RANDOM_VERSION + hexTimestamp.substring(12);

		String clodkSeq = getRandomVariantOne() + getRandomHexadecimal(3);
		String node = getRandomHexadecimal(12);

		return timeLow + "-" + timeMid + "-" + timeHi + "-" + clodkSeq + "-" + node;
	}

	/**
	 * Returns a UUID with timestamp and machine adress, but the bytes
	 * corresponding to timestamp are arranged in "natural" order, that is not
	 * compliant the standard.
	 * 
	 * Details: <br/>
	 * - Version number: 4 <br/>
	 * - Variant number: 1 <br/>
	 * - Has timestamp?: YES <br/>
	 * - Has hardware address (MAC)?: YES <br/>
	 * - Has sequential clock: NO <br/>
	 * 
	 * @param instant
	 * @return
	 */
	public static String getNaturalTimestampAndHardwareAddressUUIDString(Instant instant) {

		String node = getHardwareAddress();
		if (node == null) {
			return getNaturalTimestampUUIDString(instant);
		}

		long timestamp = getTimestamp(instant);
		String hexTimestamp = toHexadecimal(timestamp, TIMESTAMP_LENGH);

		String timeLow = hexTimestamp.substring(0, 8);
		String timeMid = hexTimestamp.substring(8, 12);
		String timeHi = RANDOM_VERSION + hexTimestamp.substring(12);
		String clodkSeq = getRandomVariantOne() + getRandomHexadecimal(3);

		return timeLow + "-" + timeMid + "-" + timeHi + "-" + clodkSeq + "-" + node;
	}

	/**
	 * Returns a random array of chars choosen from within a list of chars.
	 * 
	 * @param length
	 * @param chars
	 * @return
	 */
	protected static char[] getRandomCharactersArray(char[] chars, int length) {

		char[] array = new char[length];
		Random random = new Random();

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
		char[] array = getRandomCharactersArray(HEXADECIMAL_CHARS, length);
		return String.valueOf(array);
	}

	/**
	 * Returns a random hexadecimal char from 8 to b.
	 * 
	 * @return
	 */
	protected static char getRandomVariantOne() {
		char[] array = getRandomCharactersArray(VARIANT_ONE_CHARS, 1);
		return array[0];
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
	protected static String getHardwareAddress() {
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] mac = nic.getHardwareAddress();
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				buffer.append(String.format("%02x", mac[i]));
			}
			return buffer.toString();
		} catch (SocketException | NullPointerException e) {
			return null;
		}
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
	protected static long getTimestamp(Instant instant) {
		long seconds = START_INSTANT.until(instant, ChronoUnit.SECONDS);
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

		Instant instant = START_INSTANT.plus(seconds / SECONDS_MULTIPLYER, ChronoUnit.SECONDS);
		return instant.plus(nanoseconds * NANOSECONDS_DIVISOR, ChronoUnit.NANOS);

	}
}
