package com.github.f4b6a3.uuid;

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

	private static final long DEFAULT_LOOP_LIMIT = (int) Math.pow(10, 3);
	
	private static final String ClOCK_SEQUENCE_PATTERN = "^[89ab][0-9a-fA-F]{3}$";
	
	// Ivalid: bc9715e0-77fd-11e8-De8a-3dd3a89571d7
	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

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

	public void testGetClockSequence() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			boolean incrementClockSequence = ((i % 2) == 0);
			long clockSequence = UUIDGenerator.getClockSequence(incrementClockSequence);
			byte[] clockSequenceBytes = UUIDGenerator.copy(UUIDGenerator.copy(UUIDGenerator.toBytes(clockSequence)), 6, 8);
			String clockSequenceString = UUIDGenerator.toHexadecimal(clockSequenceBytes);
			
			boolean isValidClockSequence = clockSequenceString.matches(UUIDGeneratorTest.ClOCK_SEQUENCE_PATTERN);
			Assert.assertTrue(isValidClockSequence);
		}
	}
	
	public void testGetRandomUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getRandomUUIDString();
			if(!uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN)) {
				System.out.println(uuid.toString());
			}
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	public void testGetTimestampUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getTimestampUUIDString(UUIDGenerator.getClockInstant());
			if(!uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN)) {
				System.out.println(uuid.toString());
			}
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetSequentialUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getSequentialUUIDString(UUIDGenerator.getClockInstant());
			if (!uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN)) {
				System.out.println(uuid.toString());
			}
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetTimestampPrivateAddressUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getTimestampPrivateUUIDString(UUIDGenerator.getClockInstant());
			if(!uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN)) {
				System.out.println(uuid.toString());
			}
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetSequentialPrivateAddressUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getSequentialPrivateUUIDString(UUIDGenerator.getClockInstant());
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetTimestampUUIDStringIsTimestampCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant = UUIDGenerator.getClockInstant();
			String uuid = UUIDGenerator.getTimestampUUIDString(instant);

			long timestamp1 = UUID.fromString(uuid).timestamp();
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant);

			Assert.assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimestampPrivateUUIDStringIsTimestampCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant = UUIDGenerator.getClockInstant();
			String uuid = UUIDGenerator.getTimestampPrivateUUIDString(instant);
	
			long timestamp1 = UUID.fromString(uuid).timestamp();
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimestampUUIDVersion1() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant1 = UUIDGenerator.getClockInstant();
			UUID uuid = UUID.fromString(UUIDGenerator.getTimestampUUIDString(instant1));
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a time based UUID version 1 without hardware adress is has the
	 * correct timestamp.
	 */
	public void testGetTimestampPrivateUUIDVersion1() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant1 = UUIDGenerator.getClockInstant();
			UUID uuid = UUID.fromString(UUIDGenerator.getTimestampPrivateUUIDString(instant1));
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a sequential UUID version 4 is has the correct timestamp.
	 */
	public void testGetSequentialUUIDVersion4() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant1 = UUIDGenerator.getClockInstant();
			UUID uuid = UUID.fromString(UUIDGenerator.getSequentialUUIDString(instant1));
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a sequencial UUID version 4 without hardware adress is has the
	 * correct timestamp.
	 */
	public void testGetSequentialPrivateUUIDVersion4() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			Instant instant1 = UUIDGenerator.getClockInstant();
			UUID uuid = UUID.fromString(UUIDGenerator.getSequentialPrivateUUIDString(instant1));
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a name-based UUID version 3 is correct.
	 */
	public void testGetNameBasedUUIDVersion3() {
		
		byte[] bytes = null;
		String name = null;
		UUID uuid = null;
		
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			name = UUIDGenerator.toHexadecimal(UUIDGenerator.getRandomBytes(32));
			bytes = name.getBytes();
			uuid = UUIDGenerator.getNameBasedUUID(name);
			
			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
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
			UUIDGenerator.getTimestampPrivateUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
		
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
		System.out.println("- UUIDGenerator.getTimestampPrivateUUID():  " + miliseconds3 + " ms");
		System.out.println("- UUIDGenerator.getSequentialPrivateUUID(): " + miliseconds4 + " ms");
	}

	/**
	 * This method only prints average running times.
	 */
	public void testRunningTimeAverage() {

		long acum1 = 0;
		long acum2 = 0;
		long acum3 = 0;
		long acum4 = 0;
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
				UUIDGenerator.getTimestampPrivateUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
			acum3 = acum3 + miliseconds3;
			
			start = UUIDGenerator.getClockInstant();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getSequentialPrivateUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds4 = (end.toEpochMilli() - start.toEpochMilli());
			acum4 = acum4 + miliseconds4;
		}

		System.out.println();
		System.out.println("Average running times for 100,000 UUIDs generated:");
		System.out.println("- java.util.UUID.randomUUID():              " + (acum1 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getRandomUUID():            " + (acum2 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getTimestampPrivateUUID():  " + (acum3 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getSequentialPrivateUUID(): " + (acum4 / rounds) + " ms");
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
		System.out
				.println("- Timestamp UUID instant:  " + UUIDGenerator.extractInstant(UUID.fromString(timestampUUID)));
		System.out
				.println("- Sequential UUID instant: " + UUIDGenerator.extractInstant(UUID.fromString(sequentialUUID)));
	}
	
	
	public void testRaceCondition() {
		
		int threadCount = (int) Math.pow(10, 2);
		int threadLoopLimit = (int) Math.pow(10, 2);
		Instant instant = UUIDGenerator.getClockInstant();
		String[][] uuidArray = new String[threadCount][threadLoopLimit]; 
		
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new RaceConditionRunnable(i, instant, threadCount, threadLoopLimit, uuidArray));
			thread.start();
		}
	}
	
	private class RaceConditionException extends RuntimeException {
		private static final long serialVersionUID = 7832373879543765269L;
	}
	
	private class RaceConditionRunnable implements Runnable {
		
		private int id;
		private Instant instant;
		private int threadCount;
		private int threadLoopLimit;
		private String[][] uuidArray;
		
		private String uuid;
		
		public RaceConditionRunnable(int id, Instant instant, int threadCount, int threadLoopLimit, String[][] uuidArray) {
			this.id = id;
			this.instant = instant;
			this.threadCount = threadCount;
			this.threadLoopLimit = threadLoopLimit;
			this.uuidArray = uuidArray;
					
		}
		
		private boolean contains(String uuid) {
			for (int i = 0; i < threadCount ; i ++) {
				for (int j = 0; j < threadLoopLimit; j++) {
					if (this.uuidArray[i][j] != null && this.uuidArray[i][j].equals(uuid)) {
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public void run() {
			
			for (int i = 0; i < threadLoopLimit; i++) {
				uuid = UUIDGenerator.getSequentialPrivateUUIDString(instant);
				if(!contains(uuid)) {
					uuidArray[id][i] = uuid;
				} else {
					throw new RaceConditionException();
				}
			}
		}
	}
}
