package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Before;
import org.junit.Test;

import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultClockSeqFunction;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeBasedFactory;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedFactory;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

public class DefaultClockSeqFunctionTest {

	protected static final int THREAD_TOTAL = availableProcessors();
	protected static final int CLOCK_SEQUENCE_MAX = 16384; // 2^16
	private static final long CURRENT_TIMESTAMP = UuidTime.getGregTimestamp();

	protected static final String DUPLICATE_UUID_MSG = "A duplicate UUID was created";

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	@Before
	public void before() {
		// Reset the static ClockSequenceController
		// It could affect the test cases
		DefaultClockSeqFunction.POOL.clearPool();
	}

	@Test
	public void testNextForTheClockSequenceShouldBeIncrementedIfTheNewTimestampIsLowerOrEqualToTheOldTimestamp() {

		// It should increment if the new timestamp is LOWER THAN the old timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 999;
		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();
		long oldSequence = clockSequence.applyAsLong(oldTimestamp);
		long newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals((oldSequence + 1) % CLOCK_SEQUENCE_MAX, newSequence);

		// It should increment if the new timestamp is EQUAL TO the old timestamp
		oldTimestamp = 1000;
		newTimestamp = 1000;
		clockSequence = new DefaultClockSeqFunction();
		oldSequence = clockSequence.applyAsLong(oldTimestamp);
		newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals((oldSequence + 1) % CLOCK_SEQUENCE_MAX, newSequence);
	}

	@Test
	public void testNextForTheClockSequenceShouldNotIncrementIfTheNewTimestampIsGreaterThanTheOldTimestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old
		// timestamp
		long oldTimestamp = 1000;
		long newTimestamp = 1001;
		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();
		long oldSequence = clockSequence.applyAsLong(oldTimestamp);
		long newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals(oldSequence, newSequence);
	}

	@Test()
	public void testNextForTimestampTheLastValueShouldBeEqualToTheFirstValueMinusOne() {

		long first = 0;
		long last = 0;

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSeqFunction.POOL.clearPool();

		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();

		first = clockSequence.applyAsLong(CURRENT_TIMESTAMP);
		for (int i = 0; i < ClockSeqFunction.ClockSeqPool.POOL_MAX; i++) {
			last = clockSequence.applyAsLong(CURRENT_TIMESTAMP);
		}

		assertEquals(first - 1L, last);
	}

	@Test
	public void testNextClockSequenceWithParallelThreads() throws InterruptedException {

		Thread[] threads = new Thread[CLOCK_SEQUENCE_MAX];

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSeqFunction.POOL.clearPool();

		// Start threads threads
		for (int i = 0; i < CLOCK_SEQUENCE_MAX; i++) {
			DefaultClockSeqFunction strategy = new DefaultClockSeqFunction();
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

	@Test
	public void testGetTimeBasedCheckClockSequence() {

		int max = 0x3fff + 1; // 16,384
		Instant instant = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeBasedFactory factory = new TimeBasedFactory.Builder().withInstant(instant).build();

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = factory.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.getClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.getClockSequence(uuid);
			}

			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertEquals(DUPLICATE_UUID_MSG, set.size(), max);
		assertEquals("The last clock sequence should be equal to the first clock sequence minus 1",
				(lastClockSeq % max), ((firstClockSeq % max) - 1));
	}

	@Test
	public void testGetTimeOrderedCheckClockSequence() {

		int max = 0x3fff + 1; // 16,384
		Instant instant = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeOrderedFactory factory = new TimeOrderedFactory.Builder().withInstant(instant).build();

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = factory.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.getClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.getClockSequence(uuid);
			}
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertEquals(DUPLICATE_UUID_MSG, set.size(), max);
		assertEquals("The last clock sequence should be equal to the first clock sequence minus 1",
				(lastClockSeq % max), ((firstClockSeq % max) - 1));
	}

	private static class TestThread extends Thread {

		private int index;
		public static int[] list = new int[CLOCK_SEQUENCE_MAX];

		private ClockSeqFunction strategy;

		public TestThread(ClockSeqFunction strategy, int index) {
			this.strategy = strategy;
			this.index = index;
		}

		@Override
		public void run() {
			list[index] = (int) strategy.applyAsLong(CURRENT_TIMESTAMP);
		}
	}
}
