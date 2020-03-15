package com.github.f4b6a3.uuid.timestamp;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.util.UuidTimeUtil;

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
	
	@Test(expected = UuidCreatorException.class)
	public void testNextCounterAnOverrunExceptionShouldBeThrown() {

		long timestamp = UuidTimeUtil.getCurrentTimestamp();
		DefaultTimestampStrategy timestampStrategy = new DefaultTimestampStrategy();
		
		long offset = timestampStrategy.getNextCounter(timestamp);
		long max = DefaultTimestampStrategy.COUNTER_MAX - offset;
		
		try {
			// Generate MAX values
			for (int i = 0; i < max; i++) {
				timestampStrategy.getNextCounter(timestamp);
			}
		} catch (UuidCreatorException e) {
			// fail if the exception is thrown before the maximum value
			fail("Overrun exception thrown before the maximum value is reached.");
		}
		
		// It should throw an exception now
		timestampStrategy.getNextCounter(timestamp);
	}
}
