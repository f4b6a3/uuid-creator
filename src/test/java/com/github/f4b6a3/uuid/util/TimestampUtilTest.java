package com.github.f4b6a3.uuid.util;

import org.junit.Test;

import static com.github.f4b6a3.uuid.util.TimestampUtil.*;

import static org.junit.Assert.*;

import java.time.Instant;

public class TimestampUtilTest {
	
	@Test
	public void testToInstant() {
		
		Instant instant1 = Instant.now();
		long timestamp1 = TimestampUtil.toTimestamp(instant1);
		Instant instant2 = TimestampUtil.toInstant(timestamp1);
		assertEquals(instant1, instant2);
	}
	
	/**
	 * It works because the resolution of TimestampUtils is milliseconds.
	 */
	@Test
	public void testGetCurrentTimestamp() {
	
		long timestamp1 = getCurrentTimestamp();
		Instant instant1 = Instant.now();
		Instant instant2 = TimestampUtil.toInstant(timestamp1);
		
		assertEquals(instant1, instant2);
	}
}
