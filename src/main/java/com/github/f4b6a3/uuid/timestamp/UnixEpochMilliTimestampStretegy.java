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

package com.github.f4b6a3.uuid.timestamp;

public class UnixEpochMilliTimestampStretegy implements TimestampStrategy {

	/**
	 * Returns the count of milliseconds since 01-01-1970.
	 * 
	 * The timestamp is a 48-bit value. This value is the difference, measured
	 * in milliseconds, between the current time and midnight, January 1, 1970
	 * UTC. The the time value rolls over around AD 10,889.
	 * 
	 * <pre>
	 * s = 1000;
	 * m = 60 * s;
	 * h = 60 * m;
	 * d = 24 * h;
	 * y = 365.25 * d;
	 * 2^48 / y = ~8919
	 * 8919 + 1970 = 10889
	 * </pre>
	 * 
	 */
	@Override
	public long getTimestamp() {
		return System.currentTimeMillis();
	}
}
