package com.github.f4b6a3.uuid.demo;

import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBase16Codec;
import com.github.f4b6a3.uuid.codec.base.UuidBase32Codec;
import com.github.f4b6a3.uuid.codec.base.UuidBase32HexCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBase62Codec;
import com.github.f4b6a3.uuid.codec.base.UuidBase62HexCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBase64Codec;
import com.github.f4b6a3.uuid.codec.base.UuidBase64UrlCodec;
import com.github.f4b6a3.uuid.codec.base.UuidBaseNCodec;
import com.github.f4b6a3.uuid.codec.slug.UuidSlugCodec;

public class DemoTest {

	private static final String HORIZONTAL_LINE = "----------------------------------------";

	public static void printList() {

		int max = 100;

		System.out.println();
		System.out.println(HORIZONTAL_LINE);
		System.out.println("Print list of UUIDs");

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBased());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-ordered UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrdered());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-based UUID with Mac");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithMac());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-ordered UUID With Mac");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithMac());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-based UUID with Hash");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithHash());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-ordered UUID With Hash");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithHash());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Random UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getRandomBased());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Prefix COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getPrefixComb());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Suffix COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getSuffixComb());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Short Prefix COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortPrefixComb());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Short Suffix COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortSuffixComb());
		}

		System.out.println(HORIZONTAL_LINE);
	}

	// temporary
	public static void listBlankSlugs() {

		UuidSlugCodec codec = new UuidSlugCodec(new UuidBase64UrlCodec(), /* bit shift = */ true);

		String[] list = { //
				"00000000-0000-0000-8000-000000000000", //
				"00000000-0000-1000-8000-000000000000", //
				"00000000-0000-2000-8000-000000000000", //
				"00000000-0000-3000-8000-000000000000", //
				"00000000-0000-4000-8000-000000000000", //
				"00000000-0000-5000-8000-000000000000", //
				"00000000-0000-6000-8000-000000000000", //
				"00000000-0000-7000-8000-000000000000", //
				"00000000-0000-8000-8000-000000000000", //
				"00000000-0000-9000-8000-000000000000", //
				"00000000-0000-a000-8000-000000000000", //
				"00000000-0000-b000-8000-000000000000", //
				"00000000-0000-c000-8000-000000000000", //
				"00000000-0000-d000-8000-000000000000", //
				"00000000-0000-e000-8000-000000000000", //
				"00000000-0000-f000-8000-000000000000", //
		};
		System.out.println();
		for (int i = 0; i < list.length; i++) {
			UUID uuid = UuidCreator.fromString(list[i]);
			String slug = codec.encode(uuid);
			System.out.println(list[i] + " " + slug);
		}
	}

	// temporary
	public static void listRandomSlugs() {
		System.out.println();

		boolean bitshift = true;
		UuidBaseNCodec baseCodec = new UuidBase64UrlCodec();
		UuidSlugCodec slugCodec = new UuidSlugCodec(baseCodec, bitshift);
		for (int i = 0; i < 16; i++) {
			UUID uuid = UuidCreator.getRandomBased();
			String slug = slugCodec.encode(uuid);
			System.out.println(uuid + " " + slug);
		}
	}

	public static void main(String[] args) {
		listBlankSlugs();
		listRandomSlugs();
	}
}
