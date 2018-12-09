package com.github.f4b6a3.test.other;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidGenerator;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.timestamp.StoppedDefaultTimestampStrategy;

/**
 * Runnable to test if a UUID is created more than once.
 */
public class CollisionTest implements Runnable {

	private int id;
	private UUID uuid;

	public static int threadCount = 10;
	public static int threadLoopLimit = 10_000;

	private static UUID[][] cache = new UUID[threadCount][threadLoopLimit];
	private static AbstractTimeBasedUuidCreator creator = UuidGenerator.getTimeBasedCreator()
			.withTimestampStrategy(new StoppedDefaultTimestampStrategy());

	public CollisionTest(int id) {
		this.id = id;
	}

	private boolean contains(UUID uuid) {
		for (int i = 0; i < cache.length; i++) {
			for (int j = 0; j < cache[0].length; j++) {
				if (cache[i][j] != null && cache[i][j].equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	public void run() {

		for (int i = 0; i < cache[0].length; i++) {

			uuid = creator.create();

			if (!contains(uuid)) {
				cache[id][i] = uuid;
			} else {
				// The current UUID have been created before by another thread.
				throw new RuntimeException(String.format("[UUID collision] thread: %s, index: %s, uuid: %s", id, i, uuid.toString()));
			}
		}
	}

	public static void startThreads() {
		for (int i = 0; i < CollisionTest.threadCount; i++) {
			Thread thread = new Thread(new CollisionTest(i));
			thread.start();
		}
	}
	
	public static void main(String[] args) {
		startThreads();
	}
}