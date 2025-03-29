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

import static com.github.f4b6a3.uuid.util.UuidTime.TICKS_PER_MILLI;

import java.time.Clock;
import java.util.SplittableRandom;

import com.github.f4b6a3.uuid.factory.function.TimeFunction;

/**
 * Function that returns a number of 100-nanoseconds since 1970-01-01 (Unix
 * epoch).
 * <p>
 * It can advance 1ms or more ahead of system clock on heavy load.
 * 
 * @see TimeFunction
 */
public final class DefaultTimeFunction implements TimeFunction {

	private final Clock clock;

	private long lastTime = -1;

	// let go up to 1 second ahead of system clock
	private static final long advanceMax = 1_000L;

	// start the counter with a random number between 0 and 9,999
	private long counter = Math.abs(new SplittableRandom().nextLong()) % TICKS_PER_MILLI;
	// start the counter limit with a number between 10,000 and 19,999
	private long counterMax = counter + TICKS_PER_MILLI;

	/**
	 * Default constructor.
	 */
	public DefaultTimeFunction() {
		this.clock = Clock.systemUTC();
	}

	/**
	 * Default constructor with a {@link Clock} instance.
	 * 
	 * @param clock a clock
	 */
	public DefaultTimeFunction(Clock clock) {
		this.clock = clock;
	}

	@Override
	public long getAsLong() {

		counter++; // always increment

		// get current system time
		long time = clock.millis();

		// is it not too much ahead of system clock?
		if (advanceMax > Math.abs(lastTime - time)) {
			time = Math.max(lastTime, time);
		}

		// check time change
		if (time == lastTime) {
			// if the time repeats,
			// check the counter limit
			if (counter >= counterMax) {
				time++; // must go ahead of system clock
				// reset to a number between 0 and 9,999
				counter = counter % TICKS_PER_MILLI;
				// reset to a number between 10,000 and 19,999
				counterMax = counter + TICKS_PER_MILLI;
			}
		} else {
			// reset to a number between 0 and 9,999
			counter = counter % TICKS_PER_MILLI;
			// reset to a number between 10,000 and 19,999
			counterMax = counter + TICKS_PER_MILLI;
		}

		// save time for the next call
		lastTime = time;

		// simulate a high resolution clock
		return (time * TICKS_PER_MILLI) + counter;
	}
}