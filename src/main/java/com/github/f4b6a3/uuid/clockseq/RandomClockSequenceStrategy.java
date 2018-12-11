package com.github.f4b6a3.uuid.clockseq;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

public class RandomClockSequenceStrategy implements ClockSequenceStrategy {

	protected Random random;

	public RandomClockSequenceStrategy() {
		this.random = new Xorshift128PlusRandom();
	}

	public RandomClockSequenceStrategy(Random random) {
		this.random = random;
	}
	
	@Override
	public int getClockSequence(long timestamp, long nodeIdentifier) {
		return random.nextInt();
	}
}
