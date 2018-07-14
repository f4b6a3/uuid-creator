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

package com.github.f4b6a3.uuid.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.clock.UUIDClock;
import com.github.f4b6a3.uuid.clock.UUIDState;
import com.github.f4b6a3.uuid.factory.UUIDCreator;

public class UUIDUtils {
	
	private static Random random;
	
	/*
	 * ------------------------------------------
	 * Public static methods for version check
	 * ------------------------------------------
	 */
	
	/**
	 * Checks whether the UUID variant is the one defined by the RFC-4122.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isRFC4122Variant(UUID uuid) {
		int variant = uuid.variant();
		return (variant == UUIDCreator.VARIANT_RFC4122);
	}
	
	/**
	 * Checks whether the UUID version 4.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isRandomBasedVersion(UUID uuid) {
		int version = uuid.version();
		return (isRFC4122Variant(uuid) && (version == 4));
	}
	
	/**
	 * Checks whether the UUID version 3 or 5.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isNameBasedVersion(UUID uuid) {
		int version = uuid.version();
		return (isRFC4122Variant(uuid) && ((version == 3) || (version == 5)));
	}
	
	/**
	 * Checks whether the UUID version 0 or 1.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isTimeBasedVersion(UUID uuid) {
		int version = uuid.version();
		return (isRFC4122Variant(uuid) && ((version == 0) || (version == 1)));
	}
	
	/*
	 * ------------------------------------------
	 * Public static methods for node identifiers
	 * ------------------------------------------
	 */
	
	/**
	 * Sets the the multicast bit of a node identifier.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public static long setMulticastNode(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}

	/**
	 * Checks whether a node identifier has it's multicast bit set.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public static boolean isMulticastNode(long nodeIdentifier) {
		return ((nodeIdentifier & 0x0000010000000000L) >>> 40) == 1;
	}
	
	/**
	 * Returns a random node identifier.
	 * 
	 * @param state
	 * @return
	 */
	public static long getRandomNodeIdentifier(UUIDState state) {
		
		if(random == null) {
			 random = new Random();
		}
		
		if(state.getNodeIdentifier() != 0 && isMulticastNode(state.getNodeIdentifier())) {
			return state.getNodeIdentifier();
		}
		
		long node = random.nextLong();
		return setMulticastNode(node);
	}
	
	/**
	 * Returns a hardware address (MAC) as a node identifier.
	 * 
	 * If no hardware address is found, it returns a random node identifier.
	 * 
	 * @param state
	 * @return
	 */
	public long getHardwareAdressNodeIdentifier(UUIDState state) {
		
		if(state.getNodeIdentifier() != 0 && !isMulticastNode(state.getNodeIdentifier())) {
			return state.getNodeIdentifier();
		}
		
		try {
			// Get only the first network interface available
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return ByteUtils.toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			// If exception occurs, return a random hardware address.
		}
		
		return getRandomNodeIdentifier(state);
	}
	
	/**
	 * Get the hardware address that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractNodeIdentifier(UUID uuid) {
		
		if(!isTimeBasedVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a time-based UUID: ", uuid.toString()));
		}
		
		return uuid.getLeastSignificantBits() & 0x0000ffffffffffffL;
	}
	
	/*
	 * ------------------------------------------
	 * Public static methods for timestamps
	 * ------------------------------------------
	 */
	
	/**
	 * Get the instant that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return {@link Instant}
	 */
	public static Instant extractInstant(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return UUIDClock.getInstant(timestamp);
	}
	
	/**
	 * Get the timestamp that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractTimestamp(UUID uuid) {

		if(!isTimeBasedVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a time-based UUID: ", uuid.toString()));
		}

		if (uuid.version() == 1) {
			return extractStandardTimestamp(uuid.getMostSignificantBits());
		} else {
			return extractSequentialTimestamp(uuid.getMostSignificantBits());
		}
	}
	
	/**
	 * Get the timestamp that is embedded in the Sequential UUID.
	 *
	 * @param msb
	 *            a long value that has the "Most Significant Bits" of the UUID.
	 * @return long
	 */
	public static long extractSequentialTimestamp(long msb) {
		
		long hii = (msb & 0xffffffff00000000L) >>> 4;
		long mid = (msb & 0x00000000ffff0000L) >>> 4;
		long low = (msb & 0x0000000000000fffL);

		long timestamp = hii | mid | low;
		return timestamp;
	}
	
	/**
	 * Get the timestamp that is embedded in the Sequential UUID.
	 *
	 * @param msb
	 *            a long value that has the "Most Significant Bits" of the UUID.
	 * @return long
	 */
	public static long extractStandardTimestamp(long msb) {
		
		long hii = (msb & 0xffffffff00000000L) >>> 32;
		long mid = (msb & 0x00000000ffff0000L) << 16;
		long low = (msb & 0x0000000000000fffL) << 48;

		long timestamp = hii | mid | low;
		return timestamp;
	}
}
