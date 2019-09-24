/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

package com.github.f4b6a3.uuid.sequence;

/**
 * This abstract class represents a circular a counter or sequence.
 * 
 * If the maximum value is reached the value is reset to the minimum.
 */
public abstract class AbstractSequence implements Sequence {

	protected long value;
	public final long minValue;
	public final long maxValue;

	protected AbstractSequence(long min, long max) {
		this.minValue = min;
		this.maxValue = max;
		this.value = minValue;
	}

	@Override
	public long current() {
		return this.value;
	}

	@Override
	public long next() {
		if (this.value >= maxValue) {
			this.value = minValue;
			return this.value;
		}
		return ++this.value;
	}

	@Override
	public long min() {
		return minValue;
	}

	@Override
	public long max() {
		return maxValue;
	}

	@Override
	public void reset() {
		this.value = minValue;
	}
	
	@Override
	public void set(final long value) {
		if (value < minValue || value > maxValue) {
			this.reset();
		}
		this.value = value;
	}
}
