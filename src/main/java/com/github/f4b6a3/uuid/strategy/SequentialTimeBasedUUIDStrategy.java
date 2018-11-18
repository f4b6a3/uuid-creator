package com.github.f4b6a3.uuid.strategy;

public class SequentialTimeBasedUUIDStrategy implements TimeBasedUUIDStrategy {
	/**
	 * Returns the timestamp bits of the UUID in the 'natural' order of bytes.
	 * 
	 * It's not necessary to set the version bytes because they are aready ZERO.
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
