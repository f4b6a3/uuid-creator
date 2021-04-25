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

package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.base.BaseN;

/**
 * Function that encodes a UUID to a base-64 string.
 * 
 * See: https://tools.ietf.org/html/rfc4648
 */
public final class Base64Encoder extends BaseNEncoder {

	/**
	 * @param base an enumeration that represents the base-n encoding
	 */
	public Base64Encoder(BaseN base) {
		super(base);
	}

	@Override
	public String apply(UUID uuid) {

		final char[] chars = new char[base.getLength()];
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = alphabet.get((int) ((msb >>> 58) & 0b111111));
		chars[0x01] = alphabet.get((int) ((msb >>> 52) & 0b111111));
		chars[0x02] = alphabet.get((int) ((msb >>> 46) & 0b111111));
		chars[0x03] = alphabet.get((int) ((msb >>> 40) & 0b111111));
		chars[0x04] = alphabet.get((int) ((msb >>> 34) & 0b111111));
		chars[0x05] = alphabet.get((int) ((msb >>> 28) & 0b111111));
		chars[0x06] = alphabet.get((int) ((msb >>> 22) & 0b111111));
		chars[0x07] = alphabet.get((int) ((msb >>> 16) & 0b111111));
		chars[0x08] = alphabet.get((int) ((msb >>> 10) & 0b111111));
		chars[0x09] = alphabet.get((int) ((msb >>> 4) & 0b111111));

		chars[0x0a] = alphabet.get((int) (msb << 2 & 0b111111) | (int) ((lsb >>> 62) & 0b111111));

		chars[0x0b] = alphabet.get((int) ((lsb >>> 56) & 0b111111));
		chars[0x0c] = alphabet.get((int) ((lsb >>> 50) & 0b111111));
		chars[0x0d] = alphabet.get((int) ((lsb >>> 44) & 0b111111));
		chars[0x0e] = alphabet.get((int) ((lsb >>> 38) & 0b111111));
		chars[0x0f] = alphabet.get((int) ((lsb >>> 32) & 0b111111));
		chars[0x10] = alphabet.get((int) ((lsb >>> 26) & 0b111111));
		chars[0x11] = alphabet.get((int) ((lsb >>> 20) & 0b111111));
		chars[0x12] = alphabet.get((int) ((lsb >>> 14) & 0b111111));
		chars[0x13] = alphabet.get((int) ((lsb >>> 8) & 0b111111));
		chars[0x14] = alphabet.get((int) ((lsb >>> 2) & 0b111111));
		chars[0x15] = alphabet.get((int) ((lsb << 4) & 0b111111));

		return new String(chars);
	}
}
