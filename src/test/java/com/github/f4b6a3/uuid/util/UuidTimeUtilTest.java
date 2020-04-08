package com.github.f4b6a3.uuid.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Instant;

public class UuidTimeUtilTest {

	@Test
	public void testToInstantFromInstant() {
		Instant instant1 = Instant.now();
		long timestamp1 = UuidTimeUtil.toTimestamp(instant1);
		Instant instant2 = UuidTimeUtil.toInstant(timestamp1);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testFromUnixMillisecondsToTimestamp() {
		long milliseconds = System.currentTimeMillis();
		long timestamp = UuidTimeUtil.toTimestamp(milliseconds);
		Instant instant = UuidTimeUtil.toInstant(timestamp);
		assertEquals(milliseconds, instant.toEpochMilli());
	}
	
	@Test
	public void testFromCurrentTimestampToUnixMilliseconds() {
		long timestamp = UuidTimeUtil.getCurrentTimestamp();
		long milliseconds = UuidTimeUtil.toUnixMilliseconds(timestamp);
		Instant instant = UuidTimeUtil.toInstant(timestamp);
		assertEquals(milliseconds, instant.toEpochMilli());
	}
	
	@Test
	public void testFromTimestampToUnixMilliseconds() {
		long milliseconds1 = System.currentTimeMillis();
		long timestamp = UuidTimeUtil.toTimestamp(milliseconds1);
		long milliseconds2 = UuidTimeUtil.toUnixMilliseconds(timestamp);
		assertEquals(milliseconds1, milliseconds2);
	}
}
