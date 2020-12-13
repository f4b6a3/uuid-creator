package com.github.f4b6a3.uuid.codec.base;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.f4b6a3.uuid.codec.UuidBytesCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.exception.UuidCodecException;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

public class UuidBaseNCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final UuidCodec<byte[]> CODEC_BYTES = new UuidBytesCodec();

	// Alphabet used by BigInteger.toString(32);
	protected static final char[] BASE_32_ALPHABET = //
			{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
					'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', //
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v' };

	@Test
	public void testEncodeBase16() {

		final UuidCodec<String> codec = new UuidBase16Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			BigInteger n = new BigInteger(1, bytes);
			String string = zerofill(n.toString(16), 32).toUpperCase();
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase16() {

		final UuidCodec<String> codec = new UuidBase16Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			BigInteger n = new BigInteger(1, bytes);
			String string = zerofill(n.toString(16), 32).toUpperCase();
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testEncodeBase32() {

		final UuidCodec<String> codec = new UuidBase32Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32.getAlphabet()).substring(0, 26);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase32() {

		final UuidCodec<String> codec = new UuidBase32Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32.getAlphabet()).substring(0, 26);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testEncodeBase32Hex() {

		final UuidCodec<String> codec = new UuidBase32HexCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32_HEX.getAlphabet()).substring(0, 26);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase32Hex() {

		final UuidCodec<String> codec = new UuidBase32HexCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32_HEX.getAlphabet()).substring(0, 26);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testEncodeBase32Crockford() {

		final UuidCodec<String> codec = new UuidBase32CrockfordCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32_CROCKFORD.getAlphabet()).substring(0, 26);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase32Crockford() {

		final UuidCodec<String> codec = new UuidBase32CrockfordCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {

			byte[] padded = new byte[20];
			padded[16] = 0; // right pad
			padded[17] = 0; // right pad
			padded[18] = 0; // right pad
			padded[19] = 0; // right pad

			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			System.arraycopy(bytes, 0, padded, 0, 16);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), //
					BASE_32_ALPHABET, UuidBaseNAlphabet.ALPHABET_BASE_32_CROCKFORD.getAlphabet()).substring(0, 26);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testEncodeBase64() {

		final UuidCodec<String> codec = new UuidBase64Codec();
		Encoder encoder = Base64.getEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase64() {

		final UuidCodec<String> codec = new UuidBase64Codec();
		Encoder encoder = Base64.getEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testEncodeBase64Url() {

		final UuidCodec<String> codec = new UuidBase64UrlCodec();
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
	public void testDecodeBase64Url() {

		final UuidCodec<String> codec = new UuidBase64UrlCodec();
		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString().replace("-", ""), actual.toString().replace("-", ""));
		}
	}

	@Test
	public void testUuidCodecExceptionException() {

		String base16 = "9CC570EFBFCC45A8BF46C4CA7E039341";
		String base32 = "E4YVYO7P6MIWUL6RWEZJ7AHE2B";
		String base32hex = "4SOLOEVFUC8MKBUHM4P9V074Q1";
		String base32crockford = "4WRNREZFYC8PMBYHP4S9Z074T1";
		String base64 = "CcxXDvv8xFqL9GxMp+A5NB";
		String base64url = "CcxXDvv8xFqL9GxMp-A5NB";

		testExceptionBase16(null);
		testExceptionBase16(base16.replace('9', 'H'));
		testExceptionBase16(base16.replace('9', '.'));
		testExceptionBase16(base16.substring(0, 31));
		testExceptionBase16(base16 + "9");

		testExceptionBase32(null);
		testExceptionBase32(base32.replace('E', '1'));
		testExceptionBase32(base32.replace('E', '.'));
		testExceptionBase32(base32.substring(0, 25));
		testExceptionBase32(base32 + "E");

		testExceptionBase32Hex(null);
		testExceptionBase32Hex(base32hex.replace('4', 'Z'));
		testExceptionBase32Hex(base32hex.replace('4', '.'));
		testExceptionBase32Hex(base32hex.substring(0, 25));
		testExceptionBase32Hex(base32hex + "4");

		testExceptionBase32Crockford(null);
		testExceptionBase32Crockford(base32crockford.replace('4', 'U'));
		testExceptionBase32Crockford(base32crockford.replace('4', '.'));
		testExceptionBase32Crockford(base32crockford.substring(0, 25));
		testExceptionBase32Crockford(base32crockford + "4");

		testExceptionBase64(null);
		testExceptionBase64(base64.replace('C', '_'));
		testExceptionBase64(base64.replace('C', '.'));
		testExceptionBase64(base64.substring(0, 21));
		testExceptionBase64(base64 + "C");

		testExceptionBase64url(null);
		testExceptionBase64url(base64url.replace('C', '/'));
		testExceptionBase64url(base64url.replace('C', '.'));
		testExceptionBase64url(base64url.substring(0, 21));
		testExceptionBase64url(base64url + "C");
	}

	private void testExceptionBase16(String string) {
		final UuidCodec<String> codec = new UuidBase16Codec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase32(String string) {
		final UuidCodec<String> codec = new UuidBase32Codec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase32Hex(String string) {
		final UuidCodec<String> codec = new UuidBase32HexCodec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase32Crockford(String string) {
		final UuidCodec<String> codec = new UuidBase32CrockfordCodec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase64(String string) {
		final UuidCodec<String> codec = new UuidBase64Codec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase64url(String string) {
		final UuidCodec<String> codec = new UuidBase64UrlCodec();
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	public static String lpad(String string, int length, char fill) {
		return new String(lpad(string.toCharArray(), length, fill));
	}

	public static String zerofill(String string, int length) {
		return new String(zerofill(string.toCharArray(), length));
	}

	private static char[] lpad(char[] chars, int length, char fill) {

		int delta = 0;
		int limit = 0;

		if (length > chars.length) {
			delta = length - chars.length;
			limit = length;
		} else {
			delta = 0;
			limit = chars.length;
		}

		char[] output = new char[chars.length + delta];
		for (int i = 0; i < limit; i++) {
			if (i < delta) {
				output[i] = fill;
			} else {
				output[i] = chars[i - delta];
			}
		}
		return output;
	}

	private static char[] zerofill(char[] chars, int length) {
		return lpad(chars, length, '0');
	}

	public static String replace(String string, char[] from, char[] to) {
		return new String(replace(string.toCharArray(), from, to));
	}

	public static char[] replace(char[] chars, char[] from, char[] to) {

		char[] output = chars.clone();
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < from.length; j++) {
				if (output[i] == from[j]) {
					output[i] = to[j];
					break;
				}
			}
		}
		return output;
	}
}
