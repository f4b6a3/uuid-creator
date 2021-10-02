package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UriCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;

public class UriCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_URN_PATTERN = "^urn:uuid:[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {
		UuidCodec<URI> codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID random = UUID.randomUUID();
			String expected = URN_PREFIX + random.toString();
			String actual = codec.encode(random).toString();
			checkPattern(actual, RFC4122_URN_PATTERN);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testDecode() {
		UuidCodec<URI> codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URI.create(URN_PREFIX + string));
			assertEquals(string, uuid.toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {
		UuidCodec<URI> codec = new UriCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			URI uri = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(uri)); // decode back
		}
	}

	private void checkPattern(String string, String pattern) {
		assertTrue("Doesn't match the pattern: " + string, string.matches(pattern));
	}
}
