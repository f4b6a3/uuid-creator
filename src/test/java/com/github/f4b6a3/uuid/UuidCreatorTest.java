package com.github.f4b6a3.uuid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.f4b6a3.uniqueness.UniquenessTest;
import com.github.f4b6a3.uuid.clockseq.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.clockseq.RandomClockSequenceStrategy;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.LexicalOrderGuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.factory.RandomUuidCreator;
import com.github.f4b6a3.uuid.factory.SequentialUuidCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.nodeid.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.RandomNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.FingerprintNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;
import com.github.f4b6a3.uuid.timestamp.UnixMillisecondsTimestampStretegy;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.timestamp.NanosecondTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.RandomTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;
import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Unit test for uuid-generator.
 */
public class UuidCreatorTest {

	private static final String GITHUB_URL = "www.github.com";
	private static int processors;

	private static final int COUNTER_OFFSET_MAX = 256;
	private static final int DEFAULT_LOOP_MAX = 10_000 - COUNTER_OFFSET_MAX;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	private static final String DUPLICATE_UUID_MSG = "A duplicate UUID was created";
	private static final String CLOCK_SEQUENCE_MSG = "The last clock sequence should be equal to the first clock sequence minus 1";

	@BeforeClass
	public static void beforeClass() {

		processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
	}
	
	@Test
	public void testSequentialUuid() {
		boolean multicast = true;
		testCreateAbstractTimeBasedUuid(UuidCreator.getSequentialCreator(), multicast);
	}

	@Test
	public void testSequentialUuidWithMac() {
		boolean multicast = false;
		testCreateAbstractTimeBasedUuid(UuidCreator.getSequentialCreator().withHardwareAddressNodeIdentifier(), multicast);
	}

