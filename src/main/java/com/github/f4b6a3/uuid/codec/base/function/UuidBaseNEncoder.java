package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;
import java.util.function.Function;

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;

public abstract class UuidBaseNEncoder implements Function<UUID, String> {

	protected final UuidBaseN base;
	protected final char[] alphabet;

	public UuidBaseNEncoder(UuidBaseN base, char[] alphabet) {
		this.base = base;
		this.alphabet = alphabet;
	}
}
