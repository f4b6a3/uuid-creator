package com.github.f4b6a3.uuid.factory.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.factory.rfc4122.RandomBasedFactory;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomBasedFactoryTest extends UuidFactoryTest {

	@Test
	public void testGetRandomBased() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedFactory factory = new RandomBasedFactory();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testGetRandomBasedWithRandom() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		RandomBasedFactory factory = new RandomBasedFactory(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testGetRandomBasedWithRandomFactory() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomFunction randomFunction = x -> {
			final byte[] bytes = new byte[x];
			ThreadLocalRandom.current().nextBytes(bytes);
			return bytes;
		};
		RandomBasedFactory factory = new RandomBasedFactory(randomFunction);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
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
			threads[i] = new TestThread(new RandomBasedFactory(), DEFAULT_LOOP_MAX);
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
