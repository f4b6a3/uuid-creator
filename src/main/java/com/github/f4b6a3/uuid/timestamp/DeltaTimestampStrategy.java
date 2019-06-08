package com.github.f4b6a3.uuid.timestamp;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * This is an implementation of {@link TimestampStrategy} that provides
 * nanosecond resolution.
 * 
 * It implements a {@link Clock} that simulates a nanoseconds resolution by
 * calculating the difference (delta) between subsequent calls to the method
 * System.nanoTime(). This class provides nanosecond precision, but not
 * necessarily nanosecond resolution.
 * 
 * It's slower than {@link NanosecondTimestampStrategy} and
 * {@link DefaultTimestampStrategy}.
 * 
 */
public class DeltaTimestampStrategy implements TimestampStrategy {

	protected static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
	protected static final long TIMESTAMP_RESOLUTION = 100L;

	protected static final long GREGORIAN_SECONDS = LocalDate.parse("1582-10-15").atStartOfDay(ZoneId.of("UTC"))
			.toInstant().getEpochSecond();

	protected static final NanosClock clock = new NanosClock();

	@Override
	public long getTimestamp() {

		Instant instant = Instant.now(clock);

		final long seconds = ((instant.getEpochSecond() - GREGORIAN_SECONDS)
				* (NANOSECONDS_PER_SECOND / TIMESTAMP_RESOLUTION));
		final long milliseconds = instant.getNano() / TIMESTAMP_RESOLUTION;

		return (seconds + milliseconds);
	}

	/**
	 * Clock class responsible to create instants with nanoseconds.
	 */
	public static class NanosClock extends Clock {

		private final Clock clock;

		private Instant initialInstant;
		private long initialNanoseconds;

		private static final int SYNC_THRESHOLD = 1_000_000; // 1ms

		public NanosClock() {
			this(Clock.systemUTC());
		}

		public NanosClock(final Clock clock) {
			this.clock = clock;
			this.sync();
		}

		@Override
		public ZoneId getZone() {
			return this.clock.getZone();
		}

		@Override
		public synchronized Instant instant() {
			return this.initialInstant.plusNanos(this.getSystemNanos() - this.initialNanoseconds);
		}

		@Override
		public Clock withZone(final ZoneId zone) {
			return new NanosClock(this.clock.withZone(zone));
		}

		protected synchronized long getSystemNanos() {

			final long currentNanoseconds = System.nanoTime();

			if (Math.abs(currentNanoseconds - this.initialNanoseconds) > SYNC_THRESHOLD) {
				this.sync();
				return this.initialNanoseconds;
			}

			return currentNanoseconds;
		}

		protected void sync() {
			this.initialInstant = this.clock.instant();
			this.initialNanoseconds = System.nanoTime();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + (int) (initialNanoseconds ^ (initialNanoseconds >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			NanosClock other = (NanosClock) obj;
			return (initialNanoseconds == other.initialNanoseconds);
		}
	}
}
