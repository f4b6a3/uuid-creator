package com.github.f4b6a3.uuid.util;

import static org.junit.Assert.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.factory.AbstRandomBasedFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactory;
import com.github.f4b6a3.uuid.factory.nonstandard.SuffixCombFactory;
import com.github.f4b6a3.uuid.util.CombUtil;

public class CombUtilTest {

	private static final int DEFAULT_LOOP_MAX = 1_000;

	@Test
	public void testGetPrefix() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getPrefixComb();
			long end = System.currentTimeMillis();

			long prefix = CombUtil.getPrefix(comb);
			assertTrue("Wrong prefix", prefix >= start && prefix <= end);
		}
	}

	@Test
	public void testGetPrefixWithClock() {

		final int bits = 48;
		final long bound = (long) Math.pow(2, bits);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			// instantiate a factory with a Clock that returns a fixed value
			final long millis = Math.abs(ThreadLocalRandom.current().nextLong(bound));
			Clock clock = Clock.fixed(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
			AbstRandomBasedFactory factory = new PrefixCombFactory(clock);

			UUID comb = factory.create();

			long prefix = CombUtil.getPrefix(comb);

			assertEquals("Wrong prefix", millis, prefix);
		}
	}

	@Test
	public void testGetSuffix() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getSuffixComb();
			long end = System.currentTimeMillis();

			long suffix = CombUtil.getSuffix(comb);
			assertTrue("Wrong suffix", suffix >= start && suffix <= end);
		}
	}

	@Test
	public void testGetSuffixWithClock() {

		final int bits = 48;
		final long bound = (long) Math.pow(2, bits);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			// instantiate a factory with a Clock that returns a fixed value
			final long millis = Math.abs(ThreadLocalRandom.current().nextLong(bound));
			Clock clock = Clock.fixed(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
			AbstRandomBasedFactory factory = new SuffixCombFactory(clock);

			UUID comb = factory.create();

			long suffix = CombUtil.getSuffix(comb);

			assertEquals("Wrong suffix", millis, suffix);
		}
	}

	@Test
	public void testGetPrefixInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getPrefixComb();
			long end = System.currentTimeMillis();

			Instant instant = CombUtil.getPrefixInstant(comb);
			assertTrue("Wrong prefix", instant.toEpochMilli() >= start && instant.toEpochMilli() <= end);
		}
	}

	@Test
	public void testGetSuffixInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getSuffixComb();
			long end = System.currentTimeMillis();

			Instant instant = CombUtil.getSuffixInstant(comb);
			assertTrue("Wrong prefix", instant.toEpochMilli() >= start && instant.toEpochMilli() <= end);
		}
	}
}
