package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.util.RandomUtil;

import static org.junit.Assert.*;

public class CombGuidCreatorTest {

	private static final long DEFAULT_LOOP = 1000;

	private static final long TIMESTAMP = System.currentTimeMillis();
	private static final long MAX_LOW = LexicalOrderGuidCreator.MAX_LOW;
	private static final long MAX_HIGH = LexicalOrderGuidCreator.MAX_HIGH;

	@Test
	public void testRandomMostSignificantBits() {

		long low = RandomUtil.getInstance().nextInt();
		long high = RandomUtil.getInstance().nextInt(Short.MAX_VALUE);

		CombGuidCreator creator = new CombGuidCreatorMock(high, low, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstMsb = uuid.getMostSignificantBits();
		long firstLsb = uuid.getLeastSignificantBits() >>> 48;
		long lastMsb = 0;
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
			lastMsb = uuid.getMostSignificantBits();
		}

		boolean overflow = (firstLsb + DEFAULT_LOOP) > ((long) Math.pow(2, 16));

		if (overflow) {
			assertEquals(String.format("The last MSB should be iqual to the first %s plus 1.", firstMsb + 1),
					firstMsb + 1, lastMsb);
		} else {
			assertEquals(String.format("The last MSB should be iqual to the first %s.", firstMsb), firstMsb, lastMsb);
		}

		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastMsb = uuid.getMostSignificantBits();
		assertNotEquals("The last MSB should be random after timestamp changed.", firstMsb, lastMsb);
	}

	@Test
	public void testRandomLeastSignificantBits() {

		long low = RandomUtil.getInstance().nextInt();
		long high = RandomUtil.getInstance().nextInt(Short.MAX_VALUE);

		CombGuidCreator creator = new CombGuidCreatorMock(high, low, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = creator.create();
		long firstLsb = uuid.getLeastSignificantBits() >>> 48;
		long lastLsb = 0;
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
			lastLsb = uuid.getLeastSignificantBits() >>> 48;
		}

		long expected = (firstLsb + DEFAULT_LOOP) % (long) Math.pow(2, 16);
		assertEquals(String.format("The last LSB should be iqual to %s.", expected), expected, lastLsb);

		long notExpected = expected + 1;
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP + 1));
		uuid = creator.create();
		lastLsb = uuid.getLeastSignificantBits() >>> 48;
		assertNotEquals("The last LSB should be random after timestamp changed.", notExpected, lastLsb);
	}

	@Test
	public void testIncrementOfRandomLeastSignificantBits() {

		long low = 0;
		long high = 0;

		CombGuidCreator creator = new CombGuidCreatorMock(high, low, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long exptected = (DEFAULT_LOOP << 48) + TIMESTAMP;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", exptected), exptected, randomLsb);
	}

	@Test
	public void testIncrementOfRandomMostSignificantBits() {

		long low = MAX_LOW;
		long high = 0;

		CombGuidCreatorMock creator = new CombGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i <= DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long exptected = (high + 1) << 48;
		long randomMsb = uuid.getMostSignificantBits();
		assertEquals(String.format("The MSB should be iqual to %s.", exptected), exptected, randomMsb);
	}

	@Test(expected = UuidCreatorException.class)
	public void testShouldThrowOverflowException() {

		long low = MAX_LOW - DEFAULT_LOOP;
		long high = MAX_HIGH;

		CombGuidCreatorMock creator = new CombGuidCreatorMock(low, high, TIMESTAMP);
		creator.withTimestampStrategy(new FixedTimestampStretegy(TIMESTAMP));

		UUID uuid = new UUID(0, 0);
		for (int i = 0; i < DEFAULT_LOOP; i++) {
			uuid = creator.create();
		}

		long exptected = ((low + DEFAULT_LOOP) << 48) + TIMESTAMP;
		long randomLsb = uuid.getLeastSignificantBits();
		assertEquals(String.format("The LSB should be iqual to %s.", exptected), exptected, randomLsb);

		exptected = (high << 48) | (low >> 16);
		long randomMsb = uuid.getMostSignificantBits();
		assertEquals(String.format("The MSB should be iqual to %s.", exptected), exptected, randomMsb);

		creator.create();
		fail("It should throw an overflow exception.");
	}
}
