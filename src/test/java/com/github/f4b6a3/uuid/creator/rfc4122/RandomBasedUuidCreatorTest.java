package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

public class RandomBasedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testGetRandomUuidStringIsValid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid = UuidCreator.getRandomBased();
			checkIfStringIsValid(uuid);
		}
	}

	@Test
	public void testRandomUuid() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testRandomUuidWithFastRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withFastRandomGenerator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testRandomUuidWithCustomRandomGenerator() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new Random();
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withRandomGenerator(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}

	@Test
	public void testRandomUuidWithCustomRandomGeneratorSecure() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		Random random = new SecureRandom();
		RandomBasedUuidCreator creator = UuidCreator.getRandomBasedCreator().withRandomGenerator(random);

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			list[i] = creator.create();
		}

		checkNullOrInvalid(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_RANDOM_BASED.getValue());
	}
}
