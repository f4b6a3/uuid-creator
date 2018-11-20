package com.github.f4b6a3.uuid.time;

import com.github.f4b6a3.uuid.increment.AbstractIncrementable;
import com.github.f4b6a3.uuid.util.TimestampUtil;

/**
 * This is implementation of {@link TimestampStrategy} has resolution of
 * milliseconds. The timestamp resolution is simulated by adding the value of a
 * counter that is incremented at every call to the method
 * {@link TimestampStrategy#getCurrentTimestamp()}l.
 * 
 * This class counts how many times a timestamp was used. This value added to
 * the timestamp is used to simulate a high resolution clock.
 * 
 * The maximum value of this counter is 10,000 exclusive, 'the number of
 * 100-nanosecond intervals per' milliseconds.
 * 
 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
 * 
 * (4) A high resolution timestamp can be simulated by keeping a count of the
 * number of UUIDs that have been generated with the same value of the system
 * time, and using it to construct the low order bits of the timestamp. The
 * count will range between zero and the number of 100-nanosecond intervals per
 * system time interval.
 *
 */
public class DefaultTimestampStrategy extends AbstractIncrementable implements TimestampStrategy {

	private long timestamp = 0;

	private static final int COUNTER_MIN = 0;
	private static final int COUNTER_MAX = 9999;

	public DefaultTimestampStrategy() {
		super(COUNTER_MIN, COUNTER_MAX);
	}

	@Override
	public long getCurrentTimestamp() {

		long timestamp = TimestampUtil.getCurrentTimestamp();
		long counter = getNextForTimestamp(timestamp);

		// (4) simulate a high resolution timestamp
		return timestamp + counter;
	}

	/**
	 * Get how many times a timestamp.
	 * 
	 * @param timestamp
	 * @return
	 */
	public int getNextForTimestamp(long timestamp) {
		if (timestamp <= this.timestamp) {
			return this.getNext();
		}

		this.timestamp = timestamp;
		this.reset();
		return this.getCurrent();
	}
}
