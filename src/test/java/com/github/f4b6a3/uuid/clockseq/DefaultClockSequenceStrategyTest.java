package com.github.f4b6a3.uuid.clockseq;

import org.junit.Test;

import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.util.TimestampUtil;

import static org.junit.Assert.*;

public class DefaultClockSequenceStrategyTest {

	@Test
	public void testNextFor_should_increment_if_the_new_timestamp_is_lower_or_equal_to_the_old_timestamp() {

		// It should increment if the new timestamp is LOWER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		long new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence + 1, new_sequence);

		// It should increment if the new timestamp is EQUAL TO the old
		// timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		clockSequence = new DefaultClockSequenceStrategy();
		old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence + 1, new_sequence);
	}

	@Test
	public void testNextFor_should_not_increment_if_the_new_timestamp_is_equal_to_the_old_timestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 1001;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		long new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence, new_sequence);
	}

	@Test()
	public void testNextForTimestamp_the_last_value_should_be_equal_to_the_first_value_minus_one() {

		int first = 0;
		int last = 0;
		long timestamp = TimestampUtil.getCurrentTimestamp();
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();

		first = clockSequence.getClockSequence(timestamp, 0);
		for (int i = 0; i < DefaultClockSequenceStrategy.SEQUENCE_MAX; i++) {
			last = clockSequence.getClockSequence(timestamp, 0);
		}

		assertEquals(first - 1, last);
	}

	@Test(expected = OverrunException.class)
	public void testNextForTimestamp_should_throw_overrun_exception() {

		long timestamp = TimestampUtil.getCurrentTimestamp();
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();

		int i = 0;
		try {
			// Generate MAX values
			for (i = 0; i <= DefaultClockSequenceStrategy.SEQUENCE_MAX; i++) {
				clockSequence.getClockSequence(timestamp, 0);
			}
		} catch (OverrunException e) {
			// fail if the exception is thrown before the maximum value
			fail(String.format("Overrun exception thrown before the maximum value is reached."));
		}

		// It should throw an exception now
		clockSequence.getClockSequence(timestamp, 0);

	}
}
