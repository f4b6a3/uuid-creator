/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.clockseq;

import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.sequence.AbstractSequence;
import com.github.f4b6a3.uuid.state.AbstractUuidState;
import com.github.f4b6a3.uuid.state.FileUuidState;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;

/**
 * This class is an implementation of the 'clock sequence' of the RFC-4122. The
 * maximum value of this sequence is 16,383 or 0x3fff.
 * 
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
public class DefaultClockSequenceStrategy extends AbstractSequence implements ClockSequenceStrategy {

	private long timestamp = 0;
	private long nodeIdentifier = 0;

	// keeps count of values returned
	private int counter = 0;

	protected AbstractUuidState state;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;

	/**
	 * This constructor uses a state stored previously.
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (1) For UUID version 1, the clock sequence is used to help avoid
	 * duplicates that could arise when the clock is set backwards in time or if
	 * the node ID changes.
	 * 
	 * (2) If the clock is set backwards, or might have been set backwards
	 * (e.g., while the system was powered off), and the UUID generator can not
	 * be sure that no UUIDs were generated with timestamps larger than the
	 * value to which the clock was set, then the clock sequence has to be
	 * changed. If the previous value of the clock sequence is known, it can
	 * just be incremented; otherwise it should be set to a random or
	 * high-quality pseudo-random value.
	 * 
	 * (3) Similarly, if the node ID changes (e.g., because a network card has
	 * been moved between machines), setting the clock sequence to a random
	 * number minimizes the probability of a duplicate due to slight differences
	 * in the clock settings of the machines. If the value of clock sequence
	 * associated with the changed node ID were known, then the clock sequence
	 * could just be incremented, but that is unlikely.
	 * 
	 * (4) The clock sequence MUST be originally (i.e., once in the lifetime of
	 * a system) initialized to a random number to minimize the correlation
	 * across systems. This provides maximum protection against node identifiers
	 * that may move or switch from system to system rapidly. The initial value
	 * MUST NOT be correlated to the node identifier.
	 * 
	 * @param timestamp
	 *            the current timestamp
	 * @param nodeIdentifier
	 *            the current node identifier
	 * @param state
	 *            the previous state saved
	 */
	public DefaultClockSequenceStrategy(long timestamp, long nodeIdentifier, AbstractUuidState state) {
		super(SEQUENCE_MIN, SEQUENCE_MAX);

		this.timestamp = timestamp;
		this.nodeIdentifier = nodeIdentifier;

		int defaultClockSequence = SettingsUtil.getClockSequence();

		if (SettingsUtil.isStateEnabled()) {
			
			if (state == null) {
				this.state = new FileUuidState();
			} else {
				this.state = state;
			}

			long lastTimestamp = this.state.getTimestamp();
			long lastNodeIdentifier = this.state.getNodeIdentifier();
			int lastClockSequence = this.state.getClockSequence();

			if (lastClockSequence != 0) {
				this.set(lastClockSequence);
				if ((this.timestamp <= lastTimestamp) || (this.nodeIdentifier != lastNodeIdentifier)) {
					this.next();
				}
			} else if (defaultClockSequence != 0) {
				this.set(defaultClockSequence);
			} else {
				this.reset();
			}
			
			// Add a hook for when the program exits or is terminated
			Runtime.getRuntime().addShutdownHook(new DefaultClockSequenceShutdownHook(this));
			
		} else {
			if (defaultClockSequence != 0) {
				this.set(defaultClockSequence);
			} else {
				this.reset();
			}
		}

	}

	public DefaultClockSequenceStrategy(long timestamp, long nodeIdentifier) {
		this(timestamp, nodeIdentifier, null);
	}

	public DefaultClockSequenceStrategy() {
		this(0, 0, null);
	}

	/**
	 * Get the next value for a timestamp and a node identifier.
	 * 
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (2a) If the clock is set backwards, or might have been set backwards
	 * (e.g., while the system was powered off), and the UUID generator can not
	 * be sure that no UUIDs were generated with timestamps larger than the
	 * value to which the clock was set, then the clock sequence has to be
	 * changed. If the previous value of the clock sequence is known, it can
	 * just be incremented; otherwise it should be set to a random or
	 * high-quality pseudo-random value.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (6b) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
	 * 
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (3c) If a system overruns the generator by requesting too many UUIDs
	 * within a single system time interval, the UUID service MUST either return
	 * an error, or stall the UUID generator until the system clock catches up.
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @param nodeIdentifier
	 *            a node identifier
	 * @return a clock sequence
	 * @throws OverrunException
	 *             an overrun exception
	 */
	@Override
	public int getClockSequence(long timestamp, long nodeIdentifier) {
		if (timestamp <= this.timestamp) {
			// (3c) return an error if the clock sequence overruns.
			if (this.counter >= SEQUENCE_MAX) {
				throw new OverrunException("Too many requests.");
			}
			this.counter++;
			this.timestamp = timestamp;
			return this.next();
		}
		counter = 0;
		this.timestamp = timestamp;
		return this.current();
	}

	@Override
	public void reset() {
		this.value = RandomUtil.nextInt(SEQUENCE_MAX);
	}

	protected void storeState() {
		if (SettingsUtil.isStateEnabled()) {
			this.state.setNodeIdentifier(nodeIdentifier);
			this.state.setTimestamp(timestamp);
			this.state.setClockSequence((int) this.value);
			this.state.store();
		}
	}

	protected static class DefaultClockSequenceShutdownHook extends Thread {
		private DefaultClockSequenceStrategy strategy;

		public DefaultClockSequenceShutdownHook(DefaultClockSequenceStrategy strategy) {
			this.strategy = strategy;
		}

		public void run() {
			this.strategy.storeState();
		}
	}
}