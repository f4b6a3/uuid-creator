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
			Assert.assertTrue(clockSequenceString.matches(UUIDGeneratorTest.ClOCK_SEQUENCE_PATTERN));
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

	public void testGetTimeBasedUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(UUIDGenerator.getClockInstant(), TIMEBASED_UUID, FAKE_MAC);
			Assert.assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	public void testGetSequentialUUID_StringIsValid() {
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
		
		UUID namespace = UUIDGenerator.NAMESPACE_URL;
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
	 * This method only prints average running times.
	 */
	public void testRunningTimeAverage() {

		long acum1 = 0;
		long acum2 = 0;
		long acum3 = 0;
		long acum4 = 0;
		long rounds = 10;

		Instant start = null;
		Instant end = null;
		long max = (long) Math.pow(10, 5);
		
		for (int j = 0; j < rounds; j++) {

			start = null;
			end = null;

			start = Instant.now();
			for (int i = 0; i < max; i++) {
				UUID.randomUUID();
			}
			end = Instant.now();
			long miliseconds1 = (end.toEpochMilli() - start.toEpochMilli());
			acum1 = acum1 + miliseconds1;

			start = Instant.now();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getRandomUUID();
			}
			end = Instant.now();
			long miliseconds2 = (end.toEpochMilli() - start.toEpochMilli());
			acum2 = acum2 + miliseconds2;

			start = Instant.now();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getTimeBasedUUID();
			}
			end = Instant.now();
			long miliseconds3 = (end.toEpochMilli() - start.toEpochMilli());
			acum3 = acum3 + miliseconds3;
			
			start = Instant.now();
			for (int i = 0; i < max; i++) {
				UUIDGenerator.getSequentialUUID();
			}
			end = Instant.now();
			long miliseconds4 = (end.toEpochMilli() - start.toEpochMilli());
			acum4 = acum4 + miliseconds4;
		}

		System.out.println();
		System.out.println(String.format("Average running times for %s UUIDs generated:", max));
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
	 * It basically tests if a UUID is generated twice in the same timestamp
	 * (100-nanoseconds) for more than one thread. A UUID should not be
	 * repeated in the same timestamp.
	 */
	public void testRaceCondition() {
		
		int threadCount = (int) Math.pow(10, 1);
		int threadLoopLimit = (int) Math.pow(10, 2);
		
		// This instant won't change during this test
		Instant instant = UUIDGenerator.getClockInstant();
		
		// Start many threads to generate a lot of UUIDs in the same instant
		// (100-nanoseconds).
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new RaceConditionRunnable(i, instant, threadCount, threadLoopLimit));
			thread.start();
		}
	}
	
	/**
	 * Runnable to test if a UUID is used more than once.
	 */
	private class RaceConditionRunnable implements Runnable {
		
		private int id = 0;
		private Instant instant = null;
		private UUID[][] array = null;
		
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
					// The current UUID have been generated before by another thread.
					throw new RuntimeException(String.format("[RaceConditionRunnable] UUID conflict: %s", uuid.toString()));
				}
			}
		}
	}
	
	public void testToNumberFromBytes() {
		for(int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], UUIDGenerator.toNumber(bytes[i]));
		}
	}
	
	public void testToBytesFromHexadecimals() {
		for(int i = 0; i < bytes.length; i++) {
			assertTrue(UUIDGenerator.equals(bytes[i], UUIDGenerator.toBytes(hexadecimals[i])));
		}
	}
	
	public void testToHexadecimalFromBytes() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], UUIDGenerator.toHexadecimal(bytes[i]));
		}
	}
	
	public void testToNumberFromHexadecimal() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], UUIDGenerator.toNumber(hexadecimals[i]));
		}
	}
	
	private long[] numbers = {
			0x0000000000000000L,
			0x0000000000000001L,
			0x0000000000000012L,
			0x0000000000000123L,
			0x0000000000001234L,
			0x0000000000012345L,
			0x0000000000123456L,
			0x0000000001234567L,
			0x0000000012345678L,
			0x0000000123456789L,
			0x000000123456789aL,
			0x00000123456789abL,
			0x0000123456789abcL,
			0x000123456789abcdL,
			0x00123456789abcdeL,
			0x0123456789abcdefL};
	
	private String[] hexadecimals = {
			"0000000000000000",
			"0000000000000001",
			"0000000000000012",
			"0000000000000123",
			"0000000000001234",
			"0000000000012345",
			"0000000000123456",
			"0000000001234567",
			"0000000012345678",
			"0000000123456789",
			"000000123456789a",
			"00000123456789ab",
			"0000123456789abc",
			"000123456789abcd",
			"00123456789abcde",
			"0123456789abcdef"};
	
	private byte[][] bytes = {
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab},
			{ (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc},
			{ (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd},
			{ (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde},
			{ (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef}};
	
//	public void testSpeed() {
//		long max = (long) Math.pow(10, 6);
//		Instant start = null;
//		Instant end = null;
//
//		start = Instant.now();
//		for (int n = 0; n < max; n++) {
//			// Put some code to test here
//			for(int i = 0; i < hexadecimals.length; i++) {
//				assertEquals(numbers[i], UUIDGenerator.toNumber(hexadecimals[i]));
//			}
//		}
//		end = Instant.now();
//		long miliseconds1 = (end.toEpochMilli() - start.toEpochMilli());
//		System.out.println("Time: " + miliseconds1 + " ms");
//	}
	
}
