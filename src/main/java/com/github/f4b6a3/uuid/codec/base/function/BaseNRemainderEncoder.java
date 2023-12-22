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
import com.github.f4b6a3.uuid.codec.base.BaseNCodec.CustomDivider;

/**
 * Function that encodes a UUID to a base-n string.
 * <p>
 * It encodes using remainder operator (modulus), a common approach to encode
 * integers.
 * <p>
 * The encoding process is performed using integer arithmetic.
 */
public final class BaseNRemainderEncoder extends BaseNEncoder {

	private final int length;
	private final char padding;

	/**
	 * A custom divider for optimization.
	 */
	protected final CustomDivider divider;

	private static final long MASK = 0x00000000ffffffffL;

	/**
	 * Constructor with a base-n.
	 * 
	 * @param base a base-n
	 */
	public BaseNRemainderEncoder(BaseN base) {
		this(base, null);
	}

	/**
	 * Constructor with a base-n and a custom divider.
	 * 
	 * @param base    a base-n
	 * @param divider a custom divider
	 */
	public BaseNRemainderEncoder(BaseN base, CustomDivider divider) {
		super(base);

		length = base.getLength();
		padding = base.getPadding();
		final long radix = base.getRadix();

		if (divider != null) {
			this.divider = divider;
		} else {
			this.divider = x -> new long[] { x / radix, x % radix };
		}
	}

	@Override
	public String apply(UUID uuid) {

		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		int b = length; // buffer index
		char[] buffer = new char[length];

		long rem = 0; // remainder
		long[] ans; // [quotient, remainder]

		// fill in the buffer backwards
		while (msb != 0 || lsb != 0) {
			rem = 0;
			ans = divide(msb, divider, rem);
			msb = ans[0]; // quotient
			rem = ans[1]; // remainder
			ans = divide(lsb, divider, rem);
			lsb = ans[0]; // quotient
			rem = ans[1]; // remainder
			buffer[--b] = alphabet.get((int) rem);
		}

		// complete padding
		while (b > 0) {
			buffer[--b] = padding;
		}

		return new String(buffer);
	}

	/**
	 * Divide a long as unsigned 64 bit integer
	 * 
	 * @param x       a number to be divided
	 * @param divider a custom divider
	 * @param rem     the reminder
	 * @return an array of longs
	 */
	protected static long[] divide(final long x, CustomDivider divider, final long rem) {

		long[] div;
		long remainder;
		final long quotient1;
		final long quotient2;

		// divide the first 32 bits
		div = divider.divide((rem << 32) | (x >>> 32));
		quotient1 = div[0];
		remainder = div[1];

		// divide the last 32 bits
		div = divider.divide((remainder << 32) | (x & MASK));
		quotient2 = div[0];
		remainder = div[1];

		// prepare the answer
		final long[] answer = new long[2];
		answer[0] = (quotient1 << 32) | (quotient2 & MASK);
		answer[1] = remainder;

		return answer;
	}
}
