package com.github.f4b6a3.uuid.timestamp;

import com.github.f4b6a3.uuid.sequence.AbstractSequence;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;

/**
 * This is an implementation of {@link TimestampStrategy} that provides
 * millisecond resolution. The timestamp resolution is simulated by adding the
 * value of a counter that is incremented at every call to the method
 * {@link TimestampStrategy#getTimestamp()}.
 * 
 * This class counts how many times a timestamp was used. This value added to
 * the timestamp is used to simulate a high resolution clock.
 * 
 * The maximum value of this counter is 10,000, 'the number of 100-nanosecond
 * intervals per' milliseconds.
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
public class DefaultTimestampStrategy extends AbstractSequence implements TimestampStrategy {
	
	protected long previousTimestamp = 0;

	protected static final int COUNTER_MIN = 0;
	protected static final int COUNTER_MAX = 9_999;
	
	protected static final int COUNTER_OFFSET_MAX = 0xff; // 255

	public DefaultTimestampStrategy() {
		super(COUNTER_MIN, COUNTER_MAX);
		this.value = RandomUtil.nextInt(COUNTER_OFFSET_MAX);
	}

	@Override
	public long getTimestamp() {

		long timestamp = TimestampUtil.getCurrentTimestamp();
		long counter = getNextCounter(timestamp);

		// (4) simulate a high resolution timestamp
		return timestamp + counter;
	}

	/**
	 * Get the next counter value.
	 * 
	 * @param timestamp a timestamp
	 * @return the next counter value
	 */
	protected int getNextCounter(long timestamp) {
		
		if(timestamp > this.previousTimestamp) {
			reset();
		}

		this.previousTimestamp = timestamp;
		return this.next();
	}
	
	@Override
	public void reset() {
		this.value = (this.value & COUNTER_OFFSET_MAX);
	}
}
