package com.github.f4b6a3.uuid.timestamp;

import java.util.Random;
import com.github.f4b6a3.uuid.util.RandomUtil;

public class RandomTimestampStrategy implements TimestampStrategy {

	protected Random random;

	public RandomTimestampStrategy() {
	}

	public RandomTimestampStrategy(Random random) {
		this.random = random;
	}

	@Override
	public long getTimestamp() {
		if(this.random == null) {
			return RandomUtil.nextLong();
		}
		return this.random.nextLong();
	}
}
