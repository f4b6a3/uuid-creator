package com.github.f4b6a3.uuid;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;

/**
 * Runnable to test if a UUID is used more than once.
 */
public class RaceConditionRunnable implements Runnable {
	
	private int id = 0;
	private UUID[][] array = null;
	private UUID uuid;
	private static TimeBasedUUIDCreator creator = null;
	
	public RaceConditionRunnable(int id, Instant instant, int threadCount, int threadLoopLimit) {
		this.id = id;
		this.array = new UUID[threadCount][threadLoopLimit];
		creator = UUIDGenerator.getTimeBasedUUIDCreator().withInstant(instant);
	}
	
	private boolean contains(UUID uuid) {
		for (int i = 0; i < array.length ; i ++) {
			for (int j = 0; j < array[0].length; j++) {
				if (this.array[i][j] != null && this.array[i][j].equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void run() {
		
		for (int i = 0; i < array[0].length; i++) {
			
			uuid = creator.create();

			if(!contains(uuid)) {
				array[id][i] = uuid;
			} else {
				// The current UUID have been generated before by another thread.
				throw new RuntimeException(String.format("[RaceConditionRunnable] UUID conflict: %s", uuid.toString()));
			}
		}
	}
}