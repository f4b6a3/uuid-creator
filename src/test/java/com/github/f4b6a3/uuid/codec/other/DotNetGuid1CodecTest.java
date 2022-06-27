package com.github.f4b6a3.uuid.codec.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.other.DotNetGuid1Codec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class DotNetGuid1CodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final DotNetGuid1Codec CODEC = new DotNetGuid1Codec(); // codec for v1

	@Test
	public void testEncode() {

		UUID uuid;
		UUID guid;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid = UuidCreator.getTimeBased();
			guid = CODEC.encode(uuid);
			checkFields(uuid, guid);
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuid = UuidUtil.setVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			guid = CODEC.encode(uuid);
			checkFields(uuid, guid);
		}
	}

	@Test
	public void testEncodeAndDecode() {

		UUID uuidv1;
		UUID msguid;

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv1 = UuidCreator.getTimeBased();
			msguid = CODEC.encode(uuidv1); // encode
			assertEquals(uuidv1, CODEC.decode(msguid)); // decode back
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv1 = UuidUtil.setVersion(UuidCreator.getRandomBased(), 1); // fake v1 using v4
			msguid = CODEC.encode(uuidv1); // encode
			assertEquals(uuidv1, CODEC.decode(msguid)); // decode back
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		DotNetGuid1Codec codec = new DotNetGuid1Codec();

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

		DotNetGuid1Codec codec = new DotNetGuid1Codec();

		{
			try {
				// .Net GUID v1
				UUID uuid = new UUID(0x0000000000000010L, 0x8000000000000000L);
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

	protected static void checkFields(UUID uuid, UUID guid) {

		ByteBuffer uuidBuffer;
		ByteBuffer guidBuffer;

		byte[] uuidField;
		byte[] guidField;

		uuidBuffer = ByteBuffer.allocate(4);
		uuidBuffer.order(ByteOrder.BIG_ENDIAN);
		uuidBuffer.putInt((int) (uuid.getMostSignificantBits() >>> 32));
		uuidField = uuidBuffer.array();

		guidBuffer = ByteBuffer.allocate(4);
		guidBuffer.order(ByteOrder.LITTLE_ENDIAN);
		guidBuffer.putInt((int) (guid.getMostSignificantBits() >>> 32));
		guidField = guidBuffer.array();

		// Test the 1st field
		for (int j = 0; j < uuidField.length; j++) {
			assertEquals(uuidField[j], guidField[j]);
		}

		uuidBuffer = ByteBuffer.allocate(2);
		uuidBuffer.order(ByteOrder.BIG_ENDIAN);
		uuidBuffer.putShort((short) ((uuid.getMostSignificantBits() & 0x00000000ffff0000L) >>> 16));
		uuidField = uuidBuffer.array();

		guidBuffer = ByteBuffer.allocate(2);
		guidBuffer.order(ByteOrder.LITTLE_ENDIAN);
		guidBuffer.putShort((short) ((guid.getMostSignificantBits() & 0x00000000ffff0000L) >>> 16));
		guidField = guidBuffer.array();

		// Test the 2nd field
		for (int j = 0; j < uuidField.length; j++) {
			assertEquals(uuidField[j], guidField[j]);
		}

		uuidBuffer = ByteBuffer.allocate(4);
		uuidBuffer.order(ByteOrder.BIG_ENDIAN);
		uuidBuffer.putShort((short) ((uuid.getMostSignificantBits() & 0x000000000000ffffL)));
		uuidField = uuidBuffer.array();

		guidBuffer = ByteBuffer.allocate(4);
		guidBuffer.order(ByteOrder.LITTLE_ENDIAN);
		guidBuffer.putShort((short) ((guid.getMostSignificantBits() & 0x000000000000ffffL)));
		guidField = guidBuffer.array();

		// Test the 3nd field
		for (int j = 0; j < uuidField.length; j++) {
			assertEquals(uuidField[j], guidField[j]);
		}

		assertEquals(uuid.getLeastSignificantBits(), guid.getLeastSignificantBits());
	}
}
