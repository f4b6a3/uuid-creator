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
	
	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	private static final boolean TIMEBASED_UUID = true;
	private static final boolean SEQUENTIAL_UUID = false;
	
	private static final boolean REAL_MAC = true;
	private static final boolean FAKE_MAC = false;
	
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
			long timestamp = (i % 2);
			byte[] clockSequenceBytes = UUIDGenerator.getClockSequenceBytes(timestamp);
			String clockSequenceString = UUIDGenerator.toHexadecimal(clockSequenceBytes);
			
			boolean isValidClockSequence = clockSequenceString.matches(UUIDGeneratorTest.ClOCK_SEQUENCE_PATTERN);
			Assert.assertTrue(isValidClockSequence);
		}
	}
	
	public void testGetRandomUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getRandomUUID().toString();
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	public void testGetTimeBasedMACUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(UUIDGenerator.getClockInstant(), TIMEBASED_UUID, REAL_MAC);
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetSequentialMACUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(UUIDGenerator.getClockInstant(), SEQUENTIAL_UUID, REAL_MAC);
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetTimeBasedAddressUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(UUIDGenerator.getClockInstant(), TIMEBASED_UUID, FAKE_MAC);
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetSequentialAddressUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(UUIDGenerator.getClockInstant(), SEQUENTIAL_UUID, FAKE_MAC);
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	public void testGetTimeBasedMACUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = UUIDGenerator.getClockInstant();
			
			UUID uuid = UUIDGenerator.getTimeBasedUUID(instant1, TIMEBASED_UUID, REAL_MAC);
			Instant instant2 = UUIDGenerator.extractInstant(uuid);
			
			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a sequential UUID version 4 is has the correct timestamp.
	 */
	public void testGetSequentialMACUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = UUIDGenerator.getClockInstant();
			
			UUID uuid = UUIDGenerator.getTimeBasedUUID(instant1, SEQUENTIAL_UUID, REAL_MAC);
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDGenerator.getGregorianCalendarTimestamp(instant1);
			long timestamp2 = UUIDGenerator.getGregorianCalendarTimestamp(instant2);
			
			Assert.assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a name-based UUID version 3 with name space is correct.
	 */
	public void testGetNameBasedMD5UUID() {
		
		UUID namespace = UUIDGenerator.NAMESPACE_DNS;
		String name = null;
		UUID uuid = null;
		
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			name = UUIDGenerator.getRandomUUID().toString();
			uuid = UUIDGenerator.getNameBasedMD5UUID(namespace, name);
			
			byte[] namespaceBytes = UUIDGenerator.toBytes(namespace.toString().replaceAll("[^0-9a-fA-F]", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = UUIDGenerator.concat(namespaceBytes, nameBytes);
			
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
			UUIDGenerator.getTimeBasedUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
		
		start = UUIDGenerator.getClockInstant();
		for (int i = 0; i < max; i++) {
			UUIDGenerator.getSequentialUUID(); // example
		}
		end = UUIDGenerator.getClockInstant();
		long miliseconds4 = (end.toEpochMilli() - start.toEpochMilli());

		System.out.println();
		System.out.println("Running times for 100,000 UUIDs generated:");
		System.out.println("- java.util.UUID.randomUUID():       " + miliseconds1 + " ms");
		System.out.println("- UUIDGenerator.getRandomUUID():     " + miliseconds2 + " ms");
		System.out.println("- UUIDGenerator.getTimeBasedUUID():  " + miliseconds3 + " ms");
		System.out.println("- UUIDGenerator.getSequentialUUID(): " + miliseconds4 + " ms");
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
				UUIDGenerator.getTimeBasedUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
			acum3 = acum3 + miliseconds3;
			
			start = UUIDGenerator.getClockInstant();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getSequentialUUID(); // example
			}
			end = UUIDGenerator.getClockInstant();
			long miliseconds4 = (end.toEpochMilli() - start.toEpochMilli());
			acum4 = acum4 + miliseconds4;
		}

		System.out.println();
		System.out.println("Average running times for 100,000 UUIDs generated:");
		System.out.println("- java.util.UUID.randomUUID():       " + (acum1 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getRandomUUID():     " + (acum2 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getTimeBasedUUID():  " + (acum3 / rounds) + " ms");
		System.out.println("- UUIDGenerator.getSequentialUUID(): " + (acum4 / rounds) + " ms");
	}

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	public void testDemoDifferenceBetweenTimeBasedAndSequentialUUID() {

		Instant instant = UUIDGenerator.getClockInstant();
		String timeBasedUUID = UUIDGenerator.getTimeBasedUUID(instant, true, false).toString();
		String sequentialUUID = UUIDGenerator.getTimeBasedUUID(instant, false, false).toString();

		System.out.println();
		System.out.println("Demonstration:");
		System.out.println("- TimeBased UUID:          " + timeBasedUUID.toString());
		System.out.println("- Sequential UUID:         " + sequentialUUID.toString());
		System.out.println("- Original instant:        " + instant.toString());
		System.out.println("- TimeBased UUID instant:  " + UUIDGenerator.extractInstant(UUID.fromString(timeBasedUUID)));
		System.out.println("- Sequential UUID instant: " + UUIDGenerator.extractInstant(UUID.fromString(sequentialUUID)));
	}
	
	/**
	 * Test with many threads running at the same time.
	 * 
	 * It basically tests if a UUID is generated twice for more than one thread.
	 * A UUID should not be repeated.
	 */
	public void testRaceCondition() {
		
		int threadCount = (int) Math.pow(10, 2);
		int threadLoopLimit = (int) Math.pow(10, 2);
		Instant instant = UUIDGenerator.getClockInstant();
		
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new RaceConditionRunnable(i, instant, threadCount, threadLoopLimit));
			thread.start();
		}
	}
	
	private class RaceConditionException extends RuntimeException {
		private static final long serialVersionUID = 7832373879543765269L;
	}
	
	private class RaceConditionRunnable implements Runnable {
		
		private int id;
		private Instant instant;
		private UUID[][] array;
		
		private UUID uuid;
		
		public RaceConditionRunnable(int id, Instant instant, int threadCount, int threadLoopLimit) {
			this.id = id;
			this.instant = instant;
			this.array = new UUID[threadCount][threadLoopLimit];
					
		}
		
		private boolean contains(UUID uuid) {
			for (int i = 0; i < array.length ; i ++) {
				for (int j = 0; j < array[0].length; j++) {
					if (this.array[i][j] != null && this.array[i][j].equals(uuid)) {
						return true;
					}
				}
			}
			return false;
		}
		
		@Override
		public void run() {
			
			for (int i = 0; i < array[0].length; i++) {
				uuid = UUIDGenerator.getTimeBasedUUID(instant, false, false);
				if(!contains(uuid)) {
					array[id][i] = uuid;
				} else {
					// Throw an exeption if the same UUID was used by other thread.
					throw new RaceConditionException();
				}
			}
		}
	}
}
