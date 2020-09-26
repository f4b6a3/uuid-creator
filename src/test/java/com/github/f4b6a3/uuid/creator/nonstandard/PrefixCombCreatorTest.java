package com.github.f4b6a3.uuid.creator.nonstandard;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.PrefixCombCreator;
import com.github.f4b6a3.uuid.strategy.RandomStrategy;
import com.github.f4b6a3.uuid.strategy.random.OtherRandomStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class PrefixCombCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testGetPrefixComb() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		PrefixCombCreator creator = UuidCreator.getPrefixCombCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testPrefixCombCheckTime() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getPrefixComb();
		}

		long endTime = System.currentTimeMillis();

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getMostSignificantBits() >>> 16;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetPrefixCombWithRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		PrefixCombCreator creator = UuidCreator.getPrefixCombCreator().withRandomGenerator(random);
		;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetPrefixCombWithRandomStrategy() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		RandomStrategy strategy = new OtherRandomStrategy(random);
		PrefixCombCreator creator = UuidCreator.getPrefixCombCreator().withRandomStrategy(strategy);
		;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertNotNull("UUID is null", list[i]);
		}

		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetPrefixCombInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(UuidCreator.getPrefixCombCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL));
	}

	@Override
	protected void checkOrdering(UUID[] list) {
		UUID[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			long x = list[i].getMostSignificantBits() >>> 16;
			long y = other[i].getMostSignificantBits() >>> 16;
			assertEquals("The UUID list is not ordered", x, y);
		}
	}
}
