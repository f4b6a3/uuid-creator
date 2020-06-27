package com.github.f4b6a3.uuid.util;

import org.junit.Test;

import static com.github.f4b6a3.uuid.util.ByteUtil.*;
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
	public void testConcat() {
		for (int i = 0; i < bytes.length - 1; i++) {
			String string1 = Long.toHexString(numbers[i]);
			String string2 = Long.toHexString(numbers[i + 1]);

			byte[] bytes1 = (string1 + string2).getBytes();
			byte[] bytes2 = concat(string1.getBytes(), string2.getBytes());

			assertEquals(bytes1.length, bytes2.length);

			for (int j = 0; j < bytes1.length; j++) {
				if (bytes1[j] != bytes2[j]) {
					fail();
				}
			}
		}
	}
	
	@Test
	public void testToBytes() {

		for (int i = 0; i < bytes.length; i++) {
			byte[] bytes1 = toBytes(numbers[i]);

			assertEquals(bytes[i].length, bytes1.length);

			for (int j = 0; j < bytes[j].length; j++) {
				if (bytes1[j] != bytes[i][j]) {
					fail();
				}
			}
		}
	}

	@Test
	public void testCopy() {
		for (int i = 0; i < bytes.length; i++) {
			byte[] bytes1 = copy(bytes[i]);

			assertEquals(bytes[i].length, bytes1.length);

			for (int j = 0; j < bytes[i].length; j++) {
				if (bytes1[j] != bytes[i][j]) {
					fail();
				}
			}
		}
	}
	
	@Test
	public void testArray() {
		
		for (int i = 0; i < bytes.length; i++) {
			byte[] bytes1 = array(bytes[0].length, bytes[i][bytes[i].length - 1]);
			
			assertEquals(bytes[0].length, bytes1.length);
			
			for (int j = 0; j < bytes[0].length; j++) {
				if (bytes1[j] != bytes[i][bytes[i].length - 1]) {
					fail();
				}
			}
		}
	}
	
	@Test
	public void testToNumberFromBytes() {
		for (int i = 0; i < numbers.length; i++) {
			assertEquals(numbers[i], toNumber(bytes[i]));
		}
	}

	@Test
	public void testToBytesFromHexadecimals() {
		for (int i = 0; i < bytes.length; i++) {
			assertTrue(equalArrays(bytes[i], toBytes(hexadecimals[i])));
		}
	}

	@Test
	public void testToHexadecimalFromBytes() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(hexadecimals[i], toHexadecimal(bytes[i]));
		}
	}
	
	@Test
	public void testToHexadecimalFromNumbers() {
		for (int i = 0; i < numbers.length; i++) {
			assertEquals(hexadecimals[i], toHexadecimal(numbers[i]));
		}
	}
	
	@Test
	public void testToHexadecimalCharsFromNumbers() {
		for (int i = 0; i < numbers.length; i++) {
			assertEquals(hexadecimals[i], new String(toHexadecimalChars(numbers[i])));
		}
	}

	@Test
	public void testToNumberFromHexadecimal() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], toNumber(hexadecimals[i]));
		}
	}
	
	@Test
	public void testToNumberFromChars() {
		for (int i = 0; i < hexadecimals.length; i++) {
			assertEquals(numbers[i], toNumber(hexadecimals[i].toCharArray()));
		}
	}
}
