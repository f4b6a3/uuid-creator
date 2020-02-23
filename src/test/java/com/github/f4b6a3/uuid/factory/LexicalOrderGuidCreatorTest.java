package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.util.RandomUtil;

import static org.junit.Assert.*;

public class LexicalOrderGuidCreatorTest {

	private static final long DEFAULT_LOOP = 1000;

	private static final long TIMESTAMP = System.currentTimeMillis();
	private static final long MAX_LOW = LexicalOrderGuidCreator.MAX_LOW;
	private static final long MAX_HIGH = LexicalOrderGuidCreator.MAX_HIGH;

	@Test
	public void testRandomMostSignificantBits() {

		long low = RandomUtil.getInstance().nextInt();
		long high = RandomUtil.getInstance().nextInt(Short.MAX_VALUE);

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstMsb = (short) uuid.getMostSignificantBits();
		long lastMsb = 0;
		for (int i = 0; i <= DEFAULT_LOOP; i++) {
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
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
			lastLsb = uuid.getLeastSignificantBits();
		}

		long expected = firstLsb + DEFAULT_LOOP;
		assertEquals(String.format("The last LSB should be iqual to %s.", expected), expected, lastLsb);

		long notExpected = firstLsb + DEFAULT_LOOP + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastLsb = uuid.getLeastSignificantBits();
		assertNotEquals("The last LSB should be random after timestamp changed.", notExpected, lastLsb);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		long low = RandomUtil.getInstance().nextInt();
		long high = RandomUtil.getInstance().nextInt(Short.MAX_VALUE);

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long expected = low + DEFAULT_LOOP;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", expected), expected, randomLsb);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		long low = MAX_LOW;
		long high = RandomUtil.getInstance().nextInt(Short.MAX_VALUE);

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long expected = high + 1;
		long randomMsb = uuid.getMostSignificantBits() & MAX_HIGH;
		assertEquals(String.format("The MSB should be iqual to %s.", expected), expected, randomMsb);
	}

	@Test(expected = UuidCreatorException.class)
	public void testShouldThrowOverflowException() {

		long low = MAX_LOW - DEFAULT_LOOP;
		long high = MAX_HIGH;

		LexicalOrderGuidCreatorMock creator = new LexicalOrderGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long expected = MAX_LOW;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", expected), expected, randomLsb);

		expected = MAX_HIGH;
		long randomMsb = uuid.getMostSignificantBits() & MAX_HIGH;
		assertEquals(String.format("The MSB should be iqual to %s.", expected), expected, randomMsb);

		creator.create();
		fail("It should throw an overflow exception.");
	}
}
