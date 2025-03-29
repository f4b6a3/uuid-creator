/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.function.impl;

import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;

/**
 * Function that returns a clock sequence.
 * 
 * @see ClockSeqFunction
 */
public final class DefaultClockSeqFunction implements ClockSeqFunction {

	private AtomicInteger sequence;
	private long lastTimestamp = -1;

	/**
	 * The pool of clock sequence numbers.
	 */
	protected static final ClockSeqPool POOL = new ClockSeqPool();

	/**
	 * Default constructor.
	 */
	public DefaultClockSeqFunction() {
		final int initial = POOL.random();
		this.sequence = new AtomicInteger(initial);
	}

	@Override
	public long applyAsLong(final long timestamp) {
		if (timestamp > this.lastTimestamp) {
			this.lastTimestamp = timestamp;
			return this.sequence.get();
		}
		this.lastTimestamp = timestamp;
		return this.next();
	}

	/**
	 * Get the next random clock sequence number.
	 * 
	 * @return a number
	 */
	public int next() {
		if (this.sequence.incrementAndGet() > ClockSeqPool.POOL_MAX) {
			this.sequence.set(ClockSeqPool.POOL_MIN);
		}
		return this.sequence.updateAndGet(POOL::take);
	}

	/**
	 * Nested class that manages a pool of 16384 clock sequence values.
	 * <p>
	 * The pool is implemented as an array of 2048 bytes (16384 bits). Each bit of
	 * the array corresponds to a clock sequence value.
	 * <p>
	 * It is used to avoid that two time-based factories use the same clock sequence
	 * at same time in a class loader.
	 */
	static final class ClockSeqPool {

		private final byte[] pool = new byte[2048];
		private static final int POOL_SIZE = 16384; // 2^14 = 16384

		/**
		 * The minimum pool size, which is zero.
		 */
		public static final int POOL_MIN = 0x00000000;

		/**
		 * The maximum pool size, which is 16383 (2^14-1).
		 */
		public static final int POOL_MAX = 0x00003fff; // 2^14-1 = 16383

		/**
		 * Take a value from the pool.
		 * <p>
		 * If the value to be taken is already in use, it is incremented until a free
		 * value is found and returned.
		 * <p>
		 * In the case that all pool values are in use, the pool is cleared and the last
		 * incremented value is returned.
		 * <p>
		 * It does nothing to negative arguments.
		 * 
		 * @param take value to be taken from the pool
		 * @return the value to be borrowed if not used
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
		 * @return the random value to be borrowed if not used
		 */
		public synchronized int random() {
			// Choose a random number between 0 and 16383
			int random = Math.abs(new SplittableRandom().nextInt()) % POOL_SIZE;
			return this.take(random);
		}

		/**
		 * Set a bit from the byte array that represents the pool.
		 * <p>
		 * This operation corresponds to setting a value as used.
		 * <p>
		 * It returns false if the value is not free.
		 * <p>
		 * It does nothing to negative arguments.
		 * 
		 * @param value the value to be taken from the pool
		 * @return true if success
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
		 * @param value a value to be checked in the pool
		 * @return true if the value is used
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
		 * @param value a value to be checked in the pool
		 * @return true if the value is free
		 */
		public synchronized boolean isFree(int value) {
			return !this.isUsed(value);
		}

		/**
		 * Count the used values out of the pool
		 * 
		 * @return the count of used values
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
		 * @return the count of free values
		 */
		public synchronized int countFree() {
			return POOL_SIZE - this.countUsed();
		}

		/**
		 * Clear all bits of the byte array that represents the pool.
		 * <p>
		 * This corresponds to marking all pool values as free
		 */
		public synchronized void clearPool() {
			for (int i = 0; i < pool.length; i++) {
				pool[i] = 0;
			}
		}
	}
}
