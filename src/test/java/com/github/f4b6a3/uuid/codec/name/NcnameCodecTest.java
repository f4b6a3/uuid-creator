package com.github.f4b6a3.uuid.codec.name;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.Base32Codec;
import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;

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
		UuidCodec<String> codec = new NcnameCodec(new Base32Codec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			String actual = codec.encode(SAMPLES_UUID[i]);
			assertEquals(SAMPLES_BASE_32[i], actual);
		}
	}

	@Test
	public void testDecode32() {
		UuidCodec<String> codec = new NcnameCodec(new Base32Codec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			UUID actual = codec.decode(SAMPLES_BASE_32[i]);
			assertEquals(SAMPLES_UUID[i], actual);
		}
	}

	@Test
	public void testEncode64() {
		UuidCodec<String> codec = new NcnameCodec(new Base64UrlCodec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			String actual = codec.encode(SAMPLES_UUID[i]);
			assertEquals(SAMPLES_BASE_64[i], actual);
		}
	}

	@Test
	public void testDecode64() {
		UuidCodec<String> codec = new NcnameCodec(new Base64UrlCodec());
		for (int i = 0; i < SAMPLES_UUID.length; i++) {
			UUID actual = codec.decode(SAMPLES_BASE_64[i]);
			assertEquals(SAMPLES_UUID[i], actual);
		}
	}

	@Test
	public void testEncodeAndDecode() {
		final UuidCodec<String> codec = new NcnameCodec();
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			UUID uuid = UUID.randomUUID();
			String string = codec.encode(uuid); // encode
			assertEquals(uuid, codec.decode(string)); // decode back
		}
	}
}
