package com.github.f4b6a3.uuid.random;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.util.RandomUtil;

import static org.junit.Assert.*;

import java.util.UUID;

public class RandomTest {

	@Test
	public void testGetRandomUuid_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		for(int i = 0; i < max; i++) {
			UUID uuid = UuidCreator.getRandom();
			long msb = uuid.getMostSignificantBits();
			long lsb = uuid.getLeastSignificantBits();
			accumulator += Long.bitCount(msb) + Long.bitCount(lsb);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random UUUIDs is 63", average == 63);
	}
	
	@Test
	public void testGetFastRandomUuid_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		for(int i = 0; i < max; i++) {
			UUID uuid = UuidCreator.getFastRandom();
			long msb = uuid.getMostSignificantBits();
			long lsb = uuid.getLeastSignificantBits();
			accumulator += Long.bitCount(msb) + Long.bitCount(lsb);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random UUUIDs is 63", average == 63);
	}
	
	@Test
	public void testRandomUtilNextLong_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		for(int i = 0; i < max; i++) {
			long value = RandomUtil.nextLong();
			accumulator += Long.bitCount(value);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random long values is 32", average == 32);
	}
	
	@Test
	public void testXorshiftNextLong_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		XorshiftRandom random = new XorshiftRandom();
		
		for(int i = 0; i < max; i++) {
			long value = random.nextLong();
			accumulator += Long.bitCount(value);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random long values is 32", average == 32);
	}
	
	@Test
	public void testXorshiftStarNextLong_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		XorshiftStarRandom random = new XorshiftStarRandom();
		
		for(int i = 0; i < max; i++) {
			long value = random.nextLong();
			accumulator += Long.bitCount(value);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random long values is 32", average == 32);
	}
	
	@Test
	public void testXorshift128PlusNextLong_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		Xorshift128PlusRandom random = new Xorshift128PlusRandom();
		
		for(int i = 0; i < max; i++) {
			long value = random.nextLong();
			accumulator += Long.bitCount(value);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random long values is 32", average == 32);
	}
	
	@Test
	public void testXoroshiro128PlusNextLong_NaiveAverageBitCount() {
		
		double max = 10_000;
		double accumulator = 0;
		
		Xoroshiro128PlusRandom random = new Xoroshiro128PlusRandom();
		
		for(int i = 0; i < max; i++) {
			long value = random.nextLong();
			accumulator += Long.bitCount(value);
		}
		
		double average = Math.round(accumulator / max);
		
		assertTrue("The average bit count expected for random long values is 32", average == 32);
	}
}
