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

package com.github.f4b6a3.uuid.factory.function.impl;

import static com.github.f4b6a3.uuid.factory.function.ClockSeqFunction.ClockSeqPool.POOL_MIN;
import static com.github.f4b6a3.uuid.factory.function.ClockSeqFunction.ClockSeqPool.POOL_MAX;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;

public final class DefaultClockSeqFunction implements ClockSeqFunction {

	private AtomicInteger sequence;
	private long lastTimestamp = -1;

	protected static final ClockSeqPool POOL = new ClockSeqPool();

	public DefaultClockSeqFunction() {
		final int initial = POOL.random();
		this.sequence = new AtomicInteger(initial);
	}

	/**
	 * Returns the next value for a clock sequence.
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (P2) If the clock is set backwards, or might have been set backwards (e.g.,
	 * while the system was powered off), and the UUID generator can not be sure
	 * that no UUIDs were generated with timestamps larger than the value to which
	 * the clock was set, then the clock sequence has to be changed. If the previous
	 * value of the clock sequence is known, it can just be incremented; otherwise
	 * it should be set to a random or high-quality pseudo-random value.
	 * 
	 * @param timestamp a timestamp
	 * @return a clock sequence
	 */
	@Override
	public long applyAsLong(final long timestamp) {
		if (timestamp > this.lastTimestamp) {
			this.lastTimestamp = timestamp;
			return this.sequence.get();
		}
		this.lastTimestamp = timestamp;
		return this.next();
	}

	public int next() {
		if (this.sequence.incrementAndGet() > POOL_MAX) {
			this.sequence.set(POOL_MIN);
		}
		return this.sequence.updateAndGet(POOL::take);
	}
}
