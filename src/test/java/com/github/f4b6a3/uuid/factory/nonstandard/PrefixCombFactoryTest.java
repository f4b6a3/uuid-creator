package com.github.f4b6a3.uuid.factory.nonstandard;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.util.CombUtil;
import com.github.f4b6a3.uuid.util.UuidTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PrefixCombFactoryTest extends UuidFactoryTest {

	@Test
	public void testGetPrefixComb() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		PrefixCombFactory factory = new PrefixCombFactory();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetPrefixCombCheckTimestamp() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long random = ThreadLocalRandom.current().nextLong(1L << 48);
			Clock clock = Clock.fixed(Instant.ofEpochMilli(random), Clock.systemUTC().getZone());

			Instant instant1 = clock.instant();
			long timestamp1 = UuidTime.toUnixTimestamp(instant1);
			UUID uuid = PrefixCombFactory.builder().withClock(clock).build().create();
			Instant instant2 = CombUtil.getPrefixInstant(uuid);
			long timestamp2 = CombUtil.getPrefix(uuid) * 10_000;

			assertEquals(instant1, instant2);
			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetPrefixCombCheckTime() {

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
	public void testGetPrefixCombWithRandom() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		PrefixCombFactory factory = new PrefixCombFactory(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetPrefixCombWithRandomFunction() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomFunction randomFunction = x -> {
			final byte[] bytes = new byte[x];
			ThreadLocalRandom.current().nextBytes(bytes);
			return bytes;
		};
		PrefixCombFactory factory = new PrefixCombFactory(randomFunction);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
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
			threads[i] = new TestThread(new PrefixCombFactory(), DEFAULT_LOOP_MAX);
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
