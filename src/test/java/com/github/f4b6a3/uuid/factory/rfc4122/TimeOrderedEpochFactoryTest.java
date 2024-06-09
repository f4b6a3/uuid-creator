package com.github.f4b6a3.uuid.factory.rfc4122;

import org.junit.Ignore;
import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.UuidFactoryTest;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

public class TimeOrderedEpochFactoryTest extends UuidFactoryTest {

	@Test
	public void testDefault() {
		TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory();
		assertNotNull(factory.create());
	}

	@Test
	public void testWithRandom() {
		{
			Random random = new Random();
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(random);
			assertNotNull(factory.create());
		}
		{
			SecureRandom random = new SecureRandom();
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(random);
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testWithRandomFunction() {
		{
			SplittableRandom random = new SplittableRandom();
			LongSupplier function = () -> random.nextLong();
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(function);
			assertNotNull(factory.create());
		}
		{
			IntFunction<byte[]> function = (length) -> {
				byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			};
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(function);
			assertNotNull(factory.create());
		}
		{
			SplittableRandom random = new SplittableRandom();
			LongSupplier function = () -> random.nextLong();
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(function, Clock.systemDefaultZone());
			assertNotNull(factory.create());
		}
		{
			IntFunction<byte[]> function = (length) -> {
				byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			};
			TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory(function, Clock.systemDefaultZone());
			assertNotNull(factory.create());
		}
	}

	@Test
	public void testWithRandomNull() {
		TimeOrderedEpochFactory factory = new TimeOrderedEpochFactory((Random) null);
		assertNotNull(factory.create());
	}

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

		TimeOrderedEpochFactory factory;
		Clock clock = Clock.systemDefaultZone();

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = new TimeOrderedEpochFactory(clock);

			long startTime = clock.millis();
			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}
			// can be 1ms ahead of time
			long endTime = clock.millis() + 1;

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
			checkCreationTime(list, startTime, endTime);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withClock(clock).withIncrementPlus1().build();

			long startTime = clock.millis();
			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}
			// can be 1ms ahead of time
			long endTime = clock.millis() + 1;

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
			checkCreationTime(list, startTime, endTime);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withClock(clock).withIncrementPlusN().build();

