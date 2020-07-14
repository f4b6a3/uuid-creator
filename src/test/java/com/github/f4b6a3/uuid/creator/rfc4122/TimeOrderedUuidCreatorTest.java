package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreator;
import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.FixedClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.HashSet;
import java.util.UUID;

public class TimeOrderedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testTimeOrderedUuid() {
		boolean multicast = true;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeOrderedCreator(), multicast);
	}

	@Test
	public void testTimeOrderedUuidWithMac() {
		boolean multicast = false;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeOrderedCreator().withMacNodeIdentifier(), multicast);
	}

	@Test
	public void testGetTimeOrderedUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeOrdered();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeOrderedWithMacStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeOrderedWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeOrderedWithCustomNodeIdentifierStrategy() {

		NodeIdentifierStrategy customStrategy = new FixedNodeIdentifierStrategy(System.nanoTime());
		long nodeIdentifier1 = customStrategy.getNodeIdentifier();

		AbstractTimeBasedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withNodeIdentifierStrategy(customStrategy);

		UUID uuid = creator.create();
		long nodeIdentifier2 = UuidUtil.extractNodeIdentifier(uuid);

		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testGetTimeOrderedWithCustomClockSequenceStrategy() {

		ClockSequenceStrategy customStrategy = new FixedClockSequenceStrategy((int) System.nanoTime());
		long clockseq1 = customStrategy.getClockSequence(0);

		AbstractTimeBasedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withClockSequenceStrategy(customStrategy);

		UUID uuid = creator.create();
		long clockseq2 = UuidUtil.extractClockSequence(uuid);

		assertEquals(clockseq1, clockseq2);
	}

	@Test
	public void testGetTimeOrderedTimestampBitsAreTimeOrdered() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newTimestamp = UuidUtil.extractTimestamp(list[i]);

			if (i > 0) {
				assertTrue(newTimestamp >= oldTimestemp);
			}
			oldTimestemp = newTimestamp;
		}
	}

	@Test
	public void testGetTimeOrderedMostSignificantBitsAreTimeOrdered() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newMsb = list[i].getMostSignificantBits();

			if (i > 0) {
				assertTrue(newMsb >= oldMsb);
			}
			oldMsb = newMsb;
		}
	}

	@Test
	public void testGetTimeOrderedTimestampIsCorrect() {

		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = creator.withTimestampStrategy(new FixedTimestampStretegy(instant1)).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = UuidTime.toTimestamp(instant1);
			long timestamp2 = UuidTime.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetTimeOrderedShouldCreateAlmostSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int max = 0x3fff + 1; // 16,384
		Instant instant = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Reset the static ClockSequenceController
		// It could affect this test case
		DefaultClockSequenceStrategy.CONTROLLER.clearPool();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(instant));

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = creator.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.extractClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.extractClockSequence(uuid);
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
	public void testGetTimeOrderedParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			threads[i] = new TestThread(UuidCreator.getTimeOrderedCreator(), DEFAULT_LOOP_MAX);
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
