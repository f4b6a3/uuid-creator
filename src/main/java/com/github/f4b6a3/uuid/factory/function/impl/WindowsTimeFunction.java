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

import static com.github.f4b6a3.uuid.util.UuidTime.TICKS_PER_MILLI;

import java.time.Clock;

import com.github.f4b6a3.uuid.factory.function.TimeFunction;
import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * Function that returns a number of 100-nanoseconds since 1970-01-01 (Unix
 * epoch).
 * <p>
 * This function is for WINDOWS systems.
 * <p>
 * On WINDOWS, the typical system time granularity is 15.625ms due to a default
 * 64Hz timer frequency.
 * <p>
 * It can advance be up to 48ms ahead of system time.
 * 
 * @see TimeFunction
 */
public final class WindowsTimeFunction implements TimeFunction {

	private final Clock clock;

	private long lastTime = -1;

	// arbitrary granularity greater than 15ms
	private static final long GRANULARITY = 16;
	private static final long TICKS_PER_GRANULARITY = TICKS_PER_MILLI * GRANULARITY;

	// start the counter with a random number between 0 and 159,999
	private long counter = Math.abs(RandomUtil.nextLong() % TICKS_PER_GRANULARITY);
	// start the counter limit with a number between 160,000 and 319,999
	private long counterMax = counter + TICKS_PER_GRANULARITY;

	/**
	 * Default constructor.
	 */
	public WindowsTimeFunction() {
		this.clock = Clock.systemUTC();
	}

	/**
	 * Constructor with a clock.
	 * 
	 * @param clock a clock
	 */
	public WindowsTimeFunction(Clock clock) {
		this.clock = clock;
	}

	@Override
	public long getAsLong() {

		counter++; // always increment

		// get the calculated time
		long time = calculatedMillis();

		// check time change
		if (time == lastTime) {
			// if the time repeats,
			// check the counter limit
			if (counter >= counterMax) {
				// if the counter goes beyond the limit,
				while (time == lastTime) {
					// wait the time to advance
					time = calculatedMillis();
				}
				// reset to a number between 0 and 159,999
				counter = counter % TICKS_PER_GRANULARITY;
				// reset to a number between 160,000 and 319,999
				counterMax = counter + TICKS_PER_GRANULARITY;
			}
		} else {
			// reset to a number between 0 and 159,999
			counter = counter % TICKS_PER_GRANULARITY;
			// reset to a number between 160,000 and 319,999
			counterMax = counter + TICKS_PER_GRANULARITY;
		}

		// save time for the next call
		lastTime = time;

		// RFC-4122 - 4.2.1.2 (P4):
		// simulate a high resolution clock
		return (time * TICKS_PER_MILLI) + counter;
	}

	/**
	 * Returns the calculated time in milliseconds.
	 * 
	 * It can be 16ms ahead of system time due to time granularity.
	 * 
	 * @return the calculated time
	 */
	private long calculatedMillis() {
		final long time = clock.millis();
		return time + GRANULARITY - (time % GRANULARITY);
	}
}