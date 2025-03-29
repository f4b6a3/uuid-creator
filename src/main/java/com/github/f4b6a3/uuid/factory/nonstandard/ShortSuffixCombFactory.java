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

package com.github.f4b6a3.uuid.factory.nonstandard;

import java.time.Clock;
import java.util.Random;
import java.util.UUID;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;

/**
 * Concrete factory for creating Short Suffix COMB GUIDs.
 * <p>
 * A Short Suffix COMB GUID is a UUID that combines a creation time with random
 * bits.
 * <p>
 * The creation minute is a 2 bytes SUFFIX at the LEAST significant bits.
 * <p>
 * The suffix wraps around every ~45 days (2^16/60/24 = ~45).
 * <p>
 * The created UUID is a UUIDv4 for compatibility with RFC 9562.
 * 
 * @see <a href=
 *      "https://www.2ndquadrant.com/en/blog/sequential-uuid-generators/">Sequential
 *      UUID Generators</a>
 */
public final class ShortSuffixCombFactory extends AbstCombFactory {

	/**
	 * Interval in milliseconds.
	 */
	protected final int interval;

	/**
	 * Default interval of 60 seconds in milliseconds.
	 */
	protected static final int DEFAULT_INTERVAL = 60_000;

	/**
	 * Default constructor.
	 */
	public ShortSuffixCombFactory() {
		this(builder());
	}

	/**
	 * Constructor with a clock.
	 * 
	 * @param clock a clock
	 */
	public ShortSuffixCombFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	/**
	 * Constructor with a random.
	 * 
	 * @param random a random generator
	 */
	public ShortSuffixCombFactory(Random random) {
		this(builder().withRandom(random));
	}

	/**
	 * Constructor with a random and a clock.
	 * 
	 * @param random a random
	 * @param clock  a clock
	 */
	public ShortSuffixCombFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	/**
	 * Constructor with a function which return random numbers.
	 * 
	 * @param randomFunction a function
	 */
	public ShortSuffixCombFactory(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	/**
	 * Constructor with a function which a function which return random numbers and
	 * a clock.
	 * 
	 * @param randomFunction a function
	 * @param clock          a clock
	 */
	public ShortSuffixCombFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private ShortSuffixCombFactory(Builder builder) {
		super(UuidVersion.VERSION_RANDOM_BASED, builder);
		this.interval = builder.getInterval();
	}

	/**
	 * Builder of factories.
	 */
	public static class Builder extends AbstCombFactory.Builder<ShortSuffixCombFactory, Builder> {

		private Integer interval;

		/**
		 * Get the interval in milliseconds.
		 * 
		 * @return the interval in milliseconds.
		 */
		protected int getInterval() {
			if (this.interval == null) {
				this.interval = DEFAULT_INTERVAL;
			}
			return this.interval;
		}

		/**
		 * Set the interval in milliseconds.
		 * 
		 * @param interval the interval in milliseconds
		 * @return the builder
		 */
		public Builder withInterval(int interval) {
			this.interval = interval;
			return this;
		}

		@Override
		public ShortSuffixCombFactory build() {
			return new ShortSuffixCombFactory(this);
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
	 * Returns a Short Suffix COMB GUID.
	 * 
	 * @return a UUIDv4
	 */
	@Override
	public UUID create() {
		lock.lock();
		try {
			final long time = instantFunction.get().toEpochMilli() / interval;
			final long long1 = this.random.nextLong(8);
			final long long2 = this.random.nextLong(6);
			return make(time, long1, long2);
		} finally {
			lock.unlock();
		}
	}

	private UUID make(final long time, final long long1, final long long2) {
		return toUuid(long1,
				(((long2 & 0x0000ffff00000000L) << 16) | (time & 0xffffL) << 32) | (long2 & 0x00000000ffffffffL));
	}
}
