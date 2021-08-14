/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

/**
 * A utility class that wraps a shared {@link SecureRandom} and provides new
 * instances of {@link SecureRandom}.
 */
public final class RandomUtil {

	protected static final SecureRandom SHARED_RANDOM = getSecureRandom();

	private RandomUtil() {
	}

	public static int nextInt() {
		return SHARED_RANDOM.nextInt();
	}

	public static long nextLong() {
		return SHARED_RANDOM.nextLong();
	}

	/**
	 * Returns a new instance of {@link java.security.SecureRandom}.
	 * 
	 * It tries to create an instance with the algorithm name specified in the
	 * system property `uuidcreator.securerandom` or in the environment variable
	 * `UUIDCREATOR_SECURERANDOM`. If the algorithm name is not supported by the
	 * runtime, it returns an instance with the default algorithm.
	 * 
	 * It can be useful to make use of SHA1PRNG or DRBG as a non-blocking source of
	 * random bytes. The SHA1PRNG algorithm is default in some operating systems
	 * that don't have '/dev/random' or '/dev/urandom', e.g., in Windows. The DRBG
	 * algorithm is available in JDK9+.
	 * 
	 * To control the algorithm used by this method, use the system property
	 * `uuidcreator.securerandom` or the environment variable
	 * `UUIDCREATOR_SECURERANDOM` as in examples below.
	 * 
	 * System property:
	 * 
	 * <pre>
	 * # Use the the algorithm SHA1PRNG for SecureRandom
	 * -Duuidcreator.securerandom="SHA1PRNG"
	 * 
	 * # Use the the algorithm DRBG for SecureRandom (JDK9+)
	 * -Duuidcreator.securerandom="DRBG"
	 * </pre>
	 * 
	 * Environment variable:
	 * 
	 * <pre>
	 * # Use the the algorithm SHA1PRNG for SecureRandom
	 * export UUIDCREATOR_SECURERANDOM="SHA1PRNG"
	 * 
	 * # Use the the algorithm DRBG for SecureRandom (JDK9+)
	 * export UUIDCREATOR_SECURERANDOM="DRBG"
	 * </pre>
	 * 
	 * @return a new {@link SecureRandom}.
	 */
	public static SecureRandom getSecureRandom() {
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
}
