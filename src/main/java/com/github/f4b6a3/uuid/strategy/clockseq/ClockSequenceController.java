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

package com.github.f4b6a3.uuid.strategy.clockseq;

/**
 * Class that controls the usage of clock sequence values from 0 to 16383.
 * 
 * It manages a pool of 16384 values. The pool is implemented as an array of
 * 2048 bytes (16384 bits). Each bit of this array corresponds to a pool value.
 */
public class ClockSequenceController {

	protected byte[] pool = new byte[2048];
	protected static final int POOL_MAX = 0x3fff + 1; // 16384

	/**
	 * Borrow a value from the pool and give back another value to the same
	 * pool.
	 * 
	 * If the value to be borrowed is already in use, it is incremented until a
	 * free value is found and returned.
	 * 
	 * In the case that all pool values are in use, the pool is cleared and the
	 * last incremented value is returned.
	 * 
	 * It does nothing to negative arguments.
	 * 
	 * @param give
	 *            value to be given back to the pool
	 * @param take
	 *            value to be taken from the pool
	 * @return the value to be borrowed if not used.
	 */
	public synchronized int borrow(int give, int take) {

		for (int i = 0; i < POOL_MAX; i++) {
			if (setBit(take)) {
				clearBit(give);
				return take;
			}
			take = ++take % POOL_MAX;
		}
		clearPool();
		setBit(take);
		return take;
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
	 * @param value
	 *            the value to be taken from the pool
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
	 * Clear a bit from the byte array that represents the pool.
	 * 
	 * This operation corresponds to setting a value as free.
	 * 
	 * It does nothing to negative arguments.
	 * 
	 * @param value
	 *            the value to be taken up from the pool.
	 */
	private synchronized void clearBit(int value) {

		if (value < 0) {
			return;
		}

		final int byteIndex = value / 8;
		final int bitIndex = value % 8;

		final int mask = (~(1 << bitIndex));

		pool[byteIndex] = (byte) (pool[byteIndex] & mask);
	}

	/**
	 * Check if a value is used out of the pool.
	 * 
	 * @param value
	 *            a value to be checked in the pool.
	 * @return true if the value is used.
	 */
	public boolean isUsed(int value) {

		final int byteIndex = value / 8;
		final int bitIndex = value % 8;

		final int mask = (0x00000001 << bitIndex);
		boolean clear = (pool[byteIndex] & mask) == 0;

		return !clear;
	}

	/**
	 * Check if a value is free in the pool.
	 * 
	 * @param value
	 *            a value to be checked in the pool.
	 * @return true if the value is free.
	 */
	public boolean isFree(int value) {
		return !this.isUsed(value);
	}

	/**
	 * Count the used values out of the pool.
	 * 
	 * @return the count of used values.
	 */
	public int countUsed() {
		int counter = 0;
		for (int i = 0; i < POOL_MAX; i++) {
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
	public int countFree() {
		return POOL_MAX - this.countUsed();
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