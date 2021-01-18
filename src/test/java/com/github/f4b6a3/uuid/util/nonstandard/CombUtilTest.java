package com.github.f4b6a3.uuid.util.nonstandard;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;

public class CombUtilTest {

	private static final int DEFAULT_LOOP_MAX = 1_000;

	@Test
	public void testExtractPrefix() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getPrefixComb();
			long end = System.currentTimeMillis();

			long prefix = CombUtil.extractPrefix(comb);
			assertTrue("Wrong prefix", prefix >= start && prefix <= end);
		}
	}

	@Test
	public void testExtractSuffix() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getSuffixComb();
			long end = System.currentTimeMillis();

			long suffix = CombUtil.extractSuffix(comb);
			assertTrue("Wrong suffix", suffix >= start && suffix <= end);
		}
	}

	@Test
	public void testExtractPrefixInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getPrefixComb();
			long end = System.currentTimeMillis();

			Instant instant = CombUtil.extractPrefixInstant(comb);
			assertTrue("Wrong prefix", instant.toEpochMilli() >= start && instant.toEpochMilli() <= end);
		}
	}

	@Test
	public void testExtractSuffixInstant() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			long start = System.currentTimeMillis();
			UUID comb = UuidCreator.getSuffixComb();
			long end = System.currentTimeMillis();

			Instant instant = CombUtil.extractSuffixInstant(comb);
			assertTrue("Wrong prefix", instant.toEpochMilli() >= start && instant.toEpochMilli() <= end);
		}
	}
}
