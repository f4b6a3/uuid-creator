package com.github.f4b6a3.uuid.codec.other;

import static com.github.f4b6a3.uuid.codec.other.DotNetGuid1CodecTest.checkFields;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.other.DotNetGuid4Codec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class DotNetGuid4CodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final DotNetGuid4Codec CODEC = new DotNetGuid4Codec(); // codec for v4

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

	@Test
	public void testEncodeInvalidUuidException() {

		DotNetGuid4Codec codec = new DotNetGuid4Codec();

		{
			try {
				// RFC-4122 UUID v4
				UUID uuid = new UUID(0x0000000000004000L, 0x8000000000000000L);
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

		DotNetGuid4Codec codec = new DotNetGuid4Codec();

		{
			try {
				// .Net GUID v4
				UUID uuid = new UUID(0x0000000000000040L, 0x8000000000000000L);
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
