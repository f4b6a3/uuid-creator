package com.github.f4b6a3.uuid.timestamp;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import com.github.f4b6a3.uuid.util.TimestampUtil;

/**
 * This is an implementation of {@link TimestampStrategy} that always returns
 * the same timestamp, simulating a stopped wall clock.
 * 
 * This strategy was created to substitute the strategy
 * {@link DefaultTimestampStrategy} in test cases.
 * 
 * It's useful for tests only.
 * 
 */
public class StoppedDefaultTimestampStrategy extends DefaultTimestampStrategy {

	protected static final Clock stoppedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

	@Override
	public long getTimestamp() {

		long timestamp = TimestampUtil.toTimestamp(Instant.now(stoppedClock));
		long counter = getNextForTimestamp(timestamp);

		return timestamp + counter;
	}
}
