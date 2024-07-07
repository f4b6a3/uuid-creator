package com.github.f4b6a3.uuid.factory.standard;

import org.junit.Ignore;
import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;
import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.util.UuidComparator;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.SplittableRandom;
import java.util.UUID;

public class TimeBasedFactoryTest extends UuidFactoryTest {

	@Test
	public void testCreateTimeBased() {
		boolean multicast = true;
		testGetAbstractTimeBased(new TimeBasedFactory(), multicast);
	}

	@Test
	public void testCreateTimeBasedWithMac() {
		boolean multicast = false;
		testGetAbstractTimeBased(TimeBasedFactory.builder().withMacNodeId().build(), multicast);
	}

	@Test
	public void testCreateTimeBasedWithHash() {
		boolean multicast = true;
		testGetAbstractTimeBased(TimeBasedFactory.builder().withHashNodeId().build(), multicast);
	}

	@Test
	public void testGetTimeBasedWithNodeIdFunction() {

		long nodeid = NodeIdFunction.toExpectedRange(System.nanoTime());
		NodeIdFunction nodeIdSupplier = () -> nodeid;
		long nodeIdentifier1 = nodeIdSupplier.getAsLong();

		UUID uuid = TimeBasedFactory.builder().withNodeIdFunction(nodeIdSupplier).build().create();

		long nodeIdentifier2 = UuidUtil.getNodeIdentifier(uuid);

		assertEquals(nodeIdentifier1, nodeIdentifier2);
	}

	@Test
	public void testGetTimeBasedWithClockSeqFunction() {

		long clockSeq = ClockSeqFunction.toExpectedRange((int) System.nanoTime());
		ClockSeqFunction clockSeqOperator = x -> clockSeq;
		long clockseq1 = clockSeqOperator.applyAsLong(0);

		UUID uuid = TimeBasedFactory.builder().withClockSeqFunction(clockSeqOperator).build().create();

		long clockseq2 = UuidUtil.getClockSequence(uuid);

		assertEquals(clockseq1, clockseq2);
	}

	@Test
	public void testGetTimeBasedWithInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MICROS);
			UUID uuid = TimeBasedFactory.builder().withInstant(instant1).build().create();
			Instant instant2 = UuidUtil.getInstant(uuid).truncatedTo(ChronoUnit.MICROS);
			assertEquals(instant1, instant2);
		}
	}

	@Test
	public void testGetTimeBasedCheckTimestamp() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();
			UUID uuid = TimeBasedFactory.builder().withInstant(instant1).build().create();
			Instant instant2 = UuidUtil.getInstant(uuid);

			long timestamp1 = UuidTime.toGregTimestamp(instant1);
			long timestamp2 = UuidTime.toGregTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetTimeBasedCheckGreatestTimestamp() {

		// Check if the greatest 60 bit timestamp corresponds to the date
		long maxTimestamp = 0x0fffffffffffffffL;
		Instant maxInstant = Instant.parse("5236-03-31T21:21:00.684697500Z");

		UUID uuid = TimeBasedFactory.builder().withInstant(maxInstant).build().create();

		// Test the extraction of the maximum 60 bit timestamp
		long timestamp = UuidUtil.getTimestamp(uuid);
		assertEquals(maxTimestamp, timestamp);

		// Test the extraction of the maximum date and time
		Instant instant = UuidUtil.getInstant(uuid);
		assertEquals(maxInstant, instant);
	}

	@Test
	public void testGetTimeBasedInParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			TimeBasedFactory factory = TimeBasedFactory.builder().withHashNodeId().build();
			threads[i] = new TestThread(factory, DEFAULT_LOOP_MAX);
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
		SplittableRandom random = new SplittableRandom(1);
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
			assertTrue(UuidUtil.isStandard(uuid));

			// Check if the embedded values are correct.
			assertEquals("The timestamp is incorrect.", instant, UuidUtil.getInstant(uuid));
			assertEquals("The node identifier is incorrect", nodeid, UuidUtil.getNodeIdentifier(uuid));
			assertEquals("The clock sequence is incorrect", clockseq, UuidUtil.getClockSequence(uuid));
		}
	}

	@Test
	public void testMinAndMax() {

		long time = 0;
		SplittableRandom random = new SplittableRandom(1);
		final long mask = 0x0fffffffffffffffL;

		for (int i = 0; i < 100; i++) {

			time = (random.nextLong() & mask);

			{
				// Test MIN
				Instant instant = UuidTime.fromGregTimestamp(time);
				UUID uuid = UuidCreator.getTimeBasedMin(instant);
				assertEquals(time, uuid.timestamp());
				assertEquals(0x8000000000000000L, uuid.getLeastSignificantBits());
			}

			{
				// Test MAX
				Instant instant = UuidTime.fromGregTimestamp(time);
				UUID uuid = UuidCreator.getTimeBasedMax(instant);
				assertEquals(time, uuid.timestamp());
				assertEquals(0xbfffffffffffffffL, uuid.getLeastSignificantBits());
			}
		}
	}

	@Ignore("it takes a long time to execute")
	public void testVeryLongMonotonicityCheck() {

		// Read: https://github.com/f4b6a3/uuid-creator/issues/69
		// You can also run the `main()` example given in the issue.

		int i = 0;
		boolean ok = true;
		UUID smaller = null;
		UUID bigger = null;

		for (i = 0; i < Integer.MAX_VALUE; i++) {
			smaller = UuidCreator.getTimeBased();
			bigger = UuidCreator.getTimeBased();

			if (UuidComparator.defaultCompare(smaller, bigger) >= 0) {
				ok = false;
				break;
			}
		}

		assertTrue(String.format("Monotonicity is broken: smaller='%s', bigger='%s', i=%s", smaller, bigger, i), ok);
	}
}