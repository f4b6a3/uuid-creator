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

import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Utility for UUID validation.
 */
public final class UuidValidator {

	private UuidValidator() {
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
	 * @param uuid a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static void validate(final byte[] uuid) {
		if (!isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID byte array.");
		}
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * <pre>
	 * Examples of accepted formats:
	 * 
	 * 12345678abcdabcdabcd123456789abcd       (32 hexadecimal chars, lower case and without hyphen)
	 * 12345678ABCDABCDABCD123456789ABCD       (32 hexadecimal chars, UPPER CASE and without hyphen)
	 * 12345678-abcd-abcd-abcd-123456789abcd   (36 hexadecimal chars, lower case and with hyphen)
	 * 12345678-ABCD-ABCD-ABCD-123456789ABCD   (36 hexadecimal chars, UPPER CASE and with hyphen)
	 * </pre>
	 * 
	 * @param uuid a UUID string
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final String uuid) {
		return (uuid != null && isValid(uuid.toCharArray()));
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * <pre>
	 * Examples of accepted formats:
	 * 
	 * 12345678abcdabcdabcd123456789abcd       (32 hexadecimal chars, lower case and without hyphen)
	 * 12345678ABCDABCDABCD123456789ABCD       (32 hexadecimal chars, UPPER CASE and without hyphen)
	 * 12345678-abcd-abcd-abcd-123456789abcd   (36 hexadecimal chars, lower case and with hyphen)
	 * 12345678-ABCD-ABCD-ABCD-123456789ABCD   (36 hexadecimal chars, UPPER CASE and with hyphen)
	 * </pre>
	 * 
	 * @param chars a UUID char array
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(final char[] chars) {

		if (chars == null) {
			return false;
		}

		if (chars.length == 36) {
			return isUuidWithHyphens(chars);
		} else if (chars.length == 32) {
			return isUuidWithoutHyphens(chars);
		}

		return false;
	}

	/**
	 * Checks if the UUID string is a valid.
	 * 
	 * @param uuid a UUID string
	 * @throws InvalidUuidException if invalid
	 */
	public static void validate(final String uuid) {
		if (!isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID string.");
		}
	}

	/**
	 * Checks if the UUID char array is a valid.
	 * 
	 * @param uuid a UUID char array
	 * @throws InvalidUuidException if invalid
	 */
	public static void validate(final char[] uuid) {
		if (!isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID char array.");
		}
	}

	private static boolean isUuidWithHyphens(final char[] chars) {
		int i = 0;
		// Firstly, check hexadecimal chars
		while (i < chars.length) {
			// Skip hyphen positions for now
			if (i == 8 || i == 13 || i == 18 || i == 23) {
				i++;
			}
			final int c = chars[i++];
			if (!((c >= 0x30 && c <= 0x39) || (c >= 0x61 && c <= 0x66) || (c >= 0x41 && c <= 0x46))) {
				// ASCII codes: 0-9, a-f, A-F
				return false;
			}
		}
		// Finally, check hyphens
		return (chars[8] == '-' && chars[13] == '-' && chars[18] == '-' && chars[23] == '-');
	}

	private static boolean isUuidWithoutHyphens(final char[] chars) {
		for (int i = 0; i < chars.length; i++) {
			final int c = chars[i];
			if (!((c >= 0x30 && c <= 0x39) || (c >= 0x61 && c <= 0x66) || (c >= 0x41 && c <= 0x46))) {
				// ASCII codes: 0-9, a-f, A-F
				return false;
			}
		}
		return true;
	}
}
