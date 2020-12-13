package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;

public class UuidBytesCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	@Test
	public void testEncode() {

		UuidCodec<byte[]> codec = new UuidBytesCodec();

		// UuidCreator.fromBytes();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UUID.randomUUID();
			byte[] bytes = UuidCreator.toBytes(uuid1);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			long msb = buffer.getLong(0);
			long lsb = buffer.getLong(8);
			UUID uuid2 = new UUID(msb, lsb);
			assertEquals(uuid1, uuid2);
		}

		// UuidBytesCodec.encode();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
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

		UuidCodec<byte[]> codec = new UuidBytesCodec();

		// UuidCreator.fromBytes();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UUID.randomUUID();
			ByteBuffer buffer = ByteBuffer.allocate(16);
			buffer.putLong(uuid1.getMostSignificantBits());
			buffer.putLong(uuid1.getLeastSignificantBits());
			byte[] bytes = buffer.array();
			UUID uuid2 = UuidCreator.fromBytes(bytes);
			assertEquals(uuid1, uuid2);
		}

		// UuidBytesCodec.decode();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid1 = UUID.randomUUID();
			ByteBuffer buffer = ByteBuffer.allocate(16);
			buffer.putLong(uuid1.getMostSignificantBits());
			buffer.putLong(uuid1.getLeastSignificantBits());
			byte[] bytes = buffer.array();
			UUID uuid2 = codec.decode(bytes);
			assertEquals(uuid1, uuid2);
		}

	}
}
