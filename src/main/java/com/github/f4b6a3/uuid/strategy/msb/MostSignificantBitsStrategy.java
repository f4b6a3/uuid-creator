package com.github.f4b6a3.uuid.strategy.msb;

public interface MostSignificantBitsStrategy {
	
	long getMostSignificantBits(long timestamp, long counter);
	
}
