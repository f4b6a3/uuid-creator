package com.github.f4b6a3.uuid.creator;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractUuidCreatorTest {

	// The timestamp counter starts with a random value between 0 and 255
	protected static final int DEFAULT_LOOP_MAX = 9744; // 10_000 - 256

	protected static final String RFC4122_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	protected static final String DUPLICATE_UUID_MSG = "A duplicate UUID was created";

	protected static final String GITHUB_URL = "www.github.com";

	protected static final int THREAD_TOTAL = availableProcessors();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	protected void checkIfStringIsValid(UUID uuid) {
		assertTrue(uuid.toString().matches(AbstractUuidCreatorTest.RFC4122_PATTERN));
	}

	protected void checkNullOrInvalid(UUID[] list) {
		for (UUID uuid : list) {
			assertNotNull("UUID is null", uuid);
			assertTrue("UUID is not RFC-4122 variant", UuidUtil.isRfc4122(uuid));
		}
	}

	protected void checkVersion(UUID[] list, int version) {
		for (UUID uuid : list) {
			assertEquals(String.format("UUID is not version %s", version), uuid.version(), version);
		}
	}

	protected void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID uuid : list) {
			long creationTime = UuidUtil.extractUnixMilliseconds(uuid);
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

	}

	protected void checkNodeIdentifier(UUID[] list, boolean multicast) {
		for (UUID uuid : list) {
			long nodeIdentifier = UuidUtil.extractNodeIdentifier(uuid);

			if (multicast) {
				assertTrue("Node identifier is not multicast",
						NodeIdentifierStrategy.isMulticastNodeIdentifier(nodeIdentifier));
			}
		}
	}

	protected void checkOrdering(UUID[] list) {
		UUID[] other = Arrays.copyOf(list, list.length);
		Arrays.sort(other);

		for (int i = 0; i < list.length; i++) {
			assertEquals("The UUID list is not ordered", list[i], other[i]);
		}
	}

	protected void checkUniqueness(UUID[] list) {

		HashSet<UUID> set = new HashSet<>();

		for (UUID uuid : list) {
			assertTrue(String.format("UUID is duplicated %s", uuid), set.add(uuid));
		}

		assertEquals("There are duplicated UUIDs", set.size(), list.length);
	}

	protected void testCreateAbstractTimeBasedUuid(AbstractTimeBasedUuidCreator creator, boolean multicast) {

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

	public static class TestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
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
			
			UUID uuid;
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					try {
						uuid = creator.create();
					} catch (UuidCreatorException e) {
						uuid = creator.create();
					}
					hashSet.add(uuid);
				}
			}
		}
	}
}
