package com.github.f4b6a3.uuid.increment;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;

public class ClockSequence extends AbstractIncrementable {

	private long timestamp = 0;
	private long nodeIdentifier = 0;

	protected static final int SEQUENCE_MIN = 0; // 0x0000;
	protected static final int SEQUENCE_MAX = 16_383; // 0x3fff;

	private static final Random random = new Xoroshiro128PlusRandom();
	private static final Logger LOGGER = Logger.getAnonymousLogger();

	public ClockSequence() {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.reset();
	}

	public ClockSequence(int value) {
		super(SEQUENCE_MIN, SEQUENCE_MAX);
		this.value = value;
	}

	/**
	 * Get the next value for a timestamp and a node identifier.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (5) If the state was unavailable (e.g., non-existent or corrupted), or
	 * the saved node ID is different than the current node ID, generate a
	 * random clock sequence value.
	 * 
	 * (6) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
	 * 
	 * @param timestamp
	 * @param nodeIdentifier
	 * @return
	 */
	public int getNextFor(long timestamp, long nodeIdentifier) {

		if (nodeIdentifier != this.nodeIdentifier) {
			if (this.nodeIdentifier == 0) {
				this.nodeIdentifier = nodeIdentifier;
			} else {
				this.reset();
			}
		}

		if (timestamp <= this.timestamp) {

			// if (this.value >= this.MAX_VALUE) {
			// throw new OverrunException("UUID clock sequence overrun.");
			// }

			return this.getNext();
		}

		this.timestamp = timestamp;
		return this.getCurrent();
	}

	@Override
	public void reset() {
		this.value = random.nextInt(SEQUENCE_MAX);
	}
}