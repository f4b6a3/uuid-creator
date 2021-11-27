package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.factory.AbstNameBasedFactory;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;
import com.github.f4b6a3.uuid.factory.NoArgsFactory;
import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.util.UuidUtil;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class UuidFactoryTest {

	protected static final int DEFAULT_LOOP_MAX = 10_000;

	protected static final String DUPLICATE_UUID_MSG = "A duplicate UUID was created";

	private static final UuidCodec<byte[]> bytesCodec = new BinaryCodec();

	protected static final int THREAD_TOTAL = availableProcessors();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	protected void checkNotNull(UUID[] list) {
		for (UUID uuid : list) {
			assertNotNull("UUID is null", uuid);
		}
	}

	protected void checkVersion(UUID[] list, int version) {
		for (UUID uuid : list) {
			assertTrue("UUID is not RFC-4122 variant", UuidUtil.isRfc4122(uuid));
			assertEquals(String.format("UUID is not version %s", version), uuid.version(), version);
		}
	}

	protected void checkCreationTime(UUID[] list, long startTime, long endTime) {

		assertTrue("Start time was after end time", startTime <= endTime);

		for (UUID uuid : list) {
			long creationTime = UuidUtil.getInstant(uuid).toEpochMilli();
			assertTrue("Creation time was before start time " + creationTime + " " + startTime,
					creationTime >= startTime);
			assertTrue("Creation time was after end time", creationTime <= endTime);
		}

	}

	protected void checkNodeIdentifier(UUID[] list, boolean multicast) {
		for (UUID uuid : list) {
			long nodeIdentifier = UuidUtil.getNodeIdentifier(uuid);

			if (multicast) {
				assertTrue("Node identifier is not multicast ", NodeIdFunction.isMulticast(nodeIdentifier));
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

	protected void testGetAbstractTimeBased(AbstTimeBasedFactory factory, boolean multicast) {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = factory.create();
		}

		long endTime = System.currentTimeMillis() + 1; // TS can be 1ms ahead

		checkNotNull(list);
		checkVersion(list, factory.getVersion().getValue());
		checkCreationTime(list, startTime, endTime);
		checkNodeIdentifier(list, multicast);
		checkOrdering(list);
		checkUniqueness(list);

	}

	public static class TestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
		private NoArgsFactory factory;
		private int loopLimit;

		public TestThread(NoArgsFactory factory, int loopLimit) {
			this.factory = factory;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					UUID uuid = factory.create();
					hashSet.add(uuid);
				}
			}
		}
	}

	public static class NameBasedTestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
		private AbstNameBasedFactory factory;
		private int loopLimit;

		public NameBasedTestThread(AbstNameBasedFactory factory, int loopLimit) {
			this.factory = factory;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			UUID namespace = UuidCreator.getRandomBased();
			UUID uuid;
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					String name = ("name" + i);
					uuid = factory.create(namespace, name);
					if (UuidUtil.isNameBasedMd5(uuid)) {
						// Check if it's compatible with `UUID#nameUUIDFromBytes()`
						byte[] bytes = getBytes(namespace, name);
						UUID expected = UUID.nameUUIDFromBytes(bytes);
						if (uuid.equals(expected)) {
							hashSet.add(uuid);
						}
					} else {
						hashSet.add(uuid);
					}
				}
			}
		}

		/**
		 * Method that prepares the input for UUID#nameUUIDFromBytes()
		 * 
		 * @param namespace a UUID
		 * @param name      a string
		 * @return a byte array
		 */
		private byte[] getBytes(UUID namespace, String name) {
			byte[] ns = bytesCodec.encode(namespace);
			byte[] nm = name.getBytes();
			byte[] bytes = new byte[ns.length + nm.length];
			System.arraycopy(ns, 0, bytes, 0, ns.length);
			System.arraycopy(nm, 0, bytes, ns.length, nm.length);
			return bytes;
		}
	}
}
