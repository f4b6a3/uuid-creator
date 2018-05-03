package com.github.f4b6a3d8.uuid;

import java.time.Instant;
import java.util.UUID;

import junit.framework.Assert;
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
		Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.PATTERN));
	}

	public void testGetTimestampUUIDStringIsValid() {
		String uuid = UUIDGenerator.getTimestampUUIDString(UUIDGenerator.getClockInstant());
		Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.PATTERN));
	}

	public void testGetSequentialUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialUUIDString(UUIDGenerator.getClockInstant());
		Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.PATTERN));
	}

	public void testGetTimestampPrivateAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getTimestampPrivateUUIDString(UUIDGenerator.getClockInstant());
		Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.PATTERN));
	}

	public void testGetSequentialPrivateAddressUUIDStringIsValid() {
		String uuid = UUIDGenerator.getSequentialPrivateUUIDString(UUIDGenerator.getClockInstant());
		Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.PATTERN));
	}

	public void testGetTimestampUUIDStringIsTimestampCorrect() {

		Instant instant = UUIDGenerator.getClockInstant();
		String uuid = UUIDGenerator.getTimestampUUIDString(instant);

		long timestamp1 = UUID.fromString(uuid).timestamp();
		long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant);

		Assert.assertEquals(timestamp1, timestamp2);
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimestampPrivateUUIDStringIsTimestampCorrect() {

		Instant instant = UUIDGenerator.getClockInstant();
		String uuid = UUIDGenerator.getTimestampPrivateUUIDString(instant);

		long timestamp1 = UUID.fromString(uuid).timestamp();
		long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant);

		Assert.assertEquals(timestamp1, timestamp2);
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

		Assert.assertEquals(timestamp1, timestamp2);
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

		Assert.assertEquals(timestamp1, timestamp2);
	}

	/**
	 * This method only prints running times.
	 */
	public void testRunningTime() {

		long max = (long) Math.pow(10, 5);
		Instant start = null;
		Instant end = null;

		start = UUIDGenerator.getClockInstant();
		for (int i = 0; i < max; i++) {
			UUID.randomUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds1 = (end.toEpochMilli() - start.toEpochMilli());

		start = UUIDGenerator.getClockInstant();
		for (int i = 0; i < max; i++) {
			UUIDGenerator.getRandomUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds2 = (end.toEpochMilli() - start.toEpochMilli());

		start = UUIDGenerator.getClockInstant();
		for (int i = 0; i < max; i++) {
			UUIDGenerator.getSequentialPrivateUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds4 = (end.toEpochMilli() - start.toEpochMilli());

		System.out.println();
		System.out.println("Running times for 100,000 UUIDs generated:");
		System.out.println("- java.util.UUID.randomUUID():              " + miliseconds1 + " ms");
		System.out.println("- UUIDGenerator.getRandomUUID():            " + miliseconds2 + " ms");
		System.out.println("- UUIDGenerator.getSequentialPrivateUUID(): " + miliseconds4 + " ms");
	}

	/**
	 * This method only prints average running times.
	 */
	public void testRunningTimeAverage() {


		long acum1 = 0;
		long acum2 = 0;
		long acum3 = 0;
		long rounds = 10;

		for (int j = 0; j < rounds; j++) {
			
			Instant start = null;
			Instant end = null;
			long max = (long) Math.pow(10, 5);
			
			start = UUIDGenerator.getClockInstant();
			for (int i = 0; i < max; i++) {
				UUID.randomUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds1 = (end.toEpochMilli() - start.toEpochMilli());
			acum1 = acum1 + miliseconds1;

			start = UUIDGenerator.getClockInstant();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getRandomUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds2 = (end.toEpochMilli() - start.toEpochMilli());
			acum2 = acum2 + miliseconds2;
			
			start = UUIDGenerator.getClockInstant();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getSequentialPrivateUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
			acum3 = acum3 + miliseconds3;
		}

		System.out.println();
		System.out.println("Average running times for 100,000 UUIDs generated:");
		System.out.println("- java.util.UUID.randomUUID():              " + (acum1 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getRandomUUID():            " + (acum2 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getSequentialPrivateUUID(): " + (acum3 / rounds) + " ms");
	}

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	public void testDemoDifferenceBetweenTimestampAndSequentialUUID() {

		Instant instant = UUIDGenerator.getClockInstant();
		String timestampUUID = UUIDGenerator.getTimestampPrivateUUIDString(instant);
		String sequentialUUID = UUIDGenerator.getSequentialPrivateUUIDString(instant);

		System.out.println();
		System.out.println("Demonstration:");
		System.out.println("- Timestamp UUID:  " + timestampUUID.toString());
		System.out.println("- Sequential UUID: " + sequentialUUID.toString());
		System.out.println("- Original instant:        " + instant.toString());
		System.out.println("- Timestamp UUID instant:  " + UUIDGenerator.extractInstant(UUID.fromString(timestampUUID)));
		System.out.println("- Sequential UUID instant: " + UUIDGenerator.extractInstant(UUID.fromString(sequentialUUID)));
	}
}
