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
 * Function that encodes a UUID to a base-n string.
 * 
 * It encodes using remainder operator (modulus), a common approach to encode
 * integers.
 * 
 * The encoding process is performed using integer arithmetic.
 */
public final class BaseNRemainderEncoder extends BaseNEncoder {

	private final int n; // the radix

	private static final long HALF_LONG_MASK = 0x00000000ffffffffL;

	public BaseNRemainderEncoder(BaseN base) {
		super(base);
		n = base.getRadix();
	}

	@Override
	public String apply(UUID uuid) {

		// unsigned 128 bit number
		int[] number = new int[4];
		number[0] = (int) (uuid.getMostSignificantBits() >>> 32);
		number[1] = (int) (uuid.getMostSignificantBits() & HALF_LONG_MASK);
		number[2] = (int) (uuid.getLeastSignificantBits() >>> 32);
		number[3] = (int) (uuid.getLeastSignificantBits() & HALF_LONG_MASK);

		char[] buffer = new char[base.getLength()];
		int b = buffer.length; // buffer index

		// fill in the buffer backwards using remainder operation
		while (!isZero(number)) {
			final int[] quotient = new int[4];
			final int remainder = remainder(number, n, quotient);
			buffer[--b] = alphabet.get(remainder);
			number = quotient;
		}

		// add padding to the leading
		if (b > 0) {
			// there are x chars to be padded
			final int padding = b;
			for (int i = 0; i < padding; i++) {
				buffer[--b] = base.getPadding();
			}
		}

		return new String(buffer);
	}

	private int remainder(int[] number, int divisor, int[] quotient) {

		long temporary = 0;
		long remainder = 0;

		if (number[0] != 0) { // optimization condition
			temporary = number[0] & HALF_LONG_MASK;
			quotient[0] = (int) (temporary / divisor);
			remainder = temporary % divisor;
		}

		if (number[1] != 0 || remainder != 0) {
			temporary = (remainder << 32) | (number[1] & HALF_LONG_MASK);
			quotient[1] = (int) (temporary / divisor);
			remainder = temporary % divisor;
		}

		if (number[2] != 0 || remainder != 0) {
			temporary = (remainder << 32) | (number[2] & HALF_LONG_MASK);
			quotient[2] = (int) (temporary / divisor);
			remainder = temporary % divisor;
		}

		if (number[3] != 0 || remainder != 0) {
			temporary = (remainder << 32) | (number[3] & HALF_LONG_MASK);
			quotient[3] = (int) (temporary / divisor);
			remainder = temporary % divisor;
		}

		return (int) remainder;
	}

	private boolean isZero(int[] number) {
		return number[0] == 0 && number[1] == 0 && number[2] == 0 && number[3] == 0;
	}
}
