package com.github.f4b6a3.uuid.clockseq;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.sequence.AbstractSequence;

public class CombClockSequenceStrategy extends AbstractSequence implements ClockSequenceStrategy {

	protected long nodeIdentifier;
	
	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 65_535; // 0xffff;

	protected static final Random random = new Xorshift128PlusRandom();

	public CombClockSequenceStrategy() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	@Override
	public int getClockSequence(long timestamp, long nodeIdentifier) {
		if (nodeIdentifier != this.nodeIdentifier) {
			this.nodeIdentifier = nodeIdentifier;
			this.reset();
		}
		return this.next();
	}

	@Override
	public void reset() {
		this.value = random.nextInt(SEQUENCE_MAX);
	}
}
