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

package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.util.UuidUtil;

/**
 * Factory that creates time-based UUIDs.
 * 
 * RFC-4122 version: 1.
 */
public class TimeBasedUuidCreator extends AbstractTimeBasedUuidCreator {

	public TimeBasedUuidCreator() {
		this(UuidVersion.TIME_BASED);
	}
	
	protected TimeBasedUuidCreator(UuidVersion version) {
		super(version);
	}
	
	/**
	 * 
	 * {@link UuidUtil#formatTimeBasedMostSignificantBits(long)}
	 * 
	 * @param timestamp a timestamp
	 */
	@Override
	public long formatMostSignificantBits(final long timestamp) {
		return UuidUtil.formatTimeBasedMostSignificantBits(timestamp);
	}
}