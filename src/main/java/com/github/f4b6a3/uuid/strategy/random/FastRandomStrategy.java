package com.github.f4b6a3.uuid.strategy.random;

import java.util.Random;

import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;

public class FastRandomStrategy implements RandomStrategy {

	Random random = new Xorshift128PlusRandom();

	@Override
	public long getRandom() {
		return random.nextLong();
	}

}
