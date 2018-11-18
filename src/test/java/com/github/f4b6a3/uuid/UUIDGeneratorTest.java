package com.github.f4b6a3.uuid;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.factory.abst.AbstractUUIDCreator;
import com.github.f4b6a3.uuid.random.LCGParkMillerRandom;
import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.random.XorshiftStarRandom;
import com.github.f4b6a3.uuid.util.RandomImage;
import com.github.f4b6a3.uuid.util.RandomnesTest;
import com.github.f4b6a3.uuid.util.SimpleBenchmark;
import com.github.f4b6a3.uuid.util.SimpleRunnable;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UUIDUtil;

import static com.github.f4b6a3.uuid.util.ByteUtils.*;

/**
 * Unit test for uuid-generator.
 */
public class UUIDGeneratorTest {

	private static final long DEFAULT_LOOP_LIMIT = 100;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	private long[] numbers = { 0x0000000000000000L, 0x0000000000000001L, 0x0000000000000012L, 0x0000000000000123L,
			0x0000000000001234L, 0x0000000000012345L, 0x0000000000123456L, 0x0000000001234567L, 0x0000000012345678L,
			0x0000000123456789L, 0x000000123456789aL, 0x00000123456789abL, 0x0000123456789abcL, 0x000123456789abcdL,
			0x00123456789abcdeL, 0x0123456789abcdefL };

	private String[] hexadecimals = { "0000000000000000", "0000000000000001", "0000000000000012", "0000000000000123",
			"0000000000001234", "0000000000012345", "0000000000123456", "0000000001234567", "0000000012345678",
			"0000000123456789", "000000123456789a", "00000123456789ab", "0000123456789abc", "000123456789abcd",
			"00123456789abcde", "0123456789abcdef" };

	private byte[][] bytes = {
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc },
			{ (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd },
			{ (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde },
			{ (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd,
					(byte) 0xef } };

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

	@Test
	public void testGetSequentialUUID_TimestampBitsAreSequential() {

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequentialUUID();
			long newTimestamp = UUIDUtil.extractTimestamp(uuid);
			assertTrue(newTimestamp > oldTimestemp);
		}
	}

	@Test
	public void testGetSequentialUUID_MostSignificantBitsAreSequential() {

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequentialUUID();
			long newMsb = uuid.getMostSignificantBits();
			assertTrue(newMsb > oldMsb);
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
			Instant instant2 = UUIDUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

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
			Instant instant2 = UUIDUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a DCE Security version 2 has correct local domain and identifier.
	 */
	@Test
	public void testGetDCESecuritylUUID_DomainAndIdentifierAreCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701;

			UUID uuid = UUIDGenerator.getDCESecurityUUID(localDomain, localIdentifier);

			byte localDomain2 = UUIDUtil.extractDCESecurityLocalDomain(uuid);
			int localIdentifier2 = UUIDUtil.extractDCESecurityLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}

	/**
	 * Test if a name-based UUID version 3 with name space is correct.
	 */
	@Test
	public void testGetNameBasedMD5UUID() {

		UUID namespace = AbstractUUIDCreator.NAMESPACE_URL;
		String name = null;
		UUID uuid = null;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			name = UUIDGenerator.getRandomUUID().toString();
			uuid = UUIDGenerator.getNameBasedMD5UUID(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}

	@Test
	public void testToNumberFromBytes() {
		for (int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], toNumber(bytes[i]));
		}
	}

	@Test
	public void testToBytesFromHexadecimals() {
		for (int i = 0; i < bytes.length; i++) {
			assertTrue(equalArrays(bytes[i], toBytes(hexadecimals[i])));
		}
	}

