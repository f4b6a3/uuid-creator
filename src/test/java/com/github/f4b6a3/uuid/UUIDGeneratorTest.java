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
import com.github.f4b6a3.uuid.other.RandomImage;
import com.github.f4b6a3.uuid.other.RandomnesTest;
import com.github.f4b6a3.uuid.other.SimpleBenchmark;
import com.github.f4b6a3.uuid.other.SimpleRunnable;
import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.random.XorshiftStarRandom;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UUIDUtil;

import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Unit test for uuid-generator.
 */
public class UUIDGeneratorTest {

	private static final long DEFAULT_LOOP_LIMIT = 100;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testGetRandomUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getRandom();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetTimeBasedUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBased();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetTimeBasedWithHardwareAddress_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getTimeBasedWithMAC();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequential();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialWithHardwareAddressUUID_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequentialWithMAC();
			assertTrue(uuid.toString().matches(UUIDGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetSequentialUUID_TimestampBitsAreSequential() {

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequential();
			long newTimestamp = UUIDUtil.extractTimestamp(uuid);
			assertTrue(newTimestamp > oldTimestemp);
		}
	}

	@Test
	public void testGetSequentialUUID_MostSignificantBitsAreSequential() {

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUIDGenerator.getSequential();
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

			UUID uuid = UUIDGenerator.getTimeBasedCreator().withInstant(instant1).create();
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

			UUID uuid = UUIDGenerator.getSequentialCreator().withInstant(instant1).create();
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

			UUID uuid = UUIDGenerator.getDCESecurity(localDomain, localIdentifier);

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

			name = UUIDGenerator.getRandom().toString();
			uuid = UUIDGenerator.getNameBasedMD5(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
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
	@Test
	public void testRaceCondition() {

		// Start many threads to generate a lot of UUIDs in the same instant
		// (100-nanoseconds).

		for (int i = 0; i < SimpleRunnable.threadCount; i++) {
			Thread thread = new Thread(new SimpleRunnable(i));
			thread.start();
		}
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
		String timeBasedUUID = UUIDGenerator.getTimeBasedCreator().withInstant(instant).create().toString();
		String sequentialUUID = UUIDGenerator.getSequentialCreator().withInstant(instant).create().toString();

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
			System.out.println(UUIDGenerator.getFastRandom());
		}

		System.out.println();
		System.out.println("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UUIDGenerator.getTimeBased());
		}

		System.out.println();
		System.out.println("### Sequential UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UUIDGenerator.getSequential());
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
