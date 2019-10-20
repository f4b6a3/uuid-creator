/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

package com.github.f4b6a3.uuid.factory;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.FingerprintUtil;

/**
 * Factory that creates random-based UUIDs.
 * 
 * RFC-4122 version: 4.
 * 
 * The default random generator is {@link java.security.SecureRandom}, but it
 * can be replaced by any random generator that extends
 * {@link java.util.Random}.
 * 
 * Some fast random generators are provided along with this project, for
 * example, {@link Xorshift128PlusRandom}, which is default RNG for some web
 * browsers.
 * 
 */
public class RandomUuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	private Random random;

	public RandomUuidCreator() {
		super(UuidVersion.RANDOM_BASED);
	}

	/**
	 * Return a UUID with random value.
	 * 
	 * ### RFC-4122 - 4.4. Algorithms for Creating a UUID from Truly Random or
	 * Pseudo-Random Numbers
	 * 
	 * (1) Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * (2) Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the 4-bit version number from Section 4.1.3.
	 * 
	 * (3) Set all the other bits to randomly (or pseudo-randomly) chosen
	 * values.
	 * 
	 * @return a random UUID
	 */
	public UUID create() {

		long msb = 0;
		long lsb = 0;

		// (3) set all bit randomly
		if (this.random == null) {

			final byte[] bytes = new byte[16];
			SecureRandomLazyHolder.INSTANCE.nextBytes(bytes);
			msb = ByteUtil.toNumber(bytes, 0, 8);
			lsb = ByteUtil.toNumber(bytes, 8, 16);

		} else if (this.random instanceof SecureRandom) {

			final byte[] bytes = new byte[16];
			this.random.nextBytes(bytes);
			msb = ByteUtil.toNumber(bytes, 0, 8);
			lsb = ByteUtil.toNumber(bytes, 8, 16);

		} else {
			msb = this.random.nextLong();
			lsb = this.random.nextLong();
		}

		// (1)(2) Set the version and variant bits
		msb = setVersionBits(msb);
		lsb = setVariantBits(lsb);

		return new UUID(msb, lsb);
	}

	/**
	 * Replace the default random generator, in a fluent way, to another that
	 * extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom}.
	 * 
	 * For other faster pseudo-random generators, see {@link XorshiftRandom} and
	 * its variations.
	 * 
	 * {@link Random}.
	 * 
	 * @param random
	 *            a random generator
	 * @return {@link RandomUuidCreator}
	 */
	public synchronized RandomUuidCreator withRandomGenerator(Random random) {
		this.random = random;
		return this;
	}

	/**
	 * Replaces the default random generator with a fester one.
	 * 
	 * {@link Xorshift128PlusRandom}
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	public synchronized RandomUuidCreator withFastRandomGenerator() {
		final int salt = (int) FingerprintUtil.getFingerprint();
		this.random = new Xorshift128PlusRandom(salt);
		return this;
	}

	private static class SecureRandomLazyHolder {
		static final Random INSTANCE = new SecureRandom();
	}
}
