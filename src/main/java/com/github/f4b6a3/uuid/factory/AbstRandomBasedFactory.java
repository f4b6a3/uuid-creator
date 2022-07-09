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

package com.github.f4b6a3.uuid.factory;

import java.security.SecureRandom;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultRandomFunction;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Factory that creates random-based UUIDs.
 */
public abstract class AbstRandomBasedFactory extends UuidFactory implements NoArgsFactory {

	protected final IRandom random;

	protected static final int UUID_BYTES = 16;

	protected AbstRandomBasedFactory(UuidVersion version, Builder<?, ?> builder) {
		super(version);
		this.random = builder.getRandom();
	}

	protected abstract static class Builder<T, B extends Builder<T, B>> {

		protected IRandom random;

		protected IRandom getRandom() {
			if (this.random == null) {
				this.random = new ByteRandom(new DefaultRandomFunction());
			}
			return this.random;
		}

		@SuppressWarnings("unchecked")
		public B withRandom(Random random) {
			if (random != null) {
				if (random instanceof SecureRandom) {
					this.random = new ByteRandom(random);
				} else {
					this.random = new LongRandom(random);
				}
			}
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B withRandomFunction(LongSupplier randomFunction) {
			this.random = new LongRandom(randomFunction);
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		public B withRandomFunction(IntFunction<byte[]> randomFunction) {
			this.random = new ByteRandom(randomFunction);
			return (B) this;
		}

		public abstract T build();
	}

	protected static interface IRandom {

		public long nextLong();

		public byte[] nextBytes(int length);
	}

	protected static final class LongRandom implements IRandom {

		private final LongSupplier randomFunction;

		public LongRandom() {
			this(newRandomFunction(null));
		}

		public LongRandom(Random random) {
			this(newRandomFunction(random));
		}

		public LongRandom(LongSupplier randomFunction) {
			this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
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

		protected static LongSupplier newRandomFunction(Random random) {
			final Random entropy = random != null ? random : new SecureRandom();
			return entropy::nextLong;
		}
	}

	protected static final class ByteRandom implements IRandom {

		private final IntFunction<byte[]> randomFunction;

		public ByteRandom() {
			this(newRandomFunction(null));
		}

		public ByteRandom(Random random) {
			this(newRandomFunction(random));
		}

		public ByteRandom(IntFunction<byte[]> randomFunction) {
			this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
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

		protected static IntFunction<byte[]> newRandomFunction(Random random) {
			final Random entropy = random != null ? random : new SecureRandom();
			return (final int length) -> {
				final byte[] bytes = new byte[length];
				entropy.nextBytes(bytes);
				return bytes;
			};
		}
	}
}
