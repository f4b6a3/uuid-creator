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

package com.github.f4b6a3.uuid.enums;

/**
 * Local domains used to create DCE Security UUIDs.
 * 
 * <pre>
 * Local domains predefined by DCE 1.1 Authentication and Security Services (Chapter 11):
 * 
 * - LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
 * - LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
 * - LOCAL_DOMAIN_ORG: 2.
 * </pre>
 * 
 * Source:
 * 
 * DCE 1.1: Authentication and Security Services (Chapter 11 - domain names):
 * https://pubs.opengroup.org/onlinepubs/9696989899/chap11.htm#tagcjh_14_05_01_01
 */
public enum UuidLocalDomain {

	LOCAL_DOMAIN_PERSON((byte) 0), // POSIX UID domain
	LOCAL_DOMAIN_GROUP((byte) 1), // POSIX GID domain
	LOCAL_DOMAIN_ORG((byte) 2);

	private final byte value;

	UuidLocalDomain(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return this.value;
	}

	public static UuidLocalDomain getLocalDomain(byte value) {
		for (UuidLocalDomain domain : UuidLocalDomain.values()) {
			if (domain.getValue() == value) {
				return domain;
			}
		}
		return null;
	}
}
