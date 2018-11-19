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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Class that provides methods related to timestamps.
 * 
 * All its public methods have milliseconds precision. 
 *
 */
public class TimestampUtil {

	public static final long GREGORIAN_MILLISECONDS = getGregorianEpochMilliseconds();

	public static final long MILLISECONDS_PER_SECOND = 1_000L;
	public static final long TIMESTAMP_RESOLUTION = 10_000L;

	/**
	 * Get the current timestamp with milliseconds precision.
	 * 
	 * The UUID timestamp is a number of 100-nanos since gregorian epoch.
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
	 * @return
	 */
	public static long getCurrentTimestamp() {
		return (System.currentTimeMillis() - GREGORIAN_MILLISECONDS) * TIMESTAMP_RESOLUTION;
	}

	/**
	 * Get the timestamp of a given instant with milliseconds precision.
	 *
	 * @param instant
	 * @return
	 */
	public static long toTimestamp(Instant instant) {
		return (instant.toEpochMilli() - GREGORIAN_MILLISECONDS) * TIMESTAMP_RESOLUTION;
	}

	/**
	 * Get the instant of the given timestamp with milliseconds precision.
	 *
	 * @param timestamp
	 * @return
	 */
	public static Instant toInstant(long timestamp) {
		return Instant.ofEpochMilli((timestamp / TIMESTAMP_RESOLUTION) + GREGORIAN_MILLISECONDS);
	}

	/**
	 * Get the beggining of the Gregorian Calendar in milliseconds: 1582-10-15
	 * 00:00:00Z.
	 * 
	 * The expression "Gregorian Epoch" means the date and time the Gregorian
	 * Calendar started. This expression is similar to "Unix Epoch", started in
	 * 1970-01-01 00:00:00Z.
	 *
	 * @return
	 */
	private static long getGregorianEpochMilliseconds() {
		return LocalDate.parse("1582-10-15").atStartOfDay(ZoneId.of("UTC")).toInstant().getEpochSecond()
				* MILLISECONDS_PER_SECOND;
	}

}
