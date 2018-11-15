package com.github.f4b6a3.uuid.strategy.timestamp;

import java.time.LocalDate;
import java.time.ZoneId;

public class MillisecondsTimestampStrategy implements TimestampStrategy {
	
	public static final long MILLI_MULTIPLIER = 1_000L;
	public static final long TIMESTAMP_MULTIPLIER = 10_000L;
	
	public static final long GREGORIAN_MILLISECONDS = getGregorianEpochMilliseconds();
	
	/**
	 * Get the current timestamp.
	 * 
	 * The UUID timestamp is a number of 100-nanos since gregorian epoch.
	 *
	 * Although it has 100-nanos precision, the timestamp returned has
	 * milliseconds accuracy.
	 * 
	 * "Precision" refers to the number of significant digits, and "accuracy" is
	 * whether the number is correct.
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
	 * @return
	 */
	@Override
	public long getTimestamp() {
		return (System.currentTimeMillis() - GREGORIAN_MILLISECONDS) * TIMESTAMP_MULTIPLIER;
	}

	/**
	 * Get the beggining of the Gregorian Calendar in seconds: 1582-10-15 00:00:00Z.
	 * 
	 * The expression "Gregorian Epoch" means the date and time the Gregorian
	 * Calendar started. This expression is similar to "Unix Epoch", started in
	 * 1970-01-01 00:00:00Z.
	 *
	 * @return
	 */
	private static long getGregorianEpochMilliseconds() {
		LocalDate localDate = LocalDate.parse("1582-10-15");
		return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().getEpochSecond() * MILLI_MULTIPLIER;
	}
}
