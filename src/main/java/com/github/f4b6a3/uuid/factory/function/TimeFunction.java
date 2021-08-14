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

package com.github.f4b6a3.uuid.factory.function;

import java.time.Instant;
import java.util.function.LongSupplier;

import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;
import com.github.f4b6a3.uuid.util.UuidTime;

/**
 * It must return a number of 100-nanoseconds since 1970-01-01 (Unix epoch).
 * 
 * Use {@link TimeFunction#toTimestamp()} to convert the output to
 * 100-nanoseconds since 1970-01-01 (Unix epoch). It also sets the output within
 * the range 0 to 2^60-1.
 * 
 * The {@link AbstTimeBasedFactory} will convert the output time to Gregorian
 * epoch (1582-10-15) for you.
 * 
 * Example:
 * 
 * <pre>
 * // A `TimeFunction` that returns the `Instant.now()` as a number of 100-nanoseconds
 * TimeFunction f = () -> TimeFunction.toTimestamp(Instant.now()); // for JDK 9+
 * </pre>
 * 
 * In JDK 8, {@link Instant#now()} has millisecond precision, in spite of
 * {@link Instant} has nanoseconds resolution. In JDK 9+,{@link Instant#now()}
 * has microsecond precision.
 * 
 * Read: https://stackoverflow.com/questions/1712205
 * 
 * Read also: https://bugs.openjdk.java.net/browse/JDK-8068730
 */
@FunctionalInterface
public interface TimeFunction extends LongSupplier {

	/**
	 * This method clears the unnecessary leading bits so that the resulting number
	 * is within the range 0 to 2^60-1.
	 * 
	 * The result is equivalent to {@code n % 2^60}.
	 * 
	 * @param timestamp a number of 100-nanoseconds since 1970-01-01 (Unix epoch)
	 * @return the time within the range 0 to 2^60-1.
	 */
	public static long toExpectedRange(long timestamp) {
		return timestamp & 0x0fffffffffffffffL;
	}

	/**
	 * Converts an instant to a number of 100-nanoseconds since 1970-01-01 (Unix
	 * epoch).
	 * 
	 * @param instant an instant
	 * @return a number of 100-nanoseconds since 1970-01-01 (Unix epoch)
	 */
	public static long toTimestamp(final Instant instant) {
		final long timestamp = UuidTime.toUnixTimestamp(instant);
		return toExpectedRange(timestamp);
	}
}
