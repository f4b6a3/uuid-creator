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

package com.github.f4b6a3.uuid.util;

import java.time.Instant;

/**
 * Class that provides methods related to timestamps.
 * 
 * All its public methods have milliseconds precision.
 *
 */
public class UuidTimeUtil {

	public static final long GREGORIAN_MILLISECONDS = getGregorianMilliseconds();

	public static final long MILLISECONDS_PER_SECOND = 1_000L;
	public static final long TIMESTAMP_RESOLUTION = 10_000L;

	private UuidTimeUtil() {
	}

	/**
	 * Get the current timestamp with milliseconds precision.
	 * 
	 * The UUID timestamp is the number of 100-nanos since 1582-10-15 (Gregorian
	 * epoch).
	 * 
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * The timestamp is generated from the system time, whose resolution may be
	 * less than the resolution of the UUID timestamp.
	 * 
	 * If UUIDs do not need to be frequently generated, the timestamp can simply
	 * be the system time multiplied by the number of 100-nanosecond intervals
	 * per system time interval.
	 * 
	 * (4) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @return the current timestamp
	 */
	public static long getCurrentTimestamp() {
		return toTimestamp(System.currentTimeMillis());
	}

	/**
	 * Get the timestamp of a given Unix Epoch milliseconds.
	 * 
	 * The value returned by this method is the number of 100-nanos since
	 * 1582-10-15 (Gregorian epoch).
	 * 
	 * @param unixMilliseconds
	 *            the Unix Epoch milliseconds
	 * @return the timestamp
	 */
	public static long toTimestamp(final long unixMilliseconds) {
		return (unixMilliseconds - GREGORIAN_MILLISECONDS) * TIMESTAMP_RESOLUTION;
	}

	/**
	 * Get the Unix Epoch milliseconds of a given timestmap.
	 * 
	 * The value returned by this method is the number of milliseconds since
	 * 1970-01-01 (Unix epoch).
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @return the Unix milliseconds
	 */
	public static long toUnixMilliseconds(final long timestamp) {
		return (timestamp / TIMESTAMP_RESOLUTION) + GREGORIAN_MILLISECONDS;
	}

	/**
	 * Get the timestamp of a given instant.
	 *
	 * The value returned by this method is the number of 100-nanos since
	 * 1582-10-15 (Gregorian epoch).
	 * 
	 * @param instant
	 *            an instant
	 * @return the timestamp
	 */
	public static long toTimestamp(final Instant instant) {
		final long a = (instant.toEpochMilli() - GREGORIAN_MILLISECONDS) * 10_000;
		final long b = (instant.getNano() / 100) % 10_000;
		return a + b;
	}

	/**
	 * Get the instant of the given timestamp.
	 *
	 * @param timestamp
	 *            a timestamp
	 * @return the instant
	 */
	public static Instant toInstant(final long timestamp) {
		final long millis = ((timestamp / 10_000) + GREGORIAN_MILLISECONDS);
		final long nanos = (timestamp % 10_000) * 100;
		return Instant.ofEpochMilli(millis).plusNanos(nanos);
	}

	/**
	 * Get the beginning of the Gregorian Calendar in milliseconds: 1582-10-15
	 * 00:00:00Z.
	 * 
	 * The expression "Gregorian Epoch" means the date and time the Gregorian
	 * Calendar started. This expression is similar to "Unix Epoch", started in
	 * 1970-01-01 00:00:00Z.
	 *
	 * @return the milliseconds since gregorian epoch
	 */
	private static long getGregorianMilliseconds() {
		return Instant.parse("1582-10-15T00:00:00.000Z").getEpochSecond() * MILLISECONDS_PER_SECOND;
	}
}
