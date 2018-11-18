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

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.abst.AbstractUUIDCreator;

public class UUIDUtil {
	
	/*
	 * Public static methods for version check
	 */
	
	/**
	 * Checks whether the UUID variant is the one defined by the RFC-4122.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isRFC4122Variant(UUID uuid) {
		int variant = uuid.variant();
		return (variant == AbstractUUIDCreator.VARIANT_RFC4122);
	}
	
	/**
	 * Checks whether the UUID version 4.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isRandomBasedVersion(UUID uuid) {
		return (uuid.version() == 4);
	}
	
	/**
	 * Checks whether the UUID version 3 or 5.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isNameBasedVersion(UUID uuid) {
		int version = uuid.version();
		return ((version == 3) || (version == 5));
	}
	
	/**
	 * Checks whether the UUID version 0 or 1.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isTimeBasedVersion(UUID uuid) {
		int version = uuid.version();
		return ((version == 0) || (version == 1));
	}
	
	/**
	 * Checks whether the UUID version 2.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isDCESecurityVersion(UUID uuid) {
		int version = uuid.version();
		return (version == 2);
	}
	
	/*
	 * Public static methods for node identifiers
	 */
	
	/**
	 * Get the node identifier that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractNodeIdentifier(UUID uuid) {
		
		if(!UUIDUtil.isTimeBasedVersion(uuid) && !UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a time-based or DCE Security UUID: ", uuid.toString()));
		}
		
		return uuid.getLeastSignificantBits() & 0x0000ffffffffffffL;
	}
	
	/*
	 * Protected static methods for timestamps
	 */
	
	/**
	 * Get the instant that is embedded in the UUID.
	 *
	 * @param uuid
	 * @return {@link Instant}
	 */
	public static Instant extractInstant(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return TimestampUtil.toInstant(timestamp);
	}
	
	/**
	 * Get the timestamp that is embedded in the UUID.
	 *
	 * The timestamps returned by this method are the number of 100-nanos since
	 * Gregorian Epoch.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractTimestamp(UUID uuid) {

		if(!isTimeBasedVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a time-based UUID v%s: %s", uuid.version(), uuid.toString()));
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
	private static long extractSequentialTimestamp(long msb) {
		
		long himid = (msb & 0xffffffffffff0000L) >>> 4;
		long low = (msb & 0x0000000000000fffL);

		return (himid | low);
	}
	
	/**
	 * Get the timestamp that is embedded in the standard Time-based UUID.
	 *
	 * @param msb
	 *            a long value that has the "Most Significant Bits" of the UUID.
	 * @return long
	 */
	private static long extractStandardTimestamp(long msb) {
		
		long hii = (msb & 0xffffffff00000000L) >>> 32;
		long mid = (msb & 0x00000000ffff0000L) << 16;
		long low = (msb & 0x0000000000000fffL) << 48;

		return (hii | mid | low);
	}
	
	/**
	 * Get the local domain number that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return byte
	 */
	public static byte extractDCESecurityLocalDomain(UUID uuid) {
		
		if(!UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: ", uuid.toString()));
		}
		
		return (byte) ((uuid.getLeastSignificantBits() & 0x00ff000000000000L) >>> 48);
	}
	
	/**
	 * Get the local identifier number that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return int
	 */
	public static int extractDCESecurityLocalIdentifier(UUID uuid) {
		
		if(!UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: ", uuid.toString()));
		}
		
		return (int) (uuid.getMostSignificantBits() >>> 32);
	}
	
	/**
	 * Get the timestamp that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractDCESecurityTimestamp(UUID uuid) {
		
		if(!UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: ", uuid.toString()));
		}
		
		return extractStandardTimestamp(uuid.getMostSignificantBits() & 0x00000000ffffffffL);
	}
	
	/**
	 * Get the instant that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return {@link Instant}
	 */
	public static Instant extractDCESecurityInstant(UUID uuid) {
		long timestamp = extractDCESecurityTimestamp(uuid);
		return TimestampUtil.toInstant(timestamp);
	}
}
