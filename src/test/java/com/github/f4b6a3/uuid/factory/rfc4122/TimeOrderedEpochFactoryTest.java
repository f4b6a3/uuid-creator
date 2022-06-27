package com.github.f4b6a3.uuid.factory.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TimeOrderedEpochFactoryTest extends UuidFactoryTest {

	@Test
	public void testGetTimeOrderedEpoch() {

		UUID[] list;

		list = new UUID[DEFAULT_LOOP_MAX];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getTimeOrderedEpoch();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getTimeOrderedEpochPlus1();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getTimeOrderedEpochPlusN();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetTimeOrderedEpochCheckTimestamp() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long random = ThreadLocalRandom.current().nextLong(1L << 48);
			Clock clock = Clock.fixed(Instant.ofEpochMilli(random), Clock.systemUTC().getZone());

			Instant instant1 = clock.instant();
			long timestamp1 = UuidTime.toGregTimestamp(instant1);
			UUID uuid = TimeOrderedEpochFactory.builder().withClock(clock).build().create();
			Instant instant2 = UuidUtil.getInstant(uuid);
			long timestamp2 = UuidUtil.getTimestamp(uuid);

			assertEquals(instant1, instant2);
			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetTimeOrderedEpochCheckTime() {

		Clock clock = Clock.systemDefaultZone();

		UUID[] list;
		long startTime;
		long endTime;
		TimeOrderedEpochFactory factory;

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = new TimeOrderedEpochFactory(clock);

		startTime = clock.millis();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}
		// can be 1ms ahead of time
		endTime = clock.millis() + 1;

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withClock(clock).withIncrementPlus1().build();

		startTime = clock.millis();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}
		// can be 1ms ahead of time
		endTime = clock.millis() + 1;

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withClock(clock).withIncrementPlusN().build();

		startTime = clock.millis();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}
		// can be 1ms ahead of time
		endTime = clock.millis() + 1;

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
		checkCreationTime(list, startTime, endTime);
	}

	private void checkCreationTime(UUID[] list, long startTime, long endTime) {
		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getMostSignificantBits() >>> 16;
			assertTrue("UUID creation time before start time", startTime <= creationTime);
			assertTrue("UUID creation time after end time", creationTime <= endTime);
			assertTrue("UUID sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetTimeOrderedEpochWithRandom() {

		Random random = new Random();

		UUID[] list;
		TimeOrderedEpochFactory factory;

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = new TimeOrderedEpochFactory(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withRandom(random).withIncrementPlus1().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withRandom(random).withIncrementPlusN().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetTimeOrderedEpochWithRandomFunction() {

		RandomFunction randomFunction = x -> {
			final byte[] bytes = new byte[x];
			ThreadLocalRandom.current().nextBytes(bytes);
			return bytes;
		};

		UUID[] list;
		TimeOrderedEpochFactory factory;

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = new TimeOrderedEpochFactory(randomFunction);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withRandomFunction(randomFunction).withIncrementPlus1().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);

		list = new UUID[DEFAULT_LOOP_MAX];
		factory = TimeOrderedEpochFactory.builder().withRandomFunction(randomFunction).withIncrementPlusN().build();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkOrdering(list);
		checkUniqueness(list);
	}

	@Test
	public void testGetTimeOrderedEpochInParallel() throws InterruptedException {

		Random random = new Random();
		Clock clock = Clock.systemDefaultZone();

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(new TimeOrderedEpochFactory(random, clock), DEFAULT_LOOP_MAX);
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
