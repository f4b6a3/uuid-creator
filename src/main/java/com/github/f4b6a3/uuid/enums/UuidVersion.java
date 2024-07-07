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

package com.github.f4b6a3.uuid.enums;

/**
 * UUID versions defined by RFC 9562.
 * <p>
 * List of versions:
 * <ul>
 * <li>{@link VERSION_UNKNOWN}: 0
 * <li>{@link VERSION_TIME_BASED}: 1
 * <li>{@link VERSION_DCE_SECURITY}: 2
 * <li>{@link VERSION_NAME_BASED_MD5}: 3
 * <li>{@link VERSION_RANDOM_BASED}: 4
 * <li>{@link VERSION_NAME_BASED_SHA1}: 5
 * <li>{@link VERSION_TIME_ORDERED}: 6
 * <li>{@link VERSION_TIME_ORDERED_EPOCH}: 7
 * <li>{@link VERSION_CUSTOM}: 8
 * </ul>
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9562.html">RFC 9562</a>
 */
public enum UuidVersion {

	/**
	 * An unknown version.
	 */
	VERSION_UNKNOWN(0),
	/**
	 * The time-based version with gregorian epoch specified in RFC 9562.
	 */
	VERSION_TIME_BASED(1),
	/**
	 * The DCE Security version, with embedded POSIX UIDs.
	 */
	VERSION_DCE_SECURITY(2),
	/**
	 * The name-based version specified in RFC 9562 that uses MD5 hashing.
	 */
	VERSION_NAME_BASED_MD5(3),
	/**
	 * The randomly or pseudo-randomly generated version specified in RFC 9562.
	 */
	VERSION_RANDOM_BASED(4),
	/**
	 * The name-based version specified in RFC 9562 that uses SHA-1 hashing.
	 */
	VERSION_NAME_BASED_SHA1(5),
	/**
	 * The time-ordered version with gregorian epoch proposed by Peabody and Davis.
	 */
	VERSION_TIME_ORDERED(6),
	/**
	 * The time-ordered version with Unix epoch proposed by Peabody and Davis.
	 */
	VERSION_TIME_ORDERED_EPOCH(7),
	/**
	 * The custom or free-form version proposed by Peabody and Davis.
	 */
	VERSION_CUSTOM(8);

	private final int value;

	UuidVersion(int value) {
		this.value = value;
	}

	/**
	 * Get the number value.
	 * 
	 * @return a number
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Get the enum value.
	 * 
	 * @param value a number.
	 * @return the enum
	 */
	public static UuidVersion getVersion(int value) {
		for (UuidVersion version : UuidVersion.values()) {
			if (version.getValue() == value) {
				return version;
			}
		}
		return null;
	}
}
