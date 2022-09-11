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

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Codec for UUID URIs (specifically URNs).
 * <p>
 * {@link UriCodec} encodes UUID to and from an opaque {@link java.net.URI}.
 * <p>
 * The URN representation adds the prefix 'urn:uuid:' to a UUID canonical
 * representation.
 * 
 * See: https://github.com/f4b6a3/uuid-creator/issues/32
 * 
 * 
 *  * @see InvalidUuidException
 * @see <a href=
 *      "https://github.com/f4b6a3/uuid-creator/issues/32">UUID URIs</a>
 * @see <a href=
 *      "https://github.com/f4b6a3/uuid-creator/issues/66">UriCodec.isUuidUrn(java.net.URI
 *      uri)</a>
 * @see <a href=
 *      "https://stackoverflow.com/questions/4913343/what-is-the-difference-between-uri-url-and-urn">What
 *      is the difference between URI, URL and URN?</a>
 */
public class UriCodec implements UuidCodec<URI> {

	/**
	 * A shared immutable instance.
	 */
	public static final UriCodec INSTANCE = new UriCodec();

	/**
	 * Get a URI from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return a URI
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public URI encode(UUID uuid) {
		return URI.create(UrnCodec.INSTANCE.encode(uuid));
	}

	/**
	 * Get a UUID from a URI.
	 * 
	 * @param uri a URI
	 * @return a UUID
	 * @throws InvalidUuidException if the argument is invalid
	 */
	@Override
	public UUID decode(URI uri) {
		if (!isUuidUri(uri)) {
			throw InvalidUuidException.newInstance(uri);
		}
		return StringCodec.INSTANCE.decode(uri.toString());
	}

	/**
	 * Check if the URI is a UUID URN.
	 * 
	 * @param uri a URI
	 * @return true if the it's a URN
	 */
	public static boolean isUuidUri(URI uri) {
		return uri != null && UrnCodec.isUuidUrn(uri.toString());
	}
}
