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

import com.github.f4b6a3.uuid.state.UUIDState;
import com.github.f4b6a3.uuid.util.ByteUtils;
import com.github.f4b6a3.uuid.util.TimestampUtils;
import com.github.f4b6a3.uuid.util.UUIDUtils;

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
	private Instant initialInstant;
	private long nodeIdentifier;
	private long sequence;

	/*
	 * -------------------------
	 * Private static fields
	 * -------------------------
	 */
	private static Random random;
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	
	public TimeBasedUUIDCreator(int version) {
		super(version);
		this.state = new UUIDState();
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
	 * #### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (4b) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @return {@link UUID}
	 */
	@Override
	public synchronized UUID create() {
		
		// apply parameters provaded via fluent interface
		if (initialInstant == null) {
			initialInstant = Instant.now();
		}
		if (this.sequence != 0) {
			state.setSequence(this.sequence);
		}
		if (this.nodeIdentifier != 0) {
			state.setNodeIdentifier(this.nodeIdentifier);
		}
		
		// most and least significant bits of UUID
		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;
		
		// parts of the UUID
		long timestamp = 0x0000000000000000L;
		long nodeIdentifier = 0x0000000000000000L;
		long counter = 0x0000000000000000L;
		long sequence = 0x0000000000000000L;
		
		// (3a) get the timestamp
		timestamp = TimestampUtils.getTimestamp(initialInstant);
		
		// (4b) get the node identifier
		nodeIdentifier = getNodeIdentifier();
		
		// (4b) get the counter value and add to the timestamp
		counter = state.getCurrentCounterValue(timestamp);
		timestamp = timestamp + counter;
		
		// (5a)(6a) get the sequence value
		sequence = state.getCurrentSequenceValue(timestamp, nodeIdentifier);
		
		// (7a) save the state
		state.setTimestamp(timestamp);
		state.setNodeIdentifier(nodeIdentifier);
		state.setCounter(counter);
		state.setSequence(sequence);
		
		// (9a) format the most significant bits
		msb = getTimestampBits(timestamp);
		msb = getCounterBits(msb, counter);
		
		// (9a) format the least significant bits
		lsb = getSequenceBits(lsb, sequence);
		lsb = getNodeIdentifierBits(lsb, nodeIdentifier);
		
		return new UUID(msb, lsb);
	}
	
	/* 
	 * --------------------------------
	 * Public fluent interface methods
	 * --------------------------------
	 */
	
	public TimeBasedUUIDCreator withInstant(Instant instant) {
		this.initialInstant = instant;
		return this;
	}
	
	public TimeBasedUUIDCreator withNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
		return this;
	}

	public TimeBasedUUIDCreator withInitialSequence(long sequence) {
		this.sequence = sequence;
		return this;
	}
	
	/*
	 * ------------------------------------------
	 * Public static methods for node identifiers
	 * ------------------------------------------
	 */
	
	/**
	 * Returns a hardware address (MAC) as a node identifier.
	 * 
	 * If no hardware address is found, it returns a random node identifier.
	 * 
	 * @param state
	 * @return
	 */
	public static long getHardwareAddressNodeIdentifier() {
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
	public static long getRandomNodeIdentifier() {
		
		if(random == null) {
			 random = new Random();
		}
		
		long node = random.nextLong();
		return setMulticastNodeIdentifier(node);
	}
	
	/*
	 * ------------------------------------------
	 * Private static methods for node identifiers
	 * ------------------------------------------
	 */
	
	/**
	 * Returns a node identifier.
	 * 
	 * If the UUID version is 1, it returns the hardware address of the machine.
	 * If the hardware address counldnt be fould, it returns a random number.
	 * 
	 * If the UUID version is 0, it returns a random number.
	 * 
	 * @return
	 */
	private long getNodeIdentifier() {

		if (state.getNodeIdentifier() != 0) {
			return state.getNodeIdentifier();
		}
		
		if (version == VERSION_1) {
			return getHardwareAddressNodeIdentifier();
		} else {
			return getRandomNodeIdentifier();
		}
	}
	
	/**
	 * Sets the the multicast bit of a node identifier.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	private static long setMulticastNodeIdentifier(long nodeIdentifier) {
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
	 * {@link TimeBasedUUIDCreator#insertStandardTimestamp(long)}
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
		
		long msb = (himid | low);
		
		return msb;
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
		msb = setVersionBits(msb);
		
		return msb;
	}
	
	/**
	 * Returns the first clock sequence bits of the UUID.
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
	 * @param sequence
	 */
	private long getSequenceBits(long lsb, long sequence) {
		
		long nod = lsb & 0x0000ffffffffffffL;
		long seq = sequence << 48;
		
		lsb = (seq | nod); 
		lsb = setVariantBits(lsb);

		return lsb;
	}

	/**
	 * Returns the second clock sequence bits of the UUID.
	 * 
	 * It is a extension suggested by the RFC-4122.
	 *  
	 * @param counter
	 */
	private long getCounterBits(long msb, long counter) {
		UUID temp = new UUID(msb, 0);
		long timestamp = UUIDUtils.extractTimestamp(temp);
		return getTimestampBits(timestamp + counter);
	}
	
	/**
	 * Returns the node identifier bits of the UUID.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Set the node field to the 48-bit IEEE address in the same order of
	 * significance as the address.
	 * 
	 * @param nodeIdentifier
	 */
	private long getNodeIdentifierBits(long lsb, long nodeIdentifier) {
		
		long clkSeq = lsb & 0xffff000000000000L;
		long nodeId = nodeIdentifier & 0x0000ffffffffffffL;

		return (clkSeq | nodeId);
	}
}