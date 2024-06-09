/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.nonstandard;

import java.time.Clock;
import java.util.Random;
import java.util.UUID;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Concrete factory for creating Prefix COMB GUIDs.
 * <p>
 * A Prefix COMB GUID is a UUID that combines a creation time with random bits.
 * <p>
 * The creation millisecond is a 6 bytes PREFIX at the MOST significant bits.
 * <p>
 * The created UUID is a UUIDv4 for compatibility with RFC-4122.
 * 
 * @see <a href="http://www.informit.com/articles/article.aspx?p=25862">The Cost
 *      of GUIDs as Primary Keys</a>
 */
public final class PrefixCombFactory extends AbstCombFactory {

	/**
	 * Default constructor.
	 */
	public PrefixCombFactory() {
		this(builder());
	}

	/**
	 * Constructor with a clock.
	 * 
	 * @param clock a clock
	 */
	public PrefixCombFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	/**
	 * Constructor with a random.
	 * 
	 * @param random a random generator
	 */
	public PrefixCombFactory(Random random) {
		this(builder().withRandom(random));
	}

	/**
	 * Constructor with a random and a clock.
	 * 
	 * @param random a random
	 * @param clock  a clock
	 */
	public PrefixCombFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	/**
	 * Constructor with a function which return random numbers.
	 * 
	 * @param randomFunction a function
	 */
	public PrefixCombFactory(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	/**
	 * Constructor with a function which a function which return random numbers and
	 * a clock.
	 * 
	 * @param randomFunction a function
	 * @param clock          a clock
	 */
	public PrefixCombFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private PrefixCombFactory(Builder builder) {
		super(UuidVersion.VERSION_RANDOM_BASED, builder);
	}

	/**
	 * Builder of factories.
	 */
	public static class Builder extends AbstCombFactory.Builder<PrefixCombFactory, Builder> {
		@Override
		public PrefixCombFactory build() {
			return new PrefixCombFactory(this);
		}
	}

	/**
	 * Returns a new builder.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a Prefix COMB GUID.
	 * 
	 * @return a UUIDv4
	 */
	@Override
	public UUID create() {
		lock.lock();
		try {
			final long time = timeFunction.getAsLong();
			if (this.random instanceof SafeRandom) {
				final byte[] bytes = this.random.nextBytes(10);
				final long long1 = ByteUtil.toNumber(bytes, 0, 2);
				final long long2 = ByteUtil.toNumber(bytes, 2, 10);
				return make(time, long1, long2);
			} else {
				final long long1 = this.random.nextLong();
				final long long2 = this.random.nextLong();
				return make(time, long1, long2);
			}
		} finally {
			lock.unlock();
		}
	}

	private UUID make(final long time, final long long1, final long long2) {
		return toUuid((time << 16) | (long1 & 0x000000000000ffffL), long2);
	}
}
