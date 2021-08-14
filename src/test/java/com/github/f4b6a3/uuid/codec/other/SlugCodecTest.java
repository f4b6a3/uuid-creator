package com.github.f4b6a3.uuid.codec.other;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.other.SlugCodec;

import java.util.Base64;
import java.util.UUID;
import java.util.Base64.Encoder;

public class SlugCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final UuidCodec<byte[]> CODEC_BYTES = new BinaryCodec();

	@Test
	public void testEncode() {

		final UuidCodec<String> codec = new SlugCodec();

		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			UUID uuid1 = UUID.randomUUID();
			UUID uuid2 = moveCharacters(uuid1);

			byte[] bytes = CODEC_BYTES.encode(uuid2);
			String string = encoder.encodeToString(bytes).substring(0, 22);

			assertEquals(string, codec.encode(uuid1));
		}
	}

	@Test
	public void testDecode() {

		final UuidCodec<String> codec = new SlugCodec();

		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			UUID uuid1 = UUID.randomUUID();
			UUID uuid2 = moveCharacters(uuid1);

			byte[] bytes = CODEC_BYTES.encode(uuid2);
			String string = encoder.encodeToString(bytes).substring(0, 22);

			assertEquals(uuid1.toString(), codec.decode(string).toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {
		final UuidCodec<String> codec = new SlugCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}
	}

	private UUID moveCharacters(UUID uuid) {

		// remove the hyphens
		String string = uuid.toString().replace("-", "");

		// create a buffer
		int b = 0; // buffer index
		char[] buffer = new char[32];

		// put the version and variant characters
		// at the start positions of the buffer
		buffer[b++] = string.charAt(12); // equivalent to move version nibble to bit positions 0, 1, 2, and 3
		buffer[b++] = string.charAt(16); // equivalent to move variant nibble to bit positions 4, 5, 6, and 7

		// put the other characters into the buffer
		// ignoring version and variant characters
		for (int j = 0; j < string.length(); j++) {
			if (j != 12 && j != 16) {
				buffer[b++] = string.charAt(j); // equivalent to shifting the other bits right
			}
		}

		// add the hyphens back so that it
		// can be parsed by UUID#fromString()
		string = new String(buffer);
		string = string.substring(0, 8) + '-' + //
				string.substring(8, 12) + '-' + //
				string.substring(12, 16) + '-' + //
				string.substring(16, 20) + '-' + //
				string.substring(20, 32);

		return UUID.fromString(string);
	}
}
