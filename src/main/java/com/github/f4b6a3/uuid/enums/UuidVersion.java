package com.github.f4b6a3.uuid.enums;

public enum UuidVersion {

	// UUID versions defined by RFC-4122, plus an extension (zero)
	SEQUENTIAL(0), TIME_BASED(1), DCE_SECURITY(2), NAME_BASED_MD5(3), RANDOM_BASED(4), NAMBE_BASED_SHA1(5);

	private final int value;

	UuidVersion(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
}
