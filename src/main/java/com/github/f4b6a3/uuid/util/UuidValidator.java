/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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
public class UuidValidator {

	public static final String UUID_PATTERN = "^([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}|[0-9a-fA-F]{32})$";

	private UuidValidator() {
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid a UUID byte array
	 * @return true if valid, false if invalid
	 */
	protected static boolean isValid(byte[] uuid) {
		return uuid != null && uuid.length == 16;
	}

	/**
	 * Checks if the UUID byte array is valid.
	 * 
	 * @param uuid a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static void validate(byte[] uuid) {
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
	 * 12345678abcdabcdabcd123456789abcd       (32 hexadecimal chars, lower case and without dash)
	 * 12345678ABCDABCDABCD123456789ABCD       (32 hexadecimal chars, UPPER CASE and without dash)
	 * 12345678-abcd-abcd-abcd-123456789abcd   (36 hexadecimal chars, lower case and with dash)
	 * 12345678-ABCD-ABCD-ABCD-123456789ABCD   (36 hexadecimal chars, UPPER CASE and with dash)
	 * </pre>
	 * 
	 * @param uuid a UUID string
	 * @return true if valid, false if invalid
	 */
	public static boolean isValid(String uuid) {
		return uuid != null && (uuid.length() == 32 || uuid.length() == 36) && uuid.matches(UUID_PATTERN);
	}

	/**
	 * Checks if the UUID string is a valid.
	 * 
	 * @param uuid a UUID
	 * @throws InvalidUuidException if invalid
	 */
	public static void validate(String uuid) {
		if (!isValid(uuid)) {
			throw new InvalidUuidException("Invalid UUID string.");
		}
	}
}
