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
 * Function that decodes a base-64 string to a UUID.
 * <p>
 * It is case SENSITIVE.
 * 
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4648">RFC-4648</a>
 */
public final class Base64Decoder extends BaseNDecoder {

	/**
	 * Constructor with a base-n.
	 * 
	 * @param base a base-n
	 */
	public Base64Decoder(BaseN base) {
		super(base);
	}

	@Override
	public UUID apply(String string) {

		long msb = 0;
		long lsb = 0;

		msb = (msb << 6) | (map.get(string.charAt(0x00)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x01)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x02)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x03)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x04)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x05)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x06)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x07)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x08)) & 0xffL);
		msb = (msb << 6) | (map.get(string.charAt(0x09)) & 0xffL);
		
		msb = (msb << 4) | ((map.get(string.charAt(0x0a)) & 0xffL) >>> 2);
		
		lsb = (lsb << 6) | (map.get(string.charAt(0x0a)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x0b)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x0c)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x0d)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x0e)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x0f)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x10)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x11)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x12)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x13)) & 0xffL);
		lsb = (lsb << 6) | (map.get(string.charAt(0x14)) & 0xffL);
		
		lsb = (lsb << 2) | ((map.get(string.charAt(0x15)) & 0xffL) >>> 4);

		return new UUID(msb, lsb);
	}
}
