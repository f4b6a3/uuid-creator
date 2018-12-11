package com.github.f4b6a3.uuid.clockseq;

public class FixedClockSequenceStrategy implements ClockSequenceStrategy {
	
	protected int clockSequence = 0;

	public FixedClockSequenceStrategy(int clockSequence) {
		this.clockSequence = clockSequence;
	}

	@Override
	public int getClockSequence(long timestamp, long nodeIdentifier) {
		return this.clockSequence;
	}
}
