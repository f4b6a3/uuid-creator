/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
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

import java.util.Random;

import com.github.f4b6a3.uuid.factory.function.RandomFunction;
import com.github.f4b6a3.uuid.util.internal.RandomUtil;

public final class DefaultRandomFunction implements RandomFunction {

	// the more processors, the better the machine
	private static final int POOL_SIZE = processors();
	private static final Random[] POOL = new Random[POOL_SIZE];

	@Override
	public byte[] apply(final int length) {
		final byte[] bytes = new byte[length];
		current().nextBytes(bytes);
		return bytes;
	}

	private static Random current() {

		// calculate the pool index given the current thread ID
		final int index = (int) Thread.currentThread().getId() % POOL_SIZE;

		// lazy loading instance
		if (POOL[index] == null) {
			POOL[index] = RandomUtil.getSecureRandom();
		}

		return POOL[index];
	}

	private static int processors() {

		final int min = 1;
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