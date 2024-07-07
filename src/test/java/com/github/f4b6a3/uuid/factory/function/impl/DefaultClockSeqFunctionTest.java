package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Before;
import org.junit.Test;

import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;
import com.github.f4b6a3.uuid.factory.standard.TimeBasedFactory;
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

	@Test()
	public void testGetClockSequence1() {

		long first = 0;
		long last = 0;

		int max = DefaultClockSeqFunction.ClockSeqPool.POOL_MAX + 1; // 16,384
		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();

		first = clockSequence.applyAsLong(CURRENT_TIMESTAMP);

		for (int i = 0; i < max; i++) {
			last = clockSequence.applyAsLong(CURRENT_TIMESTAMP);
		}

		assertEquals(first, last);
	}

	@Test
	public void testGetClockSequence2() {

		int first = 0;
		int last = 0;

		int max = DefaultClockSeqFunction.ClockSeqPool.POOL_MAX + 1; // 16,384
		TimeBasedFactory factory = new TimeBasedFactory.Builder().withInstant(Instant.now()).build();

		UUID uuid = factory.create();

		first = UuidUtil.getClockSequence(uuid);

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			uuid = factory.create();
			last = UuidUtil.getClockSequence(uuid);
		}

		assertEquals(first, last);
	}

	@Test
	public void testGetClockSequenceUnique1() {

		HashSet<Long> set = new HashSet<>();
		int max = DefaultClockSeqFunction.ClockSeqPool.POOL_MAX; // 16,383
		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();

		for (int i = 0; i < max; i++) {
			assertTrue(DUPLICATE_UUID_MSG, set.add(clockSequence.applyAsLong(CURRENT_TIMESTAMP)));
		}
	}

	@Test
	public void testGetClockSequenceUnique2() {

		HashSet<UUID> set = new HashSet<>();
		int max = DefaultClockSeqFunction.ClockSeqPool.POOL_MAX; // 16,383
		TimeBasedFactory factory = new TimeBasedFactory.Builder().withInstant(Instant.now()).build();

		for (int i = 0; i < max; i++) {
			assertTrue(DUPLICATE_UUID_MSG, set.add(factory.create()));
		}
	}

	@Test
	public void testGetClockSequenceIncremented() {

		// It should increment if the new timestamp is LOWER THAN the old one
		long oldTimestamp = 1000;
		long newTimestamp = 999;
		DefaultClockSeqFunction clockSequence = new DefaultClockSeqFunction();
		long oldSequence = clockSequence.applyAsLong(oldTimestamp);
		long newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals((oldSequence + 1) % CLOCK_SEQUENCE_MAX, newSequence);

		// It should increment if the new timestamp is EQUAL TO the old one
		oldTimestamp = 1000;
		newTimestamp = 1000;
		clockSequence = new DefaultClockSeqFunction();
		oldSequence = clockSequence.applyAsLong(oldTimestamp);
		newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals((oldSequence + 1) % CLOCK_SEQUENCE_MAX, newSequence);

		// It should NOT increment if the new timestamp is GREATER THAN the old one
		oldTimestamp = 1000;
		newTimestamp = 1001;
		oldSequence = clockSequence.applyAsLong(oldTimestamp);
		newSequence = clockSequence.applyAsLong(newTimestamp);
		assertEquals(oldSequence, newSequence);
	}

	@Test
	public void testNextClockSequenceInParallel() throws InterruptedException {

		Thread[] threads = new Thread[CLOCK_SEQUENCE_MAX];

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
