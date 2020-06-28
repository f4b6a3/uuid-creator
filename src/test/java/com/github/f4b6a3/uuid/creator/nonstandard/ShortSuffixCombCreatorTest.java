package com.github.f4b6a3.uuid.creator.nonstandard;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortSuffixCombCreator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class ShortSuffixCombCreatorTest extends AbstractUuidCreatorTest {

	private static final long ONE_MINUTE = 60_000;
	
	@Test
	public void testCompGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = (System.currentTimeMillis() / ONE_MINUTE) & 0x000000000000ffffL;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getShortSuffixComb();
		}

		long endTime = (System.currentTimeMillis() / ONE_MINUTE) & 0x000000000000ffffL;

		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = (list[i].getLeastSignificantBits() & 0x0000ffff00000000L) >>> 32;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testCombGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		ShortSuffixCombCreator creator = UuidCreator.getShortSuffixCombCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertNotNull("UUID is null", list[i]);
		}

		checkUniqueness(list);
	}

	@Test
	public void testCombGuidWithCustomRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		ShortSuffixCombCreator creator = UuidCreator.getShortSuffixCombCreator().withRandomGenerator(random);
		;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertNotNull("UUID is null", list[i]);
		}

		checkUniqueness(list);
	}

	@Test
	public void testCombGuidWithCustomRandomGeneratorSecure() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new SecureRandom();
		ShortSuffixCombCreator creator = UuidCreator.getShortSuffixCombCreator().withRandomGenerator(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertNotNull("UUID is null", list[i]);
		}

		checkUniqueness(list);
	}
	
	@Test
	public void testGetShortSuffixCombParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(UuidCreator.getShortSuffixCombCreator(), DEFAULT_LOOP_MAX);
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
