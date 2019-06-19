package com.github.f4b6a3.uuid.timestamp;

public class UnixEpochMilliTimestampStretegy implements TimestampStrategy {
	@Override
	public long getTimestamp() {
		return System.currentTimeMillis();
	}
}
