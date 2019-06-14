package com.github.f4b6a3.uuid.clockseq;

public interface ClockSequenceStrategy {
	long getClockSequence(long timestamp, long nodeIdentifier);
}
