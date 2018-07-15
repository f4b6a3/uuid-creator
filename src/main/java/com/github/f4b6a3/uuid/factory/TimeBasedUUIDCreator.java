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
	private Instant instant;
	private long nodeIdentifier;
	
	private UUIDState state;
	
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
	 * 
	 * @see {@link UUIDCreator#create()}
	 */
	@Override
	public UUID create() {
		
		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;
		
		if (instant == null) {
			instant = Instant.now();
		}
		
		// Get new values
		long timestamp = TimestampUtils.getTimestamp(instant);
		long sequence1 = state.nextSequence1(timestamp);
		long sequence2 = state.nextSequence2(timestamp);
		long nodeIdentifier = getNodeIdentifier();
		
		// Update state with new values
		state.setTimestamp(timestamp);
		state.setSequence1(sequence1);
		state.setSequence2(sequence2);
		state.setNodeIdentifier(nodeIdentifier);
		
		// Set the most significant bits
		msb = insertTimestampBits(msb, timestamp);
		msb = insertSequence2Bits(msb, sequence2);
		
		// Set the least significant bits
		lsb = insertSequence1Bits(lsb, sequence1);
		lsb = insertNodeIdentifierBits(lsb, nodeIdentifier);
		
		return new UUID(msb, lsb);
	}
	
	
	/* 
	 * --------------------------------
	 * Public fluent interface methods
	 * --------------------------------
	 */
	
	public TimeBasedUUIDCreator withInstant(Instant instant) {
		this.instant = instant;
		return this;
	}
	
	public TimeBasedUUIDCreator withNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
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
	 * Sets the the multicast bit of a node identifier.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	private static long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}

	/**
	 * Checks whether a node identifier has it's multicast bit set.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	private static boolean isMulticastNodeIdentifier(long nodeIdentifier) {
		return ((nodeIdentifier & 0x0000010000000000L) >>> 40) == 1;
	}
	
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
		
		if (this.nodeIdentifier != 0 && state.getNodeIdentifier() == 0) {
			return this.nodeIdentifier;
		}
		
		if (version == VERSION_1) {
			if ((state.getNodeIdentifier() != 0) && !isMulticastNodeIdentifier(state.getNodeIdentifier())) {
				return state.getNodeIdentifier();
			}
			return getHardwareAddressNodeIdentifier();
		} else {
			if ((state.getNodeIdentifier() != 0) && isMulticastNodeIdentifier(state.getNodeIdentifier())) {
				return state.getNodeIdentifier();
			}
			return getRandomNodeIdentifier();
		}
	}
	
	/* 
	 * -------------------------------------
	 * Private methods for setting bits of the UUID parts
	 * -------------------------------------
	 */
	
	/**
	 * Set the timestamp bits of the UUID.
	 * 
	 * @param timestamp
	 */
	private long insertTimestampBits(long msb, long timestamp) {
		if(this.version == VERSION_1) {
			return insertStandardTimestamp(msb, timestamp);
		} else {
			return insertSequentialTimestamp(msb, timestamp);
		}
	}
	
	/**
	 * Set the timestamp bits of the UUID in the natural order of bytes.
	 * 
	 * @see {@link TimeBasedUUIDCreator#embedTimestampBits(long)}
	 * 
	 * @param timestamp
	 */
	private long insertSequentialTimestamp(long msb, long timestamp) {
		
		long himid = (timestamp & 0x0ffffffffffff000L) << 4;
		long low = (timestamp & 0x0000000000000fffL);
		
		msb = (himid | low);
		
		return msb;
	}
	
	/**
	 * Set the timestamp bits of the UUID in the order defined in the RFC-4122.
	 * 
	 * @param timestamp
	 */
	private long insertStandardTimestamp(long msb, long timestamp) {

		long hii = (timestamp & 0x0fff000000000000L) >>> 48;
		long mid = (timestamp & 0x0000ffff00000000L) >>> 16;
		long low = (timestamp & 0x00000000ffffffffL) << 32;

		msb = (low | mid | hii);
		msb = setVersionBits(msb);
		
		return msb;
	}
	
	/**
	 * Set the first clock sequence bits of the UUID.
	 *  
	 * @param clockSequence1
	 */
	private long insertSequence1Bits(long lsb, long clockSequence1) {
		
		long nodeId = lsb & 0x0000ffffffffffffL;
		long clkSeq = clockSequence1 << 48;

		return (clkSeq | nodeId);
	}

	/**
	 * Set the second clock sequence bits of the UUID.
	 * 
	 * It is a extension suggested by the RFC-4122.
	 *  
	 * @param clockSequence2
	 */
	private long insertSequence2Bits(long msb, long clockSequence2) {
		UUID temp = new UUID(msb, 0);
		long timestamp = UUIDUtils.extractTimestamp(temp);
		return insertTimestampBits(msb, timestamp + clockSequence2);
	}
	
	/**
	 * Set the node identifier bits of the UUID.
	 * 
	 * @param nodeIdentifier
	 */
	private long insertNodeIdentifierBits(long lsb, long nodeIdentifier) {
		
		long clkSeq = lsb & 0xffff000000000000L;
		long nodeId = nodeIdentifier & 0x0000ffffffffffffL;

		return (clkSeq | nodeId);
	}
}