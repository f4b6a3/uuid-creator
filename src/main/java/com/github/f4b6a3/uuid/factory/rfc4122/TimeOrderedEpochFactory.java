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

package com.github.f4b6a3.uuid.factory.rfc4122;

import java.time.Clock;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Concrete factory for creating Unix epoch time-ordered unique identifiers
 * (UUIDv7).
 * <p>
 * UUIDv7 is a new UUID version proposed by Peabody and Davis. It is similar to
 * Prefix COMB GUID and ULID.
 * <p>
 * This factory creates 3 types:
 * <ul>
 * <li><b>Type 1 (default)</b>: this type is divided in 3 components, namely
 * time, counter and random. The counter component is incremented by 1 when the
 * time repeats. The random component is always randomized.
 * <li><b>Type 2 (plus 1)</b>: this type is divided in 2 components, namely time
 * and monotonic random. The monotonic random component is incremented by 1 when
 * the time repeats. This type of UUID is like a Monotonic ULID. It can be much
 * faster than the other types.
 * <li><b>Type 3 (plus n)</b>: this type is also divided in 2 components, namely
 * time and monotonic random. The monotonic random component is incremented by a
 * random positive integer between 1 and MAX when the time repeats. If the value
 * of MAX is not specified, MAX is 2^32. This type of UUID is also like a
 * Monotonic ULID.
 * </ul>
 * <p>
 * <b>Warning:</b> this can change in the future.
 * 
 * @since 5.0.0
 * @see PrefixCombFactory
 * @see <a href="https://github.com/ulid/spec">ULID Specification</a>
 * @see <a href=
 *      "https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format">New
 *      UUID formats</a>
 * @see <a href="https://datatracker.ietf.org/wg/uuidrev/documents/">Revise
 *      Universally Unique Identifier Definitions (uuidrev)</a>
 */
public final class TimeOrderedEpochFactory extends AbstCombFactory {

	private final UuidFunction uuidFunction;

	private static final int INCREMENT_TYPE_DEFAULT = 0; // add 2^48 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_1 = 1; // just add 1 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_N = 2; // add a random n to `rand_b`, where 1 <= n <= 2^32

	private static final long INCREMENT_MAX_DEFAULT = 0xffffffffL; // 2^32-1

	// Used to preserve monotonicity when the system clock is
	// adjusted by NTP after a small clock drift or when the
	// system clock jumps back by 1 second due to leap second.
	private static final long CLOCK_DRIFT_TOLERANCE = 10_000;

	private static final long versionBits = 0x000000000000f000L;
	private static final long variantBits = 0xc000000000000000L;
	private static final long lower16Bits = 0x000000000000ffffL;
	private static final long upper16Bits = 0xffff000000000000L;

	/**
	 * Default constructor.
	 */
	public TimeOrderedEpochFactory() {
		this(builder());
	}

	/**
	 * Constructor with a clock.
	 * 
	 * @param clock a clock
	 */
	public TimeOrderedEpochFactory(Clock clock) {
		this(builder().withClock(clock));
	}

	/**
	 * Constructor with a random.
	 * 
	 * @param random a random
	 */
	public TimeOrderedEpochFactory(Random random) {
		this(builder().withRandom(random));
	}

