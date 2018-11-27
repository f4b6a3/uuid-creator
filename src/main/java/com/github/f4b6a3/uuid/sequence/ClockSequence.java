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

package com.github.f4b6a3.uuid.sequence;

import java.util.Random;
import java.util.logging.Logger;

import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

/**
 * This class is an implementation of the 'clock sequence' of the RFC-4122. The
 * maximum value of this sequence is 16,383 or 0x3fff.
 */
public class ClockSequence extends AbstractSequence {

	// keeps the previous timestamp
	private long timestamp = 0;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;

	protected static final Random random = new Xorshift128PlusRandom();
	protected static final Logger LOGGER = Logger.getAnonymousLogger();

	protected boolean overrunChecking = true;

	public ClockSequence() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	/**
	 * Disables the overrun exception throwing when necessary.
	 * 
	 * By default an {@link OverrunException} is thrown whenever the clock
	 * sequence is overrun during runtime.
	 * 
	 * @param enabled
	 * @return
	 */
	public void setOverrunChecking(boolean enabled) {
		this.overrunChecking = enabled;
	}

	/**
	 * Get the next value for a timestamp and a node identifier.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (6a) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
	 * 
	 * 
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (3b) If a system overruns the generator by requesting too many UUIDs
	 * within a single system time interval, the UUID service MUST either return
	 * an error, or stall the UUID generator until the system clock catches up.
	 * 
	 * @param timestamp
	 * @param nodeIdentifier
	 * @return
	 */
	public int getNextForTimestamp(long timestamp) {
		if (timestamp <= this.timestamp) {
			if (this.overrunChecking) {
				// (3b) return an error if the clock sequence overruns.
				if (this.value >= this.MAX_VALUE) {
					throw new OverrunException(
							String.format("UUID clock sequence overrun for timestamp '%s'.", timestamp));
				}
			}
			return this.next();
		}
		this.timestamp = timestamp;
		return this.current();
	}

	@Override
	public void reset() {
		this.value = random.nextInt(SEQUENCE_MAX);
	}

}