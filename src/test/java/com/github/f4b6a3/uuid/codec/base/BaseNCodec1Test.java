package com.github.f4b6a3.uuid.codec.base;

import static org.junit.Assert.*;
import org.junit.Test;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.exception.UuidCodecException;

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

		char[] alphabet = BaseN.BASE_32.getAlphabet().array();

		final UuidCodec<String> codec = new Base32Codec();
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

		char[] alphabet = BaseN.BASE_32.getAlphabet().array();

		final UuidCodec<String> codec = new Base32Codec();
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
	public void testEncodeBase32Hex() {

		char[] alphabet = BaseN.BASE_32_HEX.getAlphabet().array();

		final UuidCodec<String> codec = new Base32HexCodec();
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
	public void testDecodeBase32Hex() {

		char[] alphabet = BaseN.BASE_32_HEX.getAlphabet().array();

		final UuidCodec<String> codec = new Base32HexCodec();
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
	public void testEncodeBase32Crockford() {

		char[] alphabet = BaseN.BASE_32_CROCKFORD.getAlphabet().array();

		final UuidCodec<String> codec = new Base32CrockfordCodec();
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
	public void testDecodeBase32Crockford() {

		char[] alphabet = BaseN.BASE_32_CROCKFORD.getAlphabet().array();

		final UuidCodec<String> codec = new Base32CrockfordCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			byte[] bytes = CODEC_BYTES.encode(uuid);
			byte[] padded = getPadded(bytes);
			BigInteger n = new BigInteger(1, padded);
			String string = replace(zerofill(n.toString(32), 32), ALPHABET_JAVA, alphabet). //
					substring(0, 26); // remove padding
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

		UuidCodec<String> codec;

		codec = new Base16Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}

		codec = new Base32Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String ncname = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(ncname)); // decode back
		}

		codec = new Base32HexCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}

		codec = new Base32CrockfordCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}

		codec = new Base64Codec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}

		codec = new Base64UrlCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
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

		testExceptionBase16("");
		testExceptionBase16(null);
		testExceptionBase16(base16.replace('9', 'H'));
		testExceptionBase16(base16.replace('9', '.'));
		testExceptionBase16(base16.substring(0, 31));
		testExceptionBase16(base16 + "9");

		testExceptionBase32("");
		testExceptionBase32(null);
		testExceptionBase32(base32.replace('E', '1'));
		testExceptionBase32(base32.replace('E', '.'));
		testExceptionBase32(base32.substring(0, 25));
		testExceptionBase32(base32 + "E");

		testExceptionBase32Hex("");
		testExceptionBase32Hex(null);
		testExceptionBase32Hex(base32hex.replace('4', 'Z'));
		testExceptionBase32Hex(base32hex.replace('4', '.'));
		testExceptionBase32Hex(base32hex.substring(0, 25));
		testExceptionBase32Hex(base32hex + "4");

		testExceptionBase32Crockford("");
		testExceptionBase32Crockford(null);
		testExceptionBase32Crockford(base32crockford.replace('4', 'U'));
		testExceptionBase32Crockford(base32crockford.replace('4', '.'));
		testExceptionBase32Crockford(base32crockford.substring(0, 25));
		testExceptionBase32Crockford(base32crockford + "4");

		testExceptionBase64("");
		testExceptionBase64(null);
		testExceptionBase64(base64.replace('C', '_'));
		testExceptionBase64(base64.replace('C', '.'));
		testExceptionBase64(base64.substring(0, 21));
		testExceptionBase64(base64 + "C");

		testExceptionBase64url("");
		testExceptionBase64url(null);
		testExceptionBase64url(base64url.replace('C', '/'));
		testExceptionBase64url(base64url.replace('C', '.'));
		testExceptionBase64url(base64url.substring(0, 21));
		testExceptionBase64url(base64url + "C");
	}

	private void testExceptionBaseN(String string, BaseNCodec codec) {
		try {
			codec.decode(string);
			fail(string);
		} catch (UuidCodecException e) {
			// success
		}
	}

	private void testExceptionBase16(String string) {
		testExceptionBaseN(string, Base16Codec.INSTANCE);
	}

	private void testExceptionBase32(String string) {
		testExceptionBaseN(string, Base32Codec.INSTANCE);
	}

	private void testExceptionBase32Hex(String string) {
		testExceptionBaseN(string, Base32HexCodec.INSTANCE);
	}

	private void testExceptionBase32Crockford(String string) {
		testExceptionBaseN(string, Base32CrockfordCodec.INSTANCE);
	}

	private void testExceptionBase64(String string) {
		testExceptionBaseN(string, Base64Codec.INSTANCE);
	}

	private void testExceptionBase64url(String string) {
		testExceptionBaseN(string, Base64UrlCodec.INSTANCE);
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
