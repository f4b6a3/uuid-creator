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
 * Function that encodes a UUID to a base-n string.
 * 
 * It encodes using remainder operator (modulus), a common approach to encode
 * integers.
 * 
 * The encoding process is performed using integer arithmetic.
 */
public final class BaseNRemainderEncoder extends BaseNEncoder {

	private final BigInteger n; // the radix

	private static final int SIGNUM_POSITIVE = 1;

	public BaseNRemainderEncoder(BaseN base) {
		super(base);
		n = BigInteger.valueOf(base.getRadix());
	}

	@Override
	public String apply(UUID uuid) {

		// it must be a POSITIVE big number
		byte[] bytes = BinaryCodec.INSTANCE.encode(uuid);
		BigInteger number = new BigInteger(SIGNUM_POSITIVE, bytes);

		char[] buffer = new char[base.getLength()];
		int b = buffer.length; // buffer index

		// fill in the buffer backwards using remainder operation
		while (number.signum() == SIGNUM_POSITIVE) {
			buffer[--b] = alphabet.get(number.remainder(n).intValue());
			number = number.divide(n);
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
}
