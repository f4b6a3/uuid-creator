package com.github.f4b6a3.uuid.strategy.random;

import java.security.SecureRandom;
import java.util.Random;

public class SecureRandomStrategy implements RandomStrategy {

	Random random = new SecureRandom();

	@Override
	public long getRandom() {
		return random.nextLong();
	}

}
