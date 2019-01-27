/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.factory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;

/**
 * Factory that creates random UUIDs version 4.
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
public class RandomUuidCreator extends AbstractUuidCreator {

	private Random random;

	public RandomUuidCreator() {
		super(VERSION_4);
		random = getSecureRandom();
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

		// (3) set all bit randomly
		long msb = this.random.nextLong();
		long lsb = this.random.nextLong();

		// (1)(2) Set the version and variant bits
		msb = setVersionBits(msb);
		lsb = setVariantBits(lsb);

		return new UUID(msb, lsb);
	}

	/**
	 * Replace the default random generator, in a fluent way, to another that
	 * extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom} with
	 * SHA1PRNG algorithm.
	 * 
	 * For other faster pseudo-random generators, see {@link XorshiftRandom} and
	 * its variations.
	 * 
	 * {@link Random}.
	 * 
	 * @param random a random generator
	 * @return {@link RandomUuidCreator}
	 */
	public RandomUuidCreator withRandomGenerator(Random random) {
		this.random = random;
		return this;
	}

	/**
	 * Replaces the default random generator with a fester one.
	 * 
	 * @see {@link Xorshift128PlusRandom}
	 * 
	 * @return  {@link RandomUuidCreator}
	 */
	public RandomUuidCreator withFastRandomGenerator() {
		this.random = new Xorshift128PlusRandom();
		return this;
	}

	/**
	 * Create a secure random instance with SHA1PRNG algorithm.
	 *
	 * If this algorithm is not present, it uses JVM's default.
	 * 
	 * {@link java.security.SecureRandom}
	 * 
	 */
	private static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}
}
