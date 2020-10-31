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

package com.github.f4b6a3.uuid.creator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.UuidConverter;

/**
 * Factory that creates name-based UUIDs.
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
 * Sources:
 * 
 * RFC-4122 - 4.3. Algorithm for Creating a Name-Based UUID
 * https://tools.ietf.org/html/rfc4122#section-4.3
 * 
 * RFC-4122 - Appendix C - Some Name Space IDs
 * https://tools.ietf.org/html/rfc4122#appendix-C
 */
public abstract class AbstractNameBasedUuidCreator extends AbstractUuidCreator {

	protected byte[] namespace = null;
	protected final String algorithm; // MD5 or SHA-1

	protected static final String MESSAGE_DIGEST_MD5 = "MD5";
	protected static final String MESSAGE_DIGEST_SHA1 = "SHA-1";

	/**
	 * This constructor receives the name of a message digest.
	 * 
	 * @param version   the version number
	 * @param algorithm a message digest algorithm
	 */
	public AbstractNameBasedUuidCreator(UuidVersion version, String algorithm) {
		super(version);
		this.algorithm = algorithm;
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The (optional) namespace to be used is set by
	 * {@link AbstractNameBasedUuidCreator#withNamespace(UUID)}
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param name a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(byte[] name) {
		return create(this.namespace, name);
	}

	/**
	 * Returns a name-based UUID.
	 *
	 * The (optional) namespace to be used is set by
	 * {@link AbstractNameBasedUuidCreator#withNamespace(UUID)}
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public UUID create(String name) {
		return create(this.namespace, name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * ### RFC-4122 - 4.3. Algorithm for Creating a Name-Based UUID
	 * 
	 * (1) Allocate a UUID to use as a "name space ID" for all UUIDs generated from
	 * names in that name space; see Appendix C for some pre-defined values.
	 * 
	 * (2) Choose either MD5 [4] or SHA-1 [8] as the hash algorithm; If backward
	 * compatibility is not an issue, SHA-1 is preferred.
	 * 
	 * (3) Convert the name to a canonical sequence of octets (as defined by the
	 * standards or conventions of its name space); put the name space ID in network
	 * byte order.
	 * 
	 * (4) Compute the hash of the name space ID concatenated with the name.
	 * 
	 * (5) Set octets zero through 3 of the time_low field to octets zero through 3
	 * of the hash.
	 * 
	 * (6) Set octets zero and one of the time_mid field to octets 4 and 5 of the
	 * hash.
	 * 
	 * (7) Set octets zero and one of the time_hi_and_version field to octets 6 and
	 * 7 of the hash.
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
	 * (12) Set octets zero through five of the node field to octets 10 through 15
	 * of the hash.
	 * 
	 * (13) Convert the resulting UUID to local byte order.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(UUID namespace, byte[] name) {
		return create(UuidConverter.toBytes(namespace), name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a name space UUID (optional)
	 * @param name      a name string
	 * @return a name-based UUID
	 */
	public UUID create(UUID namespace, String name) {
		return create(UuidConverter.toBytes(namespace), name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a name space UUID in string format (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public UUID create(String namespace, byte[] name) {
		return create(UuidConverter.fromString(namespace), name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a name space UUID in string format (optional)
	 * @param name      a name string
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public UUID create(String namespace, String name) {
		return create(UuidConverter.fromString(namespace), name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a name space enumeration (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(UuidNamespace namespace, byte[] name) {
		return create(namespace.getValue(), name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a name space enumeration (optional)
	 * @param name      a name string
	 * @return a name-based UUID
	 */
	public UUID create(UuidNamespace namespace, String name) {
		return create(namespace.getValue(), name.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * See: {@link AbstractNameBasedUuidCreator#create(UUID, byte[])}
	 * 
	 * @param namespace a byte array of the name space (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	private UUID create(final byte[] namespace, final byte[] name) {

		byte[] hash;
		MessageDigest hasher;

		try {
			hasher = MessageDigest.getInstance(this.algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Message digest algorithm not available", e);
		}

		// Compute the hash of the name space concatenated with the name
		if (namespace != null) {
			hasher.update(namespace);
		}
		hash = hasher.digest(name);

		// Set the version and variant bits
		return getUuid(hash);
	}

	/**
	 * Sets a fixed name space to be used by default.
	 *
	 * The namespace provided will be used by the method
	 * {@link AbstractNameBasedUuidCreator#create(String)}
	 * 
	 * @param namespace a namespace UUID
	 * @param <T>       the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(UUID namespace) {
		this.namespace = UuidConverter.toBytes(namespace);
		return (T) this;
	}

	/**
	 * Sets a fixed name space to be used by default.
	 * 
	 * The namespace provided will be used by the method
	 * {@link AbstractNameBasedUuidCreator#create(String)}
	 * 
	 * @param namespace a namespace UUID in string format
	 * @param <T>       the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(String namespace) {
		this.namespace = UuidConverter.toBytes(UuidConverter.fromString(namespace));
		return (T) this;
	}

	/**
	 * Sets a fixed name space to be used by default.
	 * 
	 * See: {@link UuidNamespace}.
	 * 
	 * The namespace provided will be used by the method
	 * {@link AbstractNameBasedUuidCreator#create(String)}
	 * 
	 * @param namespace a namespace enumeration
	 * @param <T>       the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractNameBasedUuidCreator> T withNamespace(UuidNamespace namespace) {
		this.namespace = UuidConverter.toBytes(namespace.getValue());
		return (T) this;
	}
}
