package com.github.f4b6a3.uuid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.UUIDCreator;
import com.github.f4b6a3.uuid.util.TimestampUtils;
import com.github.f4b6a3.uuid.util.UUIDUtils;

import static com.github.f4b6a3.uuid.util.ByteUtils.*;

/**
 * Unit test for simple App.
 */
public class UUIDGeneratorTest {

	private static final long DEFAULT_LOOP_LIMIT = 1_000;  
	
	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
	
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
	public void testTest() {
		UUIDGenerator.getTimeBasedUUID();
		//UUIDGenerator.getSequentialUUID();
	}
	
	@Test
	public void testGetRandomUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getRandomUUID();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	@Test
	public void testGetTimeBasedUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedUUID();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	@Test
	public void testGetTimeBasedWithHardwareAddress_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedWithHardwareAddressUUID();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}
	
	@Test
	public void testGetSequentialUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequentialUUID();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialWithHardwareAddressUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequentialWithHardwareAddressUUID();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	@Test
	public void testGetTimeBasedUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = Instant.now();
			
			UUID uuid = UUIDGenerator.getTimeBasedUUIDCreator().withInstant(instant1).create();
			Instant instant2 = UUIDUtils.extractInstant(uuid);
			
			long timestamp1 = TimestampUtils.getTimestamp(instant1);
			long timestamp2 = TimestampUtils.getTimestamp(instant2);
			
			assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a sequential UUID version 4 is has the correct timestamp.
	 */
	@Test
	public void testGetSequentialUUID_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			Instant instant1 = Instant.now();
			
			UUID uuid = UUIDGenerator.getSequentialUUIDCreator().withInstant(instant1).create();
			Instant instant2 = UUIDUtils.extractInstant(uuid);

			long timestamp1 = TimestampUtils.getTimestamp(instant1);
			long timestamp2 = TimestampUtils.getTimestamp(instant2);
			
			assertEquals(timestamp1, timestamp2);
		}
	}
	
	/**
	 * Test if a name-based UUID version 3 with name space is correct.
	 */
	@Test
	public void testGetNameBasedMD5UUID() {
		
		UUID namespace = UUIDCreator.NAMESPACE_URL;
		String name = null;
		UUID uuid = null;
		
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			
			name = UUIDGenerator.getRandomUUID().toString();
			uuid = UUIDGenerator.getNameBasedMD5UUID(namespace, name);
			
			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("[^0-9a-fA-F]", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);
			
			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}
	
	@Test
	public void testToNumberFromBytes() {
		for(int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], toNumber(bytes[i]));
		}
	}
	
	@Test
	public void testToBytesFromHexadecimals() {
		for(int i = 0; i < bytes.length; i++) {
			assertTrue(equalArrays(bytes[i], toBytes(hexadecimals[i])));
		}
	}
	
	@Test
	public void testToHexadecimalFromBytes() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], toHexadecimal(bytes[i]));
		}
	}
	
	@Test
	public void testToNumberFromHexadecimal() {
		for(int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], toNumber(hexadecimals[i]));
		}
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
		
		int threadCount = 100;
		int threadLoopLimit = 1000;
		
		// This instant won't change during this test
		Instant instant = Instant.now();
		
		// Start many threads to generate a lot of UUIDs in the same instant
		// (100-nanoseconds).
		for (int i = 0; i < threadCount; i++) {
			Thread thread = new Thread(new SimpleRunnable(i, instant, threadCount, threadLoopLimit));
			thread.start();
		}
	}

	/**
	 * This method only prints average running times.
	 */
	@Test
	public void testEstimateRunningTimes() {
		long loopMax = 100_000;
		long randomUUID = (SimpleBenchmark.run(UUID.class, "randomUUID", loopMax) * loopMax) / 1_000_000;
		long getRandomUUID = (SimpleBenchmark.run(UUIDGenerator.class, "getRandomUUID", loopMax) * loopMax) / 1_000_000;
		long getTimeBasedUUID = (SimpleBenchmark.run(UUIDGenerator.class, "getTimeBasedUUID", loopMax) * loopMax) / 1_000_000;
		long getSequentialUUID = (SimpleBenchmark.run(UUIDGenerator.class, "getSequentialUUID", loopMax) * loopMax) / 1_000_000;

		System.out.println();
		System.out.println(String.format("Average running times for %,d UUIDs generated:", loopMax));
		System.out.println(String.format("* java.util.UUID.randomUUID():       %s ms", randomUUID));
		System.out.println(String.format("* UUIDGenerator.getRandomUUID():     %s ms", getRandomUUID));
		System.out.println(String.format("* UUIDGenerator.getTimeBasedUUID():  %s ms", getTimeBasedUUID));
		System.out.println(String.format("* UUIDGenerator.getSequentialUUID(): %s ms", getSequentialUUID));
	}
	

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	@Test
	public void testDemoDifferenceBetweenTimeBasedAndSequentialUUID() {

		Instant instant = Instant.now();
		String timeBasedUUID = UUIDGenerator.getTimeBasedUUID().toString();
		String sequentialUUID = UUIDGenerator.getSequentialUUID().toString();

		System.out.println();
		System.out.println("Demonstration:");
		System.out.println("- TimeBased UUID:          " + timeBasedUUID.toString());
		System.out.println("- Sequential UUID:         " + sequentialUUID.toString());
		System.out.println("- Original instant:        " + instant.toString());
		System.out.println("- TimeBased UUID instant:  " + UUIDUtils.extractInstant(UUID.fromString(timeBasedUUID)));
		System.out.println("- Sequential UUID instant: " + UUIDUtils.extractInstant(UUID.fromString(sequentialUUID)));
	}
}
