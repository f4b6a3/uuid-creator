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
 * Factory that creates MS SQL Server 'friendly' UUIDs.
 * 
 * RFC-4122 version: 1.
 * 
 */
public class MssqlGuidCreator extends AbstractTimeBasedUuidCreator {

	public MssqlGuidCreator() {
		super(UuidVersion.TIME_BASED);
	}

	/**
	 * Generate a MS SQL Server 'friendly' UUID.
	 * 
	 * {@link UuidUtil#formatMssqlMostSignificantBits(long)}
	 * 
	 * @param timestamp a timestamp
	 */
	@Override
	protected long formatMostSignificantBits(final long timestamp) {
		long ts = timestamp | 0x1000000000000000L; // set version bits
		return UuidUtil.formatMssqlMostSignificantBits(ts);
	}
}
