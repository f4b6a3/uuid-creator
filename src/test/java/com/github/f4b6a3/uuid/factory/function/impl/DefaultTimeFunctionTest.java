package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.function.impl.DefaultTimeFunction;

import static org.junit.Assert.*;

public class DefaultTimeFunctionTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	@Test
	public void testGetTimestampTicks() {
		// 1ms = 10,000 ticks
		DefaultTimeFunction supplier = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long m1 = System.currentTimeMillis() * 10000L;
			long ts = supplier.getAsLong();
			long m2 = System.currentTimeMillis() * 10000L + 20000L; // TS can be 20,000 ticks ahead
			assertTrue("The current timstamp millisecond is incorrect", ts >= m1 && ts <= m2);
		}
	}

	@Test
	public void testGetTimestampMillisecond() {
		// 1ms = 10,000 ticks
		DefaultTimeFunction supplier = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long m1 = System.currentTimeMillis();
			long ts = supplier.getAsLong() / 10000L;
			long m2 = System.currentTimeMillis() + 1; // TS can be 1ms ahead
			assertTrue("The current timstamp millisecond is incorrect", ts >= m1 && ts <= m2);
		}
	}

	@Test
	public void testGetTimestampMonotonicity() {
		long lastTs = 0;
		DefaultTimeFunction supplier = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long ts = supplier.getAsLong();
			assertTrue("The current timstamp should be greater than the last one" + (ts - lastTs), ts > lastTs);
			lastTs = ts;
		}
	}
}
