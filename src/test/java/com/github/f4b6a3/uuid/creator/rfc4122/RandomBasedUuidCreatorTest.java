package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.strategy.RandomStrategy;
import com.github.f4b6a3.uuid.strategy.random.OtherRandomStrategy;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;

public class RandomBasedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testGetRandomBased() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testGetRandomBasedWithRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withRandomGenerator(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}
	
	@Test
	public void testGetRandomBasedWithRandomStrategy() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		RandomStrategy strategy = new OtherRandomStrategy(random);
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withRandomStrategy(strategy);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}
	
	@Test
	public void testGetRandomBasedInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(UuidCreator.getRandomBasedCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, TestThread.hashSet.size(), (DEFAULT_LOOP_MAX * THREAD_TOTAL));
	}
}
