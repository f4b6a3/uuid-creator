package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Test;

import static org.junit.Assert.*;

public class WindowsTimeFunctionTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	@Test
	public void testGetTimestampMillisecond() {
		// 1ms = 10,000 ticks
		WindowsTimeFunction supplier = new WindowsTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long m1 = System.currentTimeMillis();
			long ts = supplier.getAsLong() / 10000L;
			// TS can be 48ms ahead due to time granularity and counter shift
			long m2 = System.currentTimeMillis() + 48;
			assertTrue("The current timstamp millisecond is incorrect", ts >= m1 && ts <= m2);
		}
	}

	@Test
	public void testGetTimestampMonotonicity() {
		long lastTs = 0;
		WindowsTimeFunction supplier = new WindowsTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long ts = supplier.getAsLong();
			assertTrue("The current timstamp should be greater than the last one", ts > lastTs);
			lastTs = ts;
		}
	}
}