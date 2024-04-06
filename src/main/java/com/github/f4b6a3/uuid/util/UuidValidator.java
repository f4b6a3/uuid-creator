/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
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

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.Base16Codec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.immutable.LongArray;

/**
 * Utility for UUID validation.
 * <p>
 * Using it is much faster than using on regular expression.
 * <p>
 * Examples of valid string formats:
 * <ul>
 * <li><code>12345678-abcd-abcd-abcd-123456789abcd</code> (36 hexadecimal chars,
 * lower case and with hyphen)
 * <li><code>12345678-ABCD-ABCD-ABCD-123456789ABCD</code> (36 hexadecimal chars,
 * UPPER CASE and with hyphen)
 * <li><code>12345678abcdabcdabcd123456789abcd</code> (32 hexadecimal chars,
 * lower case and WITHOUT hyphen)
 * <li><code>12345678ABCDABCDABCD123456789ABCD</code> (32 hexadecimal chars,
 * UPPER CASE and WITHOUT hyphen)
 * </ul>
 */
public final class UuidValidator {

	private static final LongArray MAP = Base16Codec.INSTANCE.getBase().getMap();

	private UuidValidator() {
	}

	/**
	 * Checks if the UUID is valid.
	 * 
	 * @param uuid a UUID
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final UUID uuid) {
		return uuid != null;
	}

	/**
	 * Checks if the UUID is valid.
	 * 
	 * @param uuid    a UUID
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final UUID uuid, int version) {
		return uuid != null && isVersion(uuid, version);
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid a UUID byte array
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final byte[] uuid) {
		return uuid != null && uuid.length == 16;
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid    a UUID byte array
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final byte[] uuid, int version) {
		return uuid != null && uuid.length == 16 && isVersion(uuid, version);
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * @param uuid a UUID string
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final String uuid) {
		return uuid != null && uuid.length() != 0 && isParseable(uuid.toCharArray());
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * @param uuid    a UUID string
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final String uuid, int version) {
		return uuid != null && uuid.length() != 0 && isParseable(uuid.toCharArray(), version);
	}

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * @param uuid a UUID char array
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final char[] uuid) {
		return uuid != null && uuid.length != 0 && isParseable(uuid);
	}

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * @param uuid    a UUID char array
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final char[] uuid, int version) {
		return uuid != null && uuid.length != 0 && isParseable(uuid, version);
	}

	/**
	 * Checks if the UUID is valid.
	 * 
	 * @param uuid a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final UUID uuid) {
		if (uuid == null) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID is valid.
	 * 
	 * @param uuid    a UUID
	 * @param version a version number
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final UUID uuid, int version) {
		if (uuid == null || !isVersion(uuid, version)) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid a UUID byte array
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final byte[] uuid) {
		if (uuid == null || uuid.length != 16) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid    a UUID byte array
	 * @param version a version number
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final byte[] uuid, int version) {
		if (uuid == null || uuid.length != 16 || !isVersion(uuid, version)) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID string is a valid.
	 * 
	 * @param uuid a UUID string
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final String uuid) {
		if (uuid == null || !isParseable(uuid.toCharArray())) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID string is a valid.
	 * 
	 * @param uuid    a UUID string
	 * @param version a version number
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final String uuid, int version) {
		if (uuid == null || !isParseable(uuid.toCharArray(), version)) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * @param uuid a UUID char array
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final char[] uuid) {
		if (uuid == null || !isParseable(uuid)) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * @param uuid    a UUID char array
	 * @param version a version number
	 * @throws InvalidUuidException if the argument is invalid
	 */
	public static void validate(final char[] uuid, int version) {
		if (uuid == null || !isParseable(uuid, version)) {
			throw InvalidUuidException.newInstance(uuid);
		}
	}

	private static final int[] DASH_POSITIONS = {8, 13, 18, 23};
	private static final int WITH_DASH_UUID_LENGTH = 36;
	private static final int WITHOUT_DASH_UUID_LENGTH = 32;
	private static final int MAX_DASH_COUNT = 4;
	/**
	 * Checks if the UUID char array can be parsed.
	 * 
	 * @param chars a char array
	 * @return true if valid, false if invalid
	 */
	protected static boolean isParseable(final char[] chars) {
		int dashCount = 0;
		for (int i = 0; i < chars.length; i++) {
			if (MAP.get(chars[i]) == -1) {
				if (chars[i] == '-') {
					dashCount++;
					continue;
				}
				return false; // invalid character!
			}
		}

		if (chars.length == WITH_DASH_UUID_LENGTH && dashCount == MAX_DASH_COUNT) {
			// check if the hyphens positions are correct
			return chars[DASH_POSITIONS[0]] == '-' && chars[DASH_POSITIONS[1]] == '-' && chars[DASH_POSITIONS[2]] == '-' && chars[DASH_POSITIONS[3]] == '-';
		}

		return chars.length == WITHOUT_DASH_UUID_LENGTH && dashCount == 0;
	}

	/**
	 * Checks if the UUID char array can be parsed.
	 * 
	 * @param chars   a char array
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	protected static boolean isParseable(final char[] chars, int version) {
		return isVersion(chars, version) && isParseable(chars);
	}

	/**
	 * Checks the version number of a UUID.
	 * 
	 * @param uuid    a UUID
	 * @param version a version number
	 * @return true if the UUID version is equal to the expected version number
	 */
	protected static boolean isVersion(UUID uuid, int version) {
		boolean versionOk = ((version & ~0xf) == 0) && (uuid.version() == version);
		boolean variantOk = uuid.variant() == 2; // RFC-4122
		return versionOk && variantOk;
	}

	/**
	 * Checks the version number of a UUID byte array.
	 * 
	 * @param bytes   a byte array
	 * @param version a version number
	 * @return true if the UUID version is equal to the expected version number
	 */
	protected static boolean isVersion(byte[] bytes, int version) {
		boolean versionOk = ((version & ~0xf) == 0) && (((bytes[6] & 0xff) >>> 4) == version);
		boolean variantOk = ((bytes[8] & 0xff) >>> 6) == 2; // RFC-4122
		return versionOk && variantOk;
	}

	/**
	 * Checks the version number of a UUID char array.
	 * 
	 * @param chars   a string
	 * @param version a version number
	 * @return true if the UUID version is equal to the expected version number
	 */
	protected static boolean isVersion(char[] chars, int version) {

		// valid if between 0x0 and 0xf
		if ((version & ~0xf) != 0) {
			return false;
		}

		int ver = 0; // version index
		int var = 0; // variant index

		switch (chars.length) {
		case 32: // without hyphen
			ver = 12;
			var = 16;
			break;
		case 36: // with hyphen
			ver = 14;
			var = 19;
			break;
		default:
			return false;
		}

		final char[] lower = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		final char[] upper = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		boolean versionOk = ((version & ~0xf) == 0) && (chars[ver] == lower[version] || chars[ver] == upper[version]);
		boolean variantOk = chars[var] == '8' || chars[var] == '9' //
				|| chars[var] == 'a' || chars[var] == 'b' || chars[var] == 'A' || chars[var] == 'B';

		return versionOk && variantOk;
	}
}
