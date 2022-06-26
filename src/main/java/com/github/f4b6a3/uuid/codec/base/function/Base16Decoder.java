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
 * Function that decodes a base-16 string to a UUID.
 * 
 * It is case insensitive, so it decodes in lower case and upper case.
 * 
 * See: https://tools.ietf.org/html/rfc4648
 */
public final class Base16Decoder extends BaseNDecoder {

	public Base16Decoder(BaseN base) {
		super(base);
	}

	@Override
	public UUID apply(String string) {

		char[] chars = string.toCharArray();

		long msb = 0;
		long lsb = 0;

		msb |= map.get(chars[0x00]) << 60;
		msb |= map.get(chars[0x01]) << 56;
		msb |= map.get(chars[0x02]) << 52;
		msb |= map.get(chars[0x03]) << 48;
		msb |= map.get(chars[0x04]) << 44;
		msb |= map.get(chars[0x05]) << 40;
		msb |= map.get(chars[0x06]) << 36;
		msb |= map.get(chars[0x07]) << 32;
		msb |= map.get(chars[0x08]) << 28;
		msb |= map.get(chars[0x09]) << 24;
		msb |= map.get(chars[0x0a]) << 20;
		msb |= map.get(chars[0x0b]) << 16;
		msb |= map.get(chars[0x0c]) << 12;
		msb |= map.get(chars[0x0d]) << 8;
		msb |= map.get(chars[0x0e]) << 4;
		msb |= map.get(chars[0x0f]);

		lsb |= map.get(chars[0x10]) << 60;
		lsb |= map.get(chars[0x11]) << 56;
		lsb |= map.get(chars[0x12]) << 52;
		lsb |= map.get(chars[0x13]) << 48;
		lsb |= map.get(chars[0x14]) << 44;
		lsb |= map.get(chars[0x15]) << 40;
		lsb |= map.get(chars[0x16]) << 36;
		lsb |= map.get(chars[0x17]) << 32;
		lsb |= map.get(chars[0x18]) << 28;
		lsb |= map.get(chars[0x19]) << 24;
		lsb |= map.get(chars[0x1a]) << 20;
		lsb |= map.get(chars[0x1b]) << 16;
		lsb |= map.get(chars[0x1c]) << 12;
		lsb |= map.get(chars[0x1d]) << 8;
		lsb |= map.get(chars[0x1e]) << 4;
		lsb |= map.get(chars[0x1f]);

		return new UUID(msb, lsb);
	}
}