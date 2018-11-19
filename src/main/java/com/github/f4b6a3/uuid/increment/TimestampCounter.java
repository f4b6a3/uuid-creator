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

package com.github.f4b6a3.uuid.increment;

import com.github.f4b6a3.uuid.util.TimestampUtil;

public class TimestampCounter extends AbstractIncrementable {

	private long timestamp = 0;

	// The count will range between zero and the number of
	// 100-nanosecond intervals per system time interval.
	private static final int COUNTER_MIN = 0;
	private static final int COUNTER_MAX = (int) TimestampUtil.TIMESTAMP_RESOLUTION - 1;

	public TimestampCounter() {
		super(COUNTER_MIN, COUNTER_MAX);
	}

	/**
	 * Returns a number representing how many times a timestamp was used in
	 * sequence.
	 * 
	 * It may be used to simulate a high resolution timestamp.
	 * 
	 * #### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (4) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @param timestamp
	 * @return
	 */
	public int getNextFor(long timestamp) {
		if (timestamp <= this.timestamp) {
			return this.getNext();
		}

		this.timestamp = timestamp;
		this.reset();
		return this.getCurrent();
	}
}
