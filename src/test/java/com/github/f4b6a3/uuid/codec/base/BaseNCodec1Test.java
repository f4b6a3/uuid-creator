package com.github.f4b6a3.uuid.codec.base;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

public class BaseNCodec1Test {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	private static final UuidCodec<byte[]> CODEC_BYTES = new BinaryCodec();

	// Alphabet used by BigInteger.toString(32);
	protected static final char[] ALPHABET_JAVA = "0123456789abcdefghijklmnopqrstuv".toCharArray();

	@Test
	public void testEncodeBase16() {

		final UuidCodec<String> codec = new Base16Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			BigInteger n = new BigInteger(1, bytes);
			String string = zerofill(n.toString(16), 32);
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase16() {

		final UuidCodec<String> codec = new Base16Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			BigInteger n = new BigInteger(1, bytes);
			String string = zerofill(n.toString(16), 32);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString(), actual.toString());
		}
	}

	@Test
	public void testEncodeBase32() {

		final BaseNCodec codec = new Base32Codec();
		char[] alphabet = codec.getBase().getAlphabet().array();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			byte[] padded = getPadded(bytes);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), ALPHABET_JAVA, alphabet) //
					.substring(0, 26); // remove padding
			String actual = codec.encode(uuid);
			assertEquals(string, actual);
		}
	}

	@Test
	public void testDecodeBase32() {

		final BaseNCodec codec = new Base32Codec();
		char[] alphabet = codec.getBase().getAlphabet().array();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			byte[] padded = getPadded(bytes);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), ALPHABET_JAVA, alphabet) //
					.substring(0, 26); // remove padding
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString(), actual.toString());
		}
	}

	@Test
	public void testEncodeBase64() {

		final UuidCodec<String> codec = new Base64Codec();
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

		final UuidCodec<String> codec = new Base64Codec();
		Encoder encoder = Base64.getEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString(), actual.toString());
		}
	}

	@Test
	public void testEncodeBase64Url() {

		final UuidCodec<String> codec = new Base64UrlCodec();
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

		final UuidCodec<String> codec = new Base64UrlCodec();
		Encoder encoder = Base64.getUrlEncoder();

		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			String string = encoder.encodeToString(bytes).substring(0, 22);
			UUID actual = codec.decode(string);
			assertEquals(uuid.toString(), actual.toString());
		}
	}

	@Test
	public void testEncodeAndDecode() {
		testEncodeAndDecode(new Base16Codec());
		testEncodeAndDecode(new Base32Codec());
		testEncodeAndDecode(new Base64Codec());
		testEncodeAndDecode(new Base64UrlCodec());
	}

	@Test
	public void testInvalidUuidExceptionException() {

		String base16 = "9CC570EFBFCC45A8BF46C4CA7E039341";
		String base32 = "E4YVYO7P6MIWUL6RWEZJ7AHE2B";
		String base64 = "CcxXDvv8xFqL9GxMp+A5NB";
		String base64url = "CcxXDvv8xFqL9GxMp-A5NB";

		testExceptionBaseN(Base16Codec.INSTANCE, "");
		testExceptionBaseN(Base16Codec.INSTANCE, null);
		testExceptionBaseN(Base16Codec.INSTANCE, base16.replace('9', 'H'));
		testExceptionBaseN(Base16Codec.INSTANCE, base16.replace('9', '.'));
		testExceptionBaseN(Base16Codec.INSTANCE, base16.substring(0, 31));
		testExceptionBaseN(Base16Codec.INSTANCE, base16 + "9");

		testExceptionBaseN(Base32Codec.INSTANCE, "");
		testExceptionBaseN(Base32Codec.INSTANCE, null);
		testExceptionBaseN(Base32Codec.INSTANCE, base32.replace('E', '1'));
		testExceptionBaseN(Base32Codec.INSTANCE, base32.replace('E', '.'));
		testExceptionBaseN(Base32Codec.INSTANCE, base32.substring(0, 25));
		testExceptionBaseN(Base32Codec.INSTANCE, base32 + "E");

		testExceptionBaseN(Base64Codec.INSTANCE, "");
		testExceptionBaseN(Base64Codec.INSTANCE, null);
		testExceptionBaseN(Base64Codec.INSTANCE, base64.replace('C', '_'));
		testExceptionBaseN(Base64Codec.INSTANCE, base64.replace('C', '.'));
		testExceptionBaseN(Base64Codec.INSTANCE, base64.substring(0, 21));
		testExceptionBaseN(Base64Codec.INSTANCE, base64 + "C");

		testExceptionBaseN(Base64UrlCodec.INSTANCE, "");
		testExceptionBaseN(Base64UrlCodec.INSTANCE, null);
		testExceptionBaseN(Base64UrlCodec.INSTANCE, base64url.replace('C', '/'));
		testExceptionBaseN(Base64UrlCodec.INSTANCE, base64url.replace('C', '.'));
		testExceptionBaseN(Base64UrlCodec.INSTANCE, base64url.substring(0, 21));
		testExceptionBaseN(Base64UrlCodec.INSTANCE, base64url + "C");
	}

	private void testEncodeAndDecode(UuidCodec<String> codec) {
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}
	}

	private void testExceptionBaseN(BaseNCodec codec, String string) {
		try {
			codec.decode(string);
			fail(string);
		} catch (InvalidUuidException e) {
			// success
		}
	}

	public static String lpad(String string, int length, char fill) {
		return new String(lpad(string.toCharArray(), length, fill));
	}

	public static String zerofill(String string, int length) {
		return new String(zerofill(string.toCharArray(), length));
	}

	/**
	 * Returns an array with 160 bits.
	 * 
	 * Base32 uses blocks of 40 bits. This method returns 4 groups of 40 bits.
	 * 
	 * RFC-4648: "When fewer than 40 input bits are available in an input group,
	 * bits with value zero are added (on the right) to form an integral number of
	 * 5-bit groups."
	 * 
	 * @param bytes the array to be padded
	 * @return a padded array
	 */
	private byte[] getPadded(byte[] bytes) {
		byte[] padded = new byte[20];
		padded[16] = 0; // right pad
		padded[17] = 0; // right pad
		padded[18] = 0; // right pad
		padded[19] = 0; // right pad
		System.arraycopy(bytes, 0, padded, 0, 16);
		return padded;
	}

	private static char[] lpad(char[] chars, int length, char fill) {

		if (chars.length < length) {

			int delta = length - chars.length;

			char[] output = new char[length];
			for (int i = 0; i < length; i++) {
				if (i < delta) {
					output[i] = fill;
				} else {
					output[i] = chars[i - delta];
				}
			}

			return output;
		}

		return chars;
	}

	private static char[] zerofill(char[] chars, int length) {
		return lpad(chars, length, '0');
	}

	private static String replace(String string, char[] from, char[] to) {
		return new String(replace(string.toCharArray(), from, to));
	}

	private static char[] replace(char[] chars, char[] from, char[] to) {

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
