package com.github.f4b6a3.uuid.factory.function.impl;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.function.impl.DefaultTimeFunction;

import static org.junit.Assert.*;

public class DefaultTimeFunctionTest {

	private static final int DEFAULT_LOOP_MAX = 1_000_000;

	@Test
	public void testGetTimestamp() {
		long lastTs = 0;
		DefaultTimeFunction supplier = new DefaultTimeFunction();
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long ts = supplier.getAsLong();
			assertTrue("The next timstamp should be greater than the last timestamp " + (ts - lastTs), ts > lastTs);
			lastTs = ts;
		}
	}
}
