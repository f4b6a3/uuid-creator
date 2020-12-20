package com.github.f4b6a3.uuid.util.internal;

import org.junit.Test;

import static com.github.f4b6a3.uuid.util.internal.ByteUtil.*;
import static org.junit.Assert.*;

public class ByteUtilTest {

	private long[] numbers = { 0x0000000000000000L, 0x0000000000000001L, 0x0000000000000012L, 0x0000000000000123L,
			0x0000000000001234L, 0x0000000000012345L, 0x0000000000123456L, 0x0000000001234567L, 0x0000000012345678L,
			0x0000000123456789L, 0x000000123456789aL, 0x00000123456789abL, 0x0000123456789abcL, 0x000123456789abcdL,
			0x00123456789abcdeL, 0x0123456789abcdefL };

	private String[] hexadecimals = { "0000000000000000", "0000000000000001", "0000000000000012", "0000000000000123",
			"0000000000001234", "0000000000012345", "0000000000123456", "0000000001234567", "0000000012345678",
			"0000000123456789", "000000123456789a", "00000123456789ab", "0000123456789abc", "000123456789abcd",
			"00123456789abcde", "0123456789abcdef" };

	private byte[][] bytes = {
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89 },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab },
			{ (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc },
			{ (byte) 0x00, (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd },
			{ (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde },
			{ (byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef }
			};

	@Test
	public void testToNumberFromBytes() {
		for (int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], toNumber(bytes[i]));
		}
	}

	@Test
	public void testToHexadecimalFromBytes() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], toHexadecimal(bytes[i]));
		}
	}
}
