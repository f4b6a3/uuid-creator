package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreator;
import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.FixedClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.strategy.timestamp.StoppedTimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class TimeOrderedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testTimeOrdered() {
		boolean multicast = true;
		testGetAbstractTimeBased(UuidCreator.getTimeOrderedCreator(), multicast);
	}

	@Test
	public void testTimeOrderedWithMac() {
		boolean multicast = false;
		testGetAbstractTimeBased(UuidCreator.getTimeOrderedCreator().withMacNodeIdentifier(), multicast);
	}
	
	@Test
	public void testTimeOrderedWithHash() {
		boolean multicast = true;
		testGetAbstractTimeBased(UuidCreator.getTimeOrderedCreator().withHashNodeIdentifier(), multicast);
	}

	@Test
	public void testGetTimeOrderedWithNodeIdentifierStrategy() {

		NodeIdentifierStrategy strategy = new FixedNodeIdentifierStrategy(System.nanoTime());
		long nodeIdentifier1 = strategy.getNodeIdentifier();

		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withNodeIdentifierStrategy(strategy);

		UUID uuid = creator.create();
		long nodeIdentifier2 = UuidUtil.extractNodeIdentifier(uuid);

		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testGetTimeOrderedWithClockSequenceStrategy() {

		ClockSequenceStrategy strategy = new FixedClockSequenceStrategy((int) System.nanoTime());
		long clockseq1 = strategy.getClockSequence(0);

		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator()
				.withClockSequenceStrategy(strategy);

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
	public void testGetTimeOrderedCheckTimestamp() {

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
	public void testGetTimeOrderedCheckOrder() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator();

		// Create list of UUIDs
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		// Check if the MSBs are ordered
		long old = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long msb = list[i].getMostSignificantBits();

			if (i > 0) {
				assertTrue(msb > old);
			}
			old = msb;
		}
	}
	
	@Test
	public void testGetTimeOrderedCheckGreatestTimestamp() {

		// Check if the greatest 60 bit timestamp corresponds to the date
		long timestamp0 = 0x0fffffffffffffffL;
		Instant instant0 = Instant.parse("5236-03-31T21:21:00.684697500Z");
		assertEquals(UuidTime.toInstant(timestamp0), instant0);

		// Test the extraction of the maximum 60 bit timestamp
		long timestamp1 = 0x0fffffffffffffffL;
		TimeOrderedUuidCreator creator1 = UuidCreator.getTimeOrderedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp1));
		UUID uuid1 = creator1.create();
		long timestamp2 = UuidUtil.extractTimestamp(uuid1);
		assertEquals(timestamp1, timestamp2);

		// Test the extraction of the maximum date and time
		TimeOrderedUuidCreator creator2 = UuidCreator.getTimeOrderedCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp0));
		UUID uuid2 = creator2.create();
		Instant instant2 = UuidUtil.extractInstant(uuid2);
		assertEquals(instant0, instant2);
	}

	@Test
	public void testGetTimeOrderedInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Simulate a loop faster than the clock tick
		TimestampStrategy strategy = new StoppedTimestampStrategy(UuidTime.getCurrentTimestamp());

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			TimeOrderedUuidCreator creator = UuidCreator.getTimeOrderedCreator().withHashNodeIdentifier()
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
	public void testGetTimeOrderedWithOptionalArguments() {
		
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
			UUID uuid = UuidCreator.getTimeOrdered(instant, clockseq, nodeid);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The node identifier is incorrect", nodeid, UuidUtil.extractNodeIdentifier(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));

			// Repeat the same tests to time-based UUIDs with hardware address, ignoring the
			// node identifier

			// Create a time-based UUID with those random values
			uuid = UuidCreator.getTimeOrderedWithMac(instant, clockseq);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));

			// Repeat the same tests to time-based UUIDs with system data hash, ignoring the
			// node identifier

			// Create a time-based UUID with those random values
			uuid = UuidCreator.getTimeOrderedWithHash(instant, clockseq);

			// Check if it is valid
			assertTrue(UuidUtil.isRfc4122(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.extractInstant(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.extractClockSequence(uuid));
		}
	}
}
