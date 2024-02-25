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

package com.github.f4b6a3.uuid.factory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

import static com.github.f4b6a3.uuid.enums.UuidVersion.VERSION_NAME_BASED_MD5;
import static com.github.f4b6a3.uuid.enums.UuidVersion.VERSION_NAME_BASED_SHA1;

/**
 * Abstract factory for creating name-based unique identifiers (UUIDv3 and
 * UUIDv5).
 * 
 * The name space is optional for compatibility with the JDK's UUID method for
 * generating UUIDv3, which is {@link UUID#nameUUIDFromBytes(byte[])}.
 * 
 * @see UuidNamespace
 * @see <a href= "https://www.rfc-editor.org/rfc/rfc4122#section-4.3">RFC-4122 -
 *      4.3. Algorithm for Creating a Name-Based UUID</a>
 */
public abstract class AbstNameBasedFactory extends UuidFactory {

	/**
	 * The namespace (optional).
	 */
	protected byte[] namespace; // can be null
	/**
	 * The hash algorithm.
	 */
	protected final String algorithm; // MD5 or SHA-1

	/**
	 * The MD5 algorithm.
	 */
	protected static final String ALGORITHM_MD5 = "MD5";
	/**
	 * The SHA-1 algorithm.
	 */
	protected static final String ALGORITHM_SHA1 = "SHA-1";

	/**
	 * Protected constructor that receives the message digest algorithm and an
	 * optional name space.
	 * 
	 * @param version   the version number (3 or 5)
	 * @param algorithm a message digest algorithm (MD5 or SHA-1)
	 * @param namespace a name space byte array (null or 16 bytes)
	 */
	protected AbstNameBasedFactory(UuidVersion version, String algorithm, byte[] namespace) {
		super(version);

		if (!VERSION_NAME_BASED_MD5.equals(version) && !VERSION_NAME_BASED_SHA1.equals(version)) {
			throw new IllegalArgumentException("Invalid UUID version");
		}

		if (ALGORITHM_MD5.equals(algorithm) || ALGORITHM_SHA1.equals(algorithm)) {
			this.algorithm = algorithm;
		} else {
			throw new IllegalArgumentException("Invalid message digest algorithm");
		}

		if (namespace != null) {
			if (namespace.length == 16) {
				// must be 16 bytes length
				this.namespace = namespace;
			} else {
				throw new IllegalArgumentException("Invalid namespace");
			}
		}
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param name a byte array
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 */
	public UUID create(final byte[] name) {
		return create(this.namespace, nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param name a string
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 */
	public UUID create(final String name) {
		return create(this.namespace, nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a byte array
	 * @return a name-based UUID
	 * @throws IllegalArgumentException if name is null
	 */
	public UUID create(final UUID namespace, final byte[] name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a string
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 */
	public UUID create(final UUID namespace, final String name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space string
	 * @param name      a byte array
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	public UUID create(final String namespace, final byte[] name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space string
	 * @param name      a string
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	public UUID create(final String namespace, final String name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a byte array
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 */
	public UUID create(final UuidNamespace namespace, final byte[] name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a string
	 * @return a name-based UUID
	 * @throws NullPointerException if name is null
	 */
	public UUID create(final UuidNamespace namespace, final String name) {
		return create(namespaceBytes(namespace), nameBytes(name));
	}

	@Override
	public UUID create() {
		return create(Parameters.builder().build());
	}

	@Override
	public UUID create(Parameters parameters) {
		return create(parameters.getNamespace(), parameters.getName());
	}

	private UUID create(final byte[] namespace, final byte[] name) {

		Objects.requireNonNull(name, "Null name");

		MessageDigest hasher;

		try {
			hasher = MessageDigest.getInstance(this.algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		if (namespace != null) {
			// Prepend the name space
			hasher.update(namespace);
		}

		// Compute the hash of the name
		final byte[] hash = hasher.digest(name);

		final long msb = ByteUtil.toNumber(hash, 0, 8);
		final long lsb = ByteUtil.toNumber(hash, 8, 16);
		return toUuid(msb, lsb);
	}
}
