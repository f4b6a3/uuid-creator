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

import java.math.BigInteger;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.base.BaseN;

/**
 * Function that decodes a base-n string to a UUID.
 * 
 * It decodes strings created by encoders that use remainder operator (modulus),
 * a common approach to encode integers.
 * 
 * The decoding process is performed using integer arithmetic.
 */
public final class BaseNRemainderDecoder extends BaseNDecoder {

	private final BigInteger n; // the radix

	private static final int BYTE_LENGTH = 16;

	public BaseNRemainderDecoder(BaseN base) {
		super(base);
		n = BigInteger.valueOf(base.getRadix());
	}

	@Override
	public UUID apply(String string) {

		char[] chars = toCharArray(string);
		BigInteger number = BigInteger.ZERO;

		for (int c : chars) {
			final long value = map.get(c);
			number = n.multiply(number).add(BigInteger.valueOf(value));
		}

		// prepare a byte buffer
		byte[] result = number.toByteArray();
		byte[] buffer = new byte[BYTE_LENGTH];
		int r = result.length; // result index
		int b = buffer.length; // buffer index

		// fill in the byte buffer
		while (--b >= 0 && --r >= 0) {
			buffer[b] = result[r];
		}

		return BinaryCodec.INSTANCE.decode(buffer);
	}
}