	@Test
	public void testToHexadecimalFromBytes() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], toHexadecimal(bytes[i]));
		}
	}

	@Test
	public void testToNumberFromHexadecimal() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], toNumber(hexadecimals[i]));
		}
	}

	/**
	 * Test with many threads running at the same time.
	 * 
	 * It basically tests if a UUID is generated more than once. If it occurs, a
	 * {@link RuntimeException} is thrown.
	 * 
	 * It's expected to see overrun warnings, since there's no exception thrown.
	 * 
	 * If there's more than one UUID request in the same timestamp, a counter is
	 * incremented to avoid repetitions. When that counter overrun, a warning is
	 * logged and a sequence (clock_seq) is incremented. The overrun warning
	 * only indicates that the sequence had to be incremented.
	 * 
	 */
	@Ignore
	public void testRaceCondition() {

		// Start many threads to generate a lot of UUIDs in the same instant
		// (100-nanoseconds).
		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Race condition test");
		System.out.println("----------------------------------------");

		for (int i = 0; i < SimpleRunnable.threadCount; i++) {
			Thread thread = new Thread(new SimpleRunnable(i));
			thread.start();
		}

		System.out.println("Note: warnings are expected.");
		System.out.println("----------------------------------------");
	}

	/**
	 * This method only prints average running times.
	 */
	@Ignore
	public void testEstimateRunningTimes() {
		long loopMax = 100_000;
		long nano = 1_000_000;
		long randomUUID = (SimpleBenchmark.run(null, UUID.class, "randomUUID", loopMax) * loopMax) / nano;
		long getRandomUUID = (SimpleBenchmark.run(null, UUIDGenerator.class, "getRandomUUID", loopMax) * loopMax)
				/ nano;
		long getTimeBasedUUID = (SimpleBenchmark.run(null, UUIDGenerator.class, "getTimeBasedUUID", loopMax) * loopMax)
				/ nano;
		long getSequentialUUID = (SimpleBenchmark.run(null, UUIDGenerator.class, "getSequentialUUID", loopMax)
				* loopMax) / nano;
		long javaNextLong = (SimpleBenchmark.run(new Random(), null, "nextLong", loopMax) * loopMax) / nano;
		long XorshiftNextLong = (SimpleBenchmark.run(new XorshiftRandom(), null, "nextLong", loopMax) * loopMax) / nano;

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println(String.format("Average generation times for %,d UUIDs", loopMax));
		System.out.println("----------------------------------------");
		System.out.println(String.format("* java.util.UUID.randomUUID():       %s ms", randomUUID));
		System.out.println(String.format("* UUIDGenerator.getRandomUUID():     %s ms", getRandomUUID));
		System.out.println(String.format("* UUIDGenerator.getTimeBasedUUID():  %s ms", getTimeBasedUUID));
		System.out.println(String.format("* UUIDGenerator.getSequentialUUID(): %s ms", getSequentialUUID));
		System.out.println(String.format("* java.util.Random.nextLong(): %s ms", javaNextLong));
		System.out.println(String.format("* XorshiftRandom.nextLong(): %s ms", XorshiftNextLong));
		System.out.println("----------------------------------------");
	}

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	@Ignore
	public void testDemoDifferenceBetweenTimeBasedAndSequentialUUID() {

		Instant instant = Instant.now();
		String timeBasedUUID = UUIDGenerator.getTimeBasedUUIDCreator().withInstant(instant).create().toString();
		String sequentialUUID = UUIDGenerator.getSequentialUUIDCreator().withInstant(instant).create().toString();

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Demonstration of time-baed UUIDs");
		System.out.println("----------------------------------------");
		System.out.println("- TimeBased UUID:          " + timeBasedUUID.toString());
		System.out.println("- Sequential UUID:         " + sequentialUUID.toString());
		System.out.println("- Original instant:        " + instant.toString());
		System.out.println("- TimeBased UUID instant:  " + UUIDUtil.extractInstant(UUID.fromString(timeBasedUUID)));
		System.out.println("- Sequential UUID instant: " + UUIDUtil.extractInstant(UUID.fromString(sequentialUUID)));
		System.out.println("----------------------------------------");
	}

	@Ignore
	public void testPrintList() {
		int max = 10000;

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Print list of UUIDs");
		System.out.println("----------------------------------------");

		System.out.println();
		System.out.println("### Random UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UUIDGenerator.getFastRandomUUID());
		}

		System.out.println();
		System.out.println("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UUIDGenerator.getTimeBasedUUID());
		}

		System.out.println();
		System.out.println("### Sequential UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UUIDGenerator.getSequentialUUID());
		}

		System.out.println("----------------------------------------");
	}

	/**
	 * Generate images with random pixels.
	 */
	@Ignore
	public void testCreateRandomImage() {

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Creating images from random numbers...");
		System.out.println("----------------------------------------");

		RandomImage.createRandomImageFile("/tmp/java.util.Random.png", new Random(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/java.security.SecureRandom.png", new SecureRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/XorshiftRandom.png", new XorshiftRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/XorshiftStarRandom.png", new XorshiftStarRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/Xorshift128PlusRandom.png", new Xorshift128PlusRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/Xoroshiro128PlusRandom.png", new Xoroshiro128PlusRandom(), 0, 0);

		System.out.println("----------------------------------------");
	}

	/**
	 * Test randomness of a random number generator.
	 * 
	 * If ENT is not installed, just annotate this test with @Ignore.
	 * 
	 * @param path
	 * @param random
	 */
	@Ignore
	public void testPseudoNumberSequence() throws Exception {

		Random random = new SecureRandom();
		// Random random = new Random();
		// Random random = new XorshiftRandom();
		// Random random = new XorshiftStarRandom();
		// Random random = new Xorshift128PlusRandom();
		// Random random = new Xoroshirot128PlusRandom();
		// Random random = new LCGParkerMiller();

		String path = "/tmp/testPseudoNumberSequence.dat";

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Test pseudo-number sequence");
		System.out.println("----------------------------------------");

		System.out.println(String.format("ent %s\n", path));
		RandomnesTest.runPseudoNumberSequenceTestProgram(path, random);

		System.out.println("----------------------------------------");
	}
}
