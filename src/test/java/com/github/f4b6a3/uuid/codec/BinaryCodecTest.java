package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class BinaryCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	@Test
	public void testEncode() {

		BinaryCodec codec = new BinaryCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidCreator.toBytes();
			UUID uuid1 = UUID.randomUUID();
			byte[] bytes = UuidCreator.toBytes(uuid1);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			long msb = buffer.getLong(0);
			long lsb = buffer.getLong(8);
			UUID uuid2 = new UUID(msb, lsb);
			assertEquals(uuid1, uuid2);
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidBytesCodec.encode();
			UUID uuid1 = UUID.randomUUID();
			byte[] bytes = codec.encode(uuid1);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			long msb = buffer.getLong(0);
			long lsb = buffer.getLong(8);
			UUID uuid2 = new UUID(msb, lsb);
			assertEquals(uuid1, uuid2);
		}
	}

	@Test
	public void testDecode() {

		BinaryCodec codec = new BinaryCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidCreator.fromBytes();
			UUID uuid1 = UUID.randomUUID();
			ByteBuffer buffer = ByteBuffer.allocate(16);
			buffer.putLong(uuid1.getMostSignificantBits());
			buffer.putLong(uuid1.getLeastSignificantBits());
			byte[] bytes = buffer.array();
			UUID uuid2 = UuidCreator.fromBytes(bytes);
			assertEquals(uuid1, uuid2);
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidBytesCodec.decode();
			UUID uuid1 = UUID.randomUUID();
			ByteBuffer buffer = ByteBuffer.allocate(16);
			buffer.putLong(uuid1.getMostSignificantBits());
			buffer.putLong(uuid1.getLeastSignificantBits());
			byte[] bytes = buffer.array();
			UUID uuid2 = codec.decode(bytes);
			assertEquals(uuid1, uuid2);
		}
	}

	@Test
	public void testEncodeAndDecode() {

		final BinaryCodec codec = new BinaryCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(bytes)); // decode back
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		BinaryCodec codec = new BinaryCodec();

		{
			try {
				UUID uuid = new UUID(0L, 0L);
				codec.encode(uuid);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				UUID uuid = UUID.randomUUID();
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

		BinaryCodec codec = new BinaryCodec();

		{
			try {
				// size == 16
				byte[] bytes = new byte[16];
				codec.decode(bytes);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				// null object
				byte[] bytes = null;
				codec.decode(bytes);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
			try {
				// size < 16
				byte[] bytes = new byte[15];
				codec.decode(bytes);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// size > 16
				byte[] bytes = new byte[17];
				codec.decode(bytes);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}
}
