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

package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.BaseN;

/**
 * Function that encodes a UUID to a base-32 string.
 * <p>
 * It encodes in lower case only.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base32Encoder extends BaseNEncoder {

	private static final int CHAR_LENGTH = 26;

	/**
	 * Constructor with a base-n.
	 * 
	 * @param base a base-n
	 */
	public Base32Encoder(BaseN base) {
		super(base);
	}

	@Override
	public String apply(UUID uuid) {

		final char[] chars = new char[CHAR_LENGTH];
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = get((msb >>> 59) & 0b11111);
		chars[0x01] = get((msb >>> 54) & 0b11111);
		chars[0x02] = get((msb >>> 49) & 0b11111);
		chars[0x03] = get((msb >>> 44) & 0b11111);
		chars[0x04] = get((msb >>> 39) & 0b11111);
		chars[0x05] = get((msb >>> 34) & 0b11111);
		chars[0x06] = get((msb >>> 29) & 0b11111);
		chars[0x07] = get((msb >>> 24) & 0b11111);
		chars[0x08] = get((msb >>> 19) & 0b11111);
		chars[0x09] = get((msb >>> 14) & 0b11111);
		chars[0x0a] = get((msb >>> 9) & 0b11111);
		chars[0x0b] = get((msb >>> 4) & 0b11111);

		chars[0x0c] = get(((msb << 1) & 0b11111) | ((lsb >>> 63) & 0b11111));

		chars[0x0d] = get((lsb >>> 58) & 0b11111);
		chars[0x0e] = get((lsb >>> 53) & 0b11111);
		chars[0x0f] = get((lsb >>> 48) & 0b11111);
		chars[0x10] = get((lsb >>> 43) & 0b11111);
		chars[0x11] = get((lsb >>> 38) & 0b11111);
		chars[0x12] = get((lsb >>> 33) & 0b11111);
		chars[0x13] = get((lsb >>> 28) & 0b11111);
		chars[0x14] = get((lsb >>> 23) & 0b11111);
		chars[0x15] = get((lsb >>> 18) & 0b11111);
		chars[0x16] = get((lsb >>> 13) & 0b11111);
		chars[0x17] = get((lsb >>> 8) & 0b11111);
		chars[0x18] = get((lsb >>> 3) & 0b11111);
		chars[0x19] = get((lsb << 2) & 0b11111);

		return new String(chars);
	}
}
