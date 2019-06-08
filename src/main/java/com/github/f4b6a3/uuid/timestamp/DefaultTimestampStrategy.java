package com.github.f4b6a3.uuid.timestamp;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.sequence.AbstractSequence;
import com.github.f4b6a3.uuid.util.RandomUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;

/**
 * This is an implementation of {@link TimestampStrategy} that provides
 * millisecond resolution. The timestamp resolution is simulated by adding the
 * value of a counter that is incremented at every call to the method
 * {@link TimestampStrategy#getTimestamp()}.
 * 
 * The counter's range is from 0 to 9,999, that is, the number of 100-nanosecond
 * intervals per millisecond.
 * 
 * The counter is initialized with a random number between 0 and 255 to void
 * duplicates in the case of multiple time-based generators running in different
 * JVMs.
 * 
 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
 * 
 * (4) A high resolution timestamp can be simulated by keeping a count of the
 * number of UUIDs that have been generated with the same value of the system
 * time, and using it to construct the low order bits of the timestamp. The
 * count will range between zero and the number of 100-nanosecond intervals per
 * system time interval.
 *
 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
 * 
 * (3c) If a system overruns the generator by requesting too many UUIDs within a
 * single system time interval, the UUID service MUST either return an error, or
 * stall the UUID generator until the system clock catches up.
 * 
 */
public class DefaultTimestampStrategy extends AbstractSequence implements TimestampStrategy {

	protected long previousTimestamp = 0;

	protected static final int COUNTER_MIN = 0;
	protected static final int COUNTER_MAX = 9_999;

	protected static final int COUNTER_OFFSET_MAX = 0xff; // 255
	
	protected static final String OVERRUN_MESSAGE = "The system overran the generator by requesting too many UUIDs.";

	public DefaultTimestampStrategy() {
		super(COUNTER_MIN, COUNTER_MAX);
		this.value = RandomUtil.nextInt(COUNTER_OFFSET_MAX);
	}

	@Override
	public long getTimestamp() {

		final long timestamp = TimestampUtil.getCurrentTimestamp();
		final long counter = getNextCounter(timestamp);

		// (4) simulate a high resolution timestamp
		return timestamp + counter;
	}

	/**
	 * Get the next counter value.
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @return the next counter value
	 */
	protected int getNextCounter(final long timestamp) {
		if (timestamp > this.previousTimestamp) {
			reset();
		}
		this.previousTimestamp = timestamp;
		return this.next();
	}

	@Override
	public int next() {
		if (this.value > maxValue) {
			this.value = minValue;
			// (3c) Too many requests
			throw new UuidCreatorException(OVERRUN_MESSAGE);
		}
		return this.value++;
	}

	@Override
	public void reset() {
		this.value = (this.value & COUNTER_OFFSET_MAX);
	}
}
