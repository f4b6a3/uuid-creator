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

		printTitle("### Time-based");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBased());
		}

		printTitle("### Time-ordered");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrdered());
		}

		printTitle("### Time-based with Mac");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithMac());
		}

		printTitle("### Time-ordered with Mac");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithMac());
		}

		printTitle("### Time-based with Hash");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithHash());
		}

		printTitle("### Time-ordered with Hash");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithHash());
		}

		printTitle("### Time-based with Random");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBasedWithRandom());
		}

		printTitle("### Time-ordered with Random");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedWithRandom());
		}

		printTitle("### Random-based");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getRandomBased());
		}

		printTitle("### Prefix COMB");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getPrefixComb());
		}

		printTitle("### Suffix COMB");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getSuffixComb());
		}

		printTitle("### Short Prefix COMB");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortPrefixComb());
		}

		printTitle("### Short Suffix COMB");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getShortSuffixComb());
		}

		printTitle("### Time-ordered Epoch");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedEpoch());
		}

		printTitle("### Time-ordered Epoch Plus 1");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedEpochPlus1());
		}

		printTitle("### Time-ordered Epoch Plus N");
		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeOrderedEpochPlusN());
		}
	}

	public static void main(String[] args) {
		printList();
	}
}
