package com.github.f4b6a3.uuid.clockseq;

import java.util.Random;
import com.github.f4b6a3.uuid.util.RandomUtil;

public class RandomClockSequenceStrategy implements ClockSequenceStrategy {

	protected Random random;

	public RandomClockSequenceStrategy() {
	}

	public RandomClockSequenceStrategy(Random random) {
		this.random = random;
	}
	
	@Override
	public int getClockSequence(long timestamp, long nodeIdentifier) {
		if(this.random == null) {
			return RandomUtil.nextInt();
		}
		return this.random.nextInt();
	}
}
