package com.github.f4b6a3.uuid.increment;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;

public class ClockSequence extends AbstractIncrementable {

	private long timestamp = 0;
	private long nodeIdentifier = 0;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;

	private static final Random random = new Xoroshiro128PlusRandom();

	public ClockSequence() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	public ClockSequence(int value) {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.value = value;
	}

	public int getNextFor(long timestamp, long nodeIdentifier) {

		if (nodeIdentifier != this.nodeIdentifier && this.nodeIdentifier != 0) {
			this.nodeIdentifier = nodeIdentifier;
			reset();
			return this.getCurrent();
		}

		if (timestamp <= this.timestamp) {
			this.timestamp = timestamp;
			return this.getNext();
		}

		return this.getCurrent();
	}

	@Override
	public int getNext() {
		if (super.getCurrent() >= SEQUENCE_MAX) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return super.getNext();
	}

	@Override
	public void reset() {
		this.value = random.nextInt(SEQUENCE_MAX);
	}
}