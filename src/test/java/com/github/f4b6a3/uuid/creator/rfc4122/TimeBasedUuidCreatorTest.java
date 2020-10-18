package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.FixedClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.strategy.timestamp.StoppedTimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class TimeBasedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testCreateTimeBased() {
		boolean multicast = true;
		testGetAbstractTimeBased(UuidCreator.getTimeBasedCreator(), multicast);
	}

	@Test
	public void testCreateTimeBasedWithMac() {
		boolean multicast = false;
		testGetAbstractTimeBased(UuidCreator.getTimeBasedCreator().withMacNodeIdentifier(), multicast);
	}

	@Test
	public void testCreateTimeBasedWithHash() {
		boolean multicast = true;
		testGetAbstractTimeBased(UuidCreator.getTimeBasedCreator().withHashNodeIdentifier(), multicast);
	}

	@Test
	public void testGetTimeBasedWithNodeIdentifierStrategy() {

		NodeIdentifierStrategy strategy = new FixedNodeIdentifierStrategy(System.nanoTime());
		long nodeIdentifier1 = strategy.getNodeIdentifier();

		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(strategy);

		UUID uuid = creator.create();
		long nodeIdentifier2 = UuidUtil.extractNodeIdentifier(uuid);

		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testGetTimeBasedWithClockSequenceStrategy() {

		ClockSequenceStrategy strategy = new FixedClockSequenceStrategy((int) System.nanoTime());
		long clockseq1 = strategy.getClockSequence(0);

		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(strategy);

		UUID uuid = creator.create();
		long clockseq2 = UuidUtil.extractClockSequence(uuid);

		assertEquals(clockseq1, clockseq2);
	}

	@Test
	public void testGetTimeBasedCheckTimestamp() {

		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator();

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
	public void testGetTimeBasedCheckGreatestTimestamp() {

		// Check if the greatest 60 bit timestamp corresponds to the date
		long timestamp0 = 0x0fffffffffffffffL;
		Instant instant0 = Instant.parse("5236-03-31T21:21:00.684697500Z");
		assertEquals(UuidTime.toInstant(timestamp0), instant0);

		// Test the extraction of the maximum 60 bit timestamp
		long timestamp1 = 0x0fffffffffffffffL;
		TimeBasedUuidCreator creator1 = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp1));
		UUID uuid1 = creator1.create();
		long timestamp2 = UuidUtil.extractTimestamp(uuid1);
		assertEquals(timestamp1, timestamp2);

		// Test the extraction of the maximum date and time
		TimeBasedUuidCreator creator2 = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp0));
		UUID uuid2 = creator2.create();
		Instant instant2 = UuidUtil.extractInstant(uuid2);
		assertEquals(instant0, instant2);
	}

	@Test
	public void testGetTimeBasedInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Simulate a loop faster than the clock tick
		TimestampStrategy strategy = new StoppedTimestampStrategy(UuidTime.getCurrentTimestamp());

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withHashNodeIdentifier()
					.withTimestampStrategy(strategy);
			threads[i] = new TestThread(creator, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, (DEFAULT_LOOP_MAX * THREAD_TOTAL), TestThread.hashSet.size());
	}

	@Test
	public void testGetTimeBasedWithOptionalArguments() {

		Random random = new Random();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			// Get 46 random bits to generate a date from the year 1970 to 2193.
			// (2^46 / 10000 / 60 / 60 / 24 / 365.25 + 1970 A.D. = ~2193 A.D.)
			final Instant instant = Instant.ofEpochMilli(random.nextLong() >>> 24);

			// Get 14 random bits random to generate the clock sequence
			final int clockseq = random.nextInt() & 0x000003ff;

			// Get 48 random bits for the node identifier, and set the multicast bit to ONE
			final long nodeid = random.nextLong() & 0x0000ffffffffffffL | 0x0000010000000000L;

			// Create a time-based UUID with those random values
			UUID uuid = UuidCreator.getTimeBased(instant, clockseq, nodeid);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The node identifier is incorrect", nodeid, UuidUtil.extractNodeIdentifier(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));

			// Repeat the same tests to time-based UUIDs with hardware address, ignoring the
			// node identifier

			// Create a time-based UUID with those random values
			uuid = UuidCreator.getTimeBasedWithMac(instant, clockseq);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));

			// Repeat the same tests to time-based UUIDs with system data hash, ignoring the
			// node identifier

			// Create a time-based UUID with those random values
			uuid = UuidCreator.getTimeBasedWithHash(instant, clockseq);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));
		}
	}
}
