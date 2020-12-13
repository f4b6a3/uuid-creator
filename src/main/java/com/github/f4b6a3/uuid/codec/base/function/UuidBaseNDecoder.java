package com.github.f4b6a3.uuid.codec.base.function;

import java.util.UUID;
import java.util.function.Function;

import com.github.f4b6a3.uuid.codec.base.UuidBaseN;
import com.github.f4b6a3.uuid.exception.UuidCodecException;

public abstract class UuidBaseNDecoder implements Function<String, UUID> {

	protected final UuidBaseN base;

	protected final char[] alphabet;
	protected final long[] alphabetValues = new long[128];

	public UuidBaseNDecoder(UuidBaseN base, char[] alphabet) {

		this.base = base;
		this.alphabet = alphabet;

		// Initiate all alphabet values with -1
		for (int i = 0; i < this.alphabetValues.length; i++) {
			this.alphabetValues[i] = -1;
		}

		// Set the alphabet values
		for (int i = 0; i < alphabet.length; i++) {
			this.alphabetValues[alphabet[i]] = i;
		}
	}

	protected char[] toCharArray(String string) {
		char[] chars = string == null ? null : string.toCharArray();
		validate(chars);
		return chars;
	}

	protected void validate(char[] chars) {
		if (chars == null || chars.length != base.getLength()) {
			throw new UuidCodecException("Invalid string: \"" + (chars == null ? null : new String(chars)) + "\"");
		}
		for (int i = 0; i < chars.length; i++) {
			boolean found = false;
			for (int j = 0; j < alphabet.length; j++) {
				if (chars[i] == alphabet[j]) {
					found = true;
				}
			}
			if (!found) {
				throw new UuidCodecException("Invalid string: \"" + (new String(chars)) + "\"");
			}
		}
	}
}