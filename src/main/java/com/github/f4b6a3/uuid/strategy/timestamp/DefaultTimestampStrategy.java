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

import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.internal.SharedRandom;

/**
 * Strategy that provides the current timestamp.
 * 
 * This is an implementation of {@link TimestampStrategy} that provides
 * millisecond resolution. The timestamp resolution is simulated by adding the
 * next value of a accumulator that is calculated at every call to the method
 * {@link TimestampStrategy#getTimestamp()}.
 * 
 * The accumulator's range is from 0 to 9,999, that is, the number of
 * 100-nanosecond intervals per millisecond.
 * 
 * The accumulator is initialized with a random number between 0 and 255 to void
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

	// initiate the previous time and tick
	private long prevTime = UuidTime.getCurrentTimestamp();
	private long prevTick = System.nanoTime() / TICK_UNIT;

	// initiate the accumulator with a number between 0 and 255
	private long accumulator = SharedRandom.nextLong() & ACCUMULATOR_RESET;

	// a tick is equivalent to a 100-nanosecond interval
	private static final long TICK_UNIT = 100; // 1 tick = 100ns

	// the accumulator can count a maximum of 10,000 ticks
	private static final long ACCUMULATOR_LIMIT = 10_000; // 10,000 ticks = 1ms

	// the accumulator must be initiated or reset to a number between 0 and 255
	private static final long ACCUMULATOR_RESET = 255; // 255 ticks = 0xff

	@Override
	public long getTimestamp() {

		// get the current time and tick
		final long time = UuidTime.getCurrentTimestamp();
		final long tick = System.nanoTime() / TICK_UNIT;

		if (time == prevTime) {
			// if the time is the same:
			// calculate the elapsed ticks since the last call
			final long elapsed = (tick - prevTick);
			if (elapsed > 1L && elapsed < (ACCUMULATOR_LIMIT - accumulator)) {
				// if the elapsed ticks are between the valid range:
				// add the elapsed ticks to the accumulator
				accumulator += elapsed;
			} else {
				// otherwise, increment the accumulator
				accumulator++;
			}

			if (accumulator >= ACCUMULATOR_LIMIT) {
				// if the accumulator goes beyond the limit:
				while (time == UuidTime.getCurrentTimestamp()) {
					// wait the time change for the next call
				}
			}
		} else {
			// reset the accumulator to a number between 0 and 255
			accumulator = ++accumulator & ACCUMULATOR_RESET;
		}

		// save time and tick for the next call
		prevTime = time;
		prevTick = tick;

		// RFC-4122 - 4.2.1.2 (P4):
		// simulate high resolution
		return time + accumulator;
	}
}
