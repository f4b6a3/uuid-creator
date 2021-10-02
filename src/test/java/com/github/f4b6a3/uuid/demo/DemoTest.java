package com.github.f4b6a3.uuid.demo;

import com.github.f4b6a3.uuid.UuidCreator;

public class DemoTest {

	public static void printTitle(String string) {
		final String HORIZONTAL_LINE = "----------------------------------------";
		System.out.println(HORIZONTAL_LINE);
		System.out.println(string);
		System.out.println(HORIZONTAL_LINE);
	}

	public static void printList() {

		int max = 100;

		printTitle("PRINT LIST OF UUIDs");

		printTitle("### Time-based UUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBased());
		}

		printTitle("### Time-ordered UUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrdered());
		}

		printTitle("### Time-based UUID with Mac");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithMac());
		}

		printTitle("### Time-ordered UUID with Mac");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithMac());
		}

		printTitle("### Time-based UUID with Hash");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithHash());
		}

		printTitle("### Time-ordered UUID with Hash");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithHash());
		}

		printTitle("### Time-based UUID with Random");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithRandom());
		}

		printTitle("### Time-ordered UUID with Random");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithRandom());
		}

		printTitle("### Random UUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getRandomBased());
		}

		printTitle("### Prefix COMB GUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getPrefixComb());
		}

		printTitle("### Suffix COMB GUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getSuffixComb());
		}

		printTitle("### Short Prefix COMB GUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortPrefixComb());
		}

		printTitle("### Short Suffix COMB GUID");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortSuffixComb());
		}
	}

	public static void main(String[] args) {
		printList();
	}
}
