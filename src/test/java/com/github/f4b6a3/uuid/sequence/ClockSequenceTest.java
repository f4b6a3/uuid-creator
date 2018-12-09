package com.github.f4b6a3.uuid.sequence;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.sequence.ClockSequence;
import com.github.f4b6a3.uuid.util.TimestampUtil;

import static org.junit.Assert.*;

public class ClockSequenceTest {

	@Test
	public void testNextFor_should_increment_if_the_new_timestamp_is_lower_or_equal_to_the_old_timestamp() {

		// It should increment if the new timestamp is LOWER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;
		ClockSequence clockSequence = new ClockSequence();
		long old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		long new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence + 1, new_sequence);

		// It should increment if the new timestamp is EQUAL TO the old
		// timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		clockSequence = new ClockSequence();
		old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence + 1, new_sequence);
	}

	@Test
	public void testNextFor_should_not_increment_if_the_new_timestamp_is_equal_to_the_old_timestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 1001;
		ClockSequence clockSequence = new ClockSequence();
		long old_sequence = clockSequence.getNextForTimestamp(old_timestamp);
		long new_sequence = clockSequence.getNextForTimestamp(new_timestamp);
		assertEquals(old_sequence, new_sequence);
	}

	@Test()
	public void testNextForTimestamp_the_last_value_should_be_equal_to_the_first_value_minus_one() {

		int first = 0;
		int last = 0;
		long timestamp = TimestampUtil.getCurrentTimestamp();
		ClockSequence clockSequence = new ClockSequence();

		first = clockSequence.getNextForTimestamp(timestamp);
		for (int i = 0; i < ClockSequence.SEQUENCE_MAX; i++) {
			last = clockSequence.getNextForTimestamp(timestamp);
		}

		assertEquals(first - 1, last);
	}

	@Test(expected = OverrunException.class)
	public void testNextForTimestamp_should_throw_overrun_exception() {

		long timestamp = TimestampUtil.getCurrentTimestamp();
		ClockSequence clockSequence = new ClockSequence();

		int i = 0;
		try {
			// Generate MAX values
			for (i = 0; i <= ClockSequence.SEQUENCE_MAX; i++) {
				clockSequence.getNextForTimestamp(timestamp);
			}
		} catch (OverrunException e) {
			// fail if the exception is thrown before the maximum value
			fail(String.format("Overrun exception thrown before the maximum value is reached."));
		}

		// It should throw an exception now
		clockSequence.getNextForTimestamp(timestamp);

	}
}
