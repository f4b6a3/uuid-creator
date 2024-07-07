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

package com.github.f4b6a3.uuid.factory.function;

import java.util.function.LongUnaryOperator;

import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * Function that must return a number between 0 and 16383 (2^14-1).
 * <p>
 * It receives as argument a number of 100-nanoseconds since 1970-01-01 (Unix
 * epoch).
 * <p>
 * Example:
 * 
 * <pre>{@code
 * // A function that returns new random clock sequences
 * ClockSeqFunction f = t -> ClockSeqFunction.getRandom();
 * }</pre>
 * 
 */
@FunctionalInterface
public interface ClockSeqFunction extends LongUnaryOperator {

	/**
	 * Returns a new random clock sequence in the range 0 to 16383 (2^14-1).
	 * 
	 * @return a number in the range 0 to 16383 (2^14-1)
	 */
	static long getRandom() {
		return toExpectedRange(RandomUtil.newSecureRandom().nextLong());
	}

	/**
	 * Clears the leading bits so that the resulting number is within the range 0 to
	 * 16383 (2^14-1).
	 * <p>
	 * The result is equivalent to {@code n % 2^14}.
	 * 
	 * @param clockseq a clock sequence
	 * @return a number in the range 0 to 16383 (2^14-1).
	 */
	static long toExpectedRange(final long clockseq) {
		return clockseq & 0x0000000000003fffL;
	}
}
