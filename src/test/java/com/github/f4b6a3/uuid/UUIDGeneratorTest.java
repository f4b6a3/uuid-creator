package com.github.f4b6a3.uuid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class UUIDGeneratorTest {

	private static final long DEFAULT_LOOP_LIMIT = (long) Math.pow(10, 3);
	
	private static final String ClOCK_SEQUENCE_PATTERN = "^[89ab][0-9a-fA-F]{3}$";
	
	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	private static final boolean TIMEBASED_UUID = true;
	private static final boolean SEQUENTIAL_UUID = false;
	
	private static final boolean REAL_MAC = true;
	private static final boolean FAKE_MAC = false;
	
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
	
	@Test
	public void testGetRandomUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String uuid = UUIDGenerator.getRandomUUID().toString();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	@Test
	public void testGetTimeBasedMACUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(Instant.now(), TIMEBASED_UUID, REAL_MAC);
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialMACUUIDStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(Instant.now(), SEQUENTIAL_UUID, REAL_MAC);
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetTimeBasedUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(Instant.now(), TIMEBASED_UUID, FAKE_MAC);
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID(Instant.now(), SEQUENTIAL_UUID, FAKE_MAC);
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	@Test
	public void testGetTimeBasedMACUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = Instant.now();
			
			UUID uuid = UUIDGenerator.getTimeBasedUUID(instant1, TIMEBASED_UUID, REAL_MAC);
			Instant instant2 = UUIDGenerator.extractInstant(uuid);
			
			long timestamp1 = UUIDClock.getTimestamp(instant1);
			long timestamp2 = UUIDClock.getTimestamp(instant2);
			
			assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a sequential UUID version 4 is has the correct timestamp.
	 */
	@Test
	public void testGetSequentialMACUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = Instant.now();
			
			UUID uuid = UUIDGenerator.getTimeBasedUUID(instant1, SEQUENTIAL_UUID, REAL_MAC);
			Instant instant2 = UUIDGenerator.extractInstant(uuid);

			long timestamp1 = UUIDClock.getTimestamp(instant1);
			long timestamp2 = UUIDClock.getTimestamp(instant2);
			
			assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a name-based UUID version 3 with name space is correct.
	 */
	@Test
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
	 * Just prints UUIDs generated to a specific instant.
	 */
	@Test
	public void testDemoDifferenceBetweenTimeBasedAndSequentialUUID() {

		Instant instant = Instant.now();
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
	@Test
	public void testRaceCondition() {
		
		int threadCount = (int) Math.pow(10, 2);
		int threadLoopLimit = (int) Math.pow(10, 2);
		
		// This instant won't change during this test
		Instant instant = Instant.now();
		
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
	
	@Test
	public void testToNumberFromBytes() {
		for(int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], UUIDGenerator.toNumber(bytes[i]));
		}
	}
	
	@Test
	public void testToBytesFromHexadecimals() {
		for(int i = 0; i < bytes.length; i++) {
			assertTrue(UUIDGenerator.equals(bytes[i], UUIDGenerator.toBytes(hexadecimals[i])));
		}
	}
	
	@Test
	public void testToHexadecimalFromBytes() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], UUIDGenerator.toHexadecimal(bytes[i]));
		}
	}
	
	@Test
	public void testToNumberFromHexadecimal() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], UUIDGenerator.toNumber(hexadecimals[i]));
		}
	}
	
	/**
	 * This method estimates the average running time for a method in nanoseconds.
	 */
	private static long estimateMethodExecutionTime(Class<?> clazz, String methodName, long max) {
		
		long elapsedSum = 0;
		long elapsedAvg = 0;

		long extraSum = 0;
		long extraAvg = 0;
		
		long beforeTime = 0;
		long afterTime = 0;

		try {
			Method method = clazz.getDeclaredMethod(methodName);
			
			for (int i = 0; i < Math.pow(10, 5); i++) {
				method.invoke(null);
			}
			
			for (int i = 0; i < max; i++) {
				beforeTime = System.nanoTime();
				if(i > 0) {
					extraSum += (beforeTime - afterTime);
				}
				method.invoke(null);
				afterTime = System.nanoTime();
				elapsedSum += (afterTime - beforeTime);
			}

			elapsedAvg = elapsedSum / (max - 1);
			extraAvg = extraSum / (max - 1);
			
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} 
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return elapsedAvg - extraAvg;
	}

	/**
	 * This method only prints average running times.
	 */
	@Test
	public void testEstimateRunningTimes() {
		long loopMax = 100_000;
		long randomUUID = (estimateMethodExecutionTime(UUID.class, "randomUUID", loopMax) * loopMax) / 1_000_000;
		long getRandomUUID = (estimateMethodExecutionTime(UUIDGenerator.class, "getRandomUUID", loopMax) * loopMax) / 1_000_000;
		long getTimeBasedUUID = (estimateMethodExecutionTime(UUIDGenerator.class, "getTimeBasedUUID", loopMax) * loopMax) / 1_000_000;
		long getSequentialUUID = (estimateMethodExecutionTime(UUIDGenerator.class, "getSequentialUUID", loopMax) * loopMax) / 1_000_000;

		System.out.println();
		System.out.println(String.format("Average running times for %,d UUIDs generated:", loopMax));
		System.out.println(String.format("* java.util.UUID.randomUUID():        %s ms", randomUUID));
		System.out.println(String.format("* java.util.UUID.getRandomUUID():     %s ms", getRandomUUID));
		System.out.println(String.format("* java.util.UUID.getTimeBasedUUID():  %s ms", getTimeBasedUUID));
		System.out.println(String.format("* java.util.UUID.getSequentialUUID(): %s ms", getSequentialUUID));
	}
}
