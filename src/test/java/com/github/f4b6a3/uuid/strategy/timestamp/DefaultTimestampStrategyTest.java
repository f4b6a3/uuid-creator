package com.github.f4b6a3.uuid.strategy.timestamp;

import org.junit.Test;

import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategy;

import static org.junit.Assert.*;

public class DefaultTimestampStrategyTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	@Test
	public void testGetTimestamp() {
		long lastTimestamp = 0;
		DefaultTimestampStrategy strategy = new DefaultTimestampStrategy();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long timestamp = strategy.getTimestamp();
			assertTrue("The next timstamp should be greater than the last timestamp", timestamp > lastTimestamp);
			lastTimestamp = timestamp;
		}
	}
}
