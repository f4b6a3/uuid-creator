package com.github.f4b6a3.uuid.enums;

import java.util.UUID;

public enum UuidNamespace {
	
	// UUIDs name spaces defined by RFC-4122
	NAMESPACE_DNS(new UUID(0x6ba7b8109dad11d1L, 0x80b400c04fd430c8L)), 
	NAMESPACE_URL(new UUID(0x6ba7b8119dad11d1L, 0x80b400c04fd430c8L)), 
	NAMESPACE_OID(new UUID(0x6ba7b8129dad11d1L, 0x80b400c04fd430c8L)),
	NAMESPACE_X500(new UUID(0x6ba7b8149dad11d1L, 0x80b400c04fd430c8L));

	private final UUID value;

	UuidNamespace(UUID value) {
		this.value = value;
	}

	public UUID getValue() {
		return this.value;
	}
}
