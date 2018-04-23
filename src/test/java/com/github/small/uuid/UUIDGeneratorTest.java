package com.github.small.uuid;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.UUID;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class UUIDGeneratorTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public UUIDGeneratorTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(UUIDGeneratorTest.class);
	}

	/**
	 * Test if the first part of the UUID contains date and time information.
	 */
	public void testGetHalfRandomUUID() {
		
		Instant instant = Instant.now();
		UUID uuid = UUIDGenerator.getCustomUUID(instant);
		
		long seconds = instant.getLong(ChronoField.INSTANT_SECONDS);
		long milliSeconds = instant.getLong(ChronoField.MILLI_OF_SECOND);
		
		String secondsUUID = uuid.toString().substring(0, 8);
		String milliSecondsUUID = uuid.toString().substring(9, 13);

		assertEquals(seconds, (long) Long.parseLong(secondsUUID, 16));
		assertEquals(milliSeconds, (long) Long.parseLong(milliSecondsUUID, 16));
	}
}
