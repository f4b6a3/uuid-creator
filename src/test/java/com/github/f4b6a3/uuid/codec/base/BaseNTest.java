package com.github.f4b6a3.uuid.codec.base;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

public class BaseNTest {

	@Test
	public void testExpand() {
		assertEquals("", BaseN.expand(""));
		assertEquals("0", BaseN.expand("0"));
		assertEquals("a", BaseN.expand("a"));
		assertEquals("A", BaseN.expand("A"));
		assertEquals("-", BaseN.expand("-"));
		assertEquals("--", BaseN.expand("--"));
		assertEquals("-x-", BaseN.expand("-x-"));
		assertEquals("-x-", BaseN.expand("-x-x-"));
		assertEquals("-x-x-", BaseN.expand("-x-x-x-"));
		assertEquals("-x-x-", BaseN.expand("-x-x-x-x-"));
		assertEquals("abc123", BaseN.expand("abc123"));
		assertEquals("00400", BaseN.expand("004-400"));
		assertEquals("aaxyaa", BaseN.expand("aax-yaa"));
		assertEquals("-abcdef", BaseN.expand("-abcdef"));
		assertEquals("abcdef-", BaseN.expand("abcdef-"));
		assertEquals("0123-_", BaseN.expand("0123-_"));
		assertEquals("_-0123", BaseN.expand("_-0123"));
		assertEquals("abcd-_", BaseN.expand("abcd-_"));
		assertEquals("abcd-_", BaseN.expand("abcd-_"));
		assertEquals("123789", BaseN.expand("1-37-9"));
		assertEquals("abcopq", BaseN.expand("a-co-q"));
		assertEquals("ABCOPQ", BaseN.expand("A-CO-Q"));
		assertEquals("-123789-", BaseN.expand("-1-37-9-"));
		assertEquals("-abcopq-", BaseN.expand("-a-co-q-"));
		assertEquals("-ABCOPQ-", BaseN.expand("-A-CO-Q-"));
		assertEquals("123-789", BaseN.expand("1-3-7-9"));
		assertEquals("abc-opq", BaseN.expand("a-c-o-q"));
		assertEquals("ABC-OPQ", BaseN.expand("A-C-O-Q"));
		assertEquals("-123-789-", BaseN.expand("-1-3-7-9-"));
		assertEquals("-abc-opq-", BaseN.expand("-a-c-o-q-"));
		assertEquals("-ABC-OPQ-", BaseN.expand("-A-C-O-Q-"));
	}

