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
		String uuid = UUIDGenerator.getTimestampUUIDString();
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetSequentialUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialUUIDString();
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetTimestampWithoutMachineAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getTimestampWithoutMachineAddressUUIDString();
		assertTrue(uuid.toString().matches(PATTERN));
	}

	public void testGetSequentialWithoutMachineAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialWithoutMachineAddressUUIDString();
		assertTrue(uuid.toString().matches(PATTERN));
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimestampUUIDVersion1() {

		Instant instant = Instant.now();
		UUID uuid = UUIDGenerator.getTimestampUUID(instant);
		Instant uuidInstant = UUIDGenerator.extractInstant(uuid);

		assertEquals(instant, uuidInstant);
	}

	/**
	 * Test if a time based UUID version 4 is has the correct timestamp.
	 */
	public void testGetSequentialUUIDVersion4() {

		Instant instant = Instant.now();
		UUID uuid = UUIDGenerator.getSequentialUUID(instant);
		Instant uuidInstant = UUIDGenerator.extractInstant(uuid);

		assertEquals(instant, uuidInstant);
	}
}
