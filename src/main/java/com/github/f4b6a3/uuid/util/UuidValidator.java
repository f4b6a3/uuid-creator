/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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
 * 
 * Using it is much faster than using on regular expression.
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
	 * Checks if the UUID char array is a valid.
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
	 * Checks if the UUID char array is a valid.
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

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * <pre>
	 * Examples of accepted formats:
	 * 
	 * 12345678-abcd-abcd-abcd-123456789abcd   (36 hexadecimal chars, lower case and with hyphen)
	 * 12345678-ABCD-ABCD-ABCD-123456789ABCD   (36 hexadecimal chars, UPPER CASE and with hyphen)
	 * 12345678abcdabcdabcd123456789abcd       (32 hexadecimal chars, lower case and WITHOUT hyphen)
	 * 12345678ABCDABCDABCD123456789ABCD       (32 hexadecimal chars, UPPER CASE and WITHOUT hyphen)
	 * </pre>
	 * 
	 * @param chars a char array
	 * @return true if valid, false if invalid
	 */
	protected static boolean isParseable(final char[] chars) {

		int hyphens = 0;
		for (int i = 0; i < chars.length; i++) {
			if (MAP.get(chars[i]) == -1) {
				if (chars[i] == '-') {
					hyphens++;
					continue;
				}
				return false; // invalid character!
			}
		}

		if (chars.length == 36 && hyphens == 4) {
			// check if the hyphens positions are correct
			return chars[8] == '-' && chars[13] == '-' && chars[18] == '-' && chars[23] == '-';
		}

		return chars.length == 32 && hyphens == 0;
	}

	/**
	 * Checks if the UUID char array is valid.
	 * 
	 * @param chars   a char array
	 * @param version a version number
	 * @return true if valid, false if invalid
	 */
	protected static boolean isParseable(final char[] chars, int version) {
		return isVersion(chars, version) && isParseable(chars);
	}

	protected static boolean isVersion(UUID uuid, int version) {
		boolean versionOk = ((version & ~0xf) == 0) && (uuid.version() == version);
		boolean variantOk = uuid.variant() == 2; // RFC-4122
		return versionOk && variantOk;
	}

	protected static boolean isVersion(byte[] bytes, int version) {
		boolean versionOk = ((version & ~0xf) == 0) && (((bytes[6] & 0xff) >>> 4) == version);
		boolean variantOk = ((bytes[8] & 0xff) >>> 6) == 2; // RFC-4122
		return versionOk && variantOk;
	}

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