	@Test
	public void testCreateTimeBasedUuid() {
		boolean multicast = true;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator(), multicast);
	}

	@Test
	public void testCreateTimeBasedUuidWithMac() {
		boolean multicast = false;
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator().withHardwareAddressNodeIdentifier(), multicast);
	}

	@Test
	public void testCreateTimeBasedUuidWithSystemDataHashNodeIdentifier() {
		boolean multicast = true;
		NodeIdentifierStrategy strategy = new FingerprintNodeIdentifierStrategy();
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(strategy), multicast);
	}

	@Test
	public void testCreateTimeBasedUuidWithRandomNodeIdentifierStrategy() {
		boolean multicast = true;
		NodeIdentifierStrategy strategy = new RandomNodeIdentifierStrategy();
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(strategy), multicast);
	}
	
	@Test
	public void testCreateTimeBasedUuidWithFastRandomNodeIdentifierStrategy() {
		boolean multicast = true;
		NodeIdentifierStrategy strategy = new RandomNodeIdentifierStrategy(new Xoroshiro128PlusRandom());
		testCreateAbstractTimeBasedUuid(UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(strategy), multicast);
	}
	
	@Test
	public void testCreateTimeBasedUuidWithNanosecondTimestampStrategy() {
		TimestampStrategy strategy = new NanosecondTimestampStrategy();
		testCreateGenericUuid(UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy));
	}

	@Test
	public void testCreateTimeBasedUuidWithUnixMilliTimestampStrategy() {
		TimestampStrategy strategy = new UnixMillisecondsTimestampStretegy();
		testCreateGenericUuid(UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy));
	}
	
	@Test
	public void testCreateTimeBasedUuidWithRandomTimestampStrategy() {
		TimestampStrategy strategy = new RandomTimestampStrategy();
		testCreateGenericUuid(UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy));
	}
	
	@Test
	public void testCreateTimeBasedUuidWithFastRandomTimestampStrategy() {
		TimestampStrategy strategy = new RandomTimestampStrategy(new Xoroshiro128PlusRandom());
		testCreateGenericUuid(UuidCreator.getTimeBasedCreator().withTimestampStrategy(strategy));
	}

	@Test
	public void testCreateTimeBasedUuidWithFastRandomClockSequenceStrategy() {
		ClockSequenceStrategy strategy = new RandomClockSequenceStrategy(new Xoroshiro128PlusRandom());
		testCreateGenericUuid(UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(strategy));
	}
	
	@Test
	public void testRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomUuidCreator creator = UuidCreator.getRandomCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.RANDOM_BASED.getValue());
	}
	
	@Test
	public void testFastRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomUuidCreator creator = UuidCreator.getFastRandomCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.RANDOM_BASED.getValue());
	}

	@Test
	public void testNameBasedMd5Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

		byte[] name;
		
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes(StandardCharsets.UTF_8);
			list[i] = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.NAME_BASED_MD5.getValue());

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes(StandardCharsets.UTF_8);
			UUID other = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
			assertTrue("Two different MD5 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testNameBasedSha1Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

		byte[] name;
		
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes(StandardCharsets.UTF_8);
			list[i] = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.NAMBE_BASED_SHA1.getValue());

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			name = ("url" + i).getBytes(StandardCharsets.UTF_8);
			UUID other = creator.create(UuidNamespace.NAMESPACE_URL.getValue(), name);
			assertTrue("Two different SHA1 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testCompGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getCombGuid();
		}

		long endTime = System.currentTimeMillis();

		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getLeastSignificantBits() & 0x0000ffffffffffffL;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testCreateLexicalOrderGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getLexicalOrderGuid();
		}

		long endTime = System.currentTimeMillis();

		checkOrdering(list);
		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getMostSignificantBits() >> 16;
			assertTrue("Lexical Order Guid creation time before start time", startTime <= creationTime);
			assertTrue("Lexical Order Guid creation time after end time", creationTime <= endTime);
			assertTrue("Lexical Order Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testGetSequentialUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getSequential();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetSequentialWithMacStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getSequentialWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetRandomUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getRandom();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeBased();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedWithMacStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getTimeBasedWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetSequentialTimestampBitsAreSequential() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		SequentialUuidCreator creator = UuidCreator.getSequentialCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newTimestamp = UuidUtil.extractTimestamp(list[i]);

			if (i > 0) {
				assertTrue(newTimestamp >= oldTimestemp);
			}
			oldTimestemp = newTimestamp;
		}
	}

	@Test
	public void testGetSequentialMostSignificantBitsAreSequential() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		SequentialUuidCreator creator = UuidCreator.getSequentialCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long newMsb = list[i].getMostSignificantBits();

			if (i > 0) {
				assertTrue(newMsb >= oldMsb);
			}
			oldMsb = newMsb;
		}
	}

	@Test
	public void testGetTimeBasedTimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidCreator.getTimeBasedCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetSequentialTimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidCreator.getSequentialCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetDCESecuritylLocalDomainAndLocalIdentifierAreCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701 + 1;

			UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);

			byte localDomain2 = UuidUtil.extractDceSecurityLocalDomain(uuid);
			int localIdentifier2 = UuidUtil.extractDceSecurityLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
			
			// Test with hardware address too
			uuid = UuidCreator.getDceSecurityWithMac(localDomain, localIdentifier);

			localDomain2 = UuidUtil.extractDceSecurityLocalDomain(uuid);
			localIdentifier2 = UuidUtil.extractDceSecurityLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}

	@Test
	public void testGetNameBasedMd5CompareWithJavaUtilUuidNameUuidFromBytes() {

		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = null;
		UUID uuid = null;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			name = UuidCreator.getRandom().toString();
			uuid = UuidCreator.getNameBasedMd5(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}

	@Test
	public void testGetNameBasedMd5NamespaceDnsAndSiteGithub() {
		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = GITHUB_URL;

		// Value generated by UUIDGEN (util-linux)
		UUID uuid1 = UUID.fromString("2c02fba1-0794-3c12-b62b-578ec5f03908");
		UUID uuid2 = UuidCreator.getNameBasedMd5(namespace, name);
		assertEquals(uuid1, uuid2);

		// Value generated by MD5SUM (gnu-coreutils)
		UUID uuid3 = UUID.fromString("d85b3e68-c422-3cfc-b1ea-b58b6d8dfad0");
		UUID uuid4 = UuidCreator.getNameBasedMd5(name);
		assertEquals(uuid3, uuid4);

		NameBasedMd5UuidCreator creator1 = UuidCreator.getNameBasedMd5Creator().withNamespace(namespace);
		// Value generated by UUIDGEN (util-linux)
		UUID uuid5 = UUID.fromString("2c02fba1-0794-3c12-b62b-578ec5f03908");
		UUID uuid6 = creator1.create(name);
		assertEquals(uuid5, uuid6);
	}

	@Test
	public void testGetNameBasedSha1NamespaceDnsAndSiteGithub() {

		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = GITHUB_URL;

		// Value generated by UUIDGEN (util-linux)
		UUID uuid1 = UUID.fromString("04e16ed4-cd93-55f3-b2e3-1a097fc19832");
		UUID uuid2 = UuidCreator.getNameBasedSha1(namespace, name);
		assertEquals(uuid1, uuid2);

		// Value generated by SHA1SUM (gnu-coreutils)
		UUID uuid3 = UUID.fromString("a2999f4b-523d-5e63-866a-d0d9f401fe93");
		UUID uuid4 = UuidCreator.getNameBasedSha1(name);
		assertEquals(uuid3, uuid4);
	}

	@Test
	public void testUniquenesWithParallelThreadsMakingRequestingToASingleGenerator() {
		boolean verbose = false;
		int threadCount = 16; // Number of threads to run
		int requestCount = 100_000; // Number of requests for thread
		UniquenessTest.execute(verbose, threadCount, requestCount);
	}

	@Test
	public void testGetTimeBasedShouldCreateAlmostSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int max = 0x3fff + 1; // 16,384
		Instant stoppedTime = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withInstant(stoppedTime);

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = creator.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.extractClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.extractClockSequence(uuid);
			}
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertTrue(DUPLICATE_UUID_MSG, set.size() == max);
		assertTrue(CLOCK_SEQUENCE_MSG, (lastClockSeq % max) == ((firstClockSeq % max) - 1));
	}

	@Test
	public void testGetSequentialShouldCreateAlmostSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int max = 0x3fff + 1; // 16,384
		Instant stoppedTime = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		SequentialUuidCreator creator = UuidCreator.getSequentialCreator().withInstant(stoppedTime);

		int firstClockSeq = 0;
		int lastClockSeq = 0;

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < max; i++) {
			UUID uuid = creator.create();
			if (i == 0) {
				firstClockSeq = UuidUtil.extractClockSequence(uuid);
			} else if (i == max - 1) {
				lastClockSeq = UuidUtil.extractClockSequence(uuid);
			}
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue(DUPLICATE_UUID_MSG, set.add(uuid));
		}

		assertTrue(DUPLICATE_UUID_MSG, set.size() == max);
		assertTrue(CLOCK_SEQUENCE_MSG, (lastClockSeq % max) == ((firstClockSeq % max) - 1));
	}

	@Test
	public void testGetTimeBasedParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getTimeBasedCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	@Test
	public void testGetSequentialParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getSequentialCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	@Test
	public void testGetLexicalOrderGuidParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getLexicalOrderCreator(), DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue(DUPLICATE_UUID_MSG, TestThread.hashSet.size() == (DEFAULT_LOOP_MAX * processors));
	}

	@Test
	public void testCreateTimeBasedUuidTheGreatestDateAndTimeShouldBeAtYear5236() {

		// Check if the greatest 60 bit timestamp corresponds to the date and
		// time
		long timestamp0 = 0x0fffffffffffffffL;
		Instant instant0 = Instant.parse("5236-03-31T21:21:00.684Z");
		assertEquals(TimestampUtil.toInstant(timestamp0), instant0);

		// Test the extraction of the maximum 60 bit timestamp
		long timestamp1 = 0x0fffffffffffffffL;
		TimeBasedUuidCreator creator1 = UuidCreator.getTimeBasedCreator().withTimestamp(timestamp1);
		UUID uuid1 = creator1.create();
		long timestamp2 = UuidUtil.extractTimestamp(uuid1);
		assertEquals(timestamp1, timestamp2);

		// Test the extraction of the maximum date and time
		Instant instant1 = Instant.parse("5236-03-31T21:21:00.684Z");
		TimeBasedUuidCreator creator2 = UuidCreator.getTimeBasedCreator().withTimestamp(timestamp1);
		UUID uuid2 = creator2.create();
		Instant instant2 = UuidUtil.extractInstant(uuid2);
		assertEquals(instant1, instant2);
	}

	@Test
	public void testGetLexicalOrderGuidShouldIncrementWhenTheTimeIsStopped() {

		long timestamp = System.currentTimeMillis();

		LexicalOrderGuidCreator creator = UuidCreator.getLexicalOrderCreator()
				.withTimestampStrategy(new FixedTimestampStretegy(timestamp));

		long firstLsb = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			if (i == 0) {
				firstLsb = creator.create().getLeastSignificantBits();
			}
			creator.create();
		}
		long lastLsb = creator.create().getLeastSignificantBits();
		assertEquals((lastLsb % DEFAULT_LOOP_MAX) - 1, (firstLsb % DEFAULT_LOOP_MAX));
	}

	private void checkIfStringIsValid(UUID uuid) {
		assertTrue(uuid.toString().matches(UuidCreatorTest.UUID_PATTERN));
	}

	private void checkNullOrInvalid(UUID[] list) {
		for (UUID uuid : list) {
			assertTrue("UUID is null", uuid != null);
			assertTrue("UUID is not RFC-4122 variant", UuidUtil.isRfc4122Variant(uuid));
		}
	}

	private void checkVersion(UUID[] list, int version) {
		for (UUID uuid : list) {
			assertTrue(String.format("UUID is not version %s", version),
					uuid.version() == version);
		}
	}
	
	private void testCreateAbstractTimeBasedUuid(AbstractTimeBasedUuidCreator creator, boolean multicast) {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkVersion(list, creator.getVersion());
		checkCreationTime(list, startTime, endTime);
		checkNodeIdentifier(list, multicast);
		checkOrdering(list);
		checkUniqueness(list);

	}

	private void testCreateGenericUuid(AbstractTimeBasedUuidCreator creator) {
		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}
		checkUniqueness(list);
	}
	
	private void checkUniqueness(UUID[] list) {

		HashSet<UUID> set = new HashSet<>();

		for (UUID uuid : list) {
			assertTrue(String.format("UUID is duplicated %s", uuid), set.add(uuid));
		}

		assertTrue("There are duplicated UUIDs", set.size() == list.length);
	}

	private void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID uuid : list) {
			long creationTime = UuidUtil.extractUnixMilliseconds(uuid);
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

	}

	private void checkNodeIdentifier(UUID[] list, boolean multicast) {
		for (UUID uuid : list) {
			long nodeIdentifier = UuidUtil.extractNodeIdentifier(uuid);
			assertTrue("Node identifier is not multicast",
					NodeIdentifierUtil.isMulticastNodeIdentifier(nodeIdentifier) == multicast);
		}
	}

	private void checkOrdering(UUID[] list) {
		UUID[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertTrue("The UUID list is not ordered", list[i].equals(other[i]));
		}
	}

	private static class TestThread extends Thread {

		private static Set<UUID> hashSet = new HashSet<>();
		private NoArgumentsUuidCreator creator;
		private int loopLimit;

		public TestThread(NoArgumentsUuidCreator creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create());
				}
			}
		}
	}
}
