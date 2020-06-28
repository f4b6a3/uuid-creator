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

import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.sequence.AbstractSequence;

/**
 * Strategy that provides the current clock sequence.
 * 
 * This class is an implementation of the 'clock sequence' defined in the
 * RFC-4122. The maximum value of this sequence is 16,383 or 0x3fff.
 */
public final class DefaultClockSequenceStrategy extends AbstractSequence implements ClockSequenceStrategy {

	private long previousTimestamp = 0;

	protected static final int SEQUENCE_MIN = 0x00000000;
	protected static final int SEQUENCE_MAX = 0x00003fff;

	public static final ClockSequenceController CONTROLLER = new ClockSequenceController();

	/**
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (1) For UUID version 1, the clock sequence is used to help avoid duplicates
	 * that could arise when the clock is set backwards in time or if the node ID
	 * changes.
	 * 
	 * (2) If the clock is set backwards, or might have been set backwards (e.g.,
	 * while the system was powered off), and the UUID generator can not be sure
	 * that no UUIDs were generated with timestamps larger than the value to which
	 * the clock was set, then the clock sequence has to be changed. If the previous
	 * value of the clock sequence is known, it can just be incremented; otherwise
	 * it should be set to a random or high-quality pseudo-random value.
	 * 
	 * (3) Similarly, if the node ID changes (e.g., because a network card has been
	 * moved between machines), setting the clock sequence to a random number
	 * minimizes the probability of a duplicate due to slight differences in the
	 * clock settings of the machines. If the value of clock sequence associated
	 * with the changed node ID were known, then the clock sequence could just be
	 * incremented, but that is unlikely.
	 * 
	 * (4) The clock sequence MUST be originally (i.e., once in the lifetime of a
	 * system) initialized to a random number to minimize the correlation across
	 * systems. This provides maximum protection against node identifiers that may
	 * move or switch from system to system rapidly. The initial value MUST NOT be
	 * correlated to the node identifier.
	 * 
	 */
	public DefaultClockSequenceStrategy() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.value = -1; // set an out of range value before calling reset()
		this.reset();
	}

	/**
	 * Returns the next value for a clock sequence.
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (2a) If the clock is set backwards, or might have been set backwards (e.g.,
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
	public int getClockSequence(final long timestamp) {

		if (timestamp > this.previousTimestamp) {
			this.previousTimestamp = timestamp;
			return this.current();
		}

		this.previousTimestamp = timestamp;
		return this.next();
	}

	@Override
	public int next() {
		final int give = this.current();
		final int take = super.next();
		this.value = CONTROLLER.borrow(give, take);
		return this.value;
	}

	@Override
	public void reset() {
		final int give = this.current();
		final int take = RandomUtil.get().nextInt() & SEQUENCE_MAX;
		this.value = CONTROLLER.borrow(give, take);
	}
}