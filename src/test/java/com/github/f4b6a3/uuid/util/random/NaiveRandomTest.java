package com.github.f4b6a3.uuid.util.random;

import org.junit.Test;

import com.github.f4b6a3.uuid.util.random.Xorshift128PlusRandom;

import static org.junit.Assert.assertEquals;

public class NaiveRandomTest {

	private static final int DEFAULT_LOOP_LIMIT = 100_000;

	private static final int EXPECTED_AVERAGE = 32;
	private static final String EXPECTED_BIT_COUNT_RANDOM_LONG = "The average bit count expected for random long values is 32";

	@Test
	public void testXorshift128PlusNextLongNaiveAverageBitCount() {

		double accumulator = 0;

		Xorshift128PlusRandom random = new Xorshift128PlusRandom();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			long value = random.nextLong();
			accumulator += Long.bitCount(value);
		}

		int average = (int) Math.round(accumulator / DEFAULT_LOOP_LIMIT);

		assertEquals(EXPECTED_BIT_COUNT_RANDOM_LONG, EXPECTED_AVERAGE, average);
	}
}
