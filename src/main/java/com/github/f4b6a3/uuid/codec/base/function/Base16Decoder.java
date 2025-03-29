/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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

import com.github.f4b6a3.uuid.codec.base.BaseN;

/**
 * Function that decodes a base-16 string to a UUID.
 * <p>
 * It is case insensitive, so it decodes in lower case and upper case.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base16Decoder extends BaseNDecoder {

	/**
	 * Constructor with a base-n.
	 * 
	 * @param base a base-n
	 */
	public Base16Decoder(BaseN base) {
		super(base);
	}

	@Override
	public UUID apply(String string) {

		long msb = 0;
		long lsb = 0;

		for (int i = 0; i < 16; i++) {
			msb = (msb << 4) | get(string, i);
		}

		for (int i = 16; i < 32; i++) {
			lsb = (lsb << 4) | get(string, i);
		}

		return new UUID(msb, lsb);
	}
}