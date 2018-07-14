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

package com.github.f4b6a3.uuid.clock;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Random;

/**
 * Class that provides methods related to timestamps.
 * 
 * It also has two clock sequences (counters) to prevent UUID collisions.
 * 
 * @author fabiolimace
 *
 */
public class UUIDClock implements Serializable {

	private static final long serialVersionUID = -2664354707894888058L;
	
	private int sequence1 = 0;
	private int sequence2 = 0;
	
	private static final int SEQUENCE1_MIN = 0x8000;
	private static final int SEQUENCE1_MAX = 0xbfff;
	
	private static final int SEQUENCE2_MIN = 0;
	private static final int SEQUENCE2_MAX = 10_000;
	
	private Random random;
	private UUIDState state;
	
	public static final Instant GREGORIAN_EPOCH = getGregorianEpoch();
	
	public UUIDClock(UUIDState state) {
		this.random = new Random();
		this.state = state;
		reset();
	}
	
	/**
	 * Reset both clock sequences.
	 */
	private void reset() {
		this.sequence1 = random.nextInt(SEQUENCE1_MAX - SEQUENCE1_MIN) + SEQUENCE1_MIN;
		this.sequence2 = random.nextInt(SEQUENCE2_MAX - 1);
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
	
	/**
	 * Returns a {@link Instant} calculated from the number of milliseconds
	 * since the Gregorian Epoch.
	 * 
	 * @see {@link UUIDClock#getGregorianEpoch()}
	 * 
	 * @return
	 */	
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
		long hundredNanoseconds = milliseconds * SEQUENCE2_MAX;
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
		long milliseconds = hundredNanoseconds / SEQUENCE2_MAX;
		return getGregorianEpochInstant(milliseconds);
	}
	
	/**
	 * Returns the first clock sequence (clock-seq in the RFC-4122).
	 * 
	 * Clock sequence is a number defined by RFC-4122 used to prevent UUID
	 * collisions.
	 * 
	 * The first clock sequence is a random number between 0x8000 and 0xbfff.
	 * This number is incremented every time the timestamp is repeated.
	 * 
	 * @param timestamp
	 * @return
	 */
	public long getSequence1(long timestamp) {
		if(state.getTimestamp() != 0 && timestamp > state.getTimestamp()) {
			return this.sequence1;
		} else {
			synchronized (this) {
				this.sequence1++;
				if (this.sequence1 > SEQUENCE1_MAX - 1) {
					this.sequence1 = SEQUENCE1_MIN;
				}
				return this.sequence1;
			}
		}
	}
	
	/**
	 * Returns the second clock sequence (a counter in the RFC-4122).
	 * 
	 * The second clock sequence is a random number between 0 and 100000. Both
	 * clock sequences are incremented at the same time when a timestamp is
	 * repeated.
	 * 
	 * This second clock sequence was created because in Java there's no
	 * garantee to get accurate nanoseconds. So the least bits of timestamps are
	 * filled with a counter.
	 * 
	 * @param timestamp
	 * @return
	 */
	public long getSequence2(long timestamp) {
		if(state.getTimestamp() != 0 && timestamp > state.getTimestamp()) {
			return this.sequence2;
		} else {
			synchronized (this) {
				this.sequence2++;
				if (this.sequence2 > SEQUENCE2_MAX - 1) {
					this.sequence2 = SEQUENCE2_MIN;
				}
				return this.sequence2;
			}
		}
	}
}
