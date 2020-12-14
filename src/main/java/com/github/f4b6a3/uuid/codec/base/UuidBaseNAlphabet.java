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

public enum UuidBaseNAlphabet {

	ALPHABET_BASE_16(UuidBaseN.BASE_16, //
			new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' }), //
	ALPHABET_BASE_32(UuidBaseN.BASE_32, //
			new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', //
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', //
					'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7' }), //
	ALPHABET_BASE_32_HEX(UuidBaseN.BASE_32, //
			new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', //
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V' }), //
	ALPHABET_BASE_32_CROCKFORD(UuidBaseN.BASE_32, //
			new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', //
					'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z' }), //
	ALPHABET_BASE_64(UuidBaseN.BASE_64, //
			new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', //
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', //
					'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', //
					'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
					'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', //
					'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' }), //
	ALPHABET_BASE_64_URL(UuidBaseN.BASE_64, //
			new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', //
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', //
					'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', //
					'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', //
					'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', //
					'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_' }); //

	private UuidBaseN base;
	private char[] alphabet;

	private UuidBaseNAlphabet(UuidBaseN base, char[] alphabet) {
		this.base = base;
		this.alphabet = alphabet;
	}

	public UuidBaseN getBase() {
		return base;
	}

	public char[] getAlphabet() {
		return alphabet.clone();
	}
}
