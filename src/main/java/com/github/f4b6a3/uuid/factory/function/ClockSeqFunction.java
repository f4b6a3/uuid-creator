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

package com.github.f4b6a3.uuid.factory.function;

import java.util.function.LongUnaryOperator;

import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * It must return a number between 0 and 16383 (2^14-1).
 * 
 * It receives as argument the current 100-nanoseconds since 1970-01-01 (Unix
 * epoch).
 * 
 * Use {@link ClockSeqFunction#toExpectedRange(long)} to set the output within
 * the range 0 to 16383 (2^14-1).
 * 
 * Examples:
 * 
 * <pre>
 * // A `ClockSeqFunction` that always returns a random number between 0 and 16383 (2^14-1).
 * ClockSeqFunction f = unix100Nanos -> ClockSeqFunction.getRandom();
 * </pre>
 * 
 * <pre>
 * // A `ClockSeqFunction` that always returns the same number between 0 and 16383 (2^14-1).
 * final long number = 1234;
 * ClockSeqFunction f = unix100Nanos -> ClockSeqFunction.toExpectedRange(number);
 * </pre>
 * 
 */
@FunctionalInterface
public interface ClockSeqFunction extends LongUnaryOperator {

	/**
	 * This method return a new random clock sequence.
	 * 
	 * @return a clock sequence in the range 0 to 16383 (2^14-1).
	 */
	public static long getRandom() {
		return toExpectedRange(RandomUtil.nextLong());
	}

	/**
	 * This method clears the unnecessary leading bits so that the resulting number
	 * is within the range 0 to 16383 (2^14-1).
	 * 
	 * The result is equivalent to {@code n % 2^14}.
	 * 
	 * @param clockseq the clock sequence
	 * @return a clock sequence in the range 0 to 16383 (2^14-1).
	 */
	public static long toExpectedRange(final long clockseq) {
		return clockseq & 0x0000000000003fffL;
	}

	/**
	 * Class that manages the usage of clock sequence values from 0 to 16383.
	 * 
	 * It can be used to avoid that two time-based factories use the same clock
	 * sequence at same time in a class loader.
	 * 
	 * It is a pool of 16384 values. The pool is implemented as an array of 2048
	 * bytes (16384 bits). Each bit of this array corresponds to a pool value.
	 */
	public static final class ClockSeqPool {

		private final byte[] pool = new byte[2048];
		private static final int POOL_SIZE = 16384; // 2^14 = 16384

		public static final int POOL_MIN = 0x00000000;
		public static final int POOL_MAX = 0x00003fff; // 2^14-1 = 16383

		/**
		 * Take a value from the pool.
		 * 
		 * If the value to be taken is already in use, it is incremented until a free
		 * value is found and returned.
		 * 
		 * In the case that all pool values are in use, the pool is cleared and the last
		 * incremented value is returned.
		 * 
		 * It does nothing to negative arguments.
		 * 
		 * @param take value to be taken from the pool
		 * @return the value to be borrowed if not used.
		 */
		public synchronized int take(final int take) {
			int value = take;
			for (int i = 0; i < POOL_SIZE; i++) {
				if (setBit(value)) {
					return value;
				}
				value = ++value % POOL_SIZE;
			}
			clearPool();
			setBit(value);
			return value;
		}

		/**
		 * Take a random value from the pool.
		 * 
		 * @see {@link ClockSeqPool#take(int)}
		 * 
		 * @return the random value to be borrowed if not used.
		 */
		public synchronized int random() {
			// Choose a random number between 0 and 16383
			int random = (RandomUtil.nextInt() & 0x7fffffff) % POOL_SIZE;
			return this.take(random);
		}

		/**
		 * Set a bit from the byte array that represents the pool.
		 * 
		 * This operation corresponds to setting a value as used.
		 * 
		 * It returns false if the value is not free.
		 * 
		 * It does nothing to negative arguments.
		 * 
		 * @param value the value to be taken from the pool
		 * @return true if success.
		 */
		private synchronized boolean setBit(int value) {

			if (value < 0) {
				return false;
			}

			final int byteIndex = value / 8;
			final int bitIndex = value % 8;

			final int mask = (0x00000001 << bitIndex);
			final boolean clear = (pool[byteIndex] & mask) == 0;

			if (clear) {
				pool[byteIndex] = (byte) (pool[byteIndex] | mask);
				return true;
			}

			return false;
		}

		/**
		 * Check if a value is used out of the pool.
		 * 
		 * @param value a value to be checked in the pool.
		 * @return true if the value is used.
		 */
		public synchronized boolean isUsed(int value) {

			final int byteIndex = value / 8;
			final int bitIndex = value % 8;

			final int mask = (0x00000001 << bitIndex);
			boolean clear = (pool[byteIndex] & mask) == 0;

			return !clear;
		}

		/**
		 * Check if a value is free in the pool.
		 * 
		 * @param value a value to be checked in the pool.
		 * @return true if the value is free.
		 */
		public synchronized boolean isFree(int value) {
			return !this.isUsed(value);
		}

		/**
		 * Count the used values out of the pool.
		 * 
		 * @return the count of used values.
		 */
		public synchronized int countUsed() {
			int counter = 0;
			for (int i = 0; i < POOL_SIZE; i++) {
				if (this.isUsed(i)) {
					counter++;
				}
			}
			return counter;
		}

		/**
		 * Count the free values in the pool.
		 * 
		 * @return the count of free values.
		 */
		public synchronized int countFree() {
			return POOL_SIZE - this.countUsed();
		}

		/**
		 * Clear all bits of the byte array that represents the pool.
		 * 
		 * This corresponds to marking all pool values as free.
		 */
		public synchronized void clearPool() {
			for (int i = 0; i < pool.length; i++) {
				pool[i] = 0;
			}
		}
	}
}
