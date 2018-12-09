package com.github.f4b6a3.test.other;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidGenerator;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;

/**
 * Runnable to test if a UUID is created more than once.
 */
public class RaceConditionRunnable implements Runnable {

	private int id;
	private UUID uuid;
	
	public static int threadCount = 1;
	public static int threadLoopLimit = 100_000;
	
	private static UUID[][] cache = new UUID[threadCount][threadLoopLimit];
	private static AbstractTimeBasedUuidCreator creator = UuidGenerator.getTimeBasedCreator();
	
	public RaceConditionRunnable(int id) {
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
				throw new RuntimeException(String.format("UUID conflict: %s", uuid.toString())); 
			}
		}
	}
}