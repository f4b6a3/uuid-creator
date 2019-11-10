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

import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.timestamp.UnixMillisecondsTimestampStretegy;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.util.FingerprintUtil;
import com.github.f4b6a3.uuid.util.RandomUtil;

/**
 * Factory that creates lexicographically sortable GUIDs, based on the ULID
 * specification - Universally Unique Lexicographically Sortable Identifier.
 * 
 * ULID specification: https://github.com/ulid/spec
 */
public class LexicalOrderGuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	protected static final long MAX_LOW = 0xffffffffffffffffL; // ignore signal
	protected static final long MAX_HIGH = 0x000000000000ffffL;

	protected long previousTimestamp;

	protected Random random;
	
	protected long low;
	protected long high;

	protected static final String OVERFLOW_MESSAGE = "The system caused an overflow in the generator by requesting too many GUIDs.";

	protected TimestampStrategy timestampStrategy;

	public LexicalOrderGuidCreator() {
		this.reset();
		this.timestampStrategy = new UnixMillisecondsTimestampStretegy();
	}

	/**
	 * 
	 * Return a Lexical Order GUID.
	 * 
	 * It has two parts:
	 * 
	 * 1. A part of 48 bits that represent the amount of milliseconds since Unix
	 * Epoch, 1 January 1970.
	 * 
	 * 2. A part of 80 bits that has a random value generated a secure random
	 * generator.
	 * 
	 * If more than one GUID is generated within the same millisecond, the
	 * random part is incremented by one.
	 * 
	 * The random part is reset to a new value every time the millisecond part
	 * changes.
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
	 * It is a 80 bits integer. Cryptographically secure source of randomness,
	 * if possible.
	 * 
	 * #### Sorting
	 * 
	 * The left-most character must be sorted first, and the right-most
	 * character sorted last (lexical order). The default ASCII character set
	 * must be used. Within the same millisecond, sort order is not guaranteed.
	 * 
	 * #### Monotonicity
	 * 
	 * When generating a ULID within the same millisecond, we can provide some
	 * guarantees regarding sort order. Namely, if the same millisecond is
	 * detected, the random component is incremented by 1 bit in the least
	 * significant bit position (with carrying).
	 * 
	 * If, in the extremely unlikely event that, you manage to generate more
	 * than 280 ULIDs within the same millisecond, or cause the random component
	 * to overflow with less, the generation will fail.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException
	 *             an overflow exception if too many requests within the same
	 *             millisecond causes an overflow when incrementing the random
	 *             bits of the GUID.
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = this.getTimestamp();

		final long msb = (timestamp << 16) | high;
		final long lsb = low;

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
		if (random == null) {
			this.low = RandomUtil.nextLong();
			this.high = RandomUtil.nextLong() & MAX_HIGH;
		} else {
			this.low = random.nextLong();
			this.high = random.nextLong() & MAX_HIGH;
		}
	}

	/**
	 * Increment the random part of the GUID.
	 * 
	 * @throws UuidCreatorException
	 *             if an overflow happens.
	 */
	protected synchronized void increment() {
		if (this.low++ == MAX_LOW) {
			this.low = 0;
			if (this.high++ == MAX_HIGH) {
				this.high = 0;
				// Too many requests
				throw new UuidCreatorException(OVERFLOW_MESSAGE);
			}
		}
	}

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy
	 *            a timestamp strategy
	 * @return {@link LexicalOrderGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends LexicalOrderGuidCreator> T withTimestampStrategy(
			TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
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
	 * See {@link Random}.
	 * 
	 * @param random
	 *            a random generator
	 * @return {@link RandomUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends LexicalOrderGuidCreator> T withRandomGenerator(Random random) {
		this.random = random;
		return (T) this;
	}
	
	/**
	 * Replaces the default random generator with a faster one.
	 * 
	 * The host fingerprint is used to generate a seed for the random number generator.
	 * 
	 * See {@link Xorshift128PlusRandom} and {@link FingerprintUtil#getFingerprint()}
	 * 
	 * @return {@link RandomUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends LexicalOrderGuidCreator> T withFastRandomGenerator() {
		final int salt = (int) FingerprintUtil.getFingerprint();
		this.random = new Xorshift128PlusRandom(salt);
		return (T) this;
	}
}
