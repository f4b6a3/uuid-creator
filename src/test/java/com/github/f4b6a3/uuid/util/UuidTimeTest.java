package com.github.f4b6a3.uuid.util;

import org.junit.Test;

import com.github.f4b6a3.uuid.util.UuidTime;

import static org.junit.Assert.*;

import java.time.Instant;

public class UuidTimeTest {

	@Test
	public void testToInstantFromInstant() {
		Instant instant1 = Instant.now();
		long timestamp1 = UuidTime.toTimestamp(instant1);
		Instant instant2 = UuidTime.toInstant(timestamp1);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testFromUnixMillisecondsToTimestamp() {
		long milliseconds = System.currentTimeMillis();
		long timestamp = UuidTime.toTimestamp(milliseconds);
		Instant instant = UuidTime.toInstant(timestamp);
		assertEquals(milliseconds, instant.toEpochMilli());
	}

	@Test
	public void testFromCurrentTimestampToUnixMilliseconds() {
		long timestamp = UuidTime.getCurrentTimestamp();
		long milliseconds = UuidTime.toUnixMilliseconds(timestamp);
		Instant instant = UuidTime.toInstant(timestamp);
		assertEquals(milliseconds, instant.toEpochMilli());
	}

	@Test
	public void testFromTimestampToUnixMilliseconds() {
		long milliseconds1 = System.currentTimeMillis();
		long timestamp = UuidTime.toTimestamp(milliseconds1);
		long milliseconds2 = UuidTime.toUnixMilliseconds(timestamp);
		assertEquals(milliseconds1, milliseconds2);
	}
}
