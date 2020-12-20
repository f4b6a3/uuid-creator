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

package com.github.f4b6a3.uuid.codec.base;

/**
 * Enumeration that lists the base-n alphabets of this package.
 */
public enum BaseNAlphabet {

	ALPHABET_BASE_16(BaseN.BASE_16, "0123456789abcdef"), //
	ALPHABET_BASE_32(BaseN.BASE_32, "abcdefghijklmnopqrstuvwxyz234567"), //
	ALPHABET_BASE_32_HEX(BaseN.BASE_32, "0123456789abcdefghijklmnopqrstuv"), //
	ALPHABET_BASE_32_CROCKFORD(BaseN.BASE_32, "0123456789abcdefghjkmnpqrstvwxyz"), //
	ALPHABET_BASE_64(BaseN.BASE_64, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"), //
	ALPHABET_BASE_64_URL(BaseN.BASE_64, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"); //

	private BaseN base;
	private String alphabet;

	/**
	 * @param base     an enumeration that represents the base-n encoding
	 * @param alphabet a string that contains the base-n alphabet
	 */
	private BaseNAlphabet(BaseN base, String alphabet) {
		this.base = base;
		this.alphabet = alphabet;
	}

	public BaseN getBase() {
		return this.base;
	}

	public String getAlphabet() {
		return this.alphabet;
	}
}
