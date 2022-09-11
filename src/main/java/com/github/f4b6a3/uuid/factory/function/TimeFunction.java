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

package com.github.f4b6a3.uuid.factory.function;

import java.time.Instant;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.util.UuidTime;

/**
 * Function that must return a number of 100-nanoseconds since 1970-01-01 (Unix
 * epoch).
 * <p>
 * Example:
 * 
 * <pre>{@code
 * // A function that returns `Instant.now()` as a number of 100ns
 * TimeFunction f = () -> TimeFunction.toUnixTimestamp(Instant.now());
 * }</pre>
 * 
 * <p>
 * In JDK 8, {@link Instant#now()} has millisecond precision, in spite of
 * {@link Instant} has nanoseconds resolution. In JDK 9+, {@link Instant#now()}
 * has microsecond precision.
 * 
 * @see <a href="https://stackoverflow.com/questions/1712205">Current time in
 *      microseconds in java</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8068730">Increase the
 *      precision of the implementation of java.time.Clock.systemUTC()</a>
 */
@FunctionalInterface
public interface TimeFunction extends LongSupplier {

	/**
	 * Converts an instant to a number of 100-nanoseconds since 1970-01-01 (Unix
	 * epoch).
	 * 
	 * @param instant an instant
	 * @return a number of 100-nanoseconds since 1970-01-01 (Unix epoch)
	 */
	public static long toUnixTimestamp(final Instant instant) {
		return UuidTime.toUnixTimestamp(instant);
	}

	/**
	 * Clears the leading bits so that the resulting number is in the range 0 to
	 * 2^60-1.
	 * <p>
	 * The result is equivalent to {@code n % 2^60}.
	 * 
	 * @param timestamp a number of 100-nanoseconds since 1970-01-01 (Unix epoch)
	 * @return a number in the range 0 to 2^60-1.
	 */
	public static long toExpectedRange(final long timestamp) {
		return timestamp & 0x0_fffffffffffffffL;
	}
}
