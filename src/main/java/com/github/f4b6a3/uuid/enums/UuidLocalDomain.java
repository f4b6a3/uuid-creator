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

/**
 * Local domains defined by DCE 1.1 specification, used to create DCE Security
 * unique identifiers (UUIDv2).
 * <p>
 * List of local domains:
 * <ul>
 * <li>{@link LOCAL_DOMAIN_PERSON}: 0
 * <li>{@link LOCAL_DOMAIN_GROUP}: 1
 * <li>{@link LOCAL_DOMAIN_ORG}: 2
 * </ul>
 * <p>
 * On POSIX systems, local-domain numbers 0 and 1 are for user identifiers
 * (UIDs) and group identifiers (GIDs) respectively, and other local-domain
 * numbers are site-defined.
 * <p>
 * On non-POSIX systems, all local domain numbers are site-defined.
 * 
 * @see <a href=
 *      "https://pubs.opengroup.org/onlinepubs/9696989899/chap11.htm#tagcjh_14_05_01_01">DCE
 *      Domain names</a>
 * @see <a href="https://en.wikipedia.org/wiki/User_identifier">User
 *      identifier</a>
 * @see <a href="https://en.wikipedia.org/wiki/Group_identifier">Group
 *      identifier</a>
 */
public enum UuidLocalDomain {

	/**
	 * The principal domain, interpreted as POSIX UID domain on POSIX systems.
	 */
	LOCAL_DOMAIN_PERSON((byte) 0),
	/**
	 * The group domain, interpreted as POSIX GID domain on POSIX systems.
	 */
	LOCAL_DOMAIN_GROUP((byte) 1),
	/**
	 * The organization domain, site-defined.
	 */
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
