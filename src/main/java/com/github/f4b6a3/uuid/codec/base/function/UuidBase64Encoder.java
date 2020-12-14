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

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public class UuidBase64Encoder extends UuidBaseNEncoder {

	public UuidBase64Encoder(char[] alphabet) {
		super(UuidBaseN.BASE_64, alphabet);
	}

	@Override
	public String apply(UUID uuid) {

		final char[] chars = new char[base.getLength()];
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		chars[0x00] = alphabet[(int) ((msb >>> 58) & 0b111111)];
		chars[0x01] = alphabet[(int) ((msb >>> 52) & 0b111111)];
		chars[0x02] = alphabet[(int) ((msb >>> 46) & 0b111111)];
		chars[0x03] = alphabet[(int) ((msb >>> 40) & 0b111111)];
		chars[0x04] = alphabet[(int) ((msb >>> 34) & 0b111111)];
		chars[0x05] = alphabet[(int) ((msb >>> 28) & 0b111111)];
		chars[0x06] = alphabet[(int) ((msb >>> 22) & 0b111111)];
		chars[0x07] = alphabet[(int) ((msb >>> 16) & 0b111111)];
		chars[0x08] = alphabet[(int) ((msb >>> 10) & 0b111111)];
		chars[0x09] = alphabet[(int) ((msb >>> 4) & 0b111111)];

		chars[0x0a] = alphabet[(int) (msb << 2 & 0b111111) | (int) ((lsb >>> 62) & 0b111111)];

		chars[0x0b] = alphabet[(int) ((lsb >>> 56) & 0b111111)];
		chars[0x0c] = alphabet[(int) ((lsb >>> 50) & 0b111111)];
		chars[0x0d] = alphabet[(int) ((lsb >>> 44) & 0b111111)];
		chars[0x0e] = alphabet[(int) ((lsb >>> 38) & 0b111111)];
		chars[0x0f] = alphabet[(int) ((lsb >>> 32) & 0b111111)];
		chars[0x10] = alphabet[(int) ((lsb >>> 26) & 0b111111)];
		chars[0x11] = alphabet[(int) ((lsb >>> 20) & 0b111111)];
		chars[0x12] = alphabet[(int) ((lsb >>> 14) & 0b111111)];
		chars[0x13] = alphabet[(int) ((lsb >>> 8) & 0b111111)];
		chars[0x14] = alphabet[(int) ((lsb >>> 2) & 0b111111)];
		chars[0x15] = alphabet[(int) ((lsb << 4) & 0b111111)];

		return new String(chars);
	}
}
