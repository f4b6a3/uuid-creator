package com.github.f4b6a3.uuid.timestamp;

public class FixedTimestampStretegy implements TimestampStrategy {

	protected long timestamp = 0;

	public FixedTimestampStretegy(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}
}
