package com.github.f4b6a3.uuid.codec.other;

import static com.github.f4b6a3.uuid.util.UuidUtil.isTimeOrdered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.other.TimeOrderedCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class TimeOrderedCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final TimeOrderedCodec CODEC = new TimeOrderedCodec();

	@Test
	public void testEncode() {

		UUID uuid1;
		UUID uuid6;

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidCreator.getTimeBased();
			uuid6 = CODEC.encode(uuid1); // encode
			assertTrue(isTimeOrdered(uuid6));
			assertEquals(UuidUtil.getTimestamp(uuid1), UuidUtil.getTimestamp(uuid6));
			assertEquals(uuid1.getLeastSignificantBits(), uuid6.getLeastSignificantBits());
		}

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid1 = UuidUtil.setVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			uuid6 = CODEC.encode(uuid1); // encode
			assertTrue(isTimeOrdered(uuid6));
			assertEquals(UuidUtil.getTimestamp(uuid1), UuidUtil.getTimestamp(uuid6));
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
			uuid1 = UuidUtil.setVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			uuid6 = CODEC.encode(uuid1); // encode
			assertEquals(uuid1, CODEC.decode(uuid6)); // decode back
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		TimeOrderedCodec codec = new TimeOrderedCodec();

		{
			try {
				// RFC-4122 UUID v1
				UUID uuid = new UUID(0x0000000000001000L, 0x8000000000000000L);
				codec.encode(uuid);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				UUID uuid = null;
				codec.encode(uuid);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}

	@Test
	public void testDecodeInvalidUuidException() {

		TimeOrderedCodec codec = new TimeOrderedCodec();

		{
			try {
				// RFC-4122 UUID v6
				UUID uuid = new UUID(0x0000000000006000L, 0x8000000000000000L);
				codec.decode(uuid);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				UUID uuid = null;
				codec.decode(uuid);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}
}
