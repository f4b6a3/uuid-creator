package com.github.f4b6a3.uuid.factory;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;

import static org.junit.Assert.*;

public class LexicalOrderGuidCreatorTest {

	private static final long DEFAULT_LOOP_MAX = 1_048_576; // 2^20

	private static final long TIMESTAMP = System.currentTimeMillis();

	private static final Random RANDOM = new Xorshift128PlusRandom();

	@Test
	public void testRandomMostSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) (RANDOM.nextInt());

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstMsb = (short) uuid.getMostSignificantBits();
		long lastMsb = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
			lastMsb = (short) uuid.getMostSignificantBits();
		}

		assertEquals(String.format("The last MSB should be iqual to the first %s.", firstMsb), firstMsb, lastMsb);

		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastMsb = (short) uuid.getMostSignificantBits();
		assertNotEquals("The last MSB should be be random after timestamp changed.", firstMsb, lastMsb);
	}

	@Test
	public void testRandomLeastSignificantBits() {

		LexicalOrderGuidCreator creator = new LexicalOrderGuidCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstLsb = uuid.getLeastSignificantBits();
		long lastLsb = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
			lastLsb = uuid.getLeastSignificantBits();
		}

		long expected = firstLsb + DEFAULT_LOOP_MAX;
		assertEquals(String.format("The last LSB should be iqual to %s.", expected), expected, lastLsb);

		long notExpected = firstLsb + DEFAULT_LOOP_MAX + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastLsb = uuid.getLeastSignificantBits();
		assertNotEquals("The last LSB should be random after timestamp changed.", notExpected, lastLsb);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) RANDOM.nextInt();

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expectedLsb = low + DEFAULT_LOOP_MAX;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", expectedLsb), expectedLsb, randomLsb);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) (RANDOM.nextInt());

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expected = high;
		long randomMsb = (short) (uuid.getMostSignificantBits());
		assertEquals(String.format("The MSB should be iqual to %s.", expected), expected, randomMsb);
	}

	@Test
	public void testShouldThrowOverflowException() {

		long startLow = RANDOM.nextInt() + DEFAULT_LOOP_MAX;
		long startHigh = (short) (RANDOM.nextInt() + 1);

		long low = startLow - DEFAULT_LOOP_MAX;
		long high = (short) (startHigh - 1);

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, startLow, startHigh,
				TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX - 1; i++) {
			uuid = creator.create();
		}

		long expectedLsb = startLow - 1;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", expectedLsb), expectedLsb, randomLsb);

		long expectedMsb = startHigh - 1;
		long randomMsb = (short) (uuid.getMostSignificantBits());
		assertEquals(String.format("The MSB should be iqual to %s.", expectedMsb), expectedMsb, randomMsb);

		try {
			creator.create();
			fail("It should throw an overflow exception.");
		} catch (UuidCreatorException e) {
			// success
		}
	}
}
