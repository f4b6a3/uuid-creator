package com.github.f4b6a3.uuid.codec;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

import java.util.UUID;

public class StringCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final String URN_PREFIX = "urn:uuid:";

	private static final String RFC4122_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-6][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

	@Test
	public void testEncode() {

		StringCodec codec = new StringCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidCreator.toString();
			UUID random = UUID.randomUUID();
			String expected = random.toString();
			String actual = UuidCreator.toString(random);
			checkPattern(actual, RFC4122_PATTERN);
			assertEquals(expected, actual);
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidBytesCodec.encode();
			UUID random = UUID.randomUUID();
			String expected = random.toString();
			String actual = codec.encode(random);
			checkPattern(actual, RFC4122_PATTERN);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testDecode() {

		StringCodec codec = new StringCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// UuidCreator.fromString();
			String string = UUID.randomUUID().toString();
			UUID uuid = UuidCreator.fromString(string);
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// Lower case with hyphens
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string);
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// Lower case without hyphens
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.replace("-", ""));
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// Upper case with hyphens
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.toUpperCase());
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// Upper case without hyphens
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(string.toUpperCase().replace("-", ""));
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// With URN prefix: "urn:uuid:"
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode(URN_PREFIX + string);
			assertEquals(string, uuid.toString());
		}

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			// With curly braces: '{' and '}'
			String string = UUID.randomUUID().toString();
			UUID uuid = codec.decode("{" + string + "}");
			assertEquals(string, uuid.toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {

		final StringCodec codec = new StringCodec();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		StringCodec codec = new StringCodec();

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
			UUID uuid = null;
			try {
				codec.encode(uuid);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}

	@Test
	public void testDecodeInvalidUuidException() {

		StringCodec codec = new StringCodec();

		{
			try {
				String string = new UUID(0L, 0L).toString();
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				String string = UUID.randomUUID().toString();
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				String string = URN_PREFIX + UUID.randomUUID().toString();
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				String string = "{" + UUID.randomUUID().toString() + "}";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			try {
				// null object
				String string = null;
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// empty string
				String string = "";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// invalid string
				String string = "INVALID";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// size > 36
				String string = UUID.randomUUID().toString() + "x";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// size < 36
				String string = UUID.randomUUID().toString().substring(0, 35);
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}

	@Test
	public void testToCharArray() {

		{
			// 00000000-0000-0000-0000-000000000000
			String string = UUID.randomUUID().toString();
			char[] chars = StringCodec.toCharArray(string);
			assertEquals(string, String.valueOf(chars));
		}

		{
			// urn:uuid:00000000-0000-0000-0000-000000000000
			String string = UUID.randomUUID().toString();
			char[] chars = StringCodec.toCharArray(URN_PREFIX + string);
			assertEquals(string, String.valueOf(chars));
		}

		{
			// {00000000-0000-0000-0000-000000000000}
			String string = UUID.randomUUID().toString();
			char[] chars = StringCodec.toCharArray("{" + string + "}");
			assertEquals(string, String.valueOf(chars));
		}
	}

	@Test
	public void testGetJavaVersion() {

		final String key = "java.version";
		final String backup = System.getProperty(key);

		System.setProperty(key, "1.8");
		assertEquals(8, StringCodec.getJavaVersion());

		System.setProperty(key, "1.8.0");
		assertEquals(8, StringCodec.getJavaVersion());

		System.setProperty(key, "8");
		assertEquals(8, StringCodec.getJavaVersion());

		System.setProperty(key, "8.0");
		assertEquals(8, StringCodec.getJavaVersion());

		System.setProperty(key, "8.0.0");
		assertEquals(8, StringCodec.getJavaVersion());

		System.setProperty(key, "9");
		assertEquals(9, StringCodec.getJavaVersion());

		System.setProperty(key, "9.0");
		assertEquals(9, StringCodec.getJavaVersion());

		System.setProperty(key, "9.0.0");
		assertEquals(9, StringCodec.getJavaVersion());

		System.setProperty(key, "10");
		assertEquals(10, StringCodec.getJavaVersion());

		System.setProperty(key, "10.0");
		assertEquals(10, StringCodec.getJavaVersion());

		System.setProperty(key, "10.0.0");
		assertEquals(10, StringCodec.getJavaVersion());

		System.setProperty(key, backup);
	}

	private void checkPattern(String string, String pattern) {
		assertTrue("Doesn't match the pattern: " + string, string.matches(pattern));
	}
}
