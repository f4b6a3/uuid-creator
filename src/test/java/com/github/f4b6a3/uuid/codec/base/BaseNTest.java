package com.github.f4b6a3.uuid.codec.base;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.Test;

public class BaseNTest {

	@Test
	public void testExpand() {

		String string;
		String expected;
		String actual;

		// all digits
		string = "0-9";
		expected = "0123456789";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// all lower case letters
		string = "a-z";
		expected = "abcdefghijklmnopqrstuvwxyz";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// all upper case letters
		string = "A-Z";
		expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 16
		string = "0-9a-f";
		expected = "0123456789abcdef";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 32
		string = "a-z2-7";
		expected = "abcdefghijklmnopqrstuvwxyz234567";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 32 hex
		string = "0-9a-v";
		expected = "0123456789abcdefghijklmnopqrstuv";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 32 crockford
		string = "0-9a-hj-kmnp-tv-z";
		expected = "0123456789abcdefghjkmnpqrstvwxyz";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 36
		string = "0-9a-z";
		expected = "0123456789abcdefghijklmnopqrstuvwxyz";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 58
		string = "1-9A-HJ-NP-Za-km-z";
		expected = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 62
		string = "A-Za-z0-9";
		expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 64
		string = "A-Za-z0-9+/";
		expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// base 64 url
		string = "A-Za-z0-9-_";
		expected = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
		actual = BaseN.expand(string);
		assertEquals(expected, actual);

		// other samples
		assertEquals("", BaseN.expand(""));
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
	public void testConstructorCompare() {
		assertEquals(BaseN.BASE_16.getAlphabet(), (new BaseN(16)).getAlphabet());
		assertEquals(BaseN.BASE_32_HEX.getAlphabet(), (new BaseN(32)).getAlphabet());
		assertEquals(BaseN.BASE_36.getAlphabet(), (new BaseN(36)).getAlphabet());
		assertEquals(BaseN.BASE_58.getAlphabet(), (new BaseN(58)).getAlphabet());
		assertEquals(BaseN.BASE_62.getAlphabet(), (new BaseN(62)).getAlphabet());
	}

	@Test
	public void testConstructorRadix() {
		for (int i = BaseN.RADIX_MIN; i <= BaseN.RADIX_MAX; i++) {
			BaseN base = new BaseN(i);
			assertEquals(base.getRadix(), i);
			assertEquals(base.getLength(), (int) Math.ceil(128 / (Math.log(i) / Math.log(2))));
			assertEquals(base.getAlphabet().array().length, i);
			assertEquals(base.isSensitive(), i > 36); // '0-9A-Z' has 36 chars
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
				assertEquals(uuid.toString(), decoded.toString());
				assertEquals(codec.getBase().getLength(), encoded.length());
			}
		}

		for (int i = BaseN.RADIX_MIN; i <= BaseN.RADIX_MAX; i++) {
			BaseN base = new BaseN(i);
			String alphabet = new String(base.getAlphabet().array());
			BaseNCodec codec = BaseNCodec.newInstance(alphabet);
			for (int j = 0; j <= 100; j++) {
				UUID uuid = UUID.randomUUID();
				String encoded = codec.encode(uuid);
				UUID decoded = codec.decode(encoded);
				assertEquals(uuid.toString(), decoded.toString());
				assertEquals(codec.getBase().getLength(), encoded.length());
			}
		}
	}

}
