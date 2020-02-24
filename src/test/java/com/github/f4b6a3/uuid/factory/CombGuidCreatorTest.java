package com.github.f4b6a3.uuid.factory;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;

import static org.junit.Assert.*;

public class CombGuidCreatorTest {

	private static final long DEFAULT_LOOP_MAX = 1_048_576; // 2^20

	private static final long TIMESTAMP = System.currentTimeMillis();

	private static final Random RANDOM = new Xorshift128PlusRandom();

	private static final long MAX_UNSIGNED_SHORT = 0x000000000000ffffL;

	private static final long LOOP_INCREMENT = (DEFAULT_LOOP_MAX / MAX_UNSIGNED_SHORT);

	@Test
	public void testRandomMostSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) (RANDOM.nextInt());

		CombGuidCreator creator = new CombGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstMsb = uuid.getMostSignificantBits();
		long lastMsb = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
			lastMsb = uuid.getMostSignificantBits();
		}

		long expectedMsb = firstMsb + LOOP_INCREMENT;
		assertEquals(String.format("The last MSB should be iqual to the first %s.", expectedMsb), expectedMsb, lastMsb);

		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastMsb = uuid.getMostSignificantBits();
		assertNotEquals("The last MSB should be random after timestamp changed.", firstMsb, lastMsb);
	}

	@Test
	public void testRandomLeastSignificantBits() {

		CombGuidCreator creator = new CombGuidCreator();
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstLsb = uuid.getLeastSignificantBits() >>> 48;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expected = firstLsb;
		long lastLsb = uuid.getLeastSignificantBits() >>> 48;
		assertEquals(String.format("The last LSB should be iqual to %s.", expected), expected, lastLsb);

		long notExpected = expected + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastLsb = uuid.getLeastSignificantBits() >>> 48;
		assertNotEquals("The last LSB should be random after timestamp changed.", notExpected, lastLsb);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) RANDOM.nextInt();

		CombGuidCreator creator = new CombGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long exptected = (low << 48) >>> 48;
		long randomLsb = uuid.getLeastSignificantBits() >>> 48;
		assertEquals(String.format("The LSB should be iqual to %s.", exptected), exptected, randomLsb);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		long low = RANDOM.nextLong();
		long high = (short) (RANDOM.nextInt());

		CombGuidCreatorMock creator = new CombGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i <= DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long exptectedMsb = ((high) << 48) | (low >>> 16) + LOOP_INCREMENT;
		long randomMsb = (uuid.getMostSignificantBits());
		assertEquals(String.format("The MSB should be iqual to %s.", exptectedMsb), exptectedMsb, randomMsb);
	}

	@Test
	public void testShouldThrowOverflowException() {

		long startLow = RANDOM.nextInt() + DEFAULT_LOOP_MAX;
		long startHigh = (short) (RANDOM.nextInt() + 1);

		long low = startLow - DEFAULT_LOOP_MAX;
		long high = (short) (startHigh - 1);

		CombGuidCreatorMock creator = new CombGuidCreatorMock(low, high, startLow, startHigh, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX - 1; i++) {
			uuid = creator.create();
		}

		long expectedLsb = ((startLow - 1) << 48) >>> 48;
		long randomLsb = (uuid.getLeastSignificantBits() >>> 48);
		assertEquals(String.format("The LSB should be iqual to %s.", expectedLsb), expectedLsb, randomLsb);

		long expectedMsb = ((startHigh - 1) << 48) | (startLow >>> 16);
		long randomMsb = (uuid.getMostSignificantBits());
		assertEquals(String.format("The MSB should be iqual to %s.", expectedMsb), expectedMsb, randomMsb);

		try {
			creator.create();
			fail("It should throw an overflow exception.");
		} catch (UuidCreatorException e) {
			// success
		}
	}
}
