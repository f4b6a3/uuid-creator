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

package com.github.f4b6a3.uuid.factory.function.impl;

import static com.github.f4b6a3.uuid.factory.function.ClockSeqFunction.ClockSeqPool.POOL_MIN;
import static com.github.f4b6a3.uuid.factory.function.ClockSeqFunction.ClockSeqPool.POOL_MAX;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;

/**
 * Function that returns a clock sequence.
 * 
 * @see ClockSeqFunction
 * @see ClockSeqFunction.ClockSeqPool
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
		if (this.sequence.incrementAndGet() > POOL_MAX) {
			this.sequence.set(POOL_MIN);
		}
		return this.sequence.updateAndGet(POOL::take);
	}
}
