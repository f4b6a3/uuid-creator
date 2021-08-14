package com.github.f4b6a3.uuid.util;

import org.junit.Test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UuidTimeTest {

	@Test
	public void testFromInstantToUnixTimestamp() {
		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MICROS);
		long unixTimestamp = UuidTime.toUnixTimestamp(instant1);
		Instant instant2 = UuidTime.fromUnixTimestamp(unixTimestamp);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testFromInstantToGregTimestamp() {
		Instant instant1 = Instant.now().truncatedTo(ChronoUnit.MICROS);
		long gregTimestamp = UuidTime.toGregTimestamp(instant1);
		Instant instant2 = UuidTime.fromGregTimestamp(gregTimestamp);
		assertEquals(instant1, instant2);
	}
}
