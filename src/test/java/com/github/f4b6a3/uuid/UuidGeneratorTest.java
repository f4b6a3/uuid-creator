package com.github.f4b6a3.uuid;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.test.other.RandomImage;
import com.github.f4b6a3.test.other.RandomnesTest;
import com.github.f4b6a3.test.other.SimpleBenchmark;
import com.github.f4b6a3.uuid.UuidGenerator;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.RandomNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.SystemNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.random.XorshiftStarRandom;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.DeltaTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.NanosecondTimestampStrategy;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Unit test for uuid-generator.
 */
public class UuidGeneratorTest {

	private static final long DEFAULT_LOOP_LIMIT = 100;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testGetRandomUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getRandom();
			assertTrue(uuid.toString().matches(UuidGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetTimeBasedUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getTimeBased();
			assertTrue(uuid.toString().matches(UuidGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetTimeBasedWithMac_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getTimeBasedWithMac();
			assertTrue(uuid.toString().matches(UuidGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetOrderedUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getOrdered();
			assertTrue(uuid.toString().matches(UuidGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetOrderedWithMac_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getOrderedWithMac();
			assertTrue(uuid.toString().matches(UuidGeneratorTest.UUID_PATTERN));
		}
	}

	@Test
	public void testGetOrdered_TimestampBitsAreOrdered() {

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getOrdered();
			long newTimestamp = UuidUtil.extractTimestamp(uuid);

			if (i > 0) {
				assertTrue(newTimestamp >= oldTimestemp);
			}
			oldTimestemp = newTimestamp;
		}
	}

	@Test
	public void testGetOrdered_MostSignificantBitsAreOrdered() {

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidGenerator.getOrdered();
			long newMsb = uuid.getMostSignificantBits();

			if (i > 0) {
				assertTrue(newMsb >= oldMsb);
			}
			oldMsb = newMsb;
		}
	}

	/**
	 * Test if a time based UUID version 1 is has the correct timestamp.
	 */
	@Test
	public void testGetTimeBased_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidGenerator.getTimeBasedCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a ordered UUID version 0 is has the correct timestamp.
	 */
	@Test
	public void testGetOrdered_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidGenerator.getOrderedCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	/**
	 * Test if a DCE Security version 2 has correct local domain and identifier.
	 */
	@Test
	public void testGetDCESecurityl_DomainAndIdentifierAreCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701;

			UUID uuid = UuidGenerator.getDceSecurity(localDomain, localIdentifier);

			byte localDomain2 = UuidUtil.extractDceSecurityLocalDomain(uuid);
			int localIdentifier2 = UuidUtil.extractDceSecurityLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}

	/**
	 * Test if a name-based UUID version 3 with name space is correct.
	 */
	@Test
	public void testGetNameBasedMd5() {

		UUID namespace = AbstractUuidCreator.NAMESPACE_DNS;
		String name = null;
		UUID uuid = null;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			name = UuidGenerator.getRandom().toString();
			uuid = UuidGenerator.getNameBasedMd5(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}

	@Test
	public void testGetNameBasedMd5_test_dns_www_github_com() {
		UUID namespace = AbstractUuidCreator.NAMESPACE_DNS;
		String name = "www.github.com";

		// Value generated by UUIDGEN (util-linux packege)
		UUID uuid1 = UUID.fromString("2c02fba1-0794-3c12-b62b-578ec5f03908");
		UUID uuid2 = UuidGenerator.getNameBasedMd5(namespace, name);
		assertEquals(uuid1, uuid2);
	}

	@Test
	public void testGetNameBasedSha1_test_dns_www_github_com() {

		UUID namespace = AbstractUuidCreator.NAMESPACE_DNS;
		String name = "www.github.com";

		// Value generated by UUIDGEN (util-linux packege)
		UUID uuid1 = UUID.fromString("04e16ed4-cd93-55f3-b2e3-1a097fc19832");
		UUID uuid2 = UuidGenerator.getNameBasedSha1(namespace, name);
		assertEquals(uuid1, uuid2);

	}

	/**
	 * This method only prints average running times.
	 */
	@Ignore
	public void testEstimateRunningTimes() {
		long loopMax = 100_000;
		long nano = 1_000_000;
		long randomUUID = (SimpleBenchmark.run(null, UUID.class, "randomUUID", loopMax) * loopMax) / nano;
		long getRandomUUID = (SimpleBenchmark.run(null, UuidGenerator.class, "getRandom", loopMax) * loopMax) / nano;
		long getTimeBasedUUID = (SimpleBenchmark.run(null, UuidGenerator.class, "getTimeBased", loopMax) * loopMax)
				/ nano;
		long getOrderedUUID = (SimpleBenchmark.run(null, UuidGenerator.class, "getOrdered", loopMax) * loopMax) / nano;
		long javaNextLong = (SimpleBenchmark.run(new Random(), null, "nextLong", loopMax) * loopMax) / nano;
		long XorshiftNextLong = (SimpleBenchmark.run(new XorshiftRandom(), null, "nextLong", loopMax) * loopMax) / nano;

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println(String.format("Average generation times for %,d UUIDs", loopMax));
		System.out.println("----------------------------------------");
		System.out.println(String.format("* java.util.UUID.randomUUID():       %s ms", randomUUID));
		System.out.println(String.format("* UUIDGenerator.getRandomUUID():     %s ms", getRandomUUID));
		System.out.println(String.format("* UUIDGenerator.getTimeBasedUUID():  %s ms", getTimeBasedUUID));
		System.out.println(String.format("* UUIDGenerator.getOrderedUUID(): %s ms", getOrderedUUID));
		System.out.println(String.format("* java.util.Random.nextLong(): %s ms", javaNextLong));
		System.out.println(String.format("* XorshiftRandom.nextLong(): %s ms", XorshiftNextLong));
		System.out.println("----------------------------------------");
	}

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	@Ignore
	public void testDemoDifferenceBetweenTimeBasedAndOrderedUUID() {

		Instant instant = Instant.now();
		String timeBasedUUID = UuidGenerator.getTimeBasedCreator().withInstant(instant).create().toString();
		String orderedUUID = UuidGenerator.getOrderedCreator().withInstant(instant).create().toString();

		System.out.println();
		System.out.println("----------------------------------------");
		System.out.println("Demonstration of time-baed UUIDs");
		System.out.println("----------------------------------------");
		System.out.println("- TimeBased UUID:          " + timeBasedUUID.toString());
		System.out.println("- Ordered UUID:         " + orderedUUID.toString());
		System.out.println("- Original instant:        " + instant.toString());
		System.out.println("- TimeBased UUID instant:  " + UuidUtil.extractInstant(UUID.fromString(timeBasedUUID)));
		System.out.println("- Ordered UUID instant: " + UuidUtil.extractInstant(UUID.fromString(orderedUUID)));
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
			System.out.println(UuidGenerator.getFastRandom());
		}

		System.out.println();
		System.out.println("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidGenerator.getTimeBased());
		}

		System.out.println();
		System.out.println("### Ordered UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidGenerator.getOrdered());
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

	@Ignore
	public void testStrategiesUsingFlowInterface() {

		UUID uuid = null;

		System.out.println("\n#### Time-based");

		System.out.println("\n##### Fixed values");

		uuid = UuidGenerator.getTimeBasedCreator().withInstant(Instant.now()).create();
		System.out.println(String.format("%s // with fixed instant (now)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withTimestamp(TimestampUtil.toTimestamp(Instant.now())).create();
		System.out.println(String.format("%s // with fixed timestamp (now as timestamp)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withClockSequence(0x8888).create();
		System.out.println(String.format("%s // with fixed clock sequence (0x8888)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifier(0x111111111111L).create();
		System.out.println(String.format("%s // with fixed node identifier (0x111111111111L)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withHardwareAddress().create();
		System.out.println(String.format("%s // with hardware address (first MAC found)", uuid));

		System.out.println("\n##### Timestamp strategy");

		uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new DefaultTimestampStrategy()).create();
		System.out.println(String.format("%s // with default timestamp strategy (System.currentTimeMillis() + counter)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new NanosecondTimestampStrategy()).create();
		System.out.println(String.format("%s // with nanoseconds timestamp strategy (Instant.getNano())", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new DeltaTimestampStrategy()).create();
		System.out.println(String.format("%s // with delta timestamp strategy (diff of subsequent System.nanoTime())", uuid));

		System.out.println("\n##### Node identifier strategy");

		uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new DefaultNodeIdentifierStrategy()).create();
		System.out.println(String.format("%s // with default node identifier strategy (random number generated once)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new RandomNodeIdentifierStrategy()).create();
		System.out.println(String.format("%s // with random node identifier strategy (random number generated every time)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new MacNodeIdentifierStrategy()).create();
		System.out.println(String.format("%s // with hardware address node identifier strategy (first MAC found)", uuid));

		uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new SystemNodeIdentifierStrategy()).create();
		System.out.println(String.format("%s // with system node identifier strategy (hash of hostname + MAC + IP + OS + JVM)", uuid));

		System.out.println("\n#### Name-based");

		uuid = UuidGenerator.getNameBasedMd5Creator().withNamespace("USERS").create("Paul");
		System.out.println(String.format("%s // with fixed namespace as string (USERS)", uuid));

		uuid = UuidGenerator.getNameBasedMd5Creator().withNamespace(NameBasedMd5UuidCreator.NAMESPACE_DNS).create("www.github.com");
		System.out.println(String.format("%s // with fixed namespace as UUID (standard DNS namespace)", uuid));

		System.out.println("\n#### Random");

		uuid = UuidGenerator.getRandomCreator().withRandomGenerator(new Random()).create();
		System.out.println(String.format("%s // with java random generator (java.util.Random)", uuid));

		uuid = UuidGenerator.getRandomCreator().withFastRandomGenerator().create();
		System.out.println(String.format("%s // with fast random generator (Xorshift128Plus)", uuid));

		System.out.println("\n#### DCE Security");

		uuid = UuidGenerator.getDceSecurityCreator().withLocalDomain(DceSecurityUuidCreator.LOCAL_DOMAIN_PERSON).create(1701);
		System.out.println(String.format("%s // with fixed local domain (standard POSIX User ID)", uuid));
	}
}
