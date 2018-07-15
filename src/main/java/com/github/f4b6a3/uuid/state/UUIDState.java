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

package com.github.f4b6a3.uuid.state;

import java.io.Serializable;
import java.util.Random;

/**
 * Class that stores the last status of a time-based creator (factory).  
 * 
 * @author fabiolimace
 *
 */
public class UUIDState implements Serializable {
	
	private static final long serialVersionUID = 8918010224627940388L;
	
	private long timestamp = 0;
	private long sequence1 = 0;
	private long sequence2 = 0;
	private long nodeIdentifier = 0;
	
	private Random random;
	
	protected static final int SEQUENCE1_MIN = 0x8000;
	protected static final int SEQUENCE1_MAX = 0xbfff;
	
	protected static final int SEQUENCE2_MIN = 0;
	protected static final int SEQUENCE2_MAX = 10_000;
	
	public UUIDState() {
		resetSequences();
	}
	
	public UUIDState(long timestamp, long clockSeq1, long clockSeq2, long nodeIdentifier) {
		this.timestamp = timestamp;
		this.sequence1 = clockSeq1;
		this.sequence2 = clockSeq2;
		this.nodeIdentifier = nodeIdentifier;
	}
	
	/**
	 * Reset both clock sequences.
	 */
	private void resetSequences() {
		if(random == null) {
			this.random = new Random();
		}
		this.sequence1 = random.nextInt(UUIDState.SEQUENCE1_MAX - UUIDState.SEQUENCE1_MIN) + UUIDState.SEQUENCE1_MIN;
		this.sequence2 = random.nextInt(UUIDState.SEQUENCE2_MAX - 1);
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		if(timestamp > this.timestamp || timestamp == 0) {
			this.timestamp = timestamp;
		}
	}

	public long getSequence1() {
		return this.sequence1;
	}

	public void setSequence1(long sequence1) {
		if(sequence1 > this.sequence1 || sequence1 == SEQUENCE1_MIN) {
			this.sequence1 = sequence1;
		}
	}

	public long getSequence2() {
		return this.sequence2;
	}

	public void setSequence2(long sequence2) {
		if(sequence2 > this.sequence2 || sequence2 == SEQUENCE2_MIN) {
		this.sequence2 = sequence2;
		}
	}

	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	public void setNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
	
	@Override
	public UUIDState clone() {
		return new UUIDState(this.timestamp, this.sequence1, this.sequence2, this.nodeIdentifier);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		return (other != null)
			&& (other instanceof UUIDState)
			&& (this.timestamp == ((UUIDState)other).getTimestamp())
			&& (this.sequence1 == ((UUIDState)other).getSequence1())
			&& (this.sequence2 == ((UUIDState)other).getSequence2())
			&& (this.nodeIdentifier == ((UUIDState)other).getNodeIdentifier());
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
	public long nextSequence1(long timestamp) {
		if(this.timestamp != 0 && timestamp > this.timestamp) {
			return this.sequence1;
		} else {
			synchronized (this) {
				this.sequence1++;
				if (this.sequence1 > SEQUENCE1_MAX) {
					this.sequence1 = SEQUENCE1_MIN;
					this.sequence2++; // Increment the second sequence
				}
				return this.sequence1;
			}
		}
	}
	
	/**
	 * Returns the second clock sequence (a counter in the RFC-4122).
	 * 
	 * The second clock sequence is a random number between 0 and 100000. 
	 * 
	 * @see {@link UUIDState#nextSequence1(long)}
	 * 
	 * @param timestamp
	 * @return
	 */
	public long nextSequence2(long timestamp) {
		// incremented when the first sequence over-runs
		return this.sequence2;
	}
}
