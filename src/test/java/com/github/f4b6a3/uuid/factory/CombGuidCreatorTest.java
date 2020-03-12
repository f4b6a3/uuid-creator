package com.github.f4b6a3.uuid.factory;

import java.util.Random;
import java.util.UUID;
import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;

import static org.junit.Assert.*;

public class CombGuidCreatorTest {

	private static final long DEFAULT_LOOP_MAX = 1_000_000;

	private static final long TIMESTAMP = System.currentTimeMillis();

	private static final Random RANDOM = new Xorshift128PlusRandom();

	@Test
	public void testRandomMostSignificantBits() {

		CombGuidCreatorMock creator = new CombGuidCreatorMock(TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstMsb = creator.extractRandomMsb(uuid);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();

		}

		long lastMsb = creator.extractRandomMsb(uuid);
		long expectedMsb = firstMsb;
		assertEquals(String.format("The last MSB should be iqual to the first %s.", expectedMsb), expectedMsb, lastMsb);

		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastMsb = uuid.getMostSignificantBits();
		assertNotEquals("The last MSB should be random after timestamp changed.", firstMsb, lastMsb);
	}

	@Test
	public void testRandomLeastSignificantBits() {

		CombGuidCreatorMock creator = new CombGuidCreatorMock(TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstLsb = creator.extractRandomLsb(uuid);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long lastLsb = creator.extractRandomLsb(uuid);
		long expected = firstLsb + DEFAULT_LOOP_MAX;
		assertEquals(String.format("The last LSB should be iqual to %s.", expected), expected, lastLsb);

		long notExpected = firstLsb + DEFAULT_LOOP_MAX + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastLsb = uuid.getLeastSignificantBits() >>> 48;
		assertNotEquals("The last LSB should be random after timestamp changed.", notExpected, lastLsb);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		CombGuidCreatorMock creator = new CombGuidCreatorMock(TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		long lsb = creator.getRandomLsb();

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expectedLsb = lsb + DEFAULT_LOOP_MAX;
		long randomLsb = creator.getRandomLsb();
		assertEquals("Wrong LSB after loop.", expectedLsb, randomLsb);

		randomLsb = creator.extractRandomLsb(uuid);
		assertEquals("Wrong LSB after loop.", expectedLsb, randomLsb);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		CombGuidCreatorMock creator = new CombGuidCreatorMock(TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		long msb = creator.getRandomMsb();

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			uuid = creator.create();
		}

		long expectedMsb = msb;
		long randomMsb = creator.getRandomMsb();
		assertEquals("Wrong MSB after loop.", expectedMsb, randomMsb);

		randomMsb = creator.extractRandomMsb(uuid);
		assertEquals("Wrong MSB after loop.", expectedMsb, randomMsb);
	}

	@Test
	public void testShouldThrowOverflowException1() {

		long msbMax = 0x000001ffffffffffL;
		long lsbMax = 0x000001ffffffffffL;

		long msb = msbMax - 1;
		long lsb = lsbMax - DEFAULT_LOOP_MAX;

		CombGuidCreatorMock creator = new CombGuidCreatorMock(msb, lsb, msbMax, lsbMax, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		for (int i = 0; i < DEFAULT_LOOP_MAX - 1; i++) {
			creator.create();
		}

		try {
			creator.create();
			fail("It should throw an overflow exception.");
		} catch (UuidCreatorException e) {
			// success
		}
	}

	@Test
	public void testShouldThrowOverflowException2() {

		long msbMax = (RANDOM.nextLong() & UlidBasedGuidCreatorMock.HALF_RANDOM_COMPONENT)
				| UlidBasedGuidCreatorMock.INCREMENT_MAX;
		long lsbMax = (RANDOM.nextLong() & UlidBasedGuidCreatorMock.HALF_RANDOM_COMPONENT)
				| UlidBasedGuidCreatorMock.INCREMENT_MAX;

		long msb = msbMax - 1;
		long lsb = lsbMax - DEFAULT_LOOP_MAX;

		CombGuidCreatorMock creator = new CombGuidCreatorMock(msb, lsb, msbMax, lsbMax, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP_MAX - 1; i++) {
			uuid = creator.create();
		}

		long expectedLsb = (lsbMax - 1) & UlidBasedGuidCreatorMock.HALF_RANDOM_COMPONENT;
		long randomLsb = creator.extractRandomLsb(uuid);
		assertEquals("Incorrect LSB after loop.", expectedLsb, randomLsb);

		long expectedMsb = (msbMax - 1) & UlidBasedGuidCreatorMock.HALF_RANDOM_COMPONENT;
		long randomMsb = creator.extractRandomMsb(uuid);
		assertEquals("Incorrect MSB after loop.", expectedMsb, randomMsb);

		try {
			creator.create();
			fail("It should throw an overflow exception.");
		} catch (UuidCreatorException e) {
			// success
		}
	}
}
