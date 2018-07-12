package com.github.f4b6a3.uuid;

public class UUIDState {
	
	private long timestamp = 0;
	private long clockSeq1 = 0;
	private long clockSeq2 = 0;
	private long node = 0;
	
	public UUIDState() {
		
	}
	
	public UUIDState(long timestamp, long clockSeq, long node) {
		this.timestamp = timestamp;
		this.clockSeq1 = clockSeq;
		this.node = node;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getClockSeq1() {
		return clockSeq1;
	}

	public void setClockSeq1(long clockSeq1) {
		this.clockSeq1 = clockSeq1;
	}

	public long getClockSeq2() {
		return clockSeq2;
	}

	public void setClockSeq2(long clockSeq2) {
		this.clockSeq2 = clockSeq2;
	}

	public long getNode() {
		return node;
	}

	public void setNode(long node) {
		this.node = node;
	}

	@Override
	public UUIDState clone() {
		return new UUIDState(this.timestamp, this.clockSeq1, this.node);
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
			&& (this.node == ((UUIDState)other).getNode());
	}
}
