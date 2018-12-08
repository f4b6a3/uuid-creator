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

	// keeps count of values returned
	private int counter = 0;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;

	protected static final Random random = new Xorshift128PlusRandom();
	protected static final Logger LOGGER = Logger.getAnonymousLogger();

	public ClockSequence() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	/**
	 * Get the next value for a timestamp and a node identifier.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (6a) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
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
	 * @throws OverrunException
	 */
	public int getNextForTimestamp(long timestamp) throws OverrunException {
		if (timestamp <= this.timestamp) {
			// (3b) return an error if the clock sequence overruns.
			if (++this.counter > SEQUENCE_MAX) {
				counter = 0;
				throw new OverrunException("Too many requests.");
			}
			return this.next();
		}
		this.counter = 0;
		this.timestamp = timestamp;
		return this.current();
	}

	@Override
	public void reset() {
		this.value = random.nextInt(SEQUENCE_MAX);
	}
}