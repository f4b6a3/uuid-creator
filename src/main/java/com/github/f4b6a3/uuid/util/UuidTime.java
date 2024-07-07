/*
 * MIT License
 * 
 * Copyright (c) 2018-2024 Fabio Lima
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
 * Utility for UUID time stamps.
 * <p>
 * The UUID time stamp is a 60-bit number.
 * <p>
 * The UUID time stamp resolution is 100ns, i.e., the UUID clock ticks every
 * 100-nanosecond interval.
 * <p>
 * In JDK 8, {@link Instant#now()} has millisecond precision, in spite of
 * {@link Instant} has nanoseconds resolution. In JDK 9+,{@link Instant#now()}
 * has microsecond precision.
 * 
 * @see <a href="https://stackoverflow.com/questions/1712205">Current time in
 *      microseconds in java</a>
 * @see <a href="https://bugs.openjdk.java.net/browse/JDK-8068730">Increase the
 *      precision of the implementation of java.time.Clock.systemUTC()</a>
 */
public final class UuidTime {

	/**
	 * The Unix epoch.
	 */
	public static final Instant EPOCH_UNIX = Instant.parse("1970-01-01T00:00:00.000Z"); // 0s
	/**
	 * The Gregorian epoch.
	 */
	public static final Instant EPOCH_GREG = Instant.parse("1582-10-15T00:00:00.000Z"); // -12219292800s

	/**
	 * The Unix epoch in seconds.
	 */
	public static final long EPOCH_UNIX_SECONDS = EPOCH_UNIX.getEpochSecond();
	/**
	 * The Gregorian epoch in seconds.
	 */
	public static final long EPOCH_GREG_SECONDS = EPOCH_GREG.getEpochSecond();

	/**
	 * Number nanos per clock tick.
	 */
	public static final long NANOS_PER_TICK = 100; // 1 tick = 100ns
	/**
	 * Number of clock ticks per millisecond.
	 */
	public static final long TICKS_PER_MILLI = 10_000; // 1ms = 10,000 ticks
	/**
	 * Number of clock ticks per second.
	 */
	public static final long TICKS_PER_SECOND = 10_000_000; // 1s = 10,000,000 ticks

	private UuidTime() {
	}

	/**
	 * Returns the number of 100ns since 1970-01-01 (Unix epoch).
	 * <p>
	 * It uses {@link Instant#now()} to get the the current time.
	 * 
	 * @return a number of 100ns since 1970-01-01 (Unix epoch).
	 */
	public static long getUnixTimestamp() {
		return toUnixTimestamp(Instant.now());
	}

	/**
	 * Returns the number of 100ns since 1582-10-15 (Gregorian epoch).
	 * <p>
	 * It uses {@link Instant#now()} to get the the current time.
	 * 
	 * @return a number of 100ns since 1582-10-15 (Gregorian epoch).
	 */
	public static long getGregTimestamp() {
		return toGregTimestamp(Instant.now());
	}

	/**
	 * Converts a number of 100ns since 1582-10-15 (Gregorian epoch) into a number
	 * of 100ns since 1970-01-01 (Unix epoch).
	 * 
	 * @param gregTimestamp a number of 100ns since 1582-10-15 (Gregorian epoch)
	 * @return a number of 100ns since 1970-01-01 (Unix epoch)
	 */
	public static long toUnixTimestamp(final long gregTimestamp) {
		return gregTimestamp + (EPOCH_GREG_SECONDS * TICKS_PER_SECOND);
	}

	/**
	 * Converts a number of 100ns since 1970-01-01 (Unix epoch) into a number of
	 * 100ns since 1582-10-15 (Gregorian epoch).
	 * 
	 * @param unixTimestamp a number of 100ns since 1970-01-01 (Unix epoch)
	 * @return a number of 100ns since 1582-10-15 (Gregorian epoch).
	 */
	public static long toGregTimestamp(final long unixTimestamp) {
		return unixTimestamp - (EPOCH_GREG_SECONDS * TICKS_PER_SECOND);
	}

	/**
	 * Converts an {@link Instant} into a number of 100ns since 1970-01-01 (Unix
	 * epoch).
	 * 
	 * @param instant an instant
	 * @return a number of 100ns since 1970-01-01 (Unix epoch).
	 */
	public static long toUnixTimestamp(final Instant instant) {
		final long seconds = instant.getEpochSecond() * TICKS_PER_SECOND;
		final long nanos = instant.getNano() / NANOS_PER_TICK;
		return seconds + nanos;
	}

	/**
	 * Converts an {@link Instant} into a number of 100ns since 1582-10-15
	 * (Gregorian epoch).
	 * 
	 * @param instant an instant
	 * @return a number of 100ns since 1582-10-15 (Gregorian epoch).
	 */
	public static long toGregTimestamp(final Instant instant) {
		final long seconds = (instant.getEpochSecond() - EPOCH_GREG_SECONDS) * TICKS_PER_SECOND;
		final long nanos = instant.getNano() / NANOS_PER_TICK;
		return seconds + nanos;
	}

	/**
	 * Converts a number of 100ns since 1970-01-01 (Unix epoch) into an
	 * {@link Instant}.
	 * 
	 * @param unixTimestamp a number of 100ns since 1970-01-01 (Unix epoch)
	 * @return an instant
	 */
	public static Instant fromUnixTimestamp(final long unixTimestamp) {
		final long seconds = unixTimestamp / TICKS_PER_SECOND;
		final long nanos = (unixTimestamp % TICKS_PER_SECOND) * NANOS_PER_TICK;
		return Instant.ofEpochSecond(seconds, nanos);
	}

	/**
	 * Converts a number of 100ns since 1582-10-15 (Gregorian epoch) into an
	 * {@link Instant}.
	 * 
	 * @param gregTimestamp a number of 100ns since 1582-10-15 (Gregorian epoch)
	 * @return an instant
	 */
	public static Instant fromGregTimestamp(final long gregTimestamp) {
		final long seconds = (gregTimestamp / TICKS_PER_SECOND) + EPOCH_GREG_SECONDS;
		final long nanos = (gregTimestamp % TICKS_PER_SECOND) * NANOS_PER_TICK;
		return Instant.ofEpochSecond(seconds, nanos);
	}
}
