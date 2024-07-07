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

package com.github.f4b6a3.uuid.factory;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultRandomFunction;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Abstract factory for creating random-based unique identifiers (UUIDv4).
 * 
 * @see RandomFunction
 */
public abstract class AbstRandomBasedFactory extends UuidFactory {

	/**
	 * The random generator.
	 */
	protected final IRandom random;

	/**
	 * The number of bytes of a UUID.
	 */
	protected static final int UUID_BYTES = 16;

	/**
	 * The reentrant lock for synchronization.
	 */
	protected final ReentrantLock lock = new ReentrantLock();

	/**
	 * Constructor with a version number and a builder
	 * 
	 * @param version a version number
	 * @param builder a builder
	 */
	protected AbstRandomBasedFactory(UuidVersion version, Builder<?, ?> builder) {
		super(version);
		this.random = builder.getRandom();
	}

	@Override
	public UUID create(Parameters parameters) {
		return create(); // ignore parameters
	}

	/**
	 * Abstract builder for creating a random-based factory.
	 *
	 * @param <T> factory type
	 * @param <B> builder type
	 */
	protected abstract static class Builder<T, B extends Builder<T, B>> {

		/**
		 * A random generator.
		 */
		protected IRandom random;

		/**
		 * Get the random generator.
		 * 
		 * @return a random generator
		 */
		protected IRandom getRandom() {
			if (this.random == null) {
				this.random = new SafeRandom(new DefaultRandomFunction());
			}
			return this.random;
		}

		/**
		 * Set the random generator with a fast algorithm.
		 * 
		 * Use it to replace the {@link DefaultRandomFunction} with
		 * {@link ThreadLocalRandom}.
		 * 
		 * @return the generator
		 */
		@SuppressWarnings("unchecked")
		public B withFastRandom() {
			this.random = new FastRandom();
			return (B) this;
		}

		/**
		 * Set the random generator with a safe algorithm.
		 * 
		 * Use it to replace the {@link DefaultRandomFunction} with
		 * {@link SecureRandom}.
		 * 
		 * @return the generator
		 */
		@SuppressWarnings("unchecked")
		public B withSafeRandom() {
			this.random = new SafeRandom();
			return (B) this;
		}

		/**
		 * Set the random generator.
		 * 
		 * @param random a random
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withRandom(Random random) {
			if (random != null) {
				if (random instanceof SecureRandom) {
					this.random = new SafeRandom(random);
				} else {
					this.random = new FastRandom(random);
				}
			}
			return (B) this;
		}

		/**
		 * Set a random function which returns random numbers.
		 * 
		 * @param randomFunction a function
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withRandomFunction(LongSupplier randomFunction) {
			this.random = new FastRandom(randomFunction);
			return (B) this;
		}

		/**
		 * Finishes the factory building.
		 * 
		 * @return the build factory
		 */
		public abstract T build();
	}

	/**
	 * Interface for random generator.
	 */
	protected interface IRandom {

		/**
		 * Return a random number.
		 * 
		 * @return a number
		 */
		long nextLong();

		/**
		 * Return a random array of bytes.
		 * 
		 * @param length the length
		 * @return an array
		 */
		byte[] nextBytes(int length);
	}

	/**
	 * A long random generator.
	 */
	protected static final class FastRandom implements IRandom {

		private final LongSupplier randomFunction;

		/**
		 * Default constructor.
		 */
		public FastRandom() {
			this(newFastFunction(null));
		}

		/**
		 * Constructor with a random.
		 * 
		 * @param random a random
		 */
		public FastRandom(Random random) {
			this(newFastFunction(Objects.requireNonNull(random)));
		}

		/**
		 * Constructor with a function which returns random numbers.
		 * 
		 * @param randomFunction a function
		 */
		public FastRandom(LongSupplier randomFunction) {
			this.randomFunction = Objects.requireNonNull(randomFunction);
		}

		@Override
		public long nextLong() {
			return randomFunction.getAsLong();
		}

		@Override
		public byte[] nextBytes(int length) {

			int shift = 0;
			long random = 0;
			final byte[] bytes = new byte[length];

			for (int i = 0; i < length; i++) {
				if (shift < Byte.SIZE) {
					shift = Long.SIZE;
					random = randomFunction.getAsLong();
				}
				shift -= Byte.SIZE; // 56, 48, 42...
				bytes[i] = (byte) (random >>> shift);
			}

			return bytes;
		}

		/**
		 * Returns a new random function.
		 * 
		 * @param random a random
		 * @return a function
		 */
		private static LongSupplier newFastFunction(Random random) {
			if (random != null) {
				return () -> random.nextLong();
			}
			return () -> ThreadLocalRandom.current().nextLong();
		}
	}

	/**
	 * A byte random generator.
	 */
	protected static final class SafeRandom implements IRandom {

		private final IntFunction<byte[]> randomFunction;

		/**
		 * Default constructor.
		 */
		public SafeRandom() {
			this(newSafeFunction(null));
		}

		/**
		 * Constructor with a random.
		 * 
		 * @param random a random
		 */
		public SafeRandom(Random random) {
			this(newSafeFunction(Objects.requireNonNull(random)));
		}

		/**
		 * Constructor with a function which returns random numbers.
		 * 
		 * @param randomFunction a function
		 */
		public SafeRandom(IntFunction<byte[]> randomFunction) {
			this.randomFunction = Objects.requireNonNull(randomFunction);
		}

		@Override
		public long nextLong() {
			byte[] bytes = this.randomFunction.apply(Long.BYTES);
			return ByteUtil.toNumber(bytes);
		}

		@Override
		public byte[] nextBytes(int length) {
			return this.randomFunction.apply(length);
		}

		/**
		 * Returns a new random function.
		 * 
		 * @param random a random
		 * @return a function
		 */
		private static IntFunction<byte[]> newSafeFunction(Random random) {
			final Random entropy = random != null ? random : new SecureRandom();
			return (final int length) -> {
				final byte[] bytes = new byte[length];
				entropy.nextBytes(bytes);
				return bytes;
			};
		}
	}
}
