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

package com.github.f4b6a3.uuid.codec;

import java.util.UUID;

import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidValidator;

/**
 * Codec for UUID URNs.
 * 
 * The {@link UrnCodec} encodes UUID to and from an URN.
 * 
 * The URN representation adds the prefix 'urn:uuid:' to a UUID canonical
 * representation.
 * 
 * See: https://github.com/f4b6a3/uuid-creator/issues/66
 * 
 * See also:
 * https://stackoverflow.com/questions/4913343/what-is-the-difference-between-uri-url-and-urn
 */
public class UrnCodec implements UuidCodec<String> {

	/**
	 * A shared immutable instance.
	 */
	public static final UrnCodec INSTANCE = new UrnCodec();

	private static final String URN_PREFIX = "urn:uuid:";

	/**
	 * Get a URN string from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return a URN string
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public String encode(UUID uuid) {
		UuidValidator.validate(uuid);
		return URN_PREFIX + StringCodec.INSTANCE.encode(uuid);
	}

	/**
	 * Get a UUID from a URN string.
	 * 
	 * @param urn a URN string
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(String urn) {
		if (!isUuidUrn(urn)) {
			throw InvalidUuidException.newInstance(urn);
		}
		return StringCodec.INSTANCE.decode(urn);
	}

	/**
	 * Check if a URN string is a UUID URN.
	 * 
	 * @param urn a string
	 * @return true if the it's a URN
	 */
	public static boolean isUuidUrn(String urn) {
		if (urn != null && urn.startsWith(URN_PREFIX)) {
			String uuid = urn.substring(URN_PREFIX.length());
			return UuidValidator.isValid(uuid);
		}
		return false;
	}
}
