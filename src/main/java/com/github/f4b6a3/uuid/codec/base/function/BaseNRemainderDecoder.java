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
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Function that decodes a base-n string to a UUID.
 * 
 * It decodes strings created by encoders that use remainder operator (modulus),
 * a common approach to encode integers.
 * 
 * The decoding process is performed using integer arithmetic.
 */
public final class BaseNRemainderDecoder extends BaseNDecoder {

	private final int multiplier;

	private static final long MASK = 0x00000000ffffffffL;

	public BaseNRemainderDecoder(BaseN base) {
		super(base);
		multiplier = base.getRadix();
	}

	public UUID apply(String string) {

		final char[] chars = string.toCharArray();

		long msb = 0;
		long lsb = 0;

		long rem = 0; // remainder
		long[] ans; // [product, overflow]

		for (int i = 0; i < chars.length; i++) {
			rem = (int) map.get(chars[i]);
			ans = multiply(lsb, multiplier, rem);
			lsb = ans[0];
			rem = ans[1];
			ans = multiply(msb, multiplier, rem);
			msb = ans[0];
			rem = ans[1];
		}

		if (rem != 0) {
			throw new InvalidUuidException("Invalid encoded string (overflow): \"" + string + "\"");
		}

		return new UUID(msb, lsb);
	}

	// multiply a long as unsigned 64 bit integer
	protected static long[] multiply(final long x, final long multiplier, final long rem) {

		long mul;
		long overflow;
		final long product1;
		final long product2;

		// multiply the last 32 bits
		mul = ((x & MASK) * multiplier) + rem;
		product1 = mul & MASK;
		overflow = mul >>> 32;

		// multiply the first 32 bits
		mul = ((x >>> 32) * multiplier) + overflow;
		product2 = mul & MASK;
		overflow = mul >>> 32;

		// prepare the answer
		final long[] answer = new long[2];
		answer[0] = (product2 << 32) | (product1 & MASK);
		answer[1] = overflow;

		return answer;
	}
}