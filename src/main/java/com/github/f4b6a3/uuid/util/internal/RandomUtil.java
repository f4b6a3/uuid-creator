/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.util.internal;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class that provides random generator services.
 * <p>
 * The current implementation uses a pool {@link SecureRandom}.
 * <p>
 * The pool size depends on the number of processors available, up to a maximum
 * of 32. The minimum is 4.
 * <p>
 * The pool items are deleted very often to avoid holding them for too long.
 * They are also deleted to avoid holding more instances than threads running.
 * <p>
 * The PRNG algorithm can be specified by system property or environment
 * variable. See {@link RandomUtil#newSecureRandom()}.
 */
public final class RandomUtil {

	private RandomUtil() {
	}

	/**
	 * Returns a random 64-bit number.
	 * 
	 * @return a number
	 */
	public static long nextLong() {
		return SecureRandomPool.nextLong();
	}

	/**
	 * Returns an array of random bytes.
	 * 
	 * @param length the array length
	 * @return a byte array
	 */
	public static byte[] nextBytes(final int length) {
		return SecureRandomPool.nextBytes(length);
	}

	/**
	 * Returns a new instance of {@link java.security.SecureRandom}.
	 * <p>
	 * It tries to create an instance with the algorithm name specified in the
	 * system property `uuidcreator.securerandom` or in the environment variable
	 * `UUIDCREATOR_SECURERANDOM`. If the algorithm name is not supported by the
	 * runtime, it returns an instance with the default algorithm.
	 * <p>
	 * It can be useful to make use of SHA1PRNG or DRBG as a non-blocking source of
	 * random bytes. The SHA1PRNG algorithm is default on operating systems that
	 * don't have '/dev/random', e.g., on Windows. The DRBG algorithm is available
	 * in JDK 9+.
	 * <p>
	 * To control the algorithm used by this method, use the system property
	 * `uuidcreator.securerandom` or the environment variable
	 * `UUIDCREATOR_SECURERANDOM` as in examples below.
	 * <p>
	 * System property:
	 * 
	 * <pre>{@code
	 * # Use the the algorithm SHA1PRNG for SecureRandom
	 * -Duuidcreator.securerandom="SHA1PRNG"
	 * 
	 * # Use the the algorithm DRBG for SecureRandom (JDK9+)
	 * -Duuidcreator.securerandom="DRBG"
	 * }</pre>
	 * 
	 * <p>
	 * Environment variable:
	 * 
	 * <pre>{@code
	 * # Use the the algorithm SHA1PRNG for SecureRandom
	 * export UUIDCREATOR_SECURERANDOM="SHA1PRNG"
	 * 
	 * # Use the the algorithm DRBG for SecureRandom (JDK9+)
	 * export UUIDCREATOR_SECURERANDOM="DRBG"
	 * }</pre>
	 * 
	 * @return a new {@link SecureRandom}.
	 */
	public static SecureRandom newSecureRandom() {
		String algorithm = SettingsUtil.getSecureRandom();
		if (algorithm != null) {
			try {
				return SecureRandom.getInstance(algorithm);
			} catch (NoSuchAlgorithmException e) {
				return new SecureRandom();
			}
		}
		return new SecureRandom();
	}

	private static class SecureRandomPool {

		private static final Random random = new Random();
		private static final int POOL_SIZE = processors();
		private static final Random[] POOL = new Random[POOL_SIZE];
		private static final ReentrantLock lock = new ReentrantLock();

		private SecureRandomPool() {
		}

		public static long nextLong() {
			return ByteUtil.toNumber(nextBytes(Long.BYTES));
		}

		public static byte[] nextBytes(final int length) {

			final byte[] bytes = new byte[length];
			current().nextBytes(bytes);

			// every now and then
			if (bytes.length > 0 && bytes[0x00] == 0) {
				// delete a random item from the pool
				delete(random.nextInt(POOL_SIZE));
			}

			return bytes;
		}

		private static Random current() {

			// calculate the pool index given the current thread ID
			final int index = (int) Thread.currentThread().getId() % POOL_SIZE;

			lock.lock();
			try {
				// lazy loading instance
				if (POOL[index] == null) {
					POOL[index] = RandomUtil.newSecureRandom();
				}
				return POOL[index];
			} finally {
				lock.unlock();
			}
		}

		private static void delete(int index) {
			lock.lock();
			try {
				POOL[index] = null;
			} finally {
				lock.unlock();
			}
		}

		private static int processors() {

			final int min = 4;
			final int max = 32;

			// get the number of processors from the runtime
			final int processors = Runtime.getRuntime().availableProcessors();

			if (processors < min) {
				return min;
			} else if (processors > max) {
				return max;
			}

			return processors;
		}
	}
}
