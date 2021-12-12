/*
 * MIT License
 * 
 * Copyright (c) 2018-2021 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.github.f4b6a3.uuid.util.internal.NetworkUtil;

import static com.github.f4b6a3.uuid.util.UuidUtil.setVersion;
import static com.github.f4b6a3.uuid.util.internal.ByteUtil.toHexadecimal;
import static com.github.f4b6a3.uuid.util.internal.ByteUtil.toNumber;

/**
 * Utility class to generate machine ID.
 * 
 * It works in three steps:
 * 
 * 1. Create a string containing host name, MAC and IP;
 * 
 * 2. Create a hash of the string using SHA-256 algorithm;
 * 
 * 3. Create the identifier using part of the resulting hash.
 * 
 * If the host name, MAC or IP changes, the identifier will also change.
 */
public final class MachineId {

	private static Long id;
	private static UUID uuid;
	private static String hexa;
	private static byte[] hash;
	private static String string;

	private MachineId() {
	}

	/**
	 * Returns a number generated from the machine hash.
	 * 
	 * It uses the first 8 bytes of the machine hash.
	 * 
	 * @return a number
	 */
	public static long getMachineId() {
		if (id == null) {
			final byte[] bytes = getMachineHash();
			id = toNumber(bytes, 0, 8);
		}
		return id;
	}

	/**
	 * Returns a UUID generated from the machine hash.
	 * 
	 * It uses the first 16 bytes of the machine hash.
	 * 
	 * The UUID version is 4.
	 * 
	 * @return a UUID
	 */
	public static UUID getMachineUuid() {
		if (uuid == null) {
			final byte[] bytes = getMachineHash();
			final long msb = toNumber(bytes, 0, 8);
			final long lsb = toNumber(bytes, 8, 16);
			uuid = setVersion(new UUID(msb, lsb), 4);
		}
		return uuid;
	}

	/**
	 * Returns the machine hash in hexadecimal format.
	 * 
	 * The returning string has 64 chars.
	 * 
	 * @return a string
	 */
	public static String getMachineHexa() {
		if (hexa == null) {
			final byte[] bytes = getMachineHash();
			hexa = toHexadecimal(bytes);
		}
		return hexa;
	}

	/**
	 * Returns the machine hash in a byte array.
	 * 
	 * The returning array has 32 bytes (256 bits).
	 * 
	 * @return a byte array
	 */
	public static byte[] getMachineHash() {
		if (hash == null) {
			try {
				final String string = getMachineString();
				hash = MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8));
			} catch (NoSuchAlgorithmException e) {
				throw new InternalError("Message digest algorithm not supported.", e);
			}

		}
		return hash;
	}

	/**
	 * Returns a string containing host name, MAC and IP.
	 * 
	 * Output format: "hostname123 11-22-33-44-55-66 123.123.123.123"
	 * 
	 * Note: a network interface may have more than one IP address.
	 * 
	 * @return a string
	 */
	public static String getMachineString() {

		if (string == null) {
			string = NetworkUtil.getMachineString();
		}

		return string;
	}
}
