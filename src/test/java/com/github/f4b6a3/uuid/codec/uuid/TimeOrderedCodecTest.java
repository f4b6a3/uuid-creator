package com.github.f4b6a3.uuid.codec.uuid;

import static com.github.f4b6a3.uuid.util.UuidUtil.isTimeOrdered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class TimeOrderedCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final UuidCodec<UUID> CODEC = new TimeOrderedCodec();

	@Test
	public void testEncode() {

		UUID uuid1;
		UUID uuid6;

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidCreator.getTimeBased();
			uuid6 = CODEC.encode(uuid1); // encode
			assertTrue(isTimeOrdered(uuid6));
			assertEquals(UuidUtil.extractTimestamp(uuid1), UuidUtil.extractTimestamp(uuid6));
			assertEquals(uuid1.getLeastSignificantBits(), uuid6.getLeastSignificantBits());
		}

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidUtil.applyVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			uuid6 = CODEC.encode(uuid1); // encode
			assertTrue(isTimeOrdered(uuid6));
			assertEquals(UuidUtil.extractTimestamp(uuid1), UuidUtil.extractTimestamp(uuid6));
			assertEquals(uuid1.getLeastSignificantBits(), uuid6.getLeastSignificantBits());
		}
	}

	@Test
	public void testEncodeAndDecode() {

		UUID uuid1;
		UUID uuid6;

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidCreator.getTimeBased();
			uuid6 = CODEC.encode(uuid1); // encode
			assertEquals(uuid1, CODEC.decode(uuid6)); // decode back
		}

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidUtil.applyVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			uuid6 = CODEC.encode(uuid1); // encode
			assertEquals(uuid1, CODEC.decode(uuid6)); // decode back
		}
	}
}
