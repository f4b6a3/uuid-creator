package com.github.f4b6a3.uuid.increment;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimestampCounterTest {
	
	@Test
	public void testNextFor_should_increment_if_the_new_timestamp_is_lower_than_or_equal_to__the_old_timestamp() {
		
		// It should increment if the new timestamp is LOWER THAN the old timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;
		TimestampCounter timestampCounter = new TimestampCounter();
		long old_counter = timestampCounter.getNextFor(old_timestamp);
		long new_counter = timestampCounter.getNextFor(new_timestamp);
		assertEquals(old_counter + 1, new_counter);
		
		// It should increment if the new timestamp is EQUAL TO the old timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		timestampCounter = new TimestampCounter();
		old_counter = timestampCounter.getNextFor(old_timestamp);
		new_counter = timestampCounter.getNextFor(new_timestamp);
		assertEquals(old_counter + 1, new_counter);
		
	}
	
	@Test
	public void testNextFor_should_be_zero_if_the_new_timestamp_is_greater_than_the_old_timestamp() {

		// It should be ZERO if the new timestamp is GREATER THAN the old timestamp
		long timestamp = 1000;
		TimestampCounter timestampCounter = new TimestampCounter();
		long counter = timestampCounter.getNextFor(timestamp);
		counter = timestampCounter.getNextFor(timestamp + 1);
		assertEquals(0, counter);
		
		// It should be RESET to ZERO if the new timestamp is GREATER THAN the old timestamp
		timestamp = 1000;
		timestampCounter = new TimestampCounter();
		counter = timestampCounter.getNextFor(timestamp);
		counter = timestampCounter.getNextFor(timestamp);
		assertEquals(1, counter);
		counter = timestampCounter.getNextFor(timestamp);
		assertEquals(2, counter);
		counter = timestampCounter.getNextFor(timestamp + 1);
		assertEquals(0, counter);
		
	}
	
	@Test
	public void testNextFor_should_be_reset_to_zero_if_the_new_timestamp_is_greater_than_the_old_timestamp() {
		
		// It should be RESET to ZERO if the new timestamp is GREATER THAN the old timestamp
		long timestamp = 1000;
		TimestampCounter timestampCounter = new TimestampCounter();
		long counter = timestampCounter.getNextFor(timestamp);
		counter = timestampCounter.getNextFor(timestamp);
		assertEquals(1, counter);
		counter = timestampCounter.getNextFor(timestamp);
		assertEquals(2, counter);
		counter = timestampCounter.getNextFor(timestamp + 1);
		assertEquals(0, counter);
		
	}
	
}
