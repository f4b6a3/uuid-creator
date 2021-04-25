/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

package com.github.f4b6a3.uuid.codec.base;

import com.github.f4b6a3.uuid.exception.UuidCodecException;
import com.github.f4b6a3.uuid.util.internal.immutable.CharArray;
import com.github.f4b6a3.uuid.util.internal.immutable.LongArray;

/**
 * Enumeration that lists the base-n encodings of this package.
 */
public enum BaseN {

	BASE_16(16, 32, true, "0123456789abcdef"), //
	BASE_32(32, 26, true, "abcdefghijklmnopqrstuvwxyz234567"), //
	BASE_32_HEX(32, 26, true, "0123456789abcdefghijklmnopqrstuv"), //
	BASE_32_CROCKFORD(32, 26, true, "0123456789abcdefghjkmnpqrstvwxyz"), //
	BASE_64(64, 22, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"), //
	BASE_64_URL(64, 22, false, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"); //

	private int number;
	private int length;
	private boolean insensitive;

	// Alphabets in lower and upper case
	// if case SENSITIVE, the arrays are equal
	private final CharArray lower;
	private final CharArray upper;

	// ASCII map
	// each char is mapped to a value
	private final LongArray map;

	/**
	 * @param number      the base number and also the alphabet size
	 * @param length      the string length of the encoded UUID
	 * @param insensitive a flag indicating whether the base-n is case insensitive
	 * @param alphabet    a string that contains the base-n alphabet
	 */
	BaseN(int number, int length, boolean insensitive, String alphabet) {
		this.number = number;
		this.length = length;
		this.insensitive = insensitive;

		if (insensitive) {
			// if case insensitive...
			this.lower = CharArray.from(alphabet.toLowerCase().toCharArray());
			this.upper = CharArray.from(alphabet.toUpperCase().toCharArray());
		} else {
			// else: the alphabets are equal if case SENSITIVE
			this.lower = CharArray.from(alphabet.toCharArray());
			this.upper = CharArray.from(alphabet.toCharArray());
		}

		// initialize the map with -1
		final long[] mapping = new long[128];
		for (int i = 0; i < mapping.length; i++) {
			mapping[i] = -1;
		}
		// map the alphabets chars to values
		for (int i = 0; i < this.lower.length(); i++) {
			mapping[this.lower.get(i)] = i;
			mapping[this.upper.get(i)] = i;
		}
		this.map = LongArray.from(mapping);
	}

	public int getNumber() {
		return number;
	}

	public int getLength() {
		return length;
	}

	public boolean isInsensitive() {
		return insensitive;
	}

	public CharArray getAlphabet() {
		return this.lower;
	}

	public LongArray getMap() {
		return this.map;
	}

	public void validate(char[] chars) {
		if (chars == null || chars.length != this.length) {
			throw new UuidCodecException("Invalid string: \"" + (chars == null ? null : new String(chars)) + "\"");
		}
		for (int i = 0; i < chars.length; i++) {
			boolean found = false;
			for (int j = 0; j < lower.length(); j++) {
				// check if the char is in one of the lower and upper alphabets
				if (chars[i] == lower.get(j) || chars[i] == upper.get(j)) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new UuidCodecException("Invalid string: \"" + (new String(chars)) + "\"");
			}
		}
	}
}