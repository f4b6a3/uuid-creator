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

package com.github.f4b6a3.uuid.factory.standard;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;

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
 * random positive integer between 1 and 2^32. This type of UUID is also like a
 * Monotonic ULID, but with a random increment instead of 1.
 * </ul>
 * <p>
 * If the underlying runtime provides enough clock precision, the microseconds
 * are also injected in the UUID, specifically in the {@code rand_a} field,
 * which is the name RFC 9562 gives to the 12 bits right after the milliseconds
 * field, from left to right. Otherwise, these 12 bits are randomly generated.
 * <p>
 * In JDK 11, we get 1 microsecond precision. However, in JDK 8, the maximum
 * precision we can get is 1 millisecond. On Windows, it is even worse because
 * the default precision is 15.625ms, due to the system clock's refresh rate of
 * 64Hz.
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

	private static final long versionBits = 0x000000000000f000L;
	private static final long variantBits = 0xc000000000000000L;
	private static final long upper16Bits = 0xffff000000000000L;
	private static final long upper48Bits = 0xffffffffffff0000L;

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
	 * Constructor with a function which a function which return random numbers and
	 * a clock.
	 * 
	 * @param randomFunction a function
	 * @param clock          a clock
	 */
	public TimeOrderedEpochFactory(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private TimeOrderedEpochFactory(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED_EPOCH, builder);

		switch (builder.getIncrementType()) {
		case INCREMENT_TYPE_PLUS_1:
			this.uuidFunction = new Plus1Function(random, instantFunction);
			break;
		case INCREMENT_TYPE_PLUS_N:
			this.uuidFunction = new PlusNFunction(random, instantFunction, builder.getIncrementMax());
			break;
		case INCREMENT_TYPE_DEFAULT:
		default:
			this.uuidFunction = new DefaultFunction(random, instantFunction);
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
		UUID uuid = this.uuidFunction.apply(null);
		return toUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	/**
	 * Returns a time-ordered unique identifier (UUIDv7) for a given instant.
	 * <p>
	 * The random component is generated with each method invocation.
	 * 
	 * @return a UUIDv7
	 * @param instant a given instant
	 */
	@Override
	public UUID create(Parameters parameters) {
		Objects.requireNonNull(parameters.getInstant(), "Null instant");
		UUID uuid = this.uuidFunction.apply(parameters.getInstant());
		return toUuid(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	static abstract class UuidFunction implements Function<Instant, UUID> {

		protected long msb = 0L; // most significant bits
		protected long lsb = 0L; // least significant bits

		protected final IRandom random;
		protected Supplier<Instant> instantFunction;
		protected final ReentrantLock lock = new ReentrantLock();

		// let go up to 1 second ahead of system clock
		private static final long advanceMax = 1_000L;

		// let's try to detect the system clock precision
		protected static final int precision = precision();

		protected static final int PRECISION_MILLISECOND = 1;
		protected static final int PRECISION_MICROSECOND = 2;

		protected static final long overflow = 0x0000000000000000L;

		public UuidFunction(IRandom random, Supplier<Instant> instantFunction) {

			this.random = random;
			this.instantFunction = instantFunction;

			// instantiate the internal state
			reset(this.instantFunction.get());
		}

		@Override
		public UUID apply(final Instant instant) {
			lock.lock();
			try {

				if (instant != null) {
					reset(instant); // user specified
					return new UUID(this.msb, this.lsb);
				}

				Instant now = instantFunction.get();

				long lastTime = this.lastTime();
				long time = now.toEpochMilli();

				// is it not too much ahead of system clock?
				if (advanceMax > Math.abs(lastTime - time)) {
					time = Math.max(lastTime, time);
				}

				if (time == lastTime) {
					increment(now);
				} else {
					reset(now);
				}

				return new UUID(this.msb, this.lsb);

			} finally {
				lock.unlock();
			}
		}

		/**
		 * Increment the `rand_b` field.
		 * 
		 * If the `rand_b` field rolls over, then `rand_a` should be incremented too.
		 * 
		 * Note that as `unix_ts_ms` and `rand_a` are stored in the same `long`
		 * variable, when `rand_a` rolls over, `unix_ts_ms` goes up automatically.
		 * 
		 * To be implemented by each specific subclass.
		 * 
		 * @param instant an instant
		 */
		abstract void increment(final Instant instant);

		/**
		 * Reset the `unix_ts_ms` field with the current milliseconds. Also set the
		 * `rand_a` and `rand_b` fields with random bits.
		 * 
		 * If there's enough clock precision, inject the current microseconds into the
		 * `rand_a` field instead of random bits.
		 * 
		 * @param instant an instant
		 */
		void reset(final Instant instant) {

			this.msb = instant.toEpochMilli() << 16;
			this.lsb = random.nextLong();

			if (precision == PRECISION_MILLISECOND) {
				// lack of precision: put random bits in `rand_a`
				this.msb = (msb & upper48Bits) | random.nextLong(2);
			} else {
				// set `rand_a` field
				microseconds(instant);
			}
		}

		/**
		 * Injects microseconds into the `rand_a` field.
		 * <p>
		 * It only works when the underlying runtime provides at least microsecond
		 * precision. Otherwise, this method won't change the value in `rand_a` field.
		 * 
		 * @param instant an instant
		 */
		void microseconds(final Instant instant) {

			// do nothing if not enough precision
			if (precision == PRECISION_MILLISECOND) {
				return;
			}

			final long shift = 12;
			final long scale = 1_000_000L;
			final long nanos = instant.getNano();
			final long randa = ((nanos % scale) << shift) / scale;

			// previous and next and timestamps
			final long prev = (msb & ~versionBits);
			final long next = (msb & upper48Bits) | (randa & 0x0fffL);

			// don't let the timestamp go backwards
			this.msb = (next > prev) ? next : prev;
		}

		long lastTime() {
			return this.msb >>> 16;
		}

		/**
		 * Returns the instant precision detected.
		 * 
		 * @param clock a custom clock instance
		 * @return the precision
		 */
		static int precision() {

			Clock clock = Clock.systemUTC();

			int best = 0;
			int loop = 3; // the best of 3

			for (int i = 0; i < loop; i++) {

				int x = 0;

				int nanosecond = clock.instant().getNano();

				if (nanosecond % 1_000_000 != 0) {
					x = PRECISION_MICROSECOND;
				} else {
					x = PRECISION_MILLISECOND;
				}

				best = Math.max(best, x);
			}

			return best;
		}
	}

	static final class DefaultFunction extends UuidFunction {

		public DefaultFunction(IRandom random, Supplier<Instant> instantFunction) {
			super(random, instantFunction);
		}

		@Override
		void increment(final Instant instant) {

			// set `rand_a` field
			microseconds(instant);

			// add 2^48 to `rand_b`
			this.lsb = (this.lsb & upper16Bits);
			this.lsb = (this.lsb | variantBits) + (1L << 48);

			if (this.lsb == overflow) {
				// add 1 to `rand_a` if overflow occurs
				this.msb = (this.msb | versionBits) + 1L;
			}

			// then randomize the lower 48 bits
			this.lsb = (this.lsb & upper16Bits) | this.random.nextLong(6);
		}
	}

	static final class Plus1Function extends UuidFunction {

		public Plus1Function(IRandom random, Supplier<Instant> instantFunction) {
			super(random, instantFunction);
		}

		@Override
		void increment(final Instant instant) {

			// set `rand_a` field
			microseconds(instant);

			// just add 1 to `rand_b`
			this.lsb = (this.lsb | variantBits) + 1L;

			if (this.lsb == overflow) {
				// add 1 to `rand_a` if overflow occurs
				this.msb = (this.msb | versionBits) + 1L;
			}
		}
	}

	static final class PlusNFunction extends UuidFunction {

		private final LongSupplier plusNFunction;

		public PlusNFunction(IRandom random, Supplier<Instant> instantFunction, Long incrementMax) {
			super(random, instantFunction);
			this.plusNFunction = customPlusNFunction(random, incrementMax);
		}

		@Override
		void increment(final Instant instant) {

			// set `rand_a` field
			microseconds(instant);

			// add a random n to `rand_b`, where 1 <= n <= incrementMax
			this.lsb = (this.lsb | variantBits) + plusNFunction.getAsLong();

			if (this.lsb == overflow) {
				// add 1 to `rand_a` if overflow occurs
				this.msb = (this.msb | versionBits) + 1L;
			}
		}

		private LongSupplier customPlusNFunction(IRandom random, Long incrementMax) {
			if (incrementMax == INCREMENT_MAX_DEFAULT) {
				if (random instanceof SafeRandom) {
					return () -> {
						// return n, where 1 <= n <= 2^32
						return random.nextLong(Integer.BYTES) + 1;
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
						return ((random.nextLong(size) & positive) % incrementMax) + 1;
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
