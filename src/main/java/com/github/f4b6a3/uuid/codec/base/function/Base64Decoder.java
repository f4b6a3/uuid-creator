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
 * Function that decodes a base-64 string to a UUID.
 * 
 * It is case SENSITIVE.
 * 
 * See: https://tools.ietf.org/html/rfc4648
 */
public final class Base64Decoder extends BaseNDecoder {

	public Base64Decoder(BaseN base) {
		super(base);
	}

	@Override
	public UUID apply(String string) {

		char[] chars = string.toCharArray();

		long msb = 0;
		long lsb = 0;

		msb |= map.get(chars[0x00]) << 58;
		msb |= map.get(chars[0x01]) << 52;
		msb |= map.get(chars[0x02]) << 46;
		msb |= map.get(chars[0x03]) << 40;
		msb |= map.get(chars[0x04]) << 34;
		msb |= map.get(chars[0x05]) << 28;
		msb |= map.get(chars[0x06]) << 22;
		msb |= map.get(chars[0x07]) << 16;
		msb |= map.get(chars[0x08]) << 10;
		msb |= map.get(chars[0x09]) << 4;

		msb |= map.get(chars[0x0a]) >>> 2;
		lsb |= map.get(chars[0x0a]) << 62;

		lsb |= map.get(chars[0x0b]) << 56;
		lsb |= map.get(chars[0x0c]) << 50;
		lsb |= map.get(chars[0x0d]) << 44;
		lsb |= map.get(chars[0x0e]) << 38;
		lsb |= map.get(chars[0x0f]) << 32;
		lsb |= map.get(chars[0x10]) << 26;
		lsb |= map.get(chars[0x11]) << 20;
		lsb |= map.get(chars[0x12]) << 14;
		lsb |= map.get(chars[0x13]) << 8;
		lsb |= map.get(chars[0x14]) << 2;
		lsb |= map.get(chars[0x15]) >>> 4;

		return new UUID(msb, lsb);
	}
}