			long startTime = clock.millis();
			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}
			// can be 1ms ahead of time
			long endTime = clock.millis() + 1;

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
			checkCreationTime(list, startTime, endTime);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withClock(clock).withIncrementPlusN(1_000_000).build();

			long startTime = clock.millis();
			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}
			// can be 1ms ahead of time
			long endTime = clock.millis() + 1;

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
			checkCreationTime(list, startTime, endTime);
		}
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
		TimeOrderedEpochFactory factory;

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = new TimeOrderedEpochFactory(random);

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandom(random).withIncrementPlus1().build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandom(random).withIncrementPlusN().build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandom(random).withIncrementPlusN(1_000_000).build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}
	}

	@Test
	public void testGetTimeOrderedEpochWithRandomFunction() {

		RandomFunction randomFunction = x -> {
			final byte[] bytes = new byte[x];
			ThreadLocalRandom.current().nextBytes(bytes);
			return bytes;
		};

		TimeOrderedEpochFactory factory;

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = new TimeOrderedEpochFactory(randomFunction);

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}
		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandomFunction(randomFunction).withIncrementPlus1().build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandomFunction(randomFunction).withIncrementPlusN().build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}

		{
			UUID[] list = new UUID[DEFAULT_LOOP_MAX];
			factory = TimeOrderedEpochFactory.builder().withRandomFunction(randomFunction).withIncrementPlusN(1_000_000)
					.build();

			for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
				list[i] = factory.create();
			}

			checkNotNull(list);
			checkOrdering(list);
			checkUniqueness(list);
		}
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

	@Test
	public void testWithFixedClock() {

		Instant apocalypse = Instant.parse("+10889-08-02T05:31:50.655Z");

		UUID minMinusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MIN_VALUE - 1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(apocalypse, UuidUtil.getInstant(minMinusOne));

		UUID min = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MIN_VALUE))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(Instant.EPOCH, UuidUtil.getInstant(min));

		UUID minPlusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MIN_VALUE + 1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(Instant.EPOCH.plusMillis(1), UuidUtil.getInstant(minPlusOne));

		UUID epochMinusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.EPOCH.minusMillis(1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(apocalypse, UuidUtil.getInstant(epochMinusOne));

		UUID epoch = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.EPOCH)) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(Instant.EPOCH, UuidUtil.getInstant(epoch));

		UUID epochPlusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.EPOCH.plusMillis(1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(Instant.EPOCH.plusMillis(1), UuidUtil.getInstant(epochPlusOne));

		UUID maxMinusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MAX_VALUE - 1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(apocalypse.minusMillis(1), UuidUtil.getInstant(maxMinusOne));

		UUID max = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MAX_VALUE))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(apocalypse, UuidUtil.getInstant(max));

		UUID maxPlusOne = TimeOrderedEpochFactory.builder() //
				.withClock(clock(Instant.ofEpochMilli(Long.MAX_VALUE + 1))) //
				.withRandomFunction(() -> 0).build().create();
		assertEquals(Instant.EPOCH, UuidUtil.getInstant(maxPlusOne));

		TreeSet<UUID> uuids = new TreeSet<>();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = TimeOrderedEpochFactory.builder() //
					.withClock(clock(Instant.EPOCH.minusMillis(i))) //
					.withRandomFunction(() -> 0).build().create();
			uuids.add(uuid);
			if (i == 0) {
				assertEquals(Instant.EPOCH, UuidUtil.getInstant(uuid));
			} else {
				assertEquals(apocalypse.minusMillis(i - 1), UuidUtil.getInstant(uuid));
			}
		}
		assertEquals(DEFAULT_LOOP_MAX, uuids.size());
	}

	@Test
	public void testMinAndMax() {

		long time = 0;
		Random random = new Random();
		final long mask = 0x0000ffffffffffffL;

		for (int i = 0; i < 100; i++) {

			time = random.nextLong() & mask;

			{
				// Test MIN
				Instant instant = Instant.ofEpochMilli(time);
				UUID uuid = UuidCreator.getTimeOrderedEpochMin(instant);
				assertEquals(time, uuid.getMostSignificantBits() >>> 16);
				assertEquals(0x0000000000007000L, uuid.getMostSignificantBits() & 0xffffL);
				assertEquals(0x8000000000000000L, uuid.getLeastSignificantBits());
			}

			{
				// Test MAX
				Instant instant = Instant.ofEpochMilli(time);
				UUID uuid = UuidCreator.getTimeOrderedEpochMax(instant);
				assertEquals(time, uuid.getMostSignificantBits() >>> 16);
				assertEquals(0x0000000000007fffL, uuid.getMostSignificantBits() & 0xffffL);
				assertEquals(0xbfffffffffffffffL, uuid.getLeastSignificantBits());
			}
		}
	}

	@Test
	public void testRandom() {

		long time = 0;
		Random random = new Random();
		final long mask = 0x0000ffffffffffffL;

		for (int i = 0; i < 100; i++) {
			time = random.nextLong() & mask;

			Instant instant = Instant.ofEpochMilli(time);
			UUID uuid = UuidCreator.getTimeOrderedEpoch(instant);
			assertEquals(time, uuid.getMostSignificantBits() >>> 16);
			assertEquals(0x0000000000007000L, uuid.getMostSignificantBits() & 0x7000L);
			assertEquals(0x8000000000000000L, uuid.getLeastSignificantBits() & 0x8000000000000000L);
		}
	}

	private Clock clock(Instant instant) {
		return Clock.fixed(instant, ZoneId.of("UTC"));
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

	@Ignore("it takes a long time to execute")
	public void testVeryLongMonotonicityCheck() {

		// Read: https://github.com/f4b6a3/uuid-creator/issues/69
		// You can also run the `main()` example given in the issue.

		int i = 0;
		boolean ok = true;
		UUID smaller = null;
		UUID bigger = null;

		for (i = 0; i < Integer.MAX_VALUE; i++) {
			smaller = UuidCreator.getTimeOrderedEpoch();
			bigger = UuidCreator.getTimeOrderedEpoch();

			if (smaller.compareTo(bigger) >= 0) {
				ok = false;
				break;
			}
		}

		assertTrue(String.format("Monotonicity is broken: smaller='%s', bigger='%s', i=%s", smaller, bigger, i), ok);
	}
}
