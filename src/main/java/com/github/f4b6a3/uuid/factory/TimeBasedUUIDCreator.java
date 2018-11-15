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

package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

import com.github.f4b6a3.uuid.increment.ClockSequence;
import com.github.f4b6a3.uuid.increment.TimestampCounter;
import com.github.f4b6a3.uuid.strategy.lsb.LeastSignificantBitsStrategy;
import com.github.f4b6a3.uuid.strategy.lsb.TimeBasedLeastSignificantBitsStrategy;
import com.github.f4b6a3.uuid.strategy.msb.MostSignificantBitsStrategy;
import com.github.f4b6a3.uuid.strategy.msb.TimeBasedMostSignificantBitsStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.HardwareAddressNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.MulticastNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.MillisecondsTimestampStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.TimestampStrategy;

/**
 * Factory that create time-based UUIDs version 0 and 1.
 * 
 * The version 0 is a extension of the RFC-4122. It's just a modification of the
 * UUID version 1 that sorts the timestamp bytes to the natural order.
 * 
 * @author fabiolimace
 *
 */
public class TimeBasedUUIDCreator extends UUIDCreator {

	private static final long serialVersionUID = -2701950542372522119L;
	
	/*
	 * -------------------------
	 * Private fields
	 * -------------------------
	 */
	// State fields
	
	// Strategies
	protected TimestampStrategy timestampStrategy;
	protected NodeIdentifierStrategy nodeIdentifierStrategy;
	protected MostSignificantBitsStrategy mostSignificantBitsStrategy;
	protected LeastSignificantBitsStrategy leastSignificantBitsStrategy;
	
	// incrementable fields
	protected TimestampCounter timestampCounter;
	protected ClockSequence clockSequence;
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	public TimeBasedUUIDCreator() {
		super(VERSION_1);
		
		this.timestampStrategy = new MillisecondsTimestampStrategy();
		this.nodeIdentifierStrategy = new HardwareAddressNodeIdentifierStrategy();
		this.mostSignificantBitsStrategy = new TimeBasedMostSignificantBitsStrategy();
		this.leastSignificantBitsStrategy = new TimeBasedLeastSignificantBitsStrategy();
		
		this.timestampCounter = new TimestampCounter();
		this.clockSequence = new ClockSequence();
	}
	
	/* 
	 * -------------------------
	 * Public methods
	 * -------------------------
	 */

	/**
	 * Returns a new time-based UUID.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (1a) Obtain a system-wide global lock
	 * 
	 * (2a) From a system-wide shared stable store (e.g., a file), read the UUID
	 * generator state: the values of the timestamp, clock sequence, and node ID
	 * used to generate the last UUID.
	 * 
	 * (3a) Get the current time as a 60-bit count of 100-nanosecond intervals
	 * since 00:00:00.00, 15 October 1582.
	 * 
	 * (4a) Get the current node ID.
	 * 
	 * (5a) If the state was unavailable (e.g., non-existent or corrupted), or
	 * the saved node ID is different than the current node ID, generate a
	 * random clock sequence value.
	 * 
	 * (6a) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
	 * 
	 * (7a) Save the state (current timestamp, clock sequence, and node ID) back
	 * to the stable store.
	 * 
	 * (8a) Release the global lock.
	 * 
	 * (9a) Format a UUID from the current timestamp, clock sequence, and node
	 * ID values according to the steps in Section 4.2.2.
	 *  
	 * @return {@link UUID}
	 */
	public synchronized UUID create() {
		
		// (3a) get the timestamp
		long timestamp = timestampStrategy.getTimestamp();
		//long timestamp = 0;

		// (4b) get the node identifier
		long nodeIdentifier = nodeIdentifierStrategy.getNodeIdentifier();
		
		// (4b) get the counter value
		long counter = timestampCounter.getNextFor(timestamp);
		
		// (5a)(6a) get the sequence value
		long sequence = clockSequence.getNextFor(timestamp, nodeIdentifier);
		
		// (9a) format the most significant bits
		long msb = this.mostSignificantBitsStrategy.getMostSignificantBits(timestamp, counter);
		
		// (9a) format the least significant bits
		long lsb = this.leastSignificantBitsStrategy.getLeastSignificantBits(nodeIdentifier, sequence);
		
		return new UUID(msb, lsb);
	}
	
	/* 
	 * --------------------------------
	 * Public fluent interface methods
	 * --------------------------------
	 */
	
	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Someone may think it's useful in some special case
	 * to use a fixed node identifier other than random value.
	 * 
	 * This method will always set the multicast bit to indicate that the value
	 * is not a real MAC address.
	 * 
	 * If you want to inform a fixed value that is real MAC address, use the
	 * method {@link TimeBasedUUIDCreator#withMulticastNodeIdentifier(long)}, which
	 * doesn't change the multicast bit.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public TimeBasedUUIDCreator withMulticastNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifierStrategy = new MulticastNodeIdentifierStrategy(nodeIdentifier);
		return this;
	}
	
	public TimeBasedUUIDCreator withMulticastNodeIdentifier() {
		this.nodeIdentifierStrategy = new MulticastNodeIdentifierStrategy();
		return this;
	}
	
	/**
	 * Set the node identifier to be a real hardware address of the host
	 * machine.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Today to use a real hardware address is
	 * not recommended anymore. But someone may prefer to use a real MAC
	 * address.
	 * 
	 * @return
	 */
	public TimeBasedUUIDCreator withHardwareAddressNodeIdentifier() {
		this.nodeIdentifierStrategy = new HardwareAddressNodeIdentifierStrategy();
		return this;
	}
	
	/**
	 * Set a fixed initial sequence value to generate UUIDs.
	 * 
	 * The sequence has a range from 0 to 16,383.
	 * 
	 * Every time a factory is instantiated a random value is set to the sequence
	 * by default. This method allows someone to change this value with a
	 * desired one. It is called "initial" because during the lifetime of the
	 * factory instance, this value may be incremented or even replaced by
	 * another random value to avoid repetition of UUIDs.
	 * 
	 * @param sequence
	 * @return
	 */
	public TimeBasedUUIDCreator withInitialClockSequence(int sequence) {
		this.clockSequence = new ClockSequence(sequence);
		return this;
	}
}