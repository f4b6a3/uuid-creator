package com.github.f4b6a3.collision;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.timestamp.StoppedDefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.LogUtil;

/**
 * Runnable to test if a UUID is created more than once.
 * 
 * This test creates many threads that keep requesting thousands of UUIDs
 * version 1 (time-based) fighting each other.
 * 
 * Each UUID is reduced to a `long` value to make comparisons faster. Only the
 * parts of UUIDs that does not change are considered. The timestamp high bits
 * and the node identifier bits are ignored because they don't change.
 * 
 * The timestamp strategy used is {@link StoppedDefaultTimestampStrategy} which
 * always returns the sime timestamp. The node identifier is fixed to
 * `0x111111111111L`.
 * 
 */
public class CollisionTest {

	private int threadCount; // Number of threads to run
	private int requestCount; // Number of requests for thread

	private long[][] cacheLong; // Store values generated per thread
	private UUID[][] cacheUUID; // Store UUIDs generated per thread

	private boolean verbose; // Show progress or not

	// Time based UUID creator
	private TimeBasedUuidCreator creator;

	/**
	 * Initialize the test.
	 * 
	 * @param threadCount
	 * @param requestCount
	 * @param creator
	 */
	public CollisionTest(int threadCount, int requestCount, TimeBasedUuidCreator creator, boolean progress) {
		this.threadCount = threadCount;
		this.requestCount = requestCount;
		this.creator = creator;
		this.verbose = progress;
		this.initCache();
	}

	private void initCache() {
		this.cacheUUID = new UUID[threadCount][requestCount];
		this.cacheLong = new long[threadCount][requestCount];
		for (int i = 0; i < cacheLong.length; i++) {
			for (int j = 0; j < cacheLong[0].length; j++) {
				cacheLong[i][j] = 0;
			}
		}
	}

	/**
	 * Initialize and start the threads.
	 */
	public void start() {
		for (int i = 0; i < this.threadCount; i++) {
			Thread thread = new Thread(new CollisionThread(i, verbose));
			thread.start();
		}
	}

	public class CollisionThread implements Runnable {

		private int id;
		private boolean verbose;

		public CollisionThread(int id, boolean verbose) {
			this.id = id;
			this.verbose = verbose;
		}

		/**
		 * Run the test.
		 */
		@Override
		public void run() {

			long msb = 0;
			long lsb = 0;
			long value = 0;
			double progress = 0;
			int max = cacheLong[0].length;

			for (int i = 0; i < max; i++) {

				// Request a UUID
				UUID uuid = creator.create();

				// Convert UUID into a long value, ignoring fixed bits
				msb = uuid.getMostSignificantBits() & 0xffffffffffff0000L;
				lsb = (uuid.getLeastSignificantBits() & 0xffff000000000000L) >>> 48;

				
				value = (msb | lsb);

				if (verbose) {
					// Calculate and show progress
					progress = (i * 1.0 / max) * 100;
					if (progress % 1 == 0) {
						LogUtil.log(String.format("[Thread %06d] %s %s %s%%", id, uuid, i, (int) progress));
					}
				}

				int[] position = find(value);

				// Insert the value in cache, if it does not exist in it.
				if (position.length == 0) {
					cacheLong[id][i] = value;
					cacheUUID[id][i] = uuid;
				} else {
					UUID other = cacheUUID[position[0]][position[1]];
					throw new UuidCreatorException(
							String.format("[COLLISION][Thread %s] %s %s %s %s%%", id, uuid, other, i, (int) progress));
				}
			}

			// Finished
			LogUtil.log(String.format("[Thread %s] Done.", id));
		}
		
		/**
		 * Check if a value aready exists in cache.
		 * 
		 * @param value
		 * @return
		 */
		private int[] find(long value) {
			for (int i = 0; i < cacheLong.length; i++) {
				for (int j = 0; j < cacheLong[0].length; j++) {
					if (cacheLong[i][j] == value) {
						return new int[] { i, j };
					} else if (cacheLong[i][j] == 0) {
						break;
					}
				}
			}
			return new int[0];
		}
	}

	public static void main(String[] args) {

		boolean verbose = true;
		int threadCount = 10; // Number of threads to run
		int requestCount = 100_000; // Number of requests for thread

		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator()
				.withTimestampStrategy(new StoppedDefaultTimestampStrategy()).withNodeIdentifier(0x111111111111L);

		CollisionTest test = new CollisionTest(threadCount, requestCount, creator, verbose);
		test.start();
	}
}
