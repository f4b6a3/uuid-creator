package com.github.f4b6a3.uuid.increment;

import com.github.f4b6a3.uuid.increment.AbstractIncrementable;

public class TimestampCounter extends AbstractIncrementable {

	private long timestamp = 0;

	private static final int COUNTER_MIN = 0;
	private static final int COUNTER_MAX = 10_000;

	public TimestampCounter() {
		super(COUNTER_MIN, COUNTER_MAX);
	}

	public int getNextFor(long timestamp) {
		if (timestamp <= this.timestamp) {
			this.timestamp = timestamp;
			return this.getNext();
		}
		return this.getCurrent();
	}
}
