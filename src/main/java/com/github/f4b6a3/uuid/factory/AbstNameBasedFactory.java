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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.StringCodec;
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
 * @see UuidNamespace
 * @see <a href= "https://www.rfc-editor.org/rfc/rfc4122#section-4.3">RFC-4122 -
 *      4.3. Algorithm for Creating a Name-Based UUID</a>
 */
public abstract class AbstNameBasedFactory extends UuidFactory {

	protected byte[] namespace = null;
	protected final String algorithm; // MD5 or SHA-1

	protected static final String ALGORITHM_MD5 = "MD5";
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

		if (namespace == null) {
			// null is accepted
			this.namespace = null;
		} else {
			if (namespace.length == 16) {
				// must be 16 bytes length
				this.namespace = namespace;
			} else {
				throw new IllegalArgumentException("Invalid namespace length");
			}
		}
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param name a byte array
	 * @return a name-based UUID
	 */
	public UUID create(final byte[] name) {
		return create(this.namespace, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param name a string
	 * @return a name-based UUID
	 */
	public UUID create(final String name) {
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(this.namespace, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param name a UUID
	 * @return a name-based UUID
	 */
	public UUID create(final UUID name) {
		return create(this.namespace, bytes(name));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a byte array
	 * @return a name-based UUID
	 */
	public UUID create(final UUID namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a string
	 * @return a name-based UUID
	 */
	public UUID create(final UUID namespace, final String name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space UUID
	 * @param name      a UUID
	 * @return a name-based UUID
	 */
	public UUID create(final UUID namespace, final UUID name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = bytes(name);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space string
	 * @param name      a byte array
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	public UUID create(final String namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space string
	 * @param name      a string
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	public UUID create(final String namespace, final String name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space string
	 * @param name      a UUID
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	public UUID create(final String namespace, final UUID name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = bytes(name);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a byte array
	 * @return a name-based UUID
	 * @see UuidNamespace
	 */
	public UUID create(final UuidNamespace namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * <p>
	 * The name string is encoded into a sequence of bytes using UTF-8.
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a string
	 * @return a name-based UUID
	 * @see UuidNamespace
	 */
	public UUID create(final UuidNamespace namespace, final String name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space enumeration
	 * @param name      a UUID
	 * @return a name-based UUID
	 * @see UuidNamespace
	 */
	public UUID create(final UuidNamespace namespace, final UUID name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = bytes(name);
		return create(ns, n);
	}

	/**
	 * Converts a name space enumeration into a byte array.
	 * 
	 * @param namespace a name space enumeration
	 * @return a byte array
	 */
	protected static byte[] bytes(UuidNamespace namespace) {
		return bytes(namespace.getValue());
	}

	/**
	 * Converts a name space UUID into a byte array.
	 * 
	 * @param namespace a name space UUID
	 * @return a byte array
	 */
	protected static byte[] bytes(UUID namespace) {
		return BinaryCodec.INSTANCE.encode(namespace);
	}

	/**
	 * Converts a name space string into a byte array.
	 * 
	 * @param namespace a name space string
	 * @return a byte array
	 * @throws InvalidUuidException if the name space is invalid
	 * @see InvalidUuidException
	 */
	protected static byte[] bytes(String namespace) {
		return BinaryCodec.INSTANCE.encode(StringCodec.INSTANCE.decode(namespace));
	}

	private UUID create(final byte[] namespace, final byte[] name) {

		MessageDigest hasher;

		try {
			hasher = MessageDigest.getInstance(this.algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Message digest algorithm not available: " + this.algorithm, e);
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