	@Test
	public void testExpandBase() {

		// all digits
		assertTrue(testExpandBase("0-9", "0123456789"));
		// all letters
		assertTrue(testExpandBase("a-z", "abcdefghijklmnopqrstuvwxyz"));
		// all letters upper case
		assertTrue(testExpandBase("A-Z", "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		// base 16
		assertTrue(testExpandBase("0-9a-f", "0123456789abcdef"));
		// base 16 upper case
		assertTrue(testExpandBase("0-9A-F", "0123456789ABCDEF"));
		// base 32
		assertTrue(testExpandBase("a-z2-7", "abcdefghijklmnopqrstuvwxyz234567"));
		// base 32 upper case
		assertTrue(testExpandBase("A-Z2-7", "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"));
		// base 32 hex
		assertTrue(testExpandBase("0-9a-v", "0123456789abcdefghijklmnopqrstuv"));
		// base 32 hex upper case
		assertTrue(testExpandBase("0-9A-V", "0123456789ABCDEFGHIJKLMNOPQRSTUV"));
		// base 32 crockford
		assertTrue(testExpandBase("0-9a-hjkmnp-tv-z", "0123456789abcdefghjkmnpqrstvwxyz"));
		// base 32 crockford upper case
		assertTrue(testExpandBase("0-9A-HJKMNP-TV-Z", "0123456789ABCDEFGHJKMNPQRSTVWXYZ"));
		// base 36
		assertTrue(testExpandBase("0-9a-z", "0123456789abcdefghijklmnopqrstuvwxyz"));
		// base 36 upper case
		assertTrue(testExpandBase("0-9A-Z", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		// base 58
		assertTrue(testExpandBase("0-9A-Za-v", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv"));
		// base 58 bitcoin
		assertTrue(testExpandBase("1-9A-HJ-NP-Za-km-z", "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"));
		// base 58 flickr
		assertTrue(testExpandBase("1-9a-km-zA-HJ-NP-Z", "123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"));
		// base 62
		assertTrue(testExpandBase("0-9A-Za-z", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));
		// base 64
		assertTrue(testExpandBase("A-Za-z0-9+/", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"));
		// base 64 url
		assertTrue(testExpandBase("A-Za-z0-9-_", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"));
	}

	private boolean testExpandBase(String string, String expected) {
		String actual = BaseN.expand(string);
		assertEquals(expected, actual);
		return true; // success
	}

	@Test
	public void testExpandChars() {

		// numbers
		assertTrue(testExpandChars('0', '9', "0123456789"));
		assertTrue(testExpandChars('5', '5', "5"));
		assertTrue(testExpandChars('3', '6', "3456"));
		assertTrue(testExpandChars('6', '3', ""));
		assertTrue(testExpandChars('5', 'e', ""));

		// lower case
		assertTrue(testExpandChars('a', 'z', "abcdefghijklmnopqrstuvwxyz"));
		assertTrue(testExpandChars('g', 'g', "g"));
		assertTrue(testExpandChars('e', 'l', "efghijkl"));
		assertTrue(testExpandChars('l', 'e', ""));
		assertTrue(testExpandChars('w', 'F', ""));

		// upper case
		assertTrue(testExpandChars('A', 'Z', "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertTrue(testExpandChars('R', 'R', "R"));
		assertTrue(testExpandChars('M', 'W', "MNOPQRSTUVW"));
		assertTrue(testExpandChars('W', 'M', ""));
		assertTrue(testExpandChars('T', '4', ""));
	}

	private boolean testExpandChars(char a, char b, String expected) {
		String actual = new String(BaseN.expand(a, b));
		assertEquals(expected, actual);
		return true; // success
	}

	@Test
	public void testConstructorCompare() {
		assertEquals(Base16Codec.INSTANCE.getBase().getAlphabet(), (new BaseN(16)).getAlphabet());
		assertEquals(Base32HexCodec.INSTANCE.getBase().getAlphabet(), (new BaseN(32)).getAlphabet());
		assertEquals(Base36Codec.INSTANCE.getBase().getAlphabet(), (new BaseN(36)).getAlphabet());
		assertEquals(Base58Codec.INSTANCE.getBase().getAlphabet(), (new BaseN(58)).getAlphabet());
		assertEquals(Base62Codec.INSTANCE.getBase().getAlphabet(), (new BaseN(62)).getAlphabet());
	}

	@Test
	public void testConstructorRadix() {
		for (int i = BaseN.RADIX_MIN; i <= BaseN.RADIX_MAX; i++) {
			BaseN base = new BaseN(i);
			assertEquals(base.getRadix(), i);
			assertEquals(base.getLength(), (int) Math.ceil(128 / (Math.log(i) / Math.log(2))));
			assertEquals(base.getAlphabet().array().length, i);
			assertEquals(base.isSensitive(), i > 36); // '0-9a-z' has 36 chars

			String alphabet = i > 36 ? BaseN.ALPHABET_64 : BaseN.ALPHABET_36;
			assertEquals(new String(base.getAlphabet().array()), alphabet.substring(0, i));
		}
	}

	@Test
	public void testConstructorBaseNCodec() {

		for (int i = BaseN.RADIX_MIN; i <= BaseN.RADIX_MAX; i++) {
			BaseNCodec codec = BaseNCodec.newInstance(i);
			for (int j = 0; j <= 100; j++) {
				UUID uuid = UUID.randomUUID();
				String encoded = codec.encode(uuid);
				UUID decoded = codec.decode(encoded);
				assertTrue(codec.getBase().isValid(encoded));
				assertEquals(uuid.toString(), decoded.toString());
				assertEquals(codec.getBase().getLength(), encoded.length());
			}
		}

		for (int i = BaseN.RADIX_MIN; i <= BaseN.RADIX_MAX; i++) {
			String alphabet = BaseN.ALPHABET_64.substring(0, i);
			BaseNCodec codec = BaseNCodec.newInstance(alphabet);
			for (int j = 0; j <= 100; j++) {
				UUID uuid = UUID.randomUUID();
				String encoded = codec.encode(uuid);
				UUID decoded = codec.decode(encoded);
				assertTrue(codec.getBase().isValid(encoded));
				assertEquals(uuid.toString(), decoded.toString());
				assertEquals(codec.getBase().getLength(), encoded.length());
			}
		}
	}
}
