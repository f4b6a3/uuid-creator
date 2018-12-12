package com.github.f4b6a3.uuid.timestamp;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

public class RandomTimestampStrategy implements TimestampStrategy {

	protected Random random;

	public RandomTimestampStrategy() {
		this.random = new Xorshift128PlusRandom();
	}

	public RandomTimestampStrategy(Random random) {
		this.random = random;
	}

	@Override
	public long getTimestamp() {
		return random.nextLong();
	}
}
