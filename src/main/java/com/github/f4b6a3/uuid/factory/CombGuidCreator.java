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
 * Factory that creates COMB UUIDs.
 * 
 * This implementation is derived from the ULID specification. The only
 * difference is that the millisecond bits are moved to the end of the GUID. See
 * the {@link LexicalOrderGuidCreator}.
 * 
 * The Cost of GUIDs as Primary Keys (COMB GUID inception):
 * http://www.informit.com/articles/article.aspx?p=25862
 * 
 * ULID specification: https://github.com/ulid/spec
 * 
 */
public class CombGuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	private long previousTimestamp;

	private long random1;
	private long random2;
	private long random3;

	protected static final String OVERFLOW_MESSAGE = "The system caused an overflow in the generator by requesting too many COMB GUIDs.";

	protected TimestampStrategy timestampStrategy;

	public CombGuidCreator() {
		this.reset();
		this.timestampStrategy = new EpochMilliTimestampStretegy();
	}

	/**
	 * Return a COMB GUID.
	 * 
	 * See {@link LexicalOrderGuidCreator#create()}
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = this.timestampStrategy.getTimestamp();

		if (timestamp == this.previousTimestamp) {
			this.increment();
		} else {
			this.reset();
		}

		this.previousTimestamp = timestamp;
		final long msb = (random3 << 32) | random2;
		final long lsb = (random1 << 48) | timestamp;

		return new UUID(msb, lsb);
	}

	/**
	 * Reset the random part of the GUID.
	 */
	protected void reset() {
		final byte[] bytes = new byte[10];
		RandomUtil.nextBytes(bytes);
		this.random3 = ByteUtil.toNumber(bytes, 0, 4);
		this.random2 = ByteUtil.toNumber(bytes, 4, 8);
		this.random1 = ByteUtil.toNumber(bytes, 8, 10);
	}

	/**
	 * Increment the random part of the GUID.
	 * 
	 * @throws UuidCreatorException
	 *             if an overflow happens.
	 */
	protected void increment() {
		this.random1++;
		if (this.random1 > 0x000000000000ffffL) {
			this.random1 = 0;
			this.random2++;
			if (this.random2 > 0x00000000ffffffffL) {
				this.random2 = 0;
				this.random3++;
				if (this.random3 > 0x00000000ffffffffL) {
					this.random3 = 0;
					// Too many requests
					throw new UuidCreatorException(OVERFLOW_MESSAGE);
				}
			}
		}
	}

	/**
	 * Used for changing the timestamp strategy.
	 * 
	 * @param timestampStrategy
	 *            a timestamp strategy
	 * @return {@link CombGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public <T extends CombGuidCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}
}
