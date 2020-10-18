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

package com.github.f4b6a3.uuid.strategy.timestamp;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.util.TlsSecureRandom;
import com.github.f4b6a3.uuid.util.UuidTime;

/**
 * Strategy that provides the current timestamp.
 * 
 * This is an implementation of {@link TimestampStrategy} that provides
 * millisecond resolution. The timestamp resolution is simulated by adding the
 * next value of a counter that is incremented at every call to the method
 * {@link TimestampStrategy#getTimestamp()}.
 * 
 * The counter's range is from 0 to 9,999, that is, the number of 100-nanosecond
 * intervals per millisecond.
 * 
 * The counter is initialized with a random number between 0 and 255 to void
 * duplicates in the case of multiple time-based generators running in parallel.
 * 
 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
 * 
 * (P1) The timestamp is generated from the system time, whose resolution may be
 * less than the resolution of the UUID timestamp.
 * 
 * (P2) If UUIDs do not need to be frequently generated, the timestamp can
 * simply be the system time multiplied by the number of 100-nanosecond
 * intervals per system time interval.
 * 
 * (P3) If a system overruns the generator by requesting too many UUIDs within a
 * single system time interval, the UUID service MUST either return an error, or
 * stall the UUID generator until the system clock catches up.
 * 
 * (P4) A high resolution timestamp can be simulated by keeping a COUNT of the
 * number of UUIDs that have been generated with the same value of the system
 * time, and using it to construct the low order bits of the timestamp. The
 * COUNT will range between zero and the number of 100-nanosecond intervals per
 * system time interval.
 * 
 * (P5) Note: If the processors overrun the UUID generation frequently,
 * additional node identifiers can be allocated to the system, which will permit
 * higher speed allocation by making multiple UUIDs potentially available for
 * each time stamp value.
 */
public final class DefaultTimestampStrategy implements TimestampStrategy {

	private AtomicInteger counter;
	private long lastTimestamp = 0;

	// RFC-4122 - 4.2.1.2 (P2):
	// the number of 100-nanosecond intervals per system interval
	protected static final int COUNTER_MAX = 10_000;

	private static final int COUNTER_INITIAL_MASK = 0xff; // 255

	public DefaultTimestampStrategy() {
		// Initiate the counter with a number between 0 and 255
		int initial = TlsSecureRandom.get().nextInt() & COUNTER_INITIAL_MASK;
		this.counter = new AtomicInteger(initial);
	}

	/**
	 * Get the next current timestamp.
	 * 
	 * The timestamp has millisecond accuracy. An internal counter is added to the
	 * timestamp to simulate the resolution of 100-nanoseconds.
	 * 
	 * @return the current timestamp
	 */
	@Override
	public long getTimestamp() {

		long timestamp = UuidTime.getCurrentTimestamp();

		if (timestamp == this.lastTimestamp) {
			if (this.counter.incrementAndGet() >= COUNTER_MAX) {
				this.resetCounter();
				// RFC-4122 - 4.2.1.2 (P3):
				// Too many UUIDs within a single system time interval
				timestamp = nextTimestamp(timestamp);
			}
		} else {
			this.resetCounter();
		}

		this.lastTimestamp = timestamp;
		// RFC-4122 - 4.2.1.2 (P4):
		// simulate high resolution timestamp
		return timestamp + this.counter.get();
	}

	/**
	 * Stall the creator until the system clock catches up.
	 */
	private long nextTimestamp(long timestamp) {
		while (timestamp <= this.lastTimestamp) {
			timestamp = UuidTime.getCurrentTimestamp();
		}
		return timestamp;
	}

	/**
	 * Resets and returns the counter value to a number between 0 and 255.
	 * 
	 * @return the a value between 0 and 255.
	 */
	private void resetCounter() {
		this.counter.updateAndGet(x -> ++x & COUNTER_INITIAL_MASK);
	}
}
