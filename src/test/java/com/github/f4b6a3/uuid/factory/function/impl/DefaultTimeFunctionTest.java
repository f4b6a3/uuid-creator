package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultTimeFunctionTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	@Test
	public void testGetTimestampMillisecond() {
		// 1ms = 10,000 ticks
		DefaultTimeFunction function = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long m1 = System.currentTimeMillis();
			long ts = function.getAsLong() / 10000L;
			// TS can be 1ms ahead due to counter shift
			long m2 = System.currentTimeMillis() + 1;
			assertTrue("The current timstamp millisecond is incorrect", ts >= m1 && ts <= m2);
		}
	}

	@Test
	public void testGetTimestampMillisecondWithClock() {

		// 1ms = 10,000 ticks
		final long ticks = (long) Math.pow(10, 4);
		final long bound = (long) Math.pow(2, 60) / ticks;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			// instantiate a factory with a Clock that returns a fixed value
			final long millis = Math.abs(ThreadLocalRandom.current().nextLong(bound));
			Clock clock = Clock.fixed(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
			DefaultTimeFunction function = new DefaultTimeFunction(clock);

			long ts = function.getAsLong() / 10000L;

			// TS can be 1ms ahead due to counter shift
			long tolerance = millis + 1;
			assertTrue("The current timstamp millisecond is incorrect", ts >= millis && ts <= tolerance);
		}
	}

	@Test
	public void testGetTimestampMonotonicity() {
		long lastTs = 0;
		DefaultTimeFunction function = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long ts = function.getAsLong();
			assertTrue("The current timstamp should be greater than the last one", ts > lastTs);
			lastTs = ts;
		}
	}
}