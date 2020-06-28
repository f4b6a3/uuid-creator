/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

package com.github.f4b6a3.uuid.strategy.clockseq;

import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;

import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Strategy that always provides the same clock sequence.
 * 
 * The clock sequence passed to the constructor is truncated to fit the accepted
 * range.
 */
public final class FixedClockSequenceStrategy implements ClockSequenceStrategy {

	private final int clockSequence;

	public FixedClockSequenceStrategy(int clockSequence) {
		this.clockSequence = clockSequence & 0x00003fff;
	}

	public FixedClockSequenceStrategy(byte[] clockSequence) {
		int clockseq = (int) toNumber(clockSequence);
		this.clockSequence = clockseq & 0x00003fff;
	}

	@Override
	public int getClockSequence(long timestamp) {
		return this.clockSequence;
	}
}
