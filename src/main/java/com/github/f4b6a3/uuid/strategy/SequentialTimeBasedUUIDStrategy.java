package com.github.f4b6a3.uuid.strategy;

public class SequentialTimeBasedUUIDStrategy implements TimeBasedUUIDStrategy {
	/**
	 * Returns the timestamp bits of the UUID in the natural order of bytes.
	 * 
	 * @param timestamp
	 */
	@Override
	public long getMostSignificantBits(long timestamp) {
		
		long himid = (timestamp & 0x0ffffffffffff000L) << 4;
		long low = (timestamp & 0x0000000000000fffL);
		
		return (himid | low);
	}

}
