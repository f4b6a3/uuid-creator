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
 * Function that encodes a UUID to a base-16 string.
 * <p>
 * It encodes in lower case only.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base16Encoder extends BaseNEncoder {

	private static final int CHAR_LENGTH = 32;

	/**
	 * Constructor with a base-n.
	 * 
	 * @param base a base-n
	 */
	public Base16Encoder(BaseN base) {
		super(base);
	}

	@Override
	public String apply(UUID uuid) {

		final char[] chars = new char[CHAR_LENGTH];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = get(msb >>> 0x3c & 0xf);
		chars[0x01] = get(msb >>> 0x38 & 0xf);
		chars[0x02] = get(msb >>> 0x34 & 0xf);
		chars[0x03] = get(msb >>> 0x30 & 0xf);
		chars[0x04] = get(msb >>> 0x2c & 0xf);
		chars[0x05] = get(msb >>> 0x28 & 0xf);
		chars[0x06] = get(msb >>> 0x24 & 0xf);
		chars[0x07] = get(msb >>> 0x20 & 0xf);
		chars[0x08] = get(msb >>> 0x1c & 0xf);
		chars[0x09] = get(msb >>> 0x18 & 0xf);
		chars[0x0a] = get(msb >>> 0x14 & 0xf);
		chars[0x0b] = get(msb >>> 0x10 & 0xf);
		chars[0x0c] = get(msb >>> 0x0c & 0xf);
		chars[0x0d] = get(msb >>> 0x08 & 0xf);
		chars[0x0e] = get(msb >>> 0x04 & 0xf);
		chars[0x0f] = get(msb >>> 0x00 & 0xf);

		chars[0x10] = get(lsb >>> 0x3c & 0xf);
		chars[0x11] = get(lsb >>> 0x38 & 0xf);
		chars[0x12] = get(lsb >>> 0x34 & 0xf);
		chars[0x13] = get(lsb >>> 0x30 & 0xf);
		chars[0x14] = get(lsb >>> 0x2c & 0xf);
		chars[0x15] = get(lsb >>> 0x28 & 0xf);
		chars[0x16] = get(lsb >>> 0x24 & 0xf);
		chars[0x17] = get(lsb >>> 0x20 & 0xf);
		chars[0x18] = get(lsb >>> 0x1c & 0xf);
		chars[0x19] = get(lsb >>> 0x18 & 0xf);
		chars[0x1a] = get(lsb >>> 0x14 & 0xf);
		chars[0x1b] = get(lsb >>> 0x10 & 0xf);
		chars[0x1c] = get(lsb >>> 0x0c & 0xf);
		chars[0x1d] = get(lsb >>> 0x08 & 0xf);
		chars[0x1e] = get(lsb >>> 0x04 & 0xf);
		chars[0x1f] = get(lsb >>> 0x00 & 0xf);

		return new String(chars);
	}
}
