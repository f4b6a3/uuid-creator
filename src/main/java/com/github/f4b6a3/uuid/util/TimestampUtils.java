/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.util;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Class that provides methods related to timestamps.
 * 
 * @author fabiolimace
 *
 */
public class TimestampUtils implements Serializable {

	private static final long serialVersionUID = -2664354707894888058L;
	
	/*
	 * -------------------------
	 * Private static constants
	 * -------------------------
	 */
	private static final Instant GREGORIAN_EPOCH = getGregorianEpoch();
	private static final long TIMESTAMP_MULTIPLIER = 10_000;
	
	/* 
	 * -------------------------
	 * Public static methods
	 * -------------------------
	 */
	
	/**
	 * @see {@link TimestampUtils#getTimestamp(Instant)}
	 * @return
	 */
	public static long getTimestamp() {
		return TimestampUtils.getTimestamp(Instant.now());
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
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * The timestamp is generated from the system time, whose resolution may be
	 * less than the resolution of the UUID timestamp.
	 * 
	 * If UUIDs do not need to be frequently generated, the timestamp can simply
	 * be the system time multiplied by the number of 100-nanosecond intervals
	 * per system time interval.
	 *
	 * @param instant
	 * @return
	 */
	public static long getTimestamp(Instant instant) {
		long milliseconds = getGregorianEpochMillis(instant);
		long hundredNanoseconds = milliseconds * TIMESTAMP_MULTIPLIER;
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
	 * @see {@link TimestampUtils#getTimestamp(Instant)}
	 *
	 * @param timestamp
	 * @return
	 */
	public static Instant getInstant(long timestamp) {
		long hundredNanoseconds = timestamp;
		long milliseconds = hundredNanoseconds / TIMESTAMP_MULTIPLIER;
		return getGregorianEpochInstant(milliseconds);
	}

	/**
	 * Get the number of milliseconds since the Gregorian Epoch.
	 * 
	 * @see {@link TimestampUtils#getGregorianEpoch()}
	 * 
	 * @return
	 */
	public static long getGregorianEpochMillis(Instant instant) {
		return GREGORIAN_EPOCH.until(instant, ChronoUnit.MILLIS);
	}
	
	/**
	 * Returns a {@link Instant} calculated from the number of milliseconds
	 * since the Gregorian Epoch.
	 * 
	 * @see {@link TimestampUtils#getGregorianEpoch()}
	 * 
	 * @return
	 */	
	public static Instant getGregorianEpochInstant(long milliseconds) {
		return GREGORIAN_EPOCH.plus(milliseconds, ChronoUnit.MILLIS);
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
	private static Instant getGregorianEpoch() {
		LocalDate localDate = LocalDate.parse("1582-10-15");
		return localDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
	}
}
