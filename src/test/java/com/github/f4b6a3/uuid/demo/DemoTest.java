package com.github.f4b6a3.uuid.demo;

import com.github.f4b6a3.uuid.UuidCreator;

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

	public static void main(String[] args) {
		printList();
	}
}
