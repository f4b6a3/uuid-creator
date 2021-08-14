package com.github.f4b6a3.uuid.codec.other;

import static com.github.f4b6a3.uuid.codec.other.DotNetGuid1CodecTest.checkFields;
import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.other.DotNetGuid4Codec;

public class DotNetGuid4CodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final UuidCodec<UUID> CODEC = new DotNetGuid4Codec(); // codec for v4

	@Test
	public void testEncode() {

		UUID uuid;
		UUID guid;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid = UuidCreator.getRandomBased();
			guid = CODEC.encode(uuid);
			checkFields(uuid, guid);
		}
	}

	@Test
	public void testEncodeAndDecode() {

		UUID uuidv1;
		UUID msguid;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv1 = UuidCreator.getRandomBased();
			msguid = CODEC.encode(uuidv1); // encode
			assertEquals(uuidv1, CODEC.decode(msguid)); // decode back
		}
	}
}
