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

package com.github.f4b6a3.uuid.enums;

import java.util.UUID;

/**
 * Name spaces used to create name-based UUIDs.
 * 
 * <pre>
 * Name spaces predefined by RFC-4122 (Appendix C):
 * 
 * - NAMESPACE_DNS: Name string is a fully-qualified domain name;
 * - NAMESPACE_URL: Name string is a URL;
 * - NAMESPACE_ISO_OID: Name string is an ISO OID;
 * - NAMESPACE_X500_DN: Name string is an X.500 DN (in DER or a text format).
 * </pre>
 * 
 * Source:
 * 
 * RFC-4122 - Appendix C - Some Name Space IDs
 * https://tools.ietf.org/html/rfc4122#appendix-C
 */
public enum UuidNamespace {

	NAMESPACE_DNS(new UUID(0x6ba7b8109dad11d1L, 0x80b400c04fd430c8L)), // Domain Name System
	NAMESPACE_URL(new UUID(0x6ba7b8119dad11d1L, 0x80b400c04fd430c8L)), // Uniform Resource Locator
	NAMESPACE_OID(new UUID(0x6ba7b8129dad11d1L, 0x80b400c04fd430c8L)), // ISO Object ID
	NAMESPACE_X500(new UUID(0x6ba7b8149dad11d1L, 0x80b400c04fd430c8L)); // X.500 Distinguished Name

	private final UUID value;

	UuidNamespace(UUID value) {
		this.value = value;
	}

	public UUID getValue() {
		return this.value;
	}

	public static UuidNamespace getNamespace(UUID value) {
		for (UuidNamespace namespace : UuidNamespace.values()) {
			if (namespace.getValue().equals(value)) {
				return namespace;
			}
		}
		return null;
	}
}
