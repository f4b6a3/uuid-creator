package com.github.f4b6a3.uuid.enums;

public enum UuidVariant {

	// UUID variants defined by RFC-4122
	VARIANT_RESERVED_NCS(0), VARIANT_RFC4122(2), VARIANT_RESERVED_MICROSOFT(6), VARIANT_RESERVED_FUTURE(7);

	private final int value;

	UuidVariant(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
