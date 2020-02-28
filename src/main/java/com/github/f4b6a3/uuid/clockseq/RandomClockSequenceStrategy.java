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

package com.github.f4b6a3.uuid.clockseq;

import java.util.Random;
import com.github.f4b6a3.uuid.util.RandomUtil;

public class RandomClockSequenceStrategy implements ClockSequenceStrategy {

	protected Random random;

	protected static final int SEQUENCE_MAX = 0x00003fff;

	public RandomClockSequenceStrategy() {
	}

	public RandomClockSequenceStrategy(Random random) {
		this.random = random;
	}

	@Override
	public int getClockSequence(long timestamp) {
		if (this.random == null) {
			return RandomUtil.getInstance().nextInt() & SEQUENCE_MAX;
		}
		return this.random.nextInt() & SEQUENCE_MAX;
	}
}
