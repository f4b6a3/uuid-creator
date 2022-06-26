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

package com.github.f4b6a3.uuid.util;

import java.time.Instant;

/**
 * Utility for UUID time stamps.
 * 
 * The UUID timestamp is a 60-bit number.
 * 
 * The UUID timestamp resolution is 100ns, i.e., the UUID clock 'ticks' every
 * 100-nanosecond interval.
 * 
 * In JDK 8, {@link Instant#now()} has millisecond precision, in spite of
 * {@link Instant} has nanoseconds resolution. In JDK 9+,{@link Instant#now()}
 * has microsecond precision.
 * 
 * Read: https://stackoverflow.com/questions/1712205
 * 
 * Read also: https://bugs.openjdk.java.net/browse/JDK-8068730
 * 
 */
public final class UuidTime {

	public static final Instant EPOCH_UNIX = Instant.parse("1970-01-01T00:00:00.000Z"); // 0s
	public static final Instant EPOCH_GREG = Instant.parse("1582-10-15T00:00:00.000Z"); // -12219292800s

	public static final long EPOCH_UNIX_SECONDS = EPOCH_UNIX.getEpochSecond();
	public static final long EPOCH_GREG_SECONDS = EPOCH_GREG.getEpochSecond();

	public static final long NANOS_PER_TICK = 100; // 1 tick = 100ns
	public static final long TICKS_PER_MILLI = 10_000; // 1ms = 10,000 ticks
	public static final long TICKS_PER_SECOND = 10_000_000; // 1s = 10,000,000 ticks

	private UuidTime() {
	}

	/**
	 * This method returns the number of 100ns since 1970-01-01 (Unix epoch).
	 * 
	 * It uses {@code Instant.now()} to get the the current time.
	 * 
	 * @return a number of 100ns since 1970-01-01 (Unix epoch).
	 */
	public static long getUnixTimestamp() {
		return toUnixTimestamp(Instant.now());
	}

	/**
	 * This method returns the number of 100ns since 1582-10-15 (Gregorian epoch).
	 * 
	 * It uses {@code Instant.now()} to get the the current time.
	 * 
	 * @return a number of 100ns since 1582-10-15 (Gregorian epoch).
	 */
	public static long getGregTimestamp() {
		return toGregTimestamp(Instant.now());
	}

	/**
	 * This method converts a number of 100ns since 1582-10-15 (Gregorian epoch)
	 * into a number of 100ns since 1970-01-01 (Unix epoch).
	 * 
	 * @param gregTimestamp a number of 100ns since 1582-10-15 (Gregorian epoch)
	 * @return a number of 100ns since 1970-01-01 (Unix epoch)
	 */
	public static long toUnixTimestamp(final long gregTimestamp) {
		return gregTimestamp + (EPOCH_GREG_SECONDS * TICKS_PER_SECOND);
	}

	/**
	 * This method converts a number of 100ns since 1970-01-01 (Unix epoch) into a
	 * number of 100ns since 1582-10-15 (Gregorian epoch).
	 * 
	 * @param unixTimestamp a number of 100ns since 1970-01-01 (Unix epoch)
	 * @return a number of 100ns since 1582-10-15 (Gregorian epoch).
	 */
	public static long toGregTimestamp(final long unixTimestamp) {
		return unixTimestamp - (EPOCH_GREG_SECONDS * TICKS_PER_SECOND);
	}

	/**
	 * This method converts an {@code Instant} into a number of 100ns since
	 * 1970-01-01 (Unix epoch).
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
	 * This method converts an {@code Instant} into a number of 100ns since
	 * 1582-10-15 (Gregorian epoch).
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
	 * This method converts a number of 100ns since 1970-01-01 (Unix epoch) into an
	 * {@code Instant}.
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
	 * This method converts a number of 100ns since 1582-10-15 (Gregorian epoch)
	 * into an {@code Instant}.
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
