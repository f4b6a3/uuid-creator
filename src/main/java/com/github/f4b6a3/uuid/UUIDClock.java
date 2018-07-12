package com.github.f4b6a3.uuid;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class UUIDClock {

	private int sequence1 = 0;
	private int sequence2 = 0;
	
	private static final int SEQUENCE_2_MIN = 0;
	private static final int SEQUENCE_2_MAX = 10_000;
	
	private static final int SEQUENCE_1_MIN = 0x8000;
	private static final int SEQUENCE_1_MAX = 0xbfff;
	
	private Random random;
	
	public static final Instant GREGORIAN_EPOCH = getGregorianEpoch();
	
	public UUIDClock() {
		this.random = new Random();
		reset();
	}
	
	private void reset() {
		this.sequence1 = random.nextInt(SEQUENCE_1_MAX - SEQUENCE_1_MIN) + SEQUENCE_1_MIN;
		this.sequence2 = random.nextInt(SEQUENCE_2_MAX - 1);
	}
	
	/**
	 * Get the beggining of the Gregorian Calendar: 1582-10-15 00:00:00Z.
	 * 
	 * The expression "Gregorian Epoch" means the date and time the Gregorian
	 * Calendar started. This expression is similar to "Unix Epoch", started in
	 * 1970-01-01 00:00:00Z.
	 *
	 * @return
	 */
	public static Instant getGregorianEpoch() {
		LocalDate localDate = LocalDate.parse("1582-10-15");
		return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
	}

	/**
	 * Get the number of milliseconds since the Gregorian Epoch.
	 * 
	 * @see {@link UUIDClock#getGregorianEpoch()}
	 * 
	 * @return
	 */
	public static long getGregorianEpochMillis(Instant instant) {
		return GREGORIAN_EPOCH.until(instant, ChronoUnit.MILLIS);
	}
	
	public static Instant getGregorianEpochInstant(long milliseconds) {
		return GREGORIAN_EPOCH.plus(milliseconds, ChronoUnit.MILLIS);
	}
	
	/**
	 * Get the timestamp associated with a given instant.
	 *
	 * UUID timestamp is a number of 100-nanos since gregorian epoch.
	 * 
	 * The total of milliseconds since gregorian epoch is multiplied by 10,000
	 * to get the timestamp precision.
	 *
	 * Although it has 100-nanos precision, the timestamp returned has
	 * milliseconds accuracy.
	 * 
	 * "Precision" refers to the number of significant digits, and "accuracy" is
	 * whether the number is correct.
	 *
	 * @param instant
	 * @return
	 */
	public static long getTimestamp(Instant instant) {
		long milliseconds = getGregorianEpochMillis(instant);
		long hundredNanoseconds = milliseconds * SEQUENCE_2_MAX;
		return hundredNanoseconds;
	}
	
	/**
	 * Get the instant associated with the given timestamp.
	 * 
	 * The timestamp value MUST be a UUID timestamp.
	 * 
	 * The timestamp is divided by 10,000 to get the milliseconds since
	 * gregorian epoch. That is, the timestamp is TRUNCATED to milliseconds
	 * precision. The instant is calculated from these milliseconds.
	 * 
	 * The instant returned has milliseconds accuracy.
	 * 
	 * @see {@link UUIDClock#getTimestamp(Instant)}
	 *
	 * @param timestamp
	 * @return
	 */
	public static Instant getInstant(long timestamp) {
		long hundredNanoseconds = timestamp;
		long milliseconds = hundredNanoseconds / SEQUENCE_2_MAX;
		return getGregorianEpochInstant(milliseconds);
	}
	
	// TODO
	public long getTimestamp(Instant instant, final UUIDState state) {
		long timestamp = getTimestamp(instant);
		return timestamp;
	}
	
	// TODO
	public long getSequence1(long timestamp, final UUIDState state) {
		if(state.getTimestamp() > 0 && timestamp > state.getTimestamp()) {
			return this.sequence1;
		} else {
			synchronized (this) {
				this.sequence1++;
				if (this.sequence1 > SEQUENCE_1_MAX - 1) {
					this.sequence1 = SEQUENCE_1_MIN;
				}
				return this.sequence1;
			}
		}
	}
	
	// TODO
	public long getSequence2(long timestamp, final UUIDState state) {
		if(state.getTimestamp() > 0 && timestamp > state.getTimestamp()) {
			return this.sequence2;
		} else {
			synchronized (this) {
				this.sequence2++;
				if (this.sequence2 > SEQUENCE_2_MAX - 1) {
					this.sequence2 = SEQUENCE_2_MIN;
				}
				return this.sequence2;
			}
		}
	}
}
