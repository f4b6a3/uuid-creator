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
import com.github.f4b6a3.uuid.timestamp.UnixEpochMilliTimestampStretegy;
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

	private long random1;
	private long random2;
	private long random3;

	protected static final String OVERFLOW_MESSAGE = "The system caused an overflow in the generator by requesting too many Lexical Order GUIDs.";

	protected TimestampStrategy timestampStrategy;

	public LexicalOrderGuidCreator() {
		this.reset();
		this.timestampStrategy = new UnixEpochMilliTimestampStretegy();
	}

	/**
	 * 
	 * Return a Lexical Order GUID.
	 * 
	 * It has two parts:
	 * 
	 * 1. A part of 48 bits that represent the amount of milliseconds since Unix
	 * Epoch, 1 January 1970.
	 * 
	 * 2. A part of 80 bits that has a random value generated a secure random
	 * generator.
	 * 
	 * If more than one GUID is generated within the same millisecond, the
	 * random part is incremented by one.
	 * 
	 * The random part is reset to a new value every time the millisecond part
	 * changes.
	 * 
	 * ### Specification of Universally Unique Lexicographically Sortable ID
	 * 
	 * #### Components
	 * 
	 * ##### Timestamp
	 * 
	 * It is a 48 bit integer. UNIX-time in milliseconds. Won't run out of space
	 * 'til the year 10889 AD.
	 * 
	 * ##### Randomness
	 * 
	 * It is a 80 bits integer. Cryptographically secure source of randomness,
	 * if possible.
	 * 
	 * #### Sorting
	 * 
	 * The left-most character must be sorted first, and the right-most
	 * character sorted last (lexical order). The default ASCII character set
	 * must be used. Within the same millisecond, sort order is not guaranteed.
	 * 
	 * #### Monotonicity
	 * 
	 * When generating a ULID within the same millisecond, we can provide some
	 * guarantees regarding sort order. Namely, if the same millisecond is
	 * detected, the random component is incremented by 1 bit in the least
	 * significant bit position (with carrying).
	 * 
	 * If, in the extremely unlikely event that, you manage to generate more
	 * than 280 ULIDs within the same millisecond, or cause the random component
	 * to overflow with less, the generation will fail.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException
	 *             an overflow exception if too many requests within the same
	 *             millisecond causes an overflow when incrementing the random
	 *             bits of the GUID.
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
		final long msb = (timestamp << 16) | random3;
		final long lsb = (random2 << 32) | random1;

		return new UUID(msb, lsb);
	}

	/**
	 * Reset the random part of the GUID.
	 */
	protected synchronized void reset() {
		final byte[] bytes = new byte[10];
		RandomUtil.nextBytes(bytes);
		this.random3 = ByteUtil.toNumber(bytes, 0, 2);
		this.random2 = ByteUtil.toNumber(bytes, 2, 6);
		this.random1 = ByteUtil.toNumber(bytes, 6, 10);
	}

	/**
	 * Increment the random part of the GUID.
	 * 
	 * @throws UuidCreatorException
	 *             if an overflow happens.
	 */
	protected synchronized void increment() {
		this.random1++;
		if (this.random1 > 0x00000000ffffffffL) {
			this.random1 = 0;
			this.random2++;
			if (this.random2 > 0x00000000ffffffffL) {
				this.random2 = 0;
				this.random3++;
				if (this.random3 > 0x000000000000ffffL) {
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
	 * @return {@link LexicalOrderGuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized  <T extends LexicalOrderGuidCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}
}
