package com.github.f4b6a3.uuid.clockseq;

import com.github.f4b6a3.uuid.sequence.AbstractSequence;
import com.github.f4b6a3.uuid.util.RandomUtil;

public class CombClockSequenceStrategy extends AbstractSequence implements ClockSequenceStrategy {

	protected long nodeIdentifier;
	
	protected static final int SEQUENCE_MIN = 0x0000;
	protected static final int SEQUENCE_MAX = 0xffff;

	public CombClockSequenceStrategy() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	@Override
	public long getClockSequence(final long timestamp, final long nodeIdentifier) {
		if (nodeIdentifier != this.nodeIdentifier) {
			this.nodeIdentifier = nodeIdentifier;
			this.reset();
		}
		return this.next();
	}

	@Override
	public void reset() {
		this.value = RandomUtil.nextInt(SEQUENCE_MAX);
	}
}
