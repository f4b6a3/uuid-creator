/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstCombFactory;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Concrete factory for creating Unix epoch time-ordered unique identifiers
 * (UUIDv7).
 * <p>
 * UUIDv7 is a new UUID version proposed by Peabody and Davis. It is similar to
 * Prefix COMB GUID and ULID.
 * <p>
 * This factory creates 3 types:
 * <p>
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
public final class TimeOrderedEpochFactory8 extends AbstCombFactory {

	private State state;

	private final LongSupplier plusNSupplier;
	private final LongConsumer increFunction;
	private final LongConsumer resetFunction;

	private static final int INCREMENT_TYPE_DEFAULT = 0; // add 2^48 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_1 = 1; // add 1 to `rand_b`
	private static final int INCREMENT_TYPE_PLUS_N = 2; // add n to `rand_b`, where 1 <= n <= 2^32-1

	// Used to preserve monotonicity when the system clock is
	// adjusted by NTP after a small clock drift or when the
	// system clock jumps back by 1 second due to leap second.
	protected static final int CLOCK_DRIFT_TOLERANCE = 10_000;

	// Test `ReentrantLock` vs `synchronized`
	// See: https://github.com/f4b6a3/uuid-creator/issues/92
	private ReentrantLock lock = new ReentrantLock();

	// Used to check if an overflow occurred.
	private static final long overflow = 0x0000000000000000L;

	// Used to propagate increments through bits.
	private static final long versionMask = 0x000000000000f000L;
	private static final long variantMask = 0xc000000000000000L;
	private static final long msblowrMask = 0x000000000000ffffL;
	private static final long clearerMask = 0xffff000000000000L;

	public TimeOrderedEpochFactory8() {
		this(builder());
	}

	public TimeOrderedEpochFactory8(Clock clock) {
		this(builder().withClock(clock));
	}

	public TimeOrderedEpochFactory8(Random random) {
		this(builder().withRandom(random));
	}

	public TimeOrderedEpochFactory8(Random random, Clock clock) {
		this(builder().withRandom(random).withClock(clock));
	}

