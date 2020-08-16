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

package com.github.f4b6a3.uuid.strategy.timestamp;

import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidTime;

/**
 * This is an implementation of {@link TimestampStrategy} that always returns
 * the same timestamp, simulating a stopped wall clock.
 * 
 * The milliseconds part is stopped, but the COUNTER still increments.
 * 
 * This strategy was created to substitute the strategy
 * {@link DefaultTimestampStrategy} in test cases.
 * 
 * This implementation wraps an instance of {@link DefaultTimestampStrategy}
 * instead of inheriting it.
 * 
 * It's useful for tests only.
 * 
 */
public final class StoppedTimestampStrategy implements TimestampStrategy {

	// Borrow logic from the default strategy instead of inheriting it
	private final DefaultTimestampStrategy strategy = new DefaultTimestampStrategy();

	private final long timestamp;
	
	public StoppedTimestampStrategy(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return this.timestamp + this.strategy.getNextCounter(timestamp);
	}
}
