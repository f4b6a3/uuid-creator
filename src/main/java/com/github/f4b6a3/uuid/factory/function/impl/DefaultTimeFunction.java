/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

import com.github.f4b6a3.uuid.factory.function.TimeFunction;
import com.github.f4b6a3.uuid.util.internal.RandomUtil;

public final class DefaultTimeFunction implements TimeFunction {

	private long prevTime = -1;

	// start the counter with a random number between 0 and 9,999
	private long counter = Math.abs(RandomUtil.nextLong() % TICKS_PER_MILLI);

	@Override
	public long getAsLong() {

		// get the current time
		final long time = System.currentTimeMillis();

		if (time == prevTime) {
			// increment the counter
			if (++counter >= TICKS_PER_MILLI) {
				// if the counter goes beyond the limit:
				while (time == System.currentTimeMillis()) {
					// wait the time change for the next call
				}
			}
		} else {
			// reset to a number between 0 and 9,999
			counter = ++counter % TICKS_PER_MILLI;
		}

		// save time for the next call
		prevTime = time;

		// RFC-4122 - 4.2.1.2 (P4):
		// simulate high resolution
		return (time * TICKS_PER_MILLI) + counter;
	}
}