	public TimeOrderedEpochFactory8(LongSupplier randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public TimeOrderedEpochFactory8(IntFunction<byte[]> randomFunction) {
		this(builder().withRandomFunction(randomFunction));
	}

	public TimeOrderedEpochFactory8(LongSupplier randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	public TimeOrderedEpochFactory8(IntFunction<byte[]> randomFunction, Clock clock) {
		this(builder().withRandomFunction(randomFunction).withClock(clock));
	}

	private TimeOrderedEpochFactory8(Builder builder) {
		super(UuidVersion.VERSION_TIME_ORDERED_EPOCH, builder);

		switch (builder.getIncrementType()) {
		case INCREMENT_TYPE_PLUS_1:
			this.plusNSupplier = null;
			this.increFunction = (final long unused) -> this._incrementPlus1();
			break;
		case INCREMENT_TYPE_PLUS_N:
			long incrementMax = builder.getIncrementMax();
			this.plusNSupplier = _setupPlusNSupplier(incrementMax);
			this.increFunction = (final long _void) -> this._incrementPlusN();
			break;
		case INCREMENT_TYPE_DEFAULT:
		default:
			this.plusNSupplier = null;
			this.increFunction = (final long _void) -> this._incrementDefault();
		}

		if (this.random instanceof ByteRandom) {
			this.resetFunction = (final long time) -> this._resetByteRandom(time);
		} else {
			this.resetFunction = (final long time) -> this._resetLongRandom(time);
		}

		// initialize state
		state = new State(); // with current time
		this.resetFunction.accept(timeFunction.getAsLong());
	}

	/**
	 * Concrete builder for creating a Unix epoch time-ordered factory.
	 *
	 * @see AbstCombFactory.Builder
	 */
	public static class Builder extends AbstCombFactory.Builder<TimeOrderedEpochFactory8, Builder> {

		private Integer incrementType;
		private Long incrementMax;

		public Builder withIncrementPlus1() {
			this.incrementType = INCREMENT_TYPE_PLUS_1;
			this.incrementMax = null;
			return this;
		}

		public Builder withIncrementPlusN() {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = null;
			return this;
		}

		public Builder withIncrementPlusN(long incrementMax) {
			this.incrementType = INCREMENT_TYPE_PLUS_N;
			this.incrementMax = incrementMax;
			return this;
		}

		protected int getIncrementType() {
			if (this.incrementType == null) {
				this.incrementType = INCREMENT_TYPE_DEFAULT;
			}
			return this.incrementType;
		}

		protected long getIncrementMax() {
			if (this.incrementMax == null) {
				this.incrementMax = 0xffffffffL;
			}
			return this.incrementMax;
		}

		@Override
		public TimeOrderedEpochFactory8 build() {
			return new TimeOrderedEpochFactory8(this);
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

		final long msb;
		final long lsb;

		final long time = timeFunction.getAsLong();

		lock.lock();
		try {
			final long lastTime = state.msb >>> 16;
			if ((time <= lastTime)) {
				this.increFunction.accept(0);
			} else {
				if ((time > lastTime - CLOCK_DRIFT_TOLERANCE)) {
					this.increFunction.accept(0);
				} else {
					this.resetFunction.accept(time);
				}
			}
			msb = state.msb;
			lsb = state.lsb;
		} finally {
			lock.unlock();
		}

		return toUuid(msb, lsb);
	}

	static final class State {
		private long msb; // most significant bits
		private long lsb; // least significant bits
	}

	private void _incrementPlus1() {
		state.lsb = (state.lsb | variantMask) + 1L;
		if (state.lsb == overflow) {
			state.msb = (state.msb | versionMask) + 1L;
		}
	}

	private void _incrementPlusN() {
		state.lsb = (state.lsb | variantMask) + plusNSupplier.getAsLong();
		if (state.lsb == overflow) {
			state.msb = (state.msb | versionMask) + 1L;
		}
	}

	private void _incrementDefault() {

		state.lsb = (state.lsb | variantMask) + (1L << 48);
		if ((state.lsb & clearerMask) == overflow) {
			state.msb = (state.msb | versionMask) + 1L;
		}

		state.lsb &= clearerMask; // Clear the unnecessary bits
		// And finally, randomize the lower 48 bits of the LSB.
		state.lsb |= ByteUtil.toNumber(this.random.nextBytes(6));
	}

	private void _resetByteRandom(final long time) {
		final byte[] bytes = this.random.nextBytes(10);
		state.msb = (time << 16) | (ByteUtil.toNumber(bytes, 0, 2) & msblowrMask);
		state.lsb = ByteUtil.toNumber(bytes, 2, 10);
	}

	private void _resetLongRandom(final long time) {
		state.msb = (time << 16) | (this.random.nextLong() & msblowrMask);
		state.lsb = this.random.nextLong();
	}

	private LongSupplier _setupPlusNSupplier(Long incrementMax) {
		if (incrementMax == null) {
			if (random instanceof ByteRandom) {
				return () -> {
					// add n to rand_b, where 1 <= n <= 2^32
					final byte[] bytes = this.random.nextBytes(4);
					return ByteUtil.toNumber(bytes, 0, 4) + 1;
				};
			} else {
				return () -> {
					// add n to rand_b, where 1 <= n <= 2^32
					return (this.random.nextLong() >>> 32) + 1;
				};
			}
		} else {
			final long positive = 0x7fffffffffffffffL;
			if (random instanceof ByteRandom) {
				// the minimum number of bits and bytes for incrementMax
				final int bits = (int) Math.ceil(Math.log(incrementMax) / Math.log(2));
				final int size = ((bits - 1) / Byte.SIZE) + 1;
				return () -> {
					// add n to rand_b, where 1 <= n <= incrementMax
					final byte[] bytes = this.random.nextBytes(size);
					final long random = ByteUtil.toNumber(bytes, 0, size);
					return ((random & positive) % incrementMax) + 1;
				};
			} else {
				return () -> {
					// add n to rand_b, where 1 <= n <= incrementMax
					return ((this.random.nextLong() & positive) % incrementMax) + 1;
				};
			}
		}
	}
}
