package com.github.f4b6a3.uuid.uniq;

import java.util.HashSet;
import java.util.UUID;

import com.github.f4b6a3.uuid.TestSuite;
import com.github.f4b6a3.uuid.factory.standard.TimeBasedFactory;
import com.github.f4b6a3.uuid.util.UuidUtil;

/**
 * This test runs many threads that request time-based UUIDs in a loop.
 * 
 * This is a longer and VERBOSE alternative to {@link UniquenessTest1}.
 */
public class UniquenessTest2 {

	private int threadCount; // Number of threads to run
	private int requestCount; // Number of requests for thread

	// private long[][] cacheLong; // Store values generated per thread
	private HashSet<Long> hashSet;

	private boolean verbose; // Show progress

	// Abstract time-based UUID factory
	private TimeBasedFactory factory;

	/**
	 * Initialize the test.
	 * 
	 * This test is not included in the {@link TestSuite} because it takes a long
	 * time to finish.
	 * 
	 * @param threadCount
	 * @param requestCount
	 * @param factory
	 * @param verbose
	 */
	public UniquenessTest2(TimeBasedFactory factory, int threadCount, int requestCount, boolean verbose) {
		this.threadCount = threadCount;
		this.requestCount = requestCount;
		this.factory = factory;
		this.verbose = verbose;
		this.initCache();
	}

	private void initCache() {
		this.hashSet = new HashSet<>();
	}

	/**
	 * Initialize and start the threads.
	 */
	public void start() {

		TestThread[] threads = new TestThread[this.threadCount];

		// Instantiate and start many threads
		for (int id = 0; id < this.threadCount; id++) {
			threads[id] = new TestThread(id, this.factory, verbose);
			threads[id].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public class TestThread extends Thread {

		private int id;
		private TimeBasedFactory factory;
		private boolean verbose;

		public TestThread(int id, TimeBasedFactory factory, boolean verbose) {
			this.id = id;
			this.factory = factory;
			this.verbose = verbose;

			if (this.factory == null) {
				// DEDICATED generator that creates time-based UUIDs (v1),
				// that uses a hash instead of a random node identifier,
				// and that uses a fixed millisecond to simulate a loop faster than the clock
				this.factory = newFactory();
			}
		}

		/**
		 * Run the test.
		 */
		@Override
		public void run() {

			long msb = 0;
			long lsb = 0;
			long value = 0;
			int progress = 0;
			int max = requestCount;

			for (int i = 0; i < max; i++) {

				// Request a UUID
				UUID uuid = factory.create();

				msb = UuidUtil.getTimestamp(uuid) << 16;
				lsb = UuidUtil.getClockSequence(uuid);

				value = (msb | lsb);

				if (verbose && (i % (max / 100) == 0)) {
					// Calculate and show progress
					progress = (int) ((i * 1.0 / max) * 100);
					System.out.println(String.format("[Thread %06d] %s %s %s%%", id, uuid, i, (int) progress));
				}
				synchronized (hashSet) {
					// Insert the value in cache, if it does not exist in it.
					if (!hashSet.add((Long) value)) {
						System.err.println(
								String.format("[Thread %06d] %s %s %s%% [DUPLICATE]", id, uuid, i, (int) progress));
					}
				}
			}

			if (verbose) {
				// Finished
				System.out.println(String.format("[Thread %06d] Done.", id));
			}
		}
	}

	public static void execute(TimeBasedFactory factory, int threadCount, int requestCount, boolean verbose) {
		UniquenessTest2 test = new UniquenessTest2(factory, threadCount, requestCount, verbose);
		test.start();
	}

	private static TimeBasedFactory newFactory() {
		// a new generator that creates time-based UUIDs (v1),
		// that uses a hash instead of a random node identifier,
		// and that uses a fixed millisecond to simulate a loop faster than the clock
		return new TimeBasedFactory.Builder().withHashNodeId().build();
	}

	public static void main(String[] args) {

		System.out.println("-----------------------------------------------------");
		System.out.println("SHARED generator for all threads                     ");
		System.out.println("-----------------------------------------------------");

		// SHARED generator for all threads
		TimeBasedFactory factory = newFactory();

		boolean verbose = true;
		int threadCount = 16; // Number of threads to run
		int requestCount = 1_000_000; // Number of requests for thread

		execute(factory, threadCount, requestCount, verbose);

		System.out.println();
		System.out.println("-----------------------------------------------------");
		System.out.println("DEDICATED generators for each thread                 ");
		System.out.println("-----------------------------------------------------");

		// Dedicated generators for each thread
		factory = null;

		verbose = true;
		threadCount = 16; // Number of threads to run
		requestCount = 1_000_000; // Number of requests for thread

		execute(factory, threadCount, requestCount, verbose);

	}
}