	/**
	 * Constructor with a random and a clock.
	 * 
	 * @param random a random
	 * @param clock  a clock
	 */
	public TimeOrderedEpochFactory(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	/**
	 * Constructor with a function which return random numbers.
	 * 
	 * @param randomFunction a function
	 */
	public TimeOrderedEpochFactory(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	/**
	 * Constructor with a function which returns random arrays of bytes.
	 * 
	 * @param randomFunction a function
	 */
	public TimeOrderedEpochFactory(IntFunction<byte[]> randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	/**
	 * Constructor with a function which a function which return random numbers and
	 * a clock.
	 * 
	 * @param randomFunction a function
	 * @param clock          a clock
	 */
	public TimeOrderedEpochFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	/**
	 * Constructor with a function which a function which returns random arrays of
	 * bytes and a clock.
	 * 
	 * @param randomFunction a function
	 * @param clock          a clock
	 */
	public TimeOrderedEpochFactory(IntFunction<byte[]> randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private TimeOrderedEpochFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED_EPOCH, builder);

		switch (builder.getIncrementType()) {
		case INCREMENT_TYPE_PLUS_1:
			this.uuidFunction = new Plus1Function(random, timeFunction);
			break;
		case INCREMENT_TYPE_PLUS_N:
			this.uuidFunction = new PlusNFunction(random, timeFunction, builder.getIncrementMax());
			break;
		case INCREMENT_TYPE_DEFAULT:
		default:
			this.uuidFunction = new DefaultFunction(random, timeFunction);
		}
	}

	/**
	 * Concrete builder for creating a Unix epoch time-ordered factory.
	 *
	 * @see AbstCombFactory.Builder
	 */
	public static class Builder extends AbstCombFactory.Builder<TimeOrderedEpochFactory, Builder> {

		private Integer incrementType;
		private Long incrementMax;

		/**
		 * Set the increment type to PLUS 1.
		 * 
		 * @return the builder
		 */
		public Builder withIncrementPlus1() {
			this.incrementType = INCREMENT_TYPE_PLUS_1;
			this.incrementMax = null;
			return this;
		}

		/**
		 * Set the increment type to PLUS N.
		 * 
		 * @return the builder
		 */
		public Builder withIncrementPlusN() {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = null;
			return this;
		}

		/**
		 * Set the increment type to PLUS N and set the max increment.
		 * 
		 * @param incrementMax a number
		 * @return the builder
		 */
		public Builder withIncrementPlusN(long incrementMax) {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = incrementMax;
			return this;
		}

		/**
		 * Set the increment type.
		 * 
		 * @return an number
		 */
		protected int getIncrementType() {
			if (this.incrementType == null) {
				this.incrementType = INCREMENT_TYPE_DEFAULT;
			}
			return this.incrementType;
		}

		/**
		 * Get the max increment.
		 * 
		 * @return a number
		 */
		protected long getIncrementMax() {
			if (this.incrementMax == null) {
				this.incrementMax = INCREMENT_MAX_DEFAULT;
			}
			return this.incrementMax;
		}

		@Override
		public TimeOrderedEpochFactory build() {
			return new TimeOrderedEpochFactory(this);
		}
	}

	/**
	 * Returns a builder of Unix epoch time-ordered factory.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv7).
	 * 
	 * @return a UUIDv7
	 */
	@Override
	public UUID create() {
		UUID uuid = this.uuidFunction.get();
		return toUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	static abstract class UuidFunction implements Supplier<UUID> {

		protected long msb = 0L; // most significant bits
		protected long lsb = 0L; // least significant bits

		protected final IRandom random;
		protected final LongSupplier timeFunction;
		protected final ReentrantLock lock = new ReentrantLock();

		protected static final long overflow = 0x0000000000000000L;

		public UuidFunction(IRandom random, LongSupplier timeFunction) {

			this.random = random;
			this.timeFunction = timeFunction;

			// instantiate the internal state
			reset(this.timeFunction.getAsLong());
		}

		@Override
		public UUID get() {
			lock.lock();
			try {

				final long lastTime = this.time();
				final long time = timeFunction.getAsLong();

				// Check if the current time is the same as the previous time or has moved
				// backwards after a small system clock adjustment or after a leap second.
				// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
				if ((time > lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= lastTime)) {
					increment();
				} else {
					reset(time);
				}

				return new UUID(this.msb, this.lsb);

			} finally {
				lock.unlock();
			}
		}

		// to be implemented
		abstract void increment();

		long time() {
			return this.msb >>> 16;
		}

		void reset(final long time) {
			if (random instanceof SafeRandom) {
				final byte[] bytes = random.nextBytes(10);
				this.msb = (time << 16) | (ByteUtil.toNumber(bytes, 0, 2));
				this.lsb = ByteUtil.toNumber(bytes, 2, 10);
			} else {
				this.msb = (time << 16) | (random.nextLong() & lower16Bits);
				this.lsb = random.nextLong();
			}
		}
	}

	static final class DefaultFunction extends UuidFunction {

		public DefaultFunction(IRandom random, LongSupplier timeFunction) {
			super(random, timeFunction);
		}

		@Override
		void increment() {

			// add 2^48 to rand_b
			this.lsb = (this.lsb & upper16Bits);
			this.lsb = (this.lsb | variantBits) + (1L << 48);

			if (this.lsb == overflow) {
				// add 1 to rand_a if rand_b overflows
				this.msb = (this.msb | versionBits) + 1L;
			}

			// then randomize the lower 48 bits
			if (random instanceof SafeRandom) {
				final byte[] bytes = random.nextBytes(6);
				this.lsb |= ByteUtil.toNumber(bytes);
			} else {
				this.lsb |= this.random.nextLong() & (~upper16Bits);
			}
		}
	}

	static final class Plus1Function extends UuidFunction {

		public Plus1Function(IRandom random, LongSupplier timeFunction) {
			super(random, timeFunction);
		}

		@Override
		void increment() {

			// just add 1 to rand_b
			this.lsb = (this.lsb | variantBits) + 1L;

			if (this.lsb == overflow) {
				// add 1 to rand_a if rand_b overflows
				this.msb = (this.msb | versionBits) + 1L;
			}
		}
	}

	static final class PlusNFunction extends UuidFunction {

		private final LongSupplier plusNFunction;

		public PlusNFunction(IRandom random, LongSupplier timeFunction, Long incrementMax) {
			super(random, timeFunction);
			this.plusNFunction = customPlusNFunction(random, incrementMax);
		}

		@Override
		void increment() {

			// add a random n to rand_b, where 1 <= n <= incrementMax
			this.lsb = (this.lsb | variantBits) + plusNFunction.getAsLong();

			if (this.lsb == overflow) {
				// add 1 to rand_a if rand_b overflows
				this.msb = (this.msb | versionBits) + 1L;
			}
		}

		private LongSupplier customPlusNFunction(IRandom random, Long incrementMax) {
			if (incrementMax == INCREMENT_MAX_DEFAULT) {
				if (random instanceof SafeRandom) {
					return () -> {
						// return n, where 1 <= n <= 2^32
						final byte[] bytes = random.nextBytes(4);
						return ByteUtil.toNumber(bytes, 0, 4) + 1;
					};
				} else {
					return () -> {
						// return n, where 1 <= n <= 2^32
						return (random.nextLong() >>> 32) + 1;
					};
				}
			} else {
				final long positive = 0x7fffffffffffffffL;
				if (random instanceof SafeRandom) {
					// the minimum number of bits and bytes for incrementMax
					final int bits = (int) Math.ceil(Math.log(incrementMax) / Math.log(2));
					final int size = ((bits - 1) / Byte.SIZE) + 1;
					return () -> {
						// return n, where 1 <= n <= incrementMax
						final byte[] bytes = random.nextBytes(size);
						final long entropy = ByteUtil.toNumber(bytes, 0, size);
						return ((entropy & positive) % incrementMax) + 1;
					};
				} else {
					return () -> {
						// return n, where 1 <= n <= incrementMax
						return ((random.nextLong() & positive) % incrementMax) + 1;
					};
				}
			}
		}
	}
}
