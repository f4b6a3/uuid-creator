/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.UnixMillisecondsTimestampStretegy;
import com.github.f4b6a3.commons.util.FingerprintUtil;
import com.github.f4b6a3.commons.util.RandomUtil;
import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.commons.random.Xorshift128PlusRandom;
import com.github.f4b6a3.commons.random.XorshiftRandom;

/**
 * Factory that creates GUIDs based on the ULID specification - Universally
 * Unique Lexicographically Sortable Identifier.
 * 
 * ULID specification: https://github.com/ulid/spec
 */
public class UlidBasedGuidCreator implements NoArgumentsUuidCreator {

	protected long randomMsb = 0;
	protected long randomLsb = 0;

	protected long randomLsbMax;
	protected long randomMsbMax;

	protected static final long HALF_RANDOM_COMPONENT = 0x000000ffffffffffL;
	protected static final long INCREMENT_MAX = 0x0000010000000000L;

	protected long previousTimestamp;

	protected Random random;

	protected boolean useThreadLocal = false;

	protected static final String OVERRUN_MESSAGE = "The system overran the generator by requesting too many GUIDs.";

	protected TimestampStrategy timestampStrategy;

	public UlidBasedGuidCreator() {
		this.reset();
		this.timestampStrategy = new UnixMillisecondsTimestampStretegy();
	}

	/**
	 * 
	 * Return a GUID based on the ULID specification.
	 * 
	 * It has two parts:
	 * 
	 * 1. A part of 48 bits that represent the amount of milliseconds since Unix
	 * Epoch, 1 January 1970.
	 * 
	 * 2. A part of 80 bits that has a random value generated a secure random
	 * generator.
	 * 
	 * The random part is reset to a new value every time the millisecond part
	 * changes.
	 * 
	 * If more than one GUID is generated within the same millisecond, the random
	 * part is incremented by one.
	 * 
	 * The maximum GUIDs that can be generated per millisecond is 2^80.
	 * 
	 * ### Specification of Universally Unique Lexicographically Sortable ID
	 * 
	 * #### Components
	 * 
	 * ##### Timestamp
	 * 
	 * It is a 48 bit integer. UNIX-time in milliseconds. Won't run out of space
	 * 'til the year 10889 AD.
	 * 
	 * ##### Randomness
	 * 
	 * It is a 80 bits integer. Cryptographically secure source of randomness, if
	 * possible.
	 * 
	 * #### Sorting
	 * 
	 * The left-most character must be sorted first, and the right-most character
	 * sorted last (lexical order). The default ASCII character set must be used.
	 * Within the same millisecond, sort order is not guaranteed.
	 * 
	 * #### Monotonicity
	 * 
	 * When generating a ULID within the same millisecond, we can provide some
	 * guarantees regarding sort order. Namely, if the same millisecond is detected,
	 * the random component is incremented by 1 bit in the least significant bit
	 * position (with carrying).
	 * 
	 * If, in the extremely unlikely event that, you manage to generate more than
	 * 2^80 ULIDs within the same millisecond, or cause the random component to
	 * overflow with less, the generation will fail.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException an overrun exception if too many requests are
	 *                              made within the same millisecond.
	 */
	public synchronized UUID create() {

		final long timestamp = this.getTimestamp();

		final long randomHi = truncate(randomMsb);
		final long randomLo = truncate(randomLsb);

		final long msb = (timestamp << 16) | (randomHi >>> 24);
		final long lsb = (randomHi << 40) | randomLo;

		return new UUID(msb, lsb);
	}

	/**
	 * Return the current timestamp and resets or increments the random part.
	 * 
	 * @return timestamp
	 */
	protected synchronized long getTimestamp() {

		final long timestamp = this.timestampStrategy.getTimestamp();

		if (timestamp == this.previousTimestamp) {
			this.increment();
		} else {
			this.reset();
		}

		this.previousTimestamp = timestamp;
		return timestamp;
	}

	/**
	 * Reset the random part of the GUID.
	 */
	protected synchronized void reset() {

		// Get random values
		if (useThreadLocal) {
			this.randomMsb = truncate(ThreadLocalRandom.current().nextLong());
			this.randomLsb = truncate(ThreadLocalRandom.current().nextLong());
		} else if (random == null) {
			this.randomMsb = truncate(RandomUtil.get().nextLong());
			this.randomLsb = truncate(RandomUtil.get().nextLong());
		} else {
			this.randomMsb = truncate(random.nextLong());
			this.randomLsb = truncate(random.nextLong());
		}

		// Save the random values
		this.randomMsbMax = this.randomMsb | INCREMENT_MAX;
		this.randomLsbMax = this.randomLsb | INCREMENT_MAX;
	}

	/**
	 * Increment the random part of the GUID.
	 * 
	 * An exception is thrown when more than 2^80 increment operations are made.
	 * 
	 * @throws UuidCreatorException if an overrun happens.
	 */

	protected synchronized void increment() {
		if ((++this.randomLsb == this.randomLsbMax) && (++this.randomMsb == this.randomMsbMax)) {
			this.reset();
			throw new UuidCreatorException(OVERRUN_MESSAGE);
		}
	}

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy a timestamp strategy
	 * @return {@link UlidBasedGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidBasedGuidCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Replace the default random generator, in a fluent way, to another that
	 * extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom}.
	 * 
	 * For other faster pseudo-random generators, see {@link XorshiftRandom} and its
	 * variations.
	 * 
	 * See {@link Random}.
	 * 
	 * @param random a random generator
	 * @return {@link UlidBasedGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidBasedGuidCreator> T withRandomGenerator(Random random) {

		this.random = random;

		// disable thread local
		this.useThreadLocal = false;

		return (T) this;
	}

	/**
	 * Replaces the default random generator with a faster one.
	 * 
	 * The host fingerprint is used to generate a seed for the random number
	 * generator.
	 * 
	 * See {@link Xorshift128PlusRandom} and
	 * {@link FingerprintUtil#getFingerprint()}
	 * 
	 * @return {@link UlidBasedGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidBasedGuidCreator> T withFastRandomGenerator() {

		final int salt = (int) FingerprintUtil.getFingerprint();
		this.random = new Xorshift128PlusRandom(salt);

		// disable thread local
		this.useThreadLocal = false;

		return (T) this;
	}

	/**
	 * Replaces the default random generator with ThreadLocalRandom.
	 * 
	 * See {@link java.util.concurrent.ThreadLocalRandom}
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends UlidBasedGuidCreator> T withThreadLocalRandomGenerator() {

		this.useThreadLocal = true;

		// remove random instance
		this.random = null;

		return (T) this;
	}

	/**
	 * Truncate long to half random component.
	 * 
	 * @param value a value to be truncated.
	 * @return truncated value
	 */
	protected synchronized long truncate(final long value) {
		return (value & HALF_RANDOM_COMPONENT);
	}

	/**
	 * For unit tests
	 */
	protected long extractRandomLsb(UUID uuid) {
		return uuid.getLeastSignificantBits() & HALF_RANDOM_COMPONENT;
	}

	/**
	 * For unit tests
	 */
	protected long extractRandomMsb(UUID uuid) {
		return ((uuid.getMostSignificantBits() & 0xffff) << 24) | (uuid.getLeastSignificantBits() >>> 40);
	}
}
