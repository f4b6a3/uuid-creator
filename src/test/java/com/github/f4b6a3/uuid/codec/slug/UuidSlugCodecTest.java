package com.github.f4b6a3.uuid.codec.slug;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;

import java.util.Base64;
import java.util.UUID;
import java.util.Base64.Encoder;

public class UuidSlugCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final UuidCodec<byte[]> CODEC_BYTES = new UuidBytesCodec();

	@Test
	public void testEncode() {

		boolean shift = false;
		final UuidCodec<String> codec = new UuidSlugCodec(shift);
		
		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecode() {

		boolean shift = false;
		final UuidCodec<String> codec = new UuidSlugCodec(shift);
		
		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Ignore // TODO: Not ready yet
	public void testEncodeWithShift() {

		boolean shift = true;
		final UuidCodec<String> codec = new UuidSlugCodec(shift);
		
		Encoder encoder = Base64.getUrlEncoder();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Ignore // TODO: Not ready yet
	public void testDecodeWithShift() {

		boolean shift = true;
		final UuidCodec<String> codec = new UuidSlugCodec(shift);
		
		Encoder encoder = Base64.getUrlEncoder();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}
}
