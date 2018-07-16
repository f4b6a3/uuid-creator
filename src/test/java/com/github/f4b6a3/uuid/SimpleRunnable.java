package com.github.f4b6a3.uuid;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.f4b6a3.uuid.factory.UUIDCreator;

/**
 * Runnable to test if a UUID is created more than once.
 */
public class SimpleRunnable implements Runnable {

	private int id = 0;
	private UUID[][] cache = null;
	private UUID uuid;
	private static UUIDCreator creator = null;
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	public SimpleRunnable(int id, Instant instant, int threadCount, int threadLoopLimit) {
		this.id = id;
		this.cache = new UUID[threadCount][threadLoopLimit];
		creator = UUIDGenerator.getTimeBasedUUIDCreator().withInstant(instant);
	}

	private boolean contains(UUID uuid) {
		for (int i = 0; i < cache.length; i++) {
			for (int j = 0; j < cache[0].length; j++) {
				if (this.cache[i][j] != null && this.cache[i][j].equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {

		for (int i = 0; i < cache[0].length; i++) {

			uuid = creator.create();
			
			if (!contains(uuid)) {
				cache[id][i] = uuid;
			} else {
				// The current UUID have been created before by another thread.
				LOGGER.log(Level.WARNING, String.format("UUID conflict: %s", uuid.toString()));
			}
		}
	}
}