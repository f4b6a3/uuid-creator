package com.github.small.uuid;

import java.time.Instant;
import java.util.UUID;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class UUIDGeneratorTest extends TestCase {

	private static final String PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[14][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

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

	public void testGetRandomUUIDStringIsValid() {
		String uuid = UUIDGenerator.getRandomUUIDString();
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetTimestampUUIDStringIsValid() {
		String uuid = UUIDGenerator.getTimestampUUIDString(UUIDGenerator.getClockInstant());
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetSequentialUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialUUIDString(UUIDGenerator.getClockInstant());
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetTimestampWithoutMachineAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getTimestampPrivateUUIDString(UUIDGenerator.getClockInstant());
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetSequentialWithoutMachineAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialPrivateUUIDString(UUIDGenerator.getClockInstant());
		assertTrue(uuid.toString().matches(PATTERN));
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimestampUUIDVersion1() {

		Instant instant1 = UUIDGenerator.getClockInstant();
		UUID uuid = UUID.fromString(UUIDGenerator.getTimestampUUIDString(instant1));
		Instant instant2 = UUIDGenerator.extractInstant(uuid);
		
		long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
		long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
		
		assertEquals(timestamp1, timestamp2);
	}

	/**
	 * Test if a time based UUID version 4 is has the correct timestamp.
	 */
	public void testGetSequentialUUIDVersion4() {

		Instant instant1 = UUIDGenerator.getClockInstant();
		UUID uuid = UUID.fromString(UUIDGenerator.getSequentialUUIDString(instant1));
		Instant instant2 = UUIDGenerator.extractInstant(uuid);
		
		long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
		long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);

		assertEquals(timestamp1, timestamp2);
	}
}
