package com.github.f4b6a3.uuid.strategy.msb;

import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;

public class TimeBasedMostSignificantBitsStrategy implements MostSignificantBitsStrategy {

	public static final long VERSION_BITS = 0x0000000000001000L;
	
	// TODO: merge these two commentaries.
	
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
	
	/**
	 * Returns the most significant bits of the UUID.
	 * 
	 * It is a extension suggested by the RFC-4122.
	 * 
	 * {@link TimeBasedUUIDCreator#getStandardTimestampBits(long)}
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
	@Override
	public long getMostSignificantBits(long timestamp, long counter) {
		
		// (4) add the counter to the timestamp
		long ts = timestamp + counter;

		long hii = (ts & 0x0fff000000000000L) >>> 48;
		long mid = (ts & 0x0000ffff00000000L) >>> 16;
		long low = (ts & 0x00000000ffffffffL) << 32;

		return (low | mid | hii | VERSION_BITS);
	}
}
