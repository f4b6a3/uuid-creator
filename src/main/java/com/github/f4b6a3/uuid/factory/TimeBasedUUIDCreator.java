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

import com.github.f4b6a3.uuid.clock.UUIDClock;
import com.github.f4b6a3.uuid.clock.UUIDState;
import com.github.f4b6a3.uuid.util.ByteUtils;
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
	
	private Random random;
	private UUIDState state;
	private UUIDClock clock;
	
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	
	public TimeBasedUUIDCreator(int version) {
		super(version);
		this.random = new Random();
		this.state = new UUIDState();
		this.clock = new UUIDClock(this.state);
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
		
		if (instant == null) {
			instant = Instant.now();
		}
		
		long timestamp = UUIDClock.getTimestamp(instant);
		long clockSeq1 = clock.getSequence1(timestamp);
		long clockSeq2 = clock.getSequence2(timestamp);
		long nodeIdentifier = getNodeIdentifier();
		
		setTimestamp(timestamp);
		setClockSeq1(clockSeq1);
		setClockSeq2(clockSeq2);
		setNodeIdentifier(nodeIdentifier);
		
		state.setTimestamp(timestamp);
		state.setClockSeq1(clockSeq1);
		state.setClockSeq2(clockSeq2);
		state.setNodeIdentifier(nodeIdentifier);
		
		return super.create();
	}
	
	public long getHardwareAddressNodeIdentifier() {
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
	
	public long getRandomNodeIdentifier() {
		long node = random.nextLong();
		return setMulticastNode(node);
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
	 * -------------------------
	 * Protected methods
	 * -------------------------
	 */
	
	protected void setTimestamp(long timestamp) {
		if(this.version == VERSION_1) {
			setStandardTimestamp(timestamp);
		} else {
			setSequentialTimestamp(timestamp);
		}
	}
	
	protected void setSequentialTimestamp(long timestamp) {
		
		long hii = (timestamp & 0x0fff000000000000L) << 4;
		long mid = (timestamp & 0x0000ffff00000000L) << 4;
		long lo1 = (timestamp & 0x00000000fffff000L) << 4;
		long lo2 = (timestamp & 0x0000000000000fffL);
		
		this.msb = (hii | mid | lo1 | lo2);
	}
	
	protected void setStandardTimestamp(long timestamp) {
		
		long hii = (timestamp & 0x0fff000000000000L) >>> 48;
		long mid = (timestamp & 0x0000ffff00000000L) >>> 16;
		long low = (timestamp & 0x00000000ffffffffL) << 32;

		this.msb = (low | mid | hii);
		setVersionBits();
	}
	
	protected void setClockSeq1(long clockSeq1) {
		
		long lsb = this.lsb;
		long cs1 = clockSeq1 << 48;
		
		lsb &= 0x0000ffffffffffffL;
		cs1 &= 0xffff000000000000L;

		this.lsb = (lsb | cs1);
	}
	
	protected void setClockSeq2(long clockSeq2) {
		long timestamp;
		if(version == VERSION_1) {
			timestamp = UUIDUtils.extractStandardTimestamp(this.msb) + clockSeq2;	
		} else {
			timestamp = UUIDUtils.extractSequentialTimestamp(this.msb) + clockSeq2;
		}
		setTimestamp(timestamp);
	}
	
	protected void setNodeIdentifier(long nodeIdentifier) {
		
		long lsb = this.lsb;
		long nod = nodeIdentifier;
		
		lsb &= 0xffff000000000000L;
		nod &= 0x0000ffffffffffffL;

		this.lsb = (lsb | nod);
	}
	
	protected long getNodeIdentifier() {
		
		if (this.nodeIdentifier != 0 && state.getNodeIdentifier() == 0) {
			return this.nodeIdentifier;
		}
		
		if (version == VERSION_1) {
			if ((state.getNodeIdentifier() != 0) && !isMulticastNode(state.getNodeIdentifier())) {
				return state.getNodeIdentifier();
			}
			return getHardwareAddressNodeIdentifier();
		} else {
			if ((state.getNodeIdentifier() != 0) && isMulticastNode(state.getNodeIdentifier())) {
				return state.getNodeIdentifier();
			}
			return getRandomNodeIdentifier();
		}
	}
	
	protected static long setMulticastNode(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}

	protected static boolean isMulticastNode(long nodeIdentifier) {
		return ((nodeIdentifier & 0x0000010000000000L) >>> 40) == 1;
	}
}