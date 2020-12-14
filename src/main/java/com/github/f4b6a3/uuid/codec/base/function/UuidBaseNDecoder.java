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

package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;
import java.util.function.Function;

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;
import com.github.f4b6a3.uuid.exception.UuidCodecException;

public abstract class UuidBaseNDecoder implements Function<String, UUID> {

	protected final UuidBaseN base;

	protected final char[] alphabet;
	protected final long[] alphabetValues = new long[128];

	public UuidBaseNDecoder(UuidBaseN base, char[] alphabet) {

		this.base = base;
		this.alphabet = alphabet;

		// Initiate all alphabet values with -1
		for (int i = 0; i < this.alphabetValues.length; i++) {
			this.alphabetValues[i] = -1;
		}

		// Set the alphabet values
		for (int i = 0; i < alphabet.length; i++) {
			this.alphabetValues[alphabet[i]] = i;
		}
	}

	protected char[] toCharArray(String string) {
		char[] chars = string == null ? null : string.toCharArray();
		validate(chars);
		return chars;
	}

	protected void validate(char[] chars) {
		if (chars == null || chars.length != base.getLength()) {
			throw new UuidCodecException("Invalid string: \"" + (chars == null ? null : new String(chars)) + "\"");
		}
		for (int i = 0; i < chars.length; i++) {
			boolean found = false;
			for (int j = 0; j < alphabet.length; j++) {
				if (chars[i] == alphabet[j]) {
					found = true;
				}
			}
			if (!found) {
				throw new UuidCodecException("Invalid string: \"" + (new String(chars)) + "\"");
			}
		}
	}
}