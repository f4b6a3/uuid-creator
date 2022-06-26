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
 * Codec for UUID binary encoding as defined in the RFC-4122.
 * 
 * The UUID is encoded as 16 octets (bytes).
 * 
 * Read: https://tools.ietf.org/html/rfc4122
 * 
 * Read also:
 * https://en.wikipedia.org/wiki/Universally_unique_identifier#Encoding
 */
public class BinaryCodec implements UuidCodec<byte[]> {

	/**
	 * A shared immutable instance.
	 */
	public static final BinaryCodec INSTANCE = new BinaryCodec();

	/**
	 * Get an array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public byte[] encode(final UUID uuid) {

		UuidValidator.validate(uuid);

		final byte[] bytes = new byte[16];
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();

		bytes[0x0] = (byte) (msb >>> 56);
		bytes[0x1] = (byte) (msb >>> 48);
		bytes[0x2] = (byte) (msb >>> 40);
		bytes[0x3] = (byte) (msb >>> 32);
		bytes[0x4] = (byte) (msb >>> 24);
		bytes[0x5] = (byte) (msb >>> 16);
		bytes[0x6] = (byte) (msb >>> 8);
		bytes[0x7] = (byte) (msb);

		bytes[0x8] = (byte) (lsb >>> 56);
		bytes[0x9] = (byte) (lsb >>> 48);
		bytes[0xa] = (byte) (lsb >>> 40);
		bytes[0xb] = (byte) (lsb >>> 32);
		bytes[0xc] = (byte) (lsb >>> 24);
		bytes[0xd] = (byte) (lsb >>> 16);
		bytes[0xe] = (byte) (lsb >>> 8);
		bytes[0xf] = (byte) (lsb);

		return bytes;
	}

	/**
	 * Get a UUID from an array of bytes.
	 * 
	 * @param bytes an array of bytes
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(final byte[] bytes) {

		UuidValidator.validate(bytes);

		long msb = 0;
		long lsb = 0;

		msb |= (bytes[0x0] & 0xffL) << 56;
		msb |= (bytes[0x1] & 0xffL) << 48;
		msb |= (bytes[0x2] & 0xffL) << 40;
		msb |= (bytes[0x3] & 0xffL) << 32;
		msb |= (bytes[0x4] & 0xffL) << 24;
		msb |= (bytes[0x5] & 0xffL) << 16;
		msb |= (bytes[0x6] & 0xffL) << 8;
		msb |= (bytes[0x7] & 0xffL);

		lsb |= (bytes[0x8] & 0xffL) << 56;
		lsb |= (bytes[0x9] & 0xffL) << 48;
		lsb |= (bytes[0xa] & 0xffL) << 40;
		lsb |= (bytes[0xb] & 0xffL) << 32;
		lsb |= (bytes[0xc] & 0xffL) << 24;
		lsb |= (bytes[0xd] & 0xffL) << 16;
		lsb |= (bytes[0xe] & 0xffL) << 8;
		lsb |= (bytes[0xf] & 0xffL);

		return new UUID(msb, lsb);
	}
}
