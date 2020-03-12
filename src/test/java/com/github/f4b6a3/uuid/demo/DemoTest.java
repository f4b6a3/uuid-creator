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
			System.out.println(UuidCreator.getFastRandom());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getTimeBased());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Sequential UUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getSequential());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### COMB GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getCombGuid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### ULID-based GUID");

		for (int i = 0; i < max; i++) {
			System.out.println(UuidCreator.getUlidBasedGuid());
		}

		System.out.println(HORIZONTAL_LINE);
	}

	public static void main(String[] args) {
		printList();
	}
}
