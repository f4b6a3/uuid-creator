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

/**
 * Concrete factory for creating name-based unique identifiers using MD5 hashing
 * (UUIDv3).
 * 
 * @see AbstNameBasedFactory
 */
public final class NameBasedMd5Factory extends AbstNameBasedFactory {

	/**
	 * Default constructor.
	 */
	public NameBasedMd5Factory() {
		this((byte[]) null);
	}

	/**
	 * Constructor with a namespace.
	 * 
	 * @param namespace a namespace
	 */
	public NameBasedMd5Factory(UUID namespace) {
		this(namespaceBytes(namespace));
	}

	/**
	 * Constructor with a namespace.
	 * 
	 * @param namespace a namespace
	 */
	public NameBasedMd5Factory(String namespace) {
		this(namespaceBytes(namespace));
	}

	/**
	 * Constructor with a namespace.
	 * 
	 * @param namespace a namespace
	 */
	public NameBasedMd5Factory(UuidNamespace namespace) {
		this(namespaceBytes(namespace));
	}

	private NameBasedMd5Factory(byte[] namespace) {
		super(UuidVersion.VERSION_NAME_BASED_MD5, ALGORITHM_MD5, namespace);
	}
}
