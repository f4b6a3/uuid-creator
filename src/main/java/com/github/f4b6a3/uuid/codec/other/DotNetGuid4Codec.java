/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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

package com.github.f4b6a3.uuid.codec.other;

import static com.github.f4b6a3.uuid.codec.other.DotNetGuid1Codec.toAndFromDotNetGuid;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidUtil;
import com.github.f4b6a3.uuid.util.UuidValidator;

/**
 * Codec for random-based .Net Guids.
 */
public class DotNetGuid4Codec implements UuidCodec<UUID> {

	/**
	 * A shared immutable instance.
	 */
	public static final DotNetGuid4Codec INSTANCE = new DotNetGuid4Codec();

	/**
	 * Get a .Ned Guid from a random-based UUID (v4).
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The .Net Guid stores the most significant bytes as little-endian, while the
	 * least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID encode(UUID uuid) {
		UuidValidator.validate(uuid);
		if (!UuidUtil.isRandomBased(uuid)) {
			throw new InvalidUuidException(String.format("Not a random-based UUID: %s.", uuid.toString()));
		}
		return toAndFromDotNetGuid(uuid);
	}

	/**
	 * Get a random-based UUID (v4) from a .Net Guid.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The .Net Guid stores the most significant bytes as little-endian, while the
	 * least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(UUID uuid) {
		UuidValidator.validate(uuid);
		UUID uuidv4 = toAndFromDotNetGuid(uuid);
		if (!UuidUtil.isRandomBased(uuidv4)) {
			throw new InvalidUuidException(String.format("Not a random-based UUID: %s.", uuidv4.toString()));
		}
		return uuidv4;
	}
}
