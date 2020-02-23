package com.github.f4b6a3.uuid.clockseq;

import org.junit.Test;
import com.github.f4b6a3.uuid.util.TimestampUtil;

import static org.junit.Assert.*;

public class DefaultClockSequenceStrategyTest {
	
	@Test
	public void testNextForTheClockSequenceShouldBeIncrementedIfTheNewTimestampIsLowerOrEqualToTheOldTimestamp() {

		// It should increment if the new timestamp is LOWER THAN the old timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 999;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long oldSequence = clockSequence.getClockSequence(oldTimestamp);
		long newSequence = clockSequence.getClockSequence(newTimestamp);
		assertEquals(oldSequence + 1, newSequence);

		// It should increment if the new timestamp is EQUAL TO the old timestamp
		oldTimestamp = 1000;
		newTimestamp = 1000;
		clockSequence = new DefaultClockSequenceStrategy();
		oldSequence = clockSequence.getClockSequence(oldTimestamp);
		newSequence = clockSequence.getClockSequence(newTimestamp);
		assertEquals(oldSequence + 1, newSequence);
	}

	@Test
	public void testNextForTheClockSequenceShouldNotIncrementIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 1001;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long oldSequence = clockSequence.getClockSequence(oldTimestamp);
		long newSequence = clockSequence.getClockSequence(newTimestamp);
		assertEquals(oldSequence, newSequence);
	}
	
	@Test()
	public void testNextForTimestampTheLastValueShouldBeEqualToTheFirstValueMinusOne() {

		long first = 0;
		long last = 0;
		long timestamp = TimestampUtil.getCurrentTimestamp();
		
		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSequenceStrategy.CONTROLLER.clearPool();
		
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();

		first = clockSequence.getClockSequence(timestamp);
		for (int i = 0; i < DefaultClockSequenceStrategy.SEQUENCE_MAX; i++) {
			last = clockSequence.getClockSequence(timestamp);
		}

		assertEquals(first - 1L, last);
	}
}
