package com.github.f4b6a3.uuid.increment;

public class CounterAndClockSequenceIncrementable extends AbstractIncrementable {
	
	protected static final int COUNTER_MIN = 0;
	protected static final int COUNTER_MAX = 10_000;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;
	
	protected static final int COUNTER_MASK = 0;
	protected static final int SEQUENCE_MASK = 0;
	
	public CounterAndClockSequenceIncrementable() {
		super(0, COUNTER_MAX * SEQUENCE_MAX);
	}

	public int getCounterValue() {
		return this.value % COUNTER_MAX;
	}
	
	public int getClockSequenceValue() {
		return this.value / COUNTER_MAX;
	}
	
}
