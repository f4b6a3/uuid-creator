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
	 * Checks whether the UUID version 1.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isTimeBasedVersion(UUID uuid) {
		int version = uuid.version();
		return (version == 1);
	}
	
	/**
	 * Checks whether the UUID version 0.
	 * 
	 * @param uuid
	 * @return boolean
	 */
	public static boolean isOrderedVersion(UUID uuid) {
		int version = uuid.version();
		return (version == 0);
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
		
		if(!(UUIDUtil.isTimeBasedVersion(uuid) || UUIDUtil.isOrderedVersion(uuid))) {
			throw new UnsupportedOperationException(String.format("Not a time-based or ordered UUID: " + uuid.version(), uuid.toString()));
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

		if(!(UUIDUtil.isTimeBasedVersion(uuid) || UUIDUtil.isOrderedVersion(uuid))) {
			throw new UnsupportedOperationException(String.format("Not a time-based or ordered UUID: " + uuid.version(), uuid.toString()));
		}

		if (uuid.version() == 1) {
			return extractTimeBasedTimestamp(uuid.getMostSignificantBits());
		} else {
			return extractOrderedTimestamp(uuid.getMostSignificantBits());
		}
	}
	
	/**
	 * Get the timestamp that is embedded in the Ordered UUID.
	 *
	 * @param msb
	 *            a long value that has the "Most Significant Bits" of the UUID.
	 * @return long
	 */
	private static long extractOrderedTimestamp(long msb) {
		
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
	private static long extractTimeBasedTimestamp(long msb) {
		
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
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: " + uuid.version(), uuid.toString()));
		}
		
		return (byte) ((uuid.getLeastSignificantBits() & 0x00ff000000000000L) >> 48);
	}
	
	/**
	 * Get the local identifier number that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return int
	 */
	public static int extractDCESecurityLocalIdentifier(UUID uuid) {
		
		if(!UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: " + uuid.version(), uuid.toString()));
		}
		
		return (int) (uuid.getMostSignificantBits() >> 32);
	}
	
	/**
	 * Get the timestamp that is embedded in the DCE Security UUID.
	 *
	 * @param uuid
	 * @return long
	 */
	public static long extractDCESecurityTimestamp(UUID uuid) {
		if(!UUIDUtil.isDCESecurityVersion(uuid)) {
			throw new UnsupportedOperationException(String.format("Not a DCE Security UUID: " + uuid.version(), uuid.toString()));
		}
		
		return extractTimeBasedTimestamp((uuid.getMostSignificantBits() & 0x00000000ffffffffL));
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
	
	/**
	 * Convert a ordered to a time-based UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	public static UUID fromOrderedToTimeBasedUUID(UUID uuid) {

		if(!(UUIDUtil.isOrderedVersion(uuid))) {
			throw new UnsupportedOperationException(String.format("Not a ordered UUID: " + uuid.version(), uuid.toString()));
		}
		
		long timestamp = extractTimestamp(uuid);
		
		long msb = formatTimeBasedMostSignificantBits(timestamp);
		long lsb = uuid.getLeastSignificantBits();
		
		return new UUID(msb, lsb);
	}
	
	/**
	 * Convert a time-based to a ordered UUID.
	 * 
	 * @param uuid
	 * @return
	 */
	public static UUID fromTimeBasedToOrderedUUID(UUID uuid) {

		if(!(UUIDUtil.isTimeBasedVersion(uuid))) {
			throw new UnsupportedOperationException(String.format("Not a time-based UUID: " + uuid.version(), uuid.toString()));
		}
		
		long timestamp = extractTimestamp(uuid);
		
		long msb = formatOrderedMostSignificantBits(timestamp);
		long lsb = uuid.getLeastSignificantBits();
		
		return new UUID(msb, lsb);
	}
	
	/**
	 * Returns the timestamp bits of the UUID in the 'natural' order of bytes.
	 * 
	 * It's not necessary to set the version bits because they are already ZERO.
	 * 
	 * @param timestamp
	 */
	public static long formatOrderedMostSignificantBits(long timestamp) {

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
	public static long formatTimeBasedMostSignificantBits(long timestamp) {
		
		long hii = (timestamp & 0x0fff000000000000L) >>> 48;
		long mid = (timestamp & 0x0000ffff00000000L) >>> 16;
		long low = (timestamp & 0x00000000ffffffffL) << 32;

		return (low | mid | hii | 0x0000000000001000L);
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
	 * @param clockSequence
	 */
	public static long formatLeastSignificantBits(final long nodeIdentifier, final long clockSequence) {

		long seq = clockSequence << 48;
		long nod = nodeIdentifier & 0x0000ffffffffffffL;

		return (seq | nod | 0x8000000000000000L);
	}
	
	/**
	 * Sets the the multicast bit ON to indicate that it's NOT a real MAC
	 * address.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public static long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}

	/**
	 * Sets the the multicast bit OFF to indicat that it's a MAC address.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public static long setUnicastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier & 0xFFFFFEFFFFFFFFFFL;
	}
}
