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

package com.github.f4b6a3.uuid.factory.rfc4122;

import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstNameBasedFactory;

public final class NameBasedSha1Factory extends AbstNameBasedFactory {

	/**
	 * Factory that creates name-based UUIDs (SHA1).
	 * 
	 * RFC-4122 version: 5.
	 */
	public NameBasedSha1Factory() {
		this((byte[]) null);
	}

	public NameBasedSha1Factory(UUID namespace) {
		this(bytes(namespace));
	}

	public NameBasedSha1Factory(String namespace) {
		this(bytes(namespace));
	}

	public NameBasedSha1Factory(UuidNamespace namespace) {
		this(bytes(namespace));
	}

	private NameBasedSha1Factory(byte[] namespace) {
		super(UuidVersion.VERSION_NAME_BASED_SHA1, ALGORITHM_SHA1, namespace);
	}
}
