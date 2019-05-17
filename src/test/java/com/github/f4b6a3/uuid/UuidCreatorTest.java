package com.github.f4b6a3.uuid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.time.Instant;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.factory.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.factory.SequentialUuidCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.util.NodeIdentifierUtil;
import com.github.f4b6a3.uuid.util.SystemDataUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;
import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Unit test for uuid-generator.
 */
public class UuidCreatorTest {

	private static int processors;
	private static final int DEFAULT_LOOP_LIMIT = 10_000;

	private static final String UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-5][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@BeforeClass
	public static void beforeClass() {
		processors = SystemDataUtil.getAvailableProcessors();
		if(processors < 4) {
			processors = 4;
		}
	}
	
	@Test
	public void testSequentialUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getSequential();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.SEQUENTIAL);
		checkCreationTime(list, startTime, endTime);
		checkNodeIdentifier(list);
		checkOrdering(list);

	}
	
	@Test
	public void testCreateTimeBasedUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getTimeBased();
		}

		long endTime = System.currentTimeMillis();

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.TIME_BASED);
		checkCreationTime(list, startTime, endTime);
		checkNodeIdentifier(list);
		checkOrdering(list);

	}

	@Test
	public void testRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getRandom();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.RANDOM_BASED);
	}

	@Test
	public void testNameBasedMd5Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.NAME_BASED_MD5);

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID other = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
			assertTrue("Two different MD5 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testNameBasedSha1Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.NAMBE_BASED_SHA1);

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID other = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
			assertTrue("Two different SHA1 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testNameBasedSha256Uuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getNameBasedSha256(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.RANDOM_BASED);

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID other = UuidCreator.getNameBasedSha256(UuidNamespace.NAMESPACE_URL.getValue(), "url" + i);
			assertTrue("Two different SHA256 UUIDs for the same input", list[i].equals(other));
		}
	}

	@Test
	public void testCompGuid() {
		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getCombGuid();
		}

		long endTime = System.currentTimeMillis();

		checkUniqueness(list);
		checkVersion(list, UuidVersion.RANDOM_BASED);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			long creationTime = list[i].getLeastSignificantBits() & 0x0000ffffffffffffL;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testCreateMssqlGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_LIMIT];

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			list[i] = UuidCreator.getMssqlGuid();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
	}
	
	@Test
	public void testGetSequentialUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getSequential();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetSequentialWithMac_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getSequentialWithMac();
			checkIfStringIsValid(uuid);
		}
	}
	
	@Test
	public void testGetRandomUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getRandom();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedUuid_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getTimeBased();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetTimeBasedWithMac_StringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getTimeBasedWithMac();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testGetSequential_TimestampBitsAreSequential() {

		long oldTimestemp = 0;
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getSequential();
			long newTimestamp = UuidUtil.extractTimestamp(uuid);

			if (i > 0) {
				assertTrue(newTimestamp >= oldTimestemp);
			}
			oldTimestemp = newTimestamp;
		}
	}

	@Test
	public void testGetSequential_MostSignificantBitsAreSequential() {

		long oldMsb = 0;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UuidCreator.getSequential();
			long newMsb = uuid.getMostSignificantBits();

			if (i > 0) {
				assertTrue(newMsb >= oldMsb);
			}
			oldMsb = newMsb;
		}
	}

	@Test
	public void testGetTimeBased_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidCreator.getTimeBasedCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetSequential_TimestampIsCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			Instant instant1 = Instant.now();

			UUID uuid = UuidCreator.getSequentialCreator().withInstant(instant1).create();
			Instant instant2 = UuidUtil.extractInstant(uuid);

			long timestamp1 = TimestampUtil.toTimestamp(instant1);
			long timestamp2 = TimestampUtil.toTimestamp(instant2);

			assertEquals(timestamp1, timestamp2);
		}
	}

	@Test
	public void testGetDCESecurityl_LocalDomainAndLocalIdentifierAreCorrect() {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte localDomain = (byte) i;
			int localIdentifier = 1701;

			UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);

			byte localDomain2 = UuidUtil.extractDceSecurityLocalDomain(uuid);
			int localIdentifier2 = UuidUtil.extractDceSecurityLocalIdentifier(uuid);

			assertEquals(localDomain, localDomain2);
			assertEquals(localIdentifier, localIdentifier2);
		}
	}
	
	@Test
	public void testGetNameBasedMd5_CompareWithJavaUtilUuidNameUuidFromBytes() {

		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = null;
		UUID uuid = null;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			name = UuidCreator.getRandom().toString();
			uuid = UuidCreator.getNameBasedMd5(namespace, name);

			byte[] namespaceBytes = toBytes(namespace.toString().replaceAll("-", ""));
			byte[] nameBytes = name.getBytes();
			byte[] bytes = concat(namespaceBytes, nameBytes);

			assertEquals(UUID.nameUUIDFromBytes(bytes).toString(), uuid.toString());
		}
	}

	@Test
	public void testGetNameBasedMd5_NamespaceDnsAndSiteGithub() {
		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = "www.github.com";

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
	public void testGetNameBasedSha1_NamespaceDnsAndSiteGithub() {

		UUID namespace = UuidNamespace.NAMESPACE_DNS.getValue();
		String name = "www.github.com";

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
	public void testGetNameBasedSha256_NamespaceDnsAndSiteGithub() {

		UUID namespace = UUID.fromString("30313233-3435-3637-3839-616263646566");
		String name = "www.github.com";

		// Value generated by SHA256SUM (gnu-coreutils)
		UUID uuid1 = UUID.fromString("9bda8021-25d9-40fd-8cc6-ada3ddc23812");
		UUID uuid2 = UuidCreator.getNameBasedSha256(namespace, name);
		assertEquals(uuid1, uuid2);

		// Value generated by SHA256SUM (gnu-coreutils)
		UUID uuid3 = UUID.fromString("07147717-36a9-4da8-9181-77ebd224ae8d");
		UUID uuid4 = UuidCreator.getNameBasedSha256(name);
		assertEquals(uuid3, uuid4);
	}

	@Test
	public void testGetTimeBased_ShouldCreateAboutSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int clockSequenceRange = 0x3fff + 1; // 16,384
		Instant stoppedTime = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withInstant(stoppedTime);

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < clockSequenceRange; i++) {
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue("A duplicate UUID whas created", set.add(creator.create()));
		}

		assertTrue("A duplicate UUID whas created", set.size() == clockSequenceRange);
		
		try {
			creator.create();
			fail("The overrun exception must be thrown");
		} catch (OverrunException e) {
			// The factory must throw an exception if the maximum clock sequence
			// is reached.
		}
	}

	@Test
	public void testGetSequential_ShouldCreateAboutSixteenThousandUniqueUuidsWithTheTimeStopped() {

		int clockSequenceRange = 0x3fff + 1; // 16,384
		Instant stoppedTime = Instant.now();
		HashSet<UUID> set = new HashSet<>();

		// Instantiate a factory with a fixed timestamp, to simulate a request
		// rate greater than 16,384 per 100-nanosecond interval.
		SequentialUuidCreator creator = UuidCreator.getSequentialCreator().withInstant(stoppedTime);

		// Try to create 16,384 unique UUIDs
		for (int i = 0; i < clockSequenceRange; i++) {
			// Fail if the insertion into the hash set returns false, indicating
			// that there's a duplicate UUID.
			assertTrue("A duplicate UUID whas created", set.add(creator.create()));
		}

		assertTrue("A duplicate UUID whas created", set.size() == clockSequenceRange);
		
		try {
			creator.create();
			fail("The overrun exception must be thrown");
		} catch (OverrunException e) {
			// The factory must throw an exception if the maximum clock sequence
			// is reached.
		}
	}

	@Test
	public void testGetTimeBased_ParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getTimeBasedCreator(), DEFAULT_LOOP_LIMIT);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue("A duplicate UUID whas created", TestThread.hashSet.size() == (DEFAULT_LOOP_LIMIT * processors));
	}

	@Test
	public void testGetSequential_ParallelGeneratorsShouldCreateUniqueUuids() throws InterruptedException {

		Thread[] threads = new Thread[processors];
		TestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < processors; i++) {
			threads[i] = new TestThread(UuidCreator.getSequentialCreator(), DEFAULT_LOOP_LIMIT);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertTrue("A duplicate UUID whas created", TestThread.hashSet.size() == (DEFAULT_LOOP_LIMIT * processors));
	}
	
	@Test
	public void testCreateTimeBasedUuid_TheGreatestDateAndTimeShouldBeAtYear5236() {
		
		// Check if the maximum 60 bit timestamp corresponds to the date and time
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
	
	private void checkIfStringIsValid(UUID uuid) {
		assertTrue(uuid.toString().matches(UuidCreatorTest.UUID_PATTERN));
	}

	private void checkNullOrInvalid(UUID[] list) {
		for (UUID uuid : list) {
			assertTrue("UUID is null", uuid != null);
			assertTrue("UUID is not RFC-4122 variant", UuidUtil.isRfc4122Variant(uuid));
		}
	}

	private void checkVersion(UUID[] list, UuidVersion version) {
		for (UUID uuid : list) {
			assertTrue(String.format("UUID is not %s (version %s)", version.name(), version.getValue()),
					uuid.version() == version.getValue());
		}
	}

	private void checkUniqueness(UUID[] list) {
		HashSet<UUID> set = new HashSet<>();
		for (UUID uuid : list) {
			assertTrue(String.format("UUID is duplicated %s", uuid), set.add(uuid));
		}
		assertTrue("There are duplicated UUIDs", set.size() == DEFAULT_LOOP_LIMIT);
	}

	private void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID uuid : list) {
			long creationTime = UuidUtil.extractEpochMilliseconds(uuid);
			assertTrue("Creation time was before start time", creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

	}

	private void checkNodeIdentifier(UUID[] list) {
		for (UUID uuid : list) {
			long nodeIdentifier = UuidUtil.extractNodeIdentifier(uuid);
			assertTrue("Node identifier is not multicast",
					NodeIdentifierUtil.isMulticastNodeIdentifier(nodeIdentifier));
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

		public static HashSet<UUID> hashSet = new HashSet<>();
		private AbstractTimeBasedUuidCreator creator;
		private int loopLimit;

		public TestThread(AbstractTimeBasedUuidCreator creator, int loopLimit) {
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
