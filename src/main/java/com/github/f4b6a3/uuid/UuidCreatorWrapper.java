package com.github.f4b6a3.uuid;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class was created to test ReentrantLock using a wrapper. 
 * 
 * See: https://github.com/f4b6a3/uuid-creator/issues/92
 */
public class UuidCreatorWrapper {
	private static ReentrantLock lock = new ReentrantLock();

	public static UUID getUUID() {
		lock.lock();
		try {
			return UuidCreator.getTimeOrderedEpochPlus1();
		} finally {
			lock.unlock();
		}
	}
}
