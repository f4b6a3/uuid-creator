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
 * provide clocks based on the best available system clock This may use
 * {@link System#currentTimeMillis()}, or a higher resolution clock if one is
 * available".
 * 
 * The class {@link DeltaTimestampStrategy} may be used in systems that
 * nanosecond resolution is not available.
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
	public long getTimestamp() {

		Instant instant = Instant.now();

		long seconds = ((instant.getEpochSecond() - GREGORIAN_SECONDS)
				* (NANOSECONDS_PER_SECOND / TIMESTAMP_RESOLUTION));
		long milliseconds = instant.getNano() / TIMESTAMP_RESOLUTION;

		return (seconds + milliseconds);
	}
}
