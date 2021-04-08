/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.StringCodec;
import com.github.f4b6a3.uuid.codec.uuid.DotNetGuid4Codec;
import com.github.f4b6a3.uuid.codec.uuid.TimeOrderedCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Utility that converts UUIDs to and from strings, byte arrays or other UUID
 * types.
 * 
 * @deprecated use the classes in the codec package instead.
 */
@Deprecated
public final class UuidConverter {

	private UuidConverter() {
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @deprecated use {@link BinaryCodec} instead.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 */
	@Deprecated
	public static byte[] toBytes(final UUID uuid) {
		return BinaryCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Get a UUID from an array of bytes.
	 * 
	 * @deprecated use {@link BinaryCodec} instead.
	 * 
	 * @param bytes an array of bytes
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	@Deprecated
	public static UUID fromBytes(final byte[] bytes) {
		return BinaryCodec.INSTANCE.decode(bytes);
	}

	/**
	 * Get a string from a UUID.
	 * 
	 * It is much faster than {@link UUID#toString()} in JDK 8.
	 * 
	 * In JDK9+ prefer {@link UUID#toString()}.
	 * 
	 * @deprecated use {@link StringCodec} instead.
	 * 
	 * @param uuid a UUID
	 * @return a UUID string
	 */
	@Deprecated
	public static String toString(UUID uuid) {
		return StringCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Get a UUID from a string.
	 * 
	 * It accepts strings:
	 * 
	 * - With URN prefix: "urn:uuid:";
	 * 
	 * - With curly braces: '{' and '}';
	 * 
	 * - With upper or lower case;
	 * 
	 * - With or without hyphens.
	 * 
	 * It is much faster than {@link UUID#fromString(String)} in JDK 8.
	 * 
	 * @deprecated use {@link StringCodec} instead.
	 * 
	 * @param string a UUID string
	 * @return a UUID
	 * @throws InvalidUuidException if invalid
	 */
	@Deprecated
	public static UUID fromString(String string) {
		return StringCodec.INSTANCE.decode(string);
	}

	/**
	 * Convert a time-ordered UUID to a time-based UUID.
	 * 
	 * @deprecated use {@link TimeOrderedCodec} instead.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws IllegalArgumentException if the input is not a time-ordered UUID
	 */
	@Deprecated
	public static UUID toTimeBasedUuid(UUID uuid) {
		return TimeOrderedCodec.INSTANCE.decode(uuid);
	}

	/**
	 * Convert a time-based UUID to a time-ordered UUID.
	 * 
	 * @deprecated use {@link TimeOrderedCodec} instead.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws IllegalArgumentException if the input is not a time-based UUID
	 */
	@Deprecated
	public static UUID toTimeOrderedUuid(UUID uuid) {
		return TimeOrderedCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Convert a random-based UUID (v4) to and from a MS GUID.
	 * 
	 * This method is only useful for MS SQL Server.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The Microsoft GUID format stores the most significant bytes as little-endian,
	 * while the least significant bytes are stored as big-endian (network order).
	 * 
	 * @deprecated use {@link DotNetGuid4Codec} instead.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	@Deprecated
	public static UUID toAndFromMsGuid(UUID uuid) {
		if (UuidUtil.isRandomBased(uuid)) {
			return DotNetGuid4Codec.INSTANCE.encode(uuid);
		}
		return DotNetGuid4Codec.INSTANCE.decode(uuid);
	}
}
