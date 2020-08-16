package com.github.f4b6a3.uuid.strategy.clockseq;

import org.junit.Test;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.util.UuidTime;
import java.util.HashSet;

import static org.junit.Assert.*;

public class DefaultClockSequenceStrategyTest {

	protected static final int THREAD_TOTAL = availableProcessors();
	protected static final int CLOCK_SEQUENCE_MAX = 16384; // 2^16
	private static final long CURRENT_TIMESTAMP = UuidTime.getCurrentTimestamp();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

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

		// It should NOT increment if the new timestamp is GREATER THAN the old
		// timestamp
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
		long timestamp = UuidTime.getCurrentTimestamp();

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

	@Test
	public void testNextClockSequenceWithParallelThreads() throws InterruptedException {

		Thread[] threads = new Thread[CLOCK_SEQUENCE_MAX];

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSequenceStrategy.CONTROLLER.clearPool();

		// Start threads threads
		for (int i = 0; i < CLOCK_SEQUENCE_MAX; i++) {
			DefaultClockSequenceStrategy strategy = new DefaultClockSequenceStrategy();
			threads[i] = new TestThread(strategy, i);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		HashSet<Integer> unique = new HashSet<>();
		for (Integer i : TestThread.list) {
			unique.add(i);
		}
		// Check if the quantity of unique values is correct
		assertEquals("Duplicate clock sequence", CLOCK_SEQUENCE_MAX, unique.size());
	}

	private static class TestThread extends Thread {

		private int index;
		public static int[] list = new int[CLOCK_SEQUENCE_MAX];

		private DefaultClockSequenceStrategy strategy;

		public TestThread(DefaultClockSequenceStrategy strategy, int index) {
			this.strategy = strategy;
			this.index = index;
		}

		@Override
		public void run() {
			list[index] = strategy.getClockSequence(CURRENT_TIMESTAMP);
		}
	}
}