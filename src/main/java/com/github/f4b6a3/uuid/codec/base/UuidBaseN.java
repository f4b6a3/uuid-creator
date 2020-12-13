package com.github.f4b6a3.uuid.codec.base;

public enum UuidBaseN {

	BASE_16(16, 32), BASE_32(32, 26), BASE_62(62, 22), BASE_64(64, 22);

	private int number;
	private int length;

	UuidBaseN(final int number, final int length) {
		this.number = number;
		this.length = length;
	}

	public int getNumber() {
		return number;
	}

	public int getLength() {
		return length;
	}
}