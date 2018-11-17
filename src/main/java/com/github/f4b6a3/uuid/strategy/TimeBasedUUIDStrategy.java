package com.github.f4b6a3.uuid.strategy;

public interface TimeBasedUUIDStrategy {
	
	long getMostSignificantBits(long timestamp);
}
