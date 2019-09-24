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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * This is an implementation of {@link TimestampStrategy} that may provide
 * nanosecond resolution, if available, or millisecond resolution.
 * 
 * The the {@link Clock} documentation says: "The {@code system} factory methods
 * provide clocks based on the best available system clock. This may use
 * {@link System#currentTimeMillis()}, or a higher resolution clock if one is
 * available".
 * 
 * It's slower than {@link DefaultTimestampStrategy}.
 * 
 */
public class NanosecondTimestampStrategy implements TimestampStrategy {

	public static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
	public static final long TIMESTAMP_RESOLUTION = 100L;

	public static final long GREGORIAN_SECONDS = LocalDate.parse("1582-10-15").atStartOfDay(ZoneId.of("UTC"))
			.toInstant().getEpochSecond();

	@Override
	public synchronized long getTimestamp() {

		Instant instant = Instant.now();

		long seconds = ((instant.getEpochSecond() - GREGORIAN_SECONDS)
				* (NANOSECONDS_PER_SECOND / TIMESTAMP_RESOLUTION));
		long milliseconds = instant.getNano() / TIMESTAMP_RESOLUTION;

		return (seconds + milliseconds);
	}
}
