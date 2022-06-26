/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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
 * Function that decodes a base-32 string to a UUID.
 * 
 * It is case insensitive, so it decodes in lower case and upper case.
 * 
 * See: https://tools.ietf.org/html/rfc4648
 */
public final class Base32Decoder extends BaseNDecoder {

	public Base32Decoder(BaseN base) {
		super(base);
	}

	@Override
	public UUID apply(String string) {

		char[] chars = string.toCharArray();

		long msb = 0;
		long lsb = 0;

		msb |= map.get(chars[0x00]) << 59;
		msb |= map.get(chars[0x01]) << 54;
		msb |= map.get(chars[0x02]) << 49;
		msb |= map.get(chars[0x03]) << 44;
		msb |= map.get(chars[0x04]) << 39;
		msb |= map.get(chars[0x05]) << 34;
		msb |= map.get(chars[0x06]) << 29;
		msb |= map.get(chars[0x07]) << 24;
		msb |= map.get(chars[0x08]) << 19;
		msb |= map.get(chars[0x09]) << 14;
		msb |= map.get(chars[0x0a]) << 9;
		msb |= map.get(chars[0x0b]) << 4;

		msb |= map.get(chars[0x0c]) >>> 1;
		lsb |= map.get(chars[0x0c]) << 63;

		lsb |= map.get(chars[0x0d]) << 58;
		lsb |= map.get(chars[0x0e]) << 53;
		lsb |= map.get(chars[0x0f]) << 48;
		lsb |= map.get(chars[0x10]) << 43;
		lsb |= map.get(chars[0x11]) << 38;
		lsb |= map.get(chars[0x12]) << 33;
		lsb |= map.get(chars[0x13]) << 28;
		lsb |= map.get(chars[0x14]) << 23;
		lsb |= map.get(chars[0x15]) << 18;
		lsb |= map.get(chars[0x16]) << 13;
		lsb |= map.get(chars[0x17]) << 8;
		lsb |= map.get(chars[0x18]) << 3;
		lsb |= map.get(chars[0x19]) >>> 2;

		return new UUID(msb, lsb);
	}
}
