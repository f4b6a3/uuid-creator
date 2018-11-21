package com.github.f4b6a3.uuid.sequence;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.sequence.ClockSequence;

import static org.junit.Assert.*;

import java.time.Instant;

public class ClockSequenceTest {
	
	@Test
	public void testNextFor_should_increment_if_the_new_timestamp_is_lower_or_equal_to_the_old_timestamp() {

		// It should increment if the new timestamp is LOWER THAN the old timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;				
		ClockSequence clockSequence = new ClockSequence();
		long old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		long new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence + 1, new_sequence);

		// It should increment if the new timestamp is EQUAL TO the old timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		clockSequence = new ClockSequence();
		old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence + 1, new_sequence);
	}

	@Test
	public void testNextFor_should_not_increment_if_the_new_timestamp_is_equal_to_the_old_timestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old timestamp
		long old_timestamp = 1000;
		long new_timestamp = 1001;				
		ClockSequence clockSequence = new ClockSequence();
		long old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		long new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence, new_sequence);
	}

	@Test(expected = OverrunException.class)
	public void testNextForTimestamp_should_throw_overrun_exception() {

		TimeBasedUUIDCreator creator = new TimeBasedUUIDCreator().withInstant(Instant.now())
				.withInitialClockSequence(0x3fff);

		for (int i = 0; i < ClockSequence.SEQUENCE_MAX; i++) {
			creator.create();
		}
		// fail if no exception was thrown
		fail();
	}
}
