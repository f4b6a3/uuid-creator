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

/**
 * Class that stores the last status of a time-based creator (factory).  
 * 
 * @author fabiolimace
 *
 */
public class UUIDState implements Serializable {
	
	private static final long serialVersionUID = 8918010224627940388L;
	
	private long timestamp = 0;
	private long clockSeq1 = 0;
	private long clockSeq2 = 0;
	private long nodeIdentifier = 0;
	
	public UUIDState() {
	}
	
	public UUIDState(long timestamp, long clockSeq1, long clockSeq2, long nodeIdentifier) {
		this.timestamp = timestamp;
		this.clockSeq1 = clockSeq1;
		this.clockSeq1 = clockSeq2;
		this.nodeIdentifier = nodeIdentifier;
	}
	
	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getClockSeq1() {
		return this.clockSeq1;
	}

	public void setClockSeq1(long clockSeq1) {
		this.clockSeq1 = clockSeq1;
	}

	public long getClockSeq2() {
		return this.clockSeq2;
	}

	public void setClockSeq2(long clockSeq2) {
		this.clockSeq2 = clockSeq2;
	}

	public long getNodeIdentifier() {
		return this.nodeIdentifier;
	}

	public void setNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}

	@Override
	public UUIDState clone() {
		return new UUIDState(this.timestamp, this.clockSeq1, this.clockSeq2, this.nodeIdentifier);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		return (other != null)
			&& (other instanceof UUIDState)
			&& (this.timestamp == ((UUIDState)other).getTimestamp())
			&& (this.clockSeq1 == ((UUIDState)other).getClockSeq1())
			&& (this.clockSeq2 == ((UUIDState)other).getClockSeq2())
			&& (this.nodeIdentifier == ((UUIDState)other).getNodeIdentifier());
	}
}
