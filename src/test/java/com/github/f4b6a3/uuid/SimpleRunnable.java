package com.github.f4b6a3.uuid;

import java.time.Instant;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.UUIDCreator;

/**
 * Runnable to test if a UUID is created more than once.
 */
public class SimpleRunnable implements Runnable {

	private int id;
	private UUID uuid;
	
	public static int threadCount = 100;
	public static int threadLoopLimit = 100;
	
	// This instant won't change during this test
	private static Instant instant = Instant.now();
	
	private static UUID[][] cache = new UUID[threadCount][threadLoopLimit];
	private static UUIDCreator creator = UUIDGenerator.getTimeBasedUUIDCreator().withInstant(instant);
	
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	public SimpleRunnable(int id) {
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

	@Test
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