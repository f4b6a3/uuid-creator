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

	private final int radix;

	private static final int UUID_INTS = 4;
	private static final long HALF_LONG_MASK = 0x00000000ffffffffL;

	public BaseNRemainderDecoder(BaseN base) {
		super(base);
		radix = base.getRadix();
	}

	@Override
	public UUID apply(String string) {

		final char[] chars = toCharArray(string);

		// unsigned 128 bit number
		int[] number = new int[UUID_INTS];

		for (int i = 0; i < chars.length; i++) {
			final int remainder = (int) map.get(chars[i]);
			final int[] product = multiply(number, radix, remainder, true);
			number = product;
		}

		final long msb = ((number[0] & HALF_LONG_MASK) << 32) | (number[1] & HALF_LONG_MASK);
		final long lsb = ((number[2] & HALF_LONG_MASK) << 32) | (number[3] & HALF_LONG_MASK);

		return new UUID(msb, lsb);
	}

	protected static int[] multiply(int[] number, int multiplier, int addend, boolean validate) {

		long temporary = 0;
		long overflow = addend;
		final int[] product = new int[UUID_INTS];

		for (int i = UUID_INTS - 1; i >= 0; i--) {
			temporary = ((number[i] & HALF_LONG_MASK) * multiplier) + overflow;
			product[i] = (int) temporary;
			overflow = (temporary >>> 32);
		}

		if (validate && overflow != 0) {
			throw new InvalidUuidException("Invalid string (overflow)");
		}

		return product;
	}
}