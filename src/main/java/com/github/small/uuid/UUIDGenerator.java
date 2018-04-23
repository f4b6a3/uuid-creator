package com.github.small.uuid;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.UUID;

public class UUIDGenerator {

	/**
	 * @see {@link UUIDGenerator#getCustomUUID(Instant)}
	 * 
	 * @return {@link UUID}
	 */
	public static UUID getCustomUUID() {
		Instant instant = Instant.now();
		return getCustomUUID(instant);
	}

	/**
	 * This method returns a half-random UUID (version 4) that contains date and
	 * time information.
	 * 
	 * The first two parts of the UUID contain respectively seconds since epoch
	 * and milliseconds.
	 * 
	 * It's not a standard UUID version 4, but it may be considered compatible.
	 * Refer to RFC-4122, section 4.4.
	 * 
	 * It's not a standard UUID version 1 (time based). It's not compatible.
	 * Refer to RFC-4122, section 4.2.
	 * 
	 * ### Format of UUID generated:
	 * 
	 * > ssssssss-mmmm-4xxx-Vxxx-xxxxxxxxxxxx
	 * 
	 * ### Parts of the UUID generated:
	 * 
	 * 1. ssssssss: seconds sinse 1970-01-01T00:00:00Z;<br/>
	 * 2. mmmm:milliseconds of current second;<br/>
	 * 3. 4xxx: 4 is the version, the rest are random chars;<br/>
	 * 4. Vxxx: V contains the variant, the rest are random chars;<br/>
	 * 5. xxxxxxxxxxxx: all random chars.<br/>
	 * 
	 * @return instant
	 * @return
	 */
	public static UUID getCustomUUID(Instant instant) {

		// Generate a fully random UUID
		UUID uuid = UUID.randomUUID();
		StringBuilder str = new StringBuilder(uuid.toString());

		// Get number of seconds since epoch in hexadecimal
		String hexaSeconds = getHexaSeconds(instant);
		// Get number of milliseconds in hexadecimal
		String hexaMilliseconds = getHexaMilliseconds(instant);

		// Replace the first part of the random UUID with seconds
		str.replace(0, 8, hexaSeconds);
		// Replace the second part of the random UUID with milliseconds
		str.replace(9, 13, hexaMilliseconds);

		// Return the new half random UUID
		return UUID.fromString(str.toString());
	}

	/**
	 * Generates a string that contains the seconds since epoch in hex format.
	 * 
	 * @param instant
	 * @return
	 */
	private static String getHexaSeconds(Instant instant) {
		String hexaSeconds = String.format("%08x", instant.getLong(ChronoField.INSTANT_SECONDS) & 0xFFFFFFFF);
		return hexaSeconds.substring(hexaSeconds.length() - 8);
	}

	/**
	 * Generates a string that contains the milliseconds in hex format.
	 * 
	 * @param instant
	 * @return
	 */
	private static String getHexaMilliseconds(Instant instant) {
		String hexaMilliseconds = String.format("%04x", instant.getLong(ChronoField.MILLI_OF_SECOND) & 0xFFFF);
		return hexaMilliseconds.substring(hexaMilliseconds.length() - 4);
	}
}
