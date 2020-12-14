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

public class UuidBase32Decoder extends UuidBaseNDecoder {

	public UuidBase32Decoder(char[] alphabet) {
		super(UuidBaseN.BASE_32, alphabet);
	}

	@Override
	public UUID apply(String string) {

		char[] chars = toCharArray(string);

		long msb = 0;
		long lsb = 0;

		msb |= alphabetValues[chars[0x00]] << 59;
		msb |= alphabetValues[chars[0x01]] << 54;
		msb |= alphabetValues[chars[0x02]] << 49;
		msb |= alphabetValues[chars[0x03]] << 44;
		msb |= alphabetValues[chars[0x04]] << 39;
		msb |= alphabetValues[chars[0x05]] << 34;
		msb |= alphabetValues[chars[0x06]] << 29;
		msb |= alphabetValues[chars[0x07]] << 24;
		msb |= alphabetValues[chars[0x08]] << 19;
		msb |= alphabetValues[chars[0x09]] << 14;
		msb |= alphabetValues[chars[0x0a]] << 9;
		msb |= alphabetValues[chars[0x0b]] << 4;

		msb |= alphabetValues[chars[0x0c]] >>> 1;
		lsb |= alphabetValues[chars[0x0c]] << 63;

		lsb |= alphabetValues[chars[0x0d]] << 58;
		lsb |= alphabetValues[chars[0x0e]] << 53;
		lsb |= alphabetValues[chars[0x0f]] << 48;
		lsb |= alphabetValues[chars[0x10]] << 43;
		lsb |= alphabetValues[chars[0x11]] << 38;
		lsb |= alphabetValues[chars[0x12]] << 33;
		lsb |= alphabetValues[chars[0x13]] << 28;
		lsb |= alphabetValues[chars[0x14]] << 23;
		lsb |= alphabetValues[chars[0x15]] << 18;
		lsb |= alphabetValues[chars[0x16]] << 13;
		lsb |= alphabetValues[chars[0x17]] << 8;
		lsb |= alphabetValues[chars[0x18]] << 3;
		lsb |= alphabetValues[chars[0x19]] >>> 2;

		return new UUID(msb, lsb);
	}
}
