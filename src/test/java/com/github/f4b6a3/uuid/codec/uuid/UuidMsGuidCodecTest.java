package com.github.f4b6a3.uuid.codec.uuid;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidCodec;

public class UuidMsGuidCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final UuidCodec<UUID> CODEC = new UuidMsGuidCodec();

	@Test
	public void testEncode() {
		// Using loop - Random-based
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuidv1 = UuidCreator.getTimeBased();
			UUID msguid = CODEC.encode(uuidv1);
			UUID result = CODEC.decode(msguid);
			assertEquals(uuidv1, result);
		}
	}

	@Test
	public void testDecode() {
		// Using loop - Time-based
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuidv1 = UuidCreator.getTimeBased();
			UUID msguid = CODEC.encode(uuidv1);
			UUID result = CODEC.decode(msguid);
			assertEquals(uuidv1, result);
		}
	}

	@Test
	public void testToMsGuidSample() {

		String sample1 = "9aa4dc96-3cec-11eb-adc1-0242ac120002"; // UUID v1
		String sample2 = "96dca49a-ec3c-eb11-adc1-0242ac120002"; // MS GUID

		UUID uuid = UUID.fromString(sample1);
		UUID guid = UUID.fromString(sample2);
		UUID result = CODEC.encode(uuid);

		assertEquals(guid, result);
	}

	@Test
	public void testFromMsGuidSample() {

		String sample1 = "4ed60dfd-ec3c-eb11-86be-8f01b8907292"; // MS GUID
		String sample2 = "fd0dd64e-3cec-11eb-86be-8f01b8907292"; // UUID v4

		UUID guid = UUID.fromString(sample1);
		UUID uuid = UUID.fromString(sample2);
		UUID result = CODEC.decode(guid);

		assertEquals(uuid, result);
	}
}
