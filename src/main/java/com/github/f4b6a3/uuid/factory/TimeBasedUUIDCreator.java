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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.state.UUIDState;
import com.github.f4b6a3.uuid.util.ByteUtils;
import com.github.f4b6a3.uuid.util.TimestampUtils;

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
	private UUIDState state;
	// Values to be provided by fluent interface methods
	private long fixedTimestamp;
	private long fixedNodeIdentifier;
	private long initialSequence;
	
	// default random generator: xorshift
	private Random random;
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	
	public TimeBasedUUIDCreator(int version) {
		super(version);
		random = new Xorshift128PlusRandom();
		this.state = new UUIDState(random);
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
		long timestamp = getTimestamp();

		// (4b) get the node identifier
		long nodeIdentifier = getNodeIdentifier();
		
		// (4b) get the counter value
		long counter = state.getCurrentCounterValue(timestamp);
		
		// (5a)(6a) get the sequence value
		long sequence = state.getCurrentSequenceValue(nodeIdentifier);
		
		// (7a) save the state
		state.setTimestamp(timestamp);
		state.setNodeIdentifier(nodeIdentifier);
		state.setCounter(counter);
		state.setSequence(sequence);
		
		// (9a) format the most significant bits
		long msb = getMostSignificantBits(timestamp, counter);
		
		// (9a) format the least significant bits
		long lsb = getLeastSignificantBits(nodeIdentifier, sequence);
		
		return new UUID(msb, lsb);
	}
	
	/* 
	 * --------------------------------
	 * Public fluent interface methods
	 * --------------------------------
	 */
	
	/**
	 * Set a fixed instant to generate UUIDs.
	 * 
	 * This method is intended to unit tests only. There's no use out of this
	 * purpose.
	 * 
	 * @param instant
	 * @return
	 */
	public TimeBasedUUIDCreator withFixedInstant(Instant instant) {
		this.fixedTimestamp = TimestampUtils.getTimestamp(instant);
		return this;
	}

	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Someone may think it's useful in some special case
	 * to use a fixed node identifier other than random value.
	 * 
	 * It may be useful in some cases that the real hardware address can't be
	 * found correctly by this class. In these cases someone can inform the MAC
	 * address with this method.
	 * 
	 * If you want to inform a fixed value that is NOT a real MAC address, use
	 * the method
	 * {@link TimeBasedUUIDCreator#withFixedMulticastNodeIdentifier(long)},
	 * which DOES change the multicast bit.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public TimeBasedUUIDCreator withFixedNodeIdentifier(long nodeIdentifier) {
		this.fixedNodeIdentifier = nodeIdentifier & 0x0000FFFFFFFFFFFFL;
		state.setNodeIdentifier(this.fixedNodeIdentifier);
		return this;
	}
	
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
	 * method {@link TimeBasedUUIDCreator#withFixedNodeIdentifier(long)}, which
	 * doesn't change the multicast bit.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public TimeBasedUUIDCreator withFixedMulticastNodeIdentifier(long nodeIdentifier) {
		return this.withFixedNodeIdentifier(setMulticastNodeIdentifier(nodeIdentifier));
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
		return this.withFixedNodeIdentifier(getHardwareAddressNodeIdentifier());
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
	public TimeBasedUUIDCreator withFixedInitialSequence(long sequence) {
		this.initialSequence = sequence & 0x0000000000003FFFL;
		state.setInitialSequence(this.initialSequence);
		return this;
	}
	
	/**
	 * Change the default random generator in a fluent way to another that extends {@link Random}.
	 * 
	 * The default random generator is {@link Xorshift128PlusRandom}.
	 * 
	 * @param random {@link Random}
	 */
	public TimeBasedUUIDCreator withRandomGenerator(Random random) {
		this.random = random;
		state.setRandom(this.random);
		return this;
	}
	
	/*
	 * ------------------------------------------
	 * Private methods for timestamps
	 * ------------------------------------------
	 */
	
	/*
	 * ------------------------------------------
	 * Private methods for node identifiers
	 * ------------------------------------------
	 */
	
	/**
	 * Returns a timestamp.
	 * 
	 * If no fixed value was informed or if there's no previous value, the
	 * timestamp value is returned.
	 * 
	 * @return
	 */
	private long getTimestamp() {
		
		if (this.fixedTimestamp != 0) {
			return this.fixedTimestamp;
		}
		
		if(this.state.getTimestamp() != 0) {
			return this.state.getTimestamp(); 
		}
		
		return TimestampUtils.getTimestamp(Instant.now());
	}
	
	/**
	 * Returns a node identifier.
	 * 
	 * If no fixed value was informed or if there's no previous value, a random
	 * value is returned.
	 * 
	 * @return
	 */
	private long getNodeIdentifier() {
		
		if (this.fixedNodeIdentifier != 0) {
			return this.fixedNodeIdentifier;
		}

		if (state.getNodeIdentifier() != 0) {
			return state.getNodeIdentifier();
		}

		return getRandomNodeIdentifier();
	}
	
	/**
	 * Returns a hardware address (MAC) as a node identifier.
	 * 
	 * If no hardware address is found, it returns a random node identifier.
	 * 
	 * @param state
	 * @return
	 */
	private long getHardwareAddressNodeIdentifier() {
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return ByteUtils.toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			// If exception occurs, return a random hardware address.
		}
		
		return getRandomNodeIdentifier();
	}
	
	/**
	 * Returns a random node identifier.
	 * 
	 * @param state
	 * @return
	 */
	private long getRandomNodeIdentifier() {
		return setMulticastNodeIdentifier(random.nextLong());
	}
	
	/**
	 * Sets the the multicast bit of a node identifier.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	private long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}
	
	/* 
	 * -------------------------------------
	 * Private methods for setting bits of the UUID parts
	 * -------------------------------------
	 */
	
	/**
	 * Returns the timestamp bits of the UUID.
	 * 
	 * @param timestamp
	 */
	private long getTimestampBits(long timestamp) {
		if(this.version == VERSION_1) {
			return getStandardTimestamp(timestamp);
		} else {
			return getSequentialTimestamp(timestamp);
		}
	}
	
	/**
	 * Returns the timestamp bits of the UUID in the natural order of bytes.
	 * 
	 * @param timestamp
	 */
	private long getSequentialTimestamp(long timestamp) {
		
		long himid = (timestamp & 0x0ffffffffffff000L) << 4;
		long low = (timestamp & 0x0000000000000fffL);
		
		return (himid | low);
	}

	/**
	 * Returns the timestamp bits of the UUID in the order defined in the
	 * RFC-4122.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Determine the values for the UTC-based timestamp and clock sequence to be
	 * used in the UUID, as described in Section 4.2.1.
	 * 
	 * For the purposes of this algorithm, consider the timestamp to be a 60-bit
	 * unsigned integer and the clock sequence to be a 14-bit unsigned integer.
	 * Sequentially number the bits in a field, starting with zero for the least
	 * significant bit.
	 * 
	 * "Set the time_low field equal to the least significant 32 bits (bits zero
	 * through 31) of the timestamp in the same order of significance.
	 * 
	 * Set the time_mid field equal to bits 32 through 47 from the timestamp in
	 * the same order of significance.
	 * 
	 * Set the 12 least significant bits (bits zero through 11) of the
	 * time_hi_and_version field equal to bits 48 through 59 from the timestamp
	 * in the same order of significance.
	 * 
	 * Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the 4-bit version number corresponding to
	 * the UUID version being created, as shown in the table above."
	 * 
	 * @param timestamp
	 */
	private long getStandardTimestamp(long timestamp) {

		long hii = (timestamp & 0x0fff000000000000L) >>> 48;
		long mid = (timestamp & 0x0000ffff00000000L) >>> 16;
		long low = (timestamp & 0x00000000ffffffffL) << 32;

		long msb = (low | mid | hii);
		
		return setVersionBits(msb);
	}
	
	/**
	 * Returns the most significant bits of the UUID.
	 * 
	 * It is a extension suggested by the RFC-4122.
	 * 
	 * {@link TimeBasedUUIDCreator#getStandardTimestamp(long)}
	 * 
	 * #### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (4) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @param counter
	 */
	private long getMostSignificantBits(long timestamp, long counter) {
		// (4) add the counter to the timestamp
		return getTimestampBits(timestamp + counter);
	}
	
	
	/**
	 * Returns the least significant bits of the UUID.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Set the clock_seq_low field to the eight least significant bits (bits
	 * zero through 7) of the clock sequence in the same order of significance.
	 * 
	 * Set the 6 least significant bits (bits zero through 5) of the
	 * clock_seq_hi_and_reserved field to the 6 most significant bits (bits 8
	 * through 13) of the clock sequence in the same order of significance.
	 * 
	 * Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * Set the node field to the 48-bit IEEE address in the same order of
	 * significance as the address.
	 * 
	 * @param nodeIdentifier
	 */
	private long getLeastSignificantBits(long nodeIdentifier, long sequence) {
		
		long seq = sequence << 48;
		seq = setVariantBits(seq);
		
		long nod = nodeIdentifier & 0x0000ffffffffffffL;
		
		return (seq | nod);
	}

}