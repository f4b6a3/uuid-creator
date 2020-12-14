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

package com.github.f4b6a3.uuid.codec.uri;

import java.net.URI;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.codec.UuidStringCodec;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

public class UuidUriCodec implements UuidCodec<java.net.URI> {

	protected static final String URN_PREFIX = "urn:uuid:";

	@Override
	public URI encode(UUID uuid) {
		return URI.create(URN_PREFIX + LazyHolder.CODEC.encode(uuid));
	}

	@Override
	public UUID decode(URI uri) {
		if (uri == null || !uri.toString().startsWith(URN_PREFIX)) {
			throw new InvalidUuidException("Invalid URI: \"" + uri + "\"");
		}
		return LazyHolder.CODEC.decode(uri.toString());
	}

	private static class LazyHolder {
		private static final UuidCodec<String> CODEC = new UuidStringCodec();
	}
}
