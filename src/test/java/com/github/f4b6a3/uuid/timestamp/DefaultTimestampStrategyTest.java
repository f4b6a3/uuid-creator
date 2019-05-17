package com.github.f4b6a3.uuid.timestamp;

import org.junit.Test;

import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;

import static org.junit.Assert.*;

public class DefaultTimestampStrategyTest {

	@Test
	public void testNextFor_TheCounterShouldBeIncrementedIfTheNewTimestampIsLowerThanOrEqualToTheOldTimestamp() {

		// It should increment if the new timestamp is LOWER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		long old_counter = timestampCounter.getNextForTimestamp(old_timestamp);
		long new_counter = timestampCounter.getNextForTimestamp(new_timestamp);
		assertEquals(old_counter + 1, new_counter);

		// It should increment if the new timestamp is EQUAL TO the old
		// timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		timestampCounter = new DefaultTimestampStrategy();
		old_counter = timestampCounter.getNextForTimestamp(old_timestamp);
		new_counter = timestampCounter.getNextForTimestamp(new_timestamp);
		assertEquals(old_counter + 1, new_counter);

	}

	@Test
	public void testNextFor_TheCounterShouldBeZeroIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

		// It should be ZERO if the new timestamp is GREATER THAN the old
		// timestamp
		long timestamp = 1000;
		DefaultTimestampStrategy timestampCounter = new DefaultTimestampStrategy();
		timestampCounter.getNextForTimestamp(timestamp);
		long counter = timestampCounter.getNextForTimestamp(timestamp + 1);
		assertEquals(0, counter);
		
	}

	@Test
	public void testNextFor_TheCounterShouldBeResetToZeroIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

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
