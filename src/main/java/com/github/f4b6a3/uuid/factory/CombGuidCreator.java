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

import com.github.f4b6a3.uuid.clockseq.CombClockSequenceStrategy;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUuidCreator;
import com.github.f4b6a3.uuid.nodeid.CombNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.timestamp.RandomTimestampStrategy;

/**
 * Factory that creates COMB UUIDs.
 * 
 * RFC-4122 version: 4 (number borrowed from the random-based version).
 * 
 * The Cost of GUIDs as Primary Keys
 * http://www.informit.com/articles/article.aspx?p=25862
 */
public class CombGuidCreator extends AbstractTimeBasedUuidCreator {

	public CombGuidCreator() {
		super(UuidVersion.RANDOM_BASED);
		this.timestampStrategy = new RandomTimestampStrategy();
		this.clockSequenceStrategy = new CombClockSequenceStrategy();
		this.nodeIdentifierStrategy = new CombNodeIdentifierStrategy();
	}

	@Override
	public long formatMostSignificantBits(long timestamp) {
		return setVersionBits(timestamp);
	}
}
