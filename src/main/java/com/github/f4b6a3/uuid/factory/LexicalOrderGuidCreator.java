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

import java.util.UUID;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreator;
import com.github.f4b6a3.uuid.factory.abst.NoArgumentsUuidCreator;
import com.github.f4b6a3.uuid.timestamp.EpochMilliTimestampStretegy;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.RandomUtil;

/**
 * Factory that creates lexicographically sortable GUIDs, based on the ULID
 * specification - Universally Unique Lexicographically Sortable Identifier.
 * 
 * ULID specification: https://github.com/ulid/spec
 */
public class LexicalOrderGuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	private long previousTimestamp;
	private long field1;
	private long field2;
	private long field3;

	protected static final String OVERFLOW_MESSAGE = "The system requested more than 2^80 Lexical Order GUIDs within the same millisecond.";
	
	protected TimestampStrategy timestampStrategy;

	public LexicalOrderGuidCreator() {
		this.reset();
		this.timestampStrategy = new EpochMilliTimestampStretegy();
	}

	@Override
	public synchronized UUID create() {

		final long timestamp = this.timestampStrategy.getTimestamp();

		if (timestamp <= this.previousTimestamp) {
			this.increment();
		}

		this.previousTimestamp = timestamp;
		final long msb = (timestamp << 16) | field3;
		final long lsb = (field2 << 32) | field1;
		return new UUID(msb, lsb);
	}

	protected void reset() {
		final byte[] randomness = new byte[10];
		RandomUtil.nextBytes(randomness);
		this.field3 = ByteUtil.toNumber(randomness, 0, 2);
		this.field2 = ByteUtil.toNumber(randomness, 2, 6);
		this.field1 = ByteUtil.toNumber(randomness, 6, 10);
	}

	protected void increment() {
		this.field1++;
		if (this.field1 > 0x00000000ffffffffL) {
			this.field1 = 0;
			this.field2++;
			if (this.field2 > 0x00000000ffffffffL) {
				this.field2 = 0;
				this.field3++;
				if (this.field3 > 0x000000000000ffffL) {
					this.field3 = 0;
					throw new UuidCreatorException(OVERFLOW_MESSAGE);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends LexicalOrderGuidCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}
}
