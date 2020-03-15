/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.abst;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;

import static com.github.f4b6a3.commons.util.ByteUtil.*;

/**
 * Factory that creates name-based UUIDs.
 */
public abstract class AbstractNameBasedUuidCreator extends AbstractUuidCreator {

	protected UUID namespace;
	protected MessageDigest md = null;

	protected static final String MESSAGE_DIGEST_MD5 = "MD5";
	protected static final String MESSAGE_DIGEST_SHA1 = "SHA-1";
	
	/**
	 * This constructor receives the name of a message digest.
	 * 
	 * In this implementation it's possible to use ANY message digest that Java
	 * supports, but only MD5 and SHA-1 are used by the the RFC-4122.
	 * 
	 * Someone can implement a non-standard name-based factory that uses a
	 * better message digest, by extending this abstract class.
	 * 
	 * A subclass that uses the algorithm SHA-256 is provided by this library.
	 * 
	 * @param version
	 *            the version number
	 * @param messageDigest
	 *            a message digest
	 */
	public AbstractNameBasedUuidCreator(UuidVersion version, String messageDigest) {
		super(version);

		try {
			this.md = MessageDigest.getInstance(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("Message digest algorithm not supported.", e);
		}
	}
	
	/**
	 * Sets a fixed name space with in a fluent way.
	 * 
	 * @param namespace
	 *            a namespace enum
	 * @param <T>
	 *            the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(UuidNamespace namespace) {
		this.namespace = namespace.getValue();
		return (T) this;
	}

	/**
	 * Sets a fixed name space with in a fluent way.
	 * 
	 * @param namespace
	 *            a namespace UUID
	 * @param <T>
	 *            the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(UUID namespace) {
		this.namespace = namespace;
		return (T) this;
	}

	/**
	 * Sets a fixed name space with in a fluent way.
	 * 
	 * The name space string is converted to name space UUID.
	 * 
	 * @param namespace
	 *            a name space string
	 * @param <T>
	 *            the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(String namespace) {
		this.namespace = create(namespace);
		return (T) this;
	}

	/**
	 * Returns a name-based UUID without name space.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public UUID create(String name) {
		return create((UUID) null, name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID without name space.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param name
	 *            a byte array of the name in UTF8
	 * @return a name-based UUID
	 */
	public UUID create(byte[] name) {
		return create((UUID) null, name);
	}

	/**
	 * Returns a name-based UUID with a name space and a name.
	 * 
	 * The name space string is converted to name space UUID.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace
	 *            a name space string
	 * @param name
	 *            a name string
	 * @return a name-based UUID
	 */
	public UUID create(String namespace, String name) {
		UUID namespaceUUID = create(namespace);
		return create(namespaceUUID, name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID with a name space and a name.
	 * 
	 * The name space string is converted to name space UUID.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace
	 *            a name space string
	 * @param name
	 *            a byte array of the name in UTF8
	 * @return a name-based UUID
	 */
	public UUID create(String namespace, byte[] name) {
		UUID namespaceUUID = create(namespace);
		return create(namespaceUUID, name);
	}

	/**
	 * Returns a name-based UUID with a name space and a name.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace
	 *            a name space UUID
	 * @param name
	 *            a byte array of the name in UTF8
	 * @return a name-based UUID
	 */
	public UUID create(UUID namespace, String name) {
		return create(namespace, name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID with a name space and a name.
	 * 
	 * {@link UuidNamespace}.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace
	 *            a name space enumeration
	 * @param name
	 *            a byte array of the name in UTF8
	 * @return a name-based UUID
	 */
	public UUID create(UuidNamespace namespace, String name) {
		return create(namespace.getValue(), name.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * Returns a name-based UUID with a name space and a name.
	 * 
	 * ### RFC-4122 - 4.3. Algorithm for Creating a Name-Based UUID
	 * 
	 * (1) Allocate a UUID to use as a "name space ID" for all UUIDs generated
	 * from names in that name space; see Appendix C for some pre-defined
	 * values.
	 * 
	 * (2) Choose either MD5 [4] or SHA-1 [8] as the hash algorithm; If backward
	 * compatibility is not an issue, SHA-1 is preferred.
	 * 
	 * (3) Convert the name to a canonical sequence of octets (as defined by the
	 * standards or conventions of its name space); put the name space ID in
	 * network byte order.
	 * 
	 * (4) Compute the hash of the name space ID concatenated with the name.
	 * 
	 * (5) Set octets zero through 3 of the time_low field to octets zero
	 * through 3 of the hash.
	 * 
	 * (6) Set octets zero and one of the time_mid field to octets 4 and 5 of
	 * the hash.
	 * 
	 * (7) Set octets zero and one of the time_hi_and_version field to octets 6
	 * and 7 of the hash.
	 * 
	 * (8) Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the appropriate 4-bit version number from
	 * Section 4.1.3.
	 * 
	 * (9) Set the clock_seq_hi_and_reserved field to octet 8 of the hash.
	 * 
	 * (10) Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * (11) Set the clock_seq_low field to octet 9 of the hash.
	 * 
	 * (12) Set octets zero through five of the node field to octets 10 through
	 * 15 of the hash.
	 * 
	 * (13) Convert the resulting UUID to local byte order.
	 * 
	 * @param namespace
	 *            a name space UUID
	 * @param name
	 *            a byte array of the name in UTF8
	 * @return a name-based UUID
	 */
	public UUID create(final UUID namespace, final byte[] name) {

		final byte[] hash;

		synchronized (md) {
			md.reset();
			if (namespace != null) {
				md.update(toBytes(namespace.getMostSignificantBits()));
				md.update(toBytes(namespace.getLeastSignificantBits()));
			} else if (this.namespace != null) {
				md.update(toBytes(this.namespace.getMostSignificantBits()));
				md.update(toBytes(this.namespace.getLeastSignificantBits()));
			}
			hash = md.digest(name);
		}

		long msb = toNumber(hash, 0, 8);
		long lsb = toNumber(hash, 8, 16);

		msb = setVersionBits(msb);
		lsb = setVariantBits(lsb);

		return new UUID(msb, lsb);
	}
}
