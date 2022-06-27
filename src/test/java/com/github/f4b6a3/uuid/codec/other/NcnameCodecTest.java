package com.github.f4b6a3.uuid.codec.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.base.Base32Codec;
import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;
import com.github.f4b6a3.uuid.codec.other.NcnameCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class NcnameCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 100;

	// Samples source: https://tools.ietf.org/html/draft-taylor-uuid-ncname-00
	private static final UUID[] SAMPLES_UUID = { //

			// https://tools.ietf.org/html/draft-taylor-uuid-ncname-01
			UUID.fromString("00000000-0000-0000-0000-000000000000"), //
			UUID.fromString("ca6be4c8-cbaf-11ea-b2ab-00045a86c8a1"), //
			UUID.fromString("000003e8-cbb9-21ea-b201-00045a86c8a1"), //
			UUID.fromString("3d813cbb-47fb-32ba-91df-831e1593ac29"), //
			UUID.fromString("01867b2c-a0dd-459c-98d7-89e545538d6c"), //
			UUID.fromString("21f7f8de-8051-5b89-8680-0195ef798b6a"), //

			// https://metacpan.org/pod/Data::UUID::NCName
			UUID.fromString("1ff916f3-6ed7-443a-bef5-f4c85f18cd10"),

			// https://github.com/doriantaylor/rb-uuid-ncname/blob/master/spec/uuid/ncname_spec.rb
			UUID.fromString("c89e701a-acf2-4dd5-9401-539c1fcf15cb"), //
	};

	private static final String[] SAMPLES_BASE_32 = { //

			// https://tools.ietf.org/html/draft-taylor-uuid-ncname-01
			"aaaaaaaaaaaaaaaaaaaaaaaaaa", //
			"bzjv6jsglv4pkfkyaarninsfbl", //
			"caaaah2glxepkeaiaarninsfbl", //
			"dhwatzo2h7mv2dx4ddykzhlbjj", //
			"eagdhwlfa3vm4rv4j4vcvhdlmj", //
			"feh37rxuakg4jnaabsxxxtc3ki", //

			// https://metacpan.org/pod/Data::UUID::NCName
			"ed74rn43o25b255puzbprrtiql",

			// https://github.com/doriantaylor/rb-uuid-ncname/blob/master/spec/uuid/ncname_spec.rb
			"ezcphagvm6loviakttqp46folj", //
	};

	private static final String[] SAMPLES_BASE_64 = { //

			// https://tools.ietf.org/html/draft-taylor-uuid-ncname-01
			"AAAAAAAAAAAAAAAAAAAAAA", //
			"BymvkyMuvHqKrAARahsihL", //
			"CAAAD6Mu5HqIBAARahsihL", //
			"DPYE8u0f7K6Hfgx4Vk6wpJ", //
			"EAYZ7LKDdWcjXieVFU41sJ", //
			"FIff43oBRuJaAAZXveYtqI", //

			// https://metacpan.org/pod/Data::UUID::NCName
			"EH_kW827XQ6719MhfGM0QL",

			// https://github.com/doriantaylor/rb-uuid-ncname/blob/master/spec/uuid/ncname_spec.rb
			"EyJ5wGqzy3VQBU5wfzxXLJ", //
	};

	@Test
	public void testEncode32() {
		NcnameCodec codec = new NcnameCodec(new Base32Codec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			String actual = codec.encode(SAMPLES_UUID[i]);
			assertEquals(SAMPLES_BASE_32[i], actual);
		}
	}

	@Test
	public void testDecode32() {
		NcnameCodec codec = new NcnameCodec(new Base32Codec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			UUID actual = codec.decode(SAMPLES_BASE_32[i]);
			assertEquals(SAMPLES_UUID[i], actual);
		}
	}

	@Test
	public void testEncode64() {
		NcnameCodec codec = new NcnameCodec(new Base64UrlCodec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			String actual = codec.encode(SAMPLES_UUID[i]);
			assertEquals(SAMPLES_BASE_64[i], actual);
		}
	}

	@Test
	public void testDecode64() {
		NcnameCodec codec = new NcnameCodec(new Base64UrlCodec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			UUID actual = codec.decode(SAMPLES_BASE_64[i]);
			assertEquals(SAMPLES_UUID[i], actual);
		}
	}

	@Test
	public void testEncodeAndDecode() {
		NcnameCodec codec = new NcnameCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}
	}

	@Test
	public void testEncodeInvalidUuidException() {

		NcnameCodec codec = new NcnameCodec();

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

		{
			NcnameCodec codec = new NcnameCodec();
			try {
				// 00000000-0000-0000-0000-000000000000
				String string = "AAAAAAAAAAAAAAAAAAAAAA";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// ca6be4c8-cbaf-11ea-b2ab-00045a86c8a1
				String string = "BymvkyMuvHqKrAARahsihL";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 000003e8-cbb9-21ea-b201-00045a86c8a1
				String string = "CAAAD6Mu5HqIBAARahsihL";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 3d813cbb-47fb-32ba-91df-831e1593ac29
				String string = "DPYE8u0f7K6Hfgx4Vk6wpJ";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 01867b2c-a0dd-459c-98d7-89e545538d6c
				String string = "EAYZ7LKDdWcjXieVFU41sJ";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 21f7f8de-8051-5b89-8680-0195ef798b6a
				String string = "FIff43oBRuJaAAZXveYtqI";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			NcnameCodec codec = new NcnameCodec(new Base32Codec());
			try {
				// 00000000-0000-0000-0000-000000000000
				String string = "aaaaaaaaaaaaaaaaaaaaaaaaaa";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// ca6be4c8-cbaf-11ea-b2ab-00045a86c8a1
				String string = "bzjv6jsglv4pkfkyaarninsfbl";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 000003e8-cbb9-21ea-b201-00045a86c8a1
				String string = "caaaah2glxepkeaiaarninsfbl";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 3d813cbb-47fb-32ba-91df-831e1593ac29
				String string = "dhwatzo2h7mv2dx4ddykzhlbjj";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 01867b2c-a0dd-459c-98d7-89e545538d6c
				String string = "eagdhwlfa3vm4rv4j4vcvhdlmj";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}

			try {
				// 21f7f8de-8051-5b89-8680-0195ef798b6a
				String string = "feh37rxuakg4jnaabsxxxtc3ki";
				codec.decode(string);
				// success
			} catch (InvalidUuidException e) {
				fail("Should not throw exception");
			}
		}

		{
			NcnameCodec codec = new NcnameCodec();
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
				// size > 22
				String string = "AAAAAAAAAAAAAAAAAAAAAAa";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// size < 22
				String string = "AAAAAAAAAAAAAAAAAAAAA";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// invalid first char (bookend)
				String string = "qAAAAAAAAAAAAAAAAAAAAA";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// invalid last char (bookend)
				String string = "AAAAAAAAAAAAAAAAAAAAAq";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}

		{
			NcnameCodec codec = new NcnameCodec(new Base32Codec());
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
				// size > 26
				String string = "aaaaaaaaaaaaaaaaaaaaaaaaaaA";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// size < 26
				String string = "aaaaaaaaaaaaaaaaaaaaaaaaa";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// invalid first char (bookend)
				String string = "Qaaaaaaaaaaaaaaaaaaaaaaaaa";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}

			try {
				// invalid last char (bookend)
				String string = "aaaaaaaaaaaaaaaaaaaaaaaaaQ";
				codec.decode(string);
				fail("Should throw exception");
			} catch (InvalidUuidException e) {
				// success
			}
		}
	}
}
