package com.github.f4b6a3.uuid.timestamp;

import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultTimestampStrategyTest {

	@Test
	public void testNextForTheCounterShouldBeIncrementedIfTheNewTimestampIsLowerThanOrEqualToTheOldTimestamp() {

		// It should increment if the new timestamp is LOWER THAN the old
		// timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 999;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		long oldCounter = timestampCounter.getNextForTimestamp(oldTimestamp);
		long newCounter = timestampCounter.getNextForTimestamp(newTimestamp);
		assertEquals(oldCounter + 1, newCounter);

		// It should increment if the new timestamp is EQUAL TO the old
		// timestamp
		oldTimestamp = 1000;
		newTimestamp = 1000;
		timestampCounter = new DefaultTimestampStrategy();
		oldCounter = timestampCounter.getNextForTimestamp(oldTimestamp);
		newCounter = timestampCounter.getNextForTimestamp(newTimestamp);
		assertEquals(oldCounter + 1, newCounter);

	}

	@Test
	public void testNextForTheCounterShouldBeZeroIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

		// It should be ZERO if the new timestamp is GREATER THAN the old
		// timestamp
		long timestamp = 1000;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		timestampCounter.getNextForTimestamp(timestamp);
		long counter = timestampCounter.getNextForTimestamp(timestamp + 1);
		assertEquals(0, counter);
		
	}

	@Test
	public void testNextForTheCounterShouldBeResetToZeroIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

		// It should be RESET to ZERO if the new timestamp is GREATER THAN the
		// old timestamp
		long timestamp = 1000;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		
		timestampCounter.getNextForTimestamp(timestamp);
		long counter = timestampCounter.getNextForTimestamp(timestamp);
		assertEquals(1, counter);
		counter = timestampCounter.getNextForTimestamp(timestamp);
		assertEquals(2, counter);
		counter = timestampCounter.getNextForTimestamp(timestamp + 1);
		assertEquals(0, counter);

	}

}
