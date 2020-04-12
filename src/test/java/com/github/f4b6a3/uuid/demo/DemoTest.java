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

		System.out.println();
		System.out.println("### Random UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getFastRandomBased());
		}

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
		System.out.println("### COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getCombGuid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Alt COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getAltCombGuid());
		}
		
		System.out.println(HORIZONTAL_LINE);
	}

	public static void main(String[] args) {
		printList();
	}
}
