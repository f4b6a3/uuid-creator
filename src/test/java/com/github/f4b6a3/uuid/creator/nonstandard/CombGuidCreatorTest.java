package com.github.f4b6a3.uuid.creator.nonstandard;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.CombGuidCreator;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class CombGuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testCompGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = UuidCreator.getCombGuid();
		}

		long endTime = System.currentTimeMillis();

		checkUniqueness(list);

		long previous = 0;
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			long creationTime = list[i].getLeastSignificantBits() & 0x0000ffffffffffffL;
			assertTrue("Comb Guid creation time before start time", startTime <= creationTime);
			assertTrue("Comb Guid creation time after end time", creationTime <= endTime);
			assertTrue("Comb Guid sequence is not sorted " + previous + " " + creationTime, previous <= creationTime);
			previous = creationTime;
		}
	}

	@Test
	public void testCombGuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		CombGuidCreator creator = UuidCreator.getCombCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertTrue("UUID is null", list[i] != null);
		}

		checkUniqueness(list);
	}

	@Test
	public void testCombGuidWithCustomRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		CombGuidCreator creator = UuidCreator.getCombCreator().withRandomGenerator(random);
		;

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertTrue("UUID is null", list[i] != null);
		}

		checkUniqueness(list);
	}

	@Test
	public void testCombGuidWithCustomRandomGeneratorSecure() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new SecureRandom();
		CombGuidCreator creator = UuidCreator.getCombCreator().withRandomGenerator(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
			assertTrue("UUID is null", list[i] != null);
		}

		checkUniqueness(list);
	}
}
