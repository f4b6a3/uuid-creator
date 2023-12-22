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

package com.github.f4b6a3.uuid.factory.function.impl;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.internal.RandomUtil;

/**
 * Function that returns an array of bytes with the given length.
 * <p>
 * The current implementation uses a pool {@link SecureRandom}.
 * <p>
 * The pool size depends on the number of processors available, up to a maximum
 * of 32. The minimum is 4.
 * <p>
 * The pool items are deleted very often to avoid holding them for too long.
 * They are also deleted to avoid holding more instances than threads running.
 * <p>
 * The PRNG algorithm can be specified by system property or environment
 * variable. See {@link RandomUtil#newSecureRandom()}.
 * 
 * @see RandomFunction
 * @see RandomUtil#newSecureRandom()
 */
public final class DefaultRandomFunction implements RandomFunction {

	private static final int POOL_SIZE = processors();
	private static final Random[] POOL = new Random[POOL_SIZE];
	private static final ReentrantLock lock = new ReentrantLock();

	@Override
	public byte[] apply(final int length) {

		final byte[] bytes = new byte[length];
		current().nextBytes(bytes);

		// every now and then
		if (bytes.length > 0 && bytes[0x00] == 0) {
			// delete a random item from the pool
			delete((new Random()).nextInt(POOL_SIZE));
		}

		return bytes;
	}

	private static Random current() {

		// calculate the pool index given the current thread ID
		final int index = (int) Thread.currentThread().getId() % POOL_SIZE;

		lock.lock();
		try {
			// lazy loading instance
			if (POOL[index] == null) {
				POOL[index] = RandomUtil.newSecureRandom();
			}
			return POOL[index];
		} finally {
			lock.unlock();
		}
	}

	private static void delete(int index) {
		lock.lock();
		try {
			POOL[index] = null;
		} finally {
			lock.unlock();
		}
	}

	private static int processors() {

		final int min = 4;
		final int max = 32;

		// get the number of processors from the runtime
		final int processors = Runtime.getRuntime().availableProcessors();

		if (processors < min) {
			return min;
		} else if (processors > max) {
			return max;
		}

		return processors;
	}
}