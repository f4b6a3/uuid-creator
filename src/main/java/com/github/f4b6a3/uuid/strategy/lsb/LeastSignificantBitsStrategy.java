package com.github.f4b6a3.uuid.strategy.lsb;

public interface LeastSignificantBitsStrategy {
	
	long getLeastSignificantBits(long nodeIdentifier, long sequence);
}
