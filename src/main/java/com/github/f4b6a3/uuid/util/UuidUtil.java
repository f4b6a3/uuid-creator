/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.util;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVariant;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Utility that provides methods for checking UUID version, for extracting
 * information from UUIDs, and etc.
 */
public final class UuidUtil {

	private static final String MESSAGE_NOT_A_TIME_BASED_UUID = "Not a time-based, time-ordered or DCE Security UUID: %s.";
	private static final String MESSAGE_NOT_A_DCE_SECURITY_UUID = "Not a DCE Security UUID: %s.";

	private UuidUtil() {
	}

	/**
	 * Get the UUID version.
	 * 
	 * See: {@link UuidVersion}
	 * 
	 * @param uuid a UUID
	 * @return a {@link UuidVersion}
	 */
	public static UuidVersion getVersion(UUID uuid) {
		return UuidVersion.getVersion(uuid.version());
	}

	/**
	 * Get the UUID version.
	 * 
	 * See: {@link UuidVariant}
	 * 
	 * @param uuid a UUID
	 * @return a {@link UuidVariant}
	 */
	public static UuidVariant getVariant(UUID uuid) {
		return UuidVariant.getVariant(uuid.variant());
	}

	/**
	 * Applies UUID version bits into the UUID
	 * 
	 * @param uuid    a UUID
	 * @param version a version
	 * @return a UUID
	 */
	public static UUID applyVersion(UUID uuid, int version) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		msb = (msb & 0xffffffffffff0fffL) | ((version & 0x0000000f) << 12); // apply version
		lsb = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // apply variant
		return new UUID(msb, lsb);
	}

	/**
	 * Checks whether the UUID is equal to the Nil UUID.
	 * 
	 * The nil UUID is special UUID that has all 128 bits set to zero.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an RFC4122 variant
	 */
	public static boolean isNil(UUID uuid) {
		if (uuid == null) {
			throw new InvalidUuidException("Null UUID is not comparable to Nil UUID");
		}
		return uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L;
	}

	/**
	 * Checks whether the UUID variant is the one defined by the RFC-4122.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an RFC4122 variant
	 */
	public static boolean isRfc4122(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RFC_4122);
	}

	/**
	 * Checks whether the UUID variant is reserved NCS.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved NCS variant
	 */
	public static boolean isReservedNcs(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_NCS);
	}

	/**
	 * Checks whether the UUID variant is reserved Microsoft.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved Microsoft variant
	 */
	public static boolean isReservedMicrosoft(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_MICROSOFT);
	}

	/**
	 * Checks whether the UUID variant is reserved future.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved future variant
	 */
	public static boolean isReservedFuture(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_FUTURE);
	}

	/**
	 * Checks whether the UUID version 4.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a random UUID
	 */
	public static boolean isRandomBased(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_RANDOM_BASED);
	}

	/**
	 * Checks whether the UUID version 3.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a name-based UUID
	 */
	public static boolean isNameBasedMd5(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_NAME_BASED_MD5);
	}

	/**
	 * Checks whether the UUID version 5.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a name-based UUID
	 */
	public static boolean isNameBasedSha1(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_NAME_BASED_SHA1);
	}

	/**
	 * Checks whether the UUID version 1.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a time-based UUID
	 */
	public static boolean isTimeBased(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_TIME_BASED);
	}

	/**
	 * Checks whether the UUID version 6.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a time-ordered UUID
	 */
	public static boolean isTimeOrdered(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_TIME_ORDERED);
	}

	/**
	 * Checks whether the UUID version 2.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a DCE Security UUID
	 */
	public static boolean isDceSecurity(UUID uuid) {
		return isVersion(uuid, UuidVersion.VERSION_DCE_SECURITY);
	}

	/**
	 * Get the node identifier from a time-based, time-ordered or DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return long the node identifier
	 * @throws IllegalArgumentException if the input is not a time-based,
	 *                                  time-ordered or DCE Security UUID.
	 */
	public static long extractNodeIdentifier(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid) || UuidUtil.isTimeOrdered(uuid) || UuidUtil.isDceSecurity(uuid))) {
			throw new IllegalArgumentException(String.format(MESSAGE_NOT_A_TIME_BASED_UUID, uuid.toString()));
		}

		return uuid.getLeastSignificantBits() & 0x0000ffffffffffffL;
	}

	/**
	 * Get the clock sequence from a time-based, time-ordered or DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return int the clock sequence
	 * @throws IllegalArgumentException if the input is not a time-based,
	 *                                  time-ordered or DCE Security UUID.
	 */
	public static int extractClockSequence(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid) || UuidUtil.isTimeOrdered(uuid)) || UuidUtil.isDceSecurity(uuid)) {
			throw new IllegalArgumentException(String.format(MESSAGE_NOT_A_TIME_BASED_UUID, uuid.toString()));
		}

		if (UuidUtil.isDceSecurity(uuid)) {
			return (int) (uuid.getLeastSignificantBits() >>> 56) & 0x0000003f;
		}

		return (int) (uuid.getLeastSignificantBits() >>> 48) & 0x00003fff;
	}

	/**
	 * Get the instant from a time-based, time-ordered or DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return {@link Instant}
	 * @throws IllegalArgumentException if the input is not a time-based,
	 *                                  time-ordered or DCE Security UUID.
	 */
	public static Instant extractInstant(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return UuidTime.toInstant(timestamp);
	}

	/**
	 * Get the Unix epoch milliseconds from a time-based, time-ordered or DCE
	 * Security UUID.
	 *
	 * The value returned by this method is the number of milliseconds since
	 * 1970-01-01 (Unix epoch).
	 * 
	 * @param uuid a UUID
	 * @return Unix milliseconds
	 * @throws IllegalArgumentException if the input is not a time-based,
	 *                                  time-ordered or DCE Security UUID.
	 */
	public static long extractUnixMilliseconds(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return UuidTime.toUnixMilliseconds(timestamp);
	}

	/**
	 * Get the timestamp from a time-based, time-ordered or DCE Security UUID.
	 *
	 * The value returned by this method is the number of 100-nanos since 1582-10-15
	 * (Gregorian epoch).
	 *
	 * @param uuid a UUID
	 * @return long the timestamp
	 * @throws IllegalArgumentException if the input is not a time-based,
	 *                                  time-ordered or DCE Security UUID.
	 */
	public static long extractTimestamp(UUID uuid) {
		if (UuidUtil.isTimeBased(uuid)) {
			return extractTimeBasedTimestamp(uuid.getMostSignificantBits());
		} else if (UuidUtil.isTimeOrdered(uuid)) {
			return extractTimeOrderedTimestamp(uuid.getMostSignificantBits());
		} else if (UuidUtil.isDceSecurity(uuid)) {
			return extractDceSecurityTimestamp(uuid.getMostSignificantBits());
		} else {
			throw new IllegalArgumentException(String.format(MESSAGE_NOT_A_TIME_BASED_UUID, uuid.toString()));
		}
	}

	/**
	 * Get the local domain number from a DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return the local domain
	 * @throws IllegalArgumentException if the input is not a DCE Security UUID.
	 */
	public static byte extractLocalDomain(UUID uuid) {

		if (!UuidUtil.isDceSecurity(uuid)) {
			throw new IllegalArgumentException(String.format(MESSAGE_NOT_A_DCE_SECURITY_UUID, uuid.toString()));
		}

		return (byte) ((uuid.getLeastSignificantBits() & 0x00ff000000000000L) >>> 48);
	}

	/**
	 * Get the local identifier number from a DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return the local identifier
	 * @throws IllegalArgumentException if the input is not a DCE Security UUID.
	 */
	public static int extractLocalIdentifier(UUID uuid) {

		if (!UuidUtil.isDceSecurity(uuid)) {
			throw new IllegalArgumentException(String.format(MESSAGE_NOT_A_DCE_SECURITY_UUID, uuid.toString()));
		}

		return (int) (uuid.getMostSignificantBits() >>> 32);
	}

	private static boolean isVariant(UUID uuid, UuidVariant variant) {
		if (uuid == null) {
			throw new InvalidUuidException("Null UUID has no variant");
		}
		return (uuid.variant() == variant.getValue());
	}

	private static boolean isVersion(UUID uuid, UuidVersion version) {
		if (uuid == null) {
			throw new InvalidUuidException("Null UUID has no version");
		}
		return isRfc4122(uuid) && (uuid.version() == version.getValue());
	}

	/**
	 * Get the timestamp from a standard time-based MSB.
	 *
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractTimeBasedTimestamp(long msb) {

		long hii = (msb & 0xffffffff00000000L) >>> 32;
		long mid = (msb & 0x00000000ffff0000L) << 16;
		long low = (msb & 0x0000000000000fffL) << 48;

		return (hii | mid | low);
	}

	/**
	 * Get the timestamp from a time-ordered MSB.
	 *
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractTimeOrderedTimestamp(long msb) {

		long himid = (msb & 0xffffffffffff0000L) >>> 4;
		long low = (msb & 0x0000000000000fffL);

		return (himid | low);
	}

	/**
	 * Get the timestamp from a standard DCE security MSB.
	 *
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractDceSecurityTimestamp(long msb) {
		return extractTimeBasedTimestamp((msb & 0x00000000ffffffffL));
	}

	protected static char[] removeHyphens(final char[] input) {

		int count = 0;
		char[] buffer = new char[input.length];

		for (int i = 0; i < input.length; i++) {
			if ((input[i] != '-')) {
				buffer[count++] = input[i];
			}
		}

		char[] output = new char[count];
		System.arraycopy(buffer, 0, output, 0, count);

		return output;
	}
}
