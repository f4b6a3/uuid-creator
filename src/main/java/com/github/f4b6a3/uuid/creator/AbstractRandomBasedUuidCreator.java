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

package com.github.f4b6a3.uuid.creator;

import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.strategy.RandomStrategy;
import com.github.f4b6a3.uuid.strategy.random.DefaultRandomStrategy;
import com.github.f4b6a3.uuid.strategy.random.OtherRandomStrategy;

/**
 * Factory that creates random-based UUIDs.
 */
public abstract class AbstractRandomBasedUuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	protected RandomStrategy randomStrategy;

	public AbstractRandomBasedUuidCreator(UuidVersion version) {
		super(version);
		this.randomStrategy = new DefaultRandomStrategy();
	}

	/**
	 * Returns a random-based UUID.
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
	 * (3) Set all the other bits to randomly (or pseudo-randomly) chosen values.
	 * 
	 * @return a random-based UUID
	 */
	public UUID create() {

		// (3) set all bit randomly
		final byte[] bytes = new byte[16];
		this.randomStrategy.nextBytes(bytes);
		
		// (1)(2) Set the version and variant bits
		return getUuid(bytes);
	}

	/**
	 * Replaces the default random strategy with another.
	 * 
	 * The default random strategy uses {@link java.util.Random}.
	 * 
	 * See {@link Random}.
	 * 
	 * @param randomStrategy a random strategy
	 * @param <T>    the type parameter
	 * @return {@link AbstractRandomBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractRandomBasedUuidCreator> T withRandomStrategy(RandomStrategy randomStrategy) {
		this.randomStrategy = randomStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default random strategy with another that uses the input
	 * {@link Random} instance.
	 * 
	 * It replaces the internal {@link DefaultRandomStrategy} with
	 * {@link OtherRandomStrategy}.
	 * 
	 * @param random a random generator
	 * @param <T>    the type parameter
	 * @return {@link AbstractRandomBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractRandomBasedUuidCreator> T withRandomGenerator(Random random) {
		this.randomStrategy = new OtherRandomStrategy(random);
		return (T) this;
	}
}
