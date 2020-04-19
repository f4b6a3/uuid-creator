/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.UuidTimeUtil;
import com.github.f4b6a3.uuid.util.sequence.AbstractSequence;

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
 * (4a) A high resolution timestamp can be simulated by keeping a count of the
 * number of UUIDs that have been generated with the same value of the system
 * time, and using it to construct the low order bits of the timestamp. The
 * count will range between zero and the number of 100-nanosecond intervals per
 * system time interval.
 *
 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
 * 
 * (3b) If a system overruns the generator by requesting too many UUIDs within a
 * single system time interval, the UUID service MUST either return an error, or
 * stall the UUID generator until the system clock catches up.
 * 
 */
public class DefaultTimestampStrategy extends AbstractSequence implements TimestampStrategy {

	protected long previousTimestamp = 0;
	protected boolean enableOverrunException = true;

	protected static final int COUNTER_MIN = 0;
	protected static final int COUNTER_MAX = 9_999;

	protected static final int COUNTER_OFFSET_MAX = 0xff; // 255

	protected static final String OVERRUN_MESSAGE = "The system overran the generator by requesting too many UUIDs.";

	public DefaultTimestampStrategy() {
		this(/* enableOverrunException = */ true);
	}

	public DefaultTimestampStrategy(boolean enableOverrunException) {
		super(COUNTER_MIN, COUNTER_MAX);
		this.enableOverrunException = enableOverrunException;
		this.value = RandomUtil.get().nextInt(COUNTER_OFFSET_MAX);
	}

	@Override
	public long getTimestamp() {

		final long timestamp = UuidTimeUtil.getCurrentTimestamp();
		final long counter = getNextCounter(timestamp);

		// (4a) simulate a high resolution timestamp
		return timestamp + counter;
	}

	/**
	 * Get the next counter value.
	 * 
	 * @param timestamp a timestamp
	 * @return the next counter value
	 * 
	 * @throws UuidCreatorException an overrun exception if more than 10 thousand
	 *                              UUIDs are requested within the same millisecond
	 */
	protected long getNextCounter(final long timestamp) {
		if (timestamp > this.previousTimestamp) {
			this.reset();
		}
		this.previousTimestamp = timestamp;
		return this.next();
	}

	@Override
	public int next() {
		if (this.value > maxValue) {
			this.value = minValue;
			// (3b) Too many requests!
			if (enableOverrunException) {
				throw new UuidCreatorException(OVERRUN_MESSAGE);
			}
		}
		return this.value++;
	}

	@Override
	public void reset() {
		this.value = this.value & COUNTER_OFFSET_MAX;
	}
}
