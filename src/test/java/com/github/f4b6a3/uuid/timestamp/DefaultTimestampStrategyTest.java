package com.github.f4b6a3.uuid.timestamp;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultTimestampStrategyTest {

	@Test
	public void testNextForTheCounterShouldBeIncrementedIfTheNewTimestampIsLowerThanOrEqualToTheOldTimestamp() {

		// It should increment if the new timestamp is LOWER THAN the old timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 999;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		long oldCounter = timestampCounter.getNextCounter(oldTimestamp);
		long newCounter = timestampCounter.getNextCounter(newTimestamp);
		assertEquals(oldCounter + 1, newCounter);

		// It should increment if the new timestamp is EQUAL TO the old timestamp
		oldTimestamp = 1000;
		newTimestamp = 1000;
		timestampCounter = new DefaultTimestampStrategy();
		oldCounter = timestampCounter.getNextCounter(oldTimestamp);
		newCounter = timestampCounter.getNextCounter(newTimestamp);
		assertEquals(oldCounter + 1, newCounter);

	}
}
