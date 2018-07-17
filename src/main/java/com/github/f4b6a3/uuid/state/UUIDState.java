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

package com.github.f4b6a3.uuid.state;

import java.io.Serializable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.f4b6a3.uuid.util.TimestampUtils;

/**
 * Class that stores the last status of a time-based creator (factory).
 * 
 * @author fabiolimace
 *
 */
public class UUIDState implements Serializable {

	private static final long serialVersionUID = 8918010224627940388L;

	/*
	 * -------------------------
	 * Private fields
	 * -------------------------
	 */
	private long timestamp = 0;
	private long nodeIdentifier = 0;
	private long counter = 0;
	private long sequence = 0;

	private boolean enableCounterIncrement = false;
	private boolean enableSequenceIncrement = false;
	private long lastOverranTimestamp = 0;
	
	/*
	 * -------------------------
	 * Private static fields
	 * -------------------------
	 */
	private static Random random;
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	/*
	 * -------------------------
	 * Private static constants
	 * -------------------------
	 */
	protected static final int COUNTER_MIN = 0;
	protected static final int COUNTER_MAX = 10_000;

	protected static final int SEQUENCE_MIN = 0x0000;
	protected static final int SEQUENCE_MAX = 0x3fff;

	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	
	public UUIDState() {
		resetSequence();
	}

	public UUIDState(long timestamp, long sequence, long counter, long nodeIdentifier) {
		this.timestamp = timestamp;
		this.sequence = sequence;
		this.counter = counter;
		this.nodeIdentifier = nodeIdentifier;
	}

	/* 
	 * -------------------------
	 * Public getters and setters
	 * -------------------------
	 */
	
	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		if (timestamp > this.timestamp || timestamp == 0) {
			this.timestamp = timestamp;
		}
	}

	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	public void setNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	public long getCounter() {
		return this.counter;
	}

	public void setCounter(long counter) {
		if (counter > this.counter || counter == COUNTER_MIN) {
			this.counter = counter;
		}
	}

	public long getSequence() {
		return this.sequence;
	}

	public void setSequence(long sequence) {
		if (sequence > this.sequence || sequence == SEQUENCE_MIN) {
			this.sequence = sequence;
		}
	}
	
	/* 
	 * -------------------------
	 * Public methods
	 * -------------------------
	 */
	
	/**
	 * Returns the counter value (a counter in the RFC-4122).
	 * 
	 * The counter range is between 0 and 10,000.
	 * 
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (3) If a system overruns the generator by requesting too many UUIDs
	 * within a single system time interval, the UUID service MUST either return
	 * an error, or stall the UUID generator until the system clock catches up.
	 * 
	 * (4) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @param timestamp
	 * @return
	 */
	public long getCurrentCounterValue(long timestamp) {

		// (4) increment the counter if timestemp is backwards or is repeated.
		// also increment the counter when the sequence is overrun.
		if (timestamp <= this.timestamp || this.enableCounterIncrement) {
			
			this.counter++;
			
			if (this.counter > COUNTER_MAX) {
				
				// (3) log a warning (just once per timestamp)
				if (timestamp > this.lastOverranTimestamp) {
					this.lastOverranTimestamp = timestamp;
					LOGGER.log(Level.WARNING,
							String.format("Timestamp counter overrun at \"%s\"", TimestampUtils.getInstant(timestamp)));
				}
				this.enableSequenceIncrement = true;
				this.counter = COUNTER_MIN;
			}
			
			this.enableCounterIncrement = false;
			return this.counter;
		}
		
		this.counter = COUNTER_MIN;
		return this.counter;
	}

	/**
	 * Returns the sequence value (clock-seq in the RFC-4122).
	 * 
	 * Clock sequence is a number defined by RFC-4122 used to prevent UUID
	 * collisions.
	 * 
	 * The first clock sequence is a random number between 0x8000 and 0xbfff.
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (1a) For UUID version 1, the clock sequence is used to help avoid
	 * duplicates that could arise when the clock is set backwards in time or if
	 * the node ID changes.
	 * 
	 * (2a) If the clock is set backwards, or might have been set backwards
	 * (e.g., while the system was powered off), and the UUID generator can not
	 * be sure that no UUIDs were generated with timestamps larger than the
	 * value to which the clock was set, then the clock sequence has to be
	 * changed. If the previous value of the clock sequence is known, it can
	 * just be incremented; otherwise it should be set to a random or
	 * high-quality pseudo-random value.
	 * 
	 * (3a) Similarly, if the node ID changes (e.g., because a network card has
	 * been moved between machines), setting the clock sequence to a random
	 * number minimizes the probability of a duplicate due to slight differences
	 * in the clock settings of the machines. If the value of clock sequence
	 * associated with the changed node ID were known, then the clock sequence
	 * could just be incremented, but that is unlikely.
	 * 
	 * (4a) The clock sequence MUST be originally (i.e., once in the lifetime of
	 * a system) initialized to a random number to minimize the correlation
	 * across systems. This provides maximum protection against node identifiers
	 * that may move or switch from system to system rapidly. The initial value
	 * MUST NOT be correlated to the node identifier.
	 * 
	 * @param timestamp
	 * @return
	 */
	public long getCurrentSequenceValue(long nodeIdentifier) {

		// (2a) increment sequence if timestemp is backwards or is repeated
		if (this.enableSequenceIncrement) {
			this.sequence++;
			if (this.sequence > SEQUENCE_MAX) {
				this.sequence = SEQUENCE_MIN;
				
				// force the counter to be incremented
				this.enableCounterIncrement = true;
			}
			this.enableSequenceIncrement = false;
			return this.sequence;
		}
		
		// (3a) set a random value to the sequence if the node ID has changed
		if (nodeIdentifier != this.nodeIdentifier) {
			resetSequence();
			return this.sequence;
		}

		return this.sequence;
	}
	
	@Override
	public UUIDState clone() {
		return new UUIDState(this.timestamp, this.sequence, this.counter, this.nodeIdentifier);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		return (other != null) && (other instanceof UUIDState) && (this.timestamp == ((UUIDState) other).getTimestamp())
				&& (this.sequence == ((UUIDState) other).getSequence())
				&& (this.counter == ((UUIDState) other).getCounter())
				&& (this.nodeIdentifier == ((UUIDState) other).getNodeIdentifier());
	}
	
	/* 
	 * -------------------------
	 * Private methods
	 * -------------------------
	 */
	
	/**
	 * Reset the sequence to a random number
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * (3) Similarly, if the node ID changes (e.g., because a network card has
	 * been moved between machines), setting the clock sequence to a random
	 * number minimizes the probability of a duplicate due to slight differences
	 * in the clock settings of the machines. If the value of clock sequence
	 * associated with the changed node ID were known, then the clock sequence
	 * could just be incremented, but that is unlikely.
	 */
	private void resetSequence() {
		if (random == null) {
			random = new Random();
		}
		this.sequence = random.nextInt(UUIDState.SEQUENCE_MAX - UUIDState.SEQUENCE_MIN) + UUIDState.SEQUENCE_MIN;
	}
}
