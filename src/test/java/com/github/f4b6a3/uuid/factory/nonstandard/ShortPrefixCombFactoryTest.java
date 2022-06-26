package com.github.f4b6a3.uuid.factory.nonstandard;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortPrefixCombFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ShortPrefixCombFactoryTest extends UuidFactoryTest {

	private static final long DEFAULT_INTERVAL = 60_000;

	@Test
	public void testGetShortPrefixComb() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		ShortPrefixCombFactory factory = new ShortPrefixCombFactory((Random) null, null);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetShortPrefixCombCheckTime() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = (System.currentTimeMillis() / DEFAULT_INTERVAL) & 0x000000000000ffffL;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getShortPrefixComb();
		}
		long endTime = (System.currentTimeMillis() / DEFAULT_INTERVAL) & 0x000000000000ffffL;

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getMostSignificantBits() >>> 48;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetShortPrefixCombCheckTimeWithDifferentInterval() {

		int interval = 1000; // increment the prefix every 1 second interval
		ShortPrefixCombFactory factory = ShortPrefixCombFactory.builder().withInterval(interval).build();

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = (System.currentTimeMillis() / interval) & 0x000000000000ffffL;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}
		long endTime = (System.currentTimeMillis() / interval) & 0x000000000000ffffL;

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getMostSignificantBits() >>> 48;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetShortPrefixCombWithRandom() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		ShortPrefixCombFactory factory = new ShortPrefixCombFactory(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetShortPrefixCombWithRandomFunction() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomFunction randomFunction = x -> {
			final byte[] bytes = new byte[x];
			ThreadLocalRandom.current().nextBytes(bytes);
			return bytes;
		};
		ShortPrefixCombFactory factory = new ShortPrefixCombFactory(randomFunction);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetShortPrefixCombInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(new ShortPrefixCombFactory(), DEFAULT_LOOP_MAX);
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
			long x = list[i].getMostSignificantBits() >>> 48;
			long y = other[i].getMostSignificantBits() >>> 48;
			assertEquals("The UUID list is not ordered", x, y);
		}
	}
}
