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

import static com.github.f4b6a3.uuid.enums.UuidVersion.VERSION_NAME_BASED_MD5;
import static com.github.f4b6a3.uuid.enums.UuidVersion.VERSION_NAME_BASED_SHA1;

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
public abstract class AbstNameBasedFactory extends UuidFactory {

	protected byte[] namespace = null;
	protected final String algorithm; // MD5 or SHA-1

	protected static final String ALGORITHM_MD5 = "MD5";
	protected static final String ALGORITHM_SHA1 = "SHA-1";

	/**
	 * This constructor receives the name of a message digest.
	 * 
	 * @param version   the version number (3 or 5)
	 * @param algorithm a message digest algorithm (MD5 or SHA-1)
	 * @param namespace a namespace byte array (null or 16 bytes)
	 */
	public AbstNameBasedFactory(UuidVersion version, String algorithm, byte[] namespace) {
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
	 * @param name a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(final byte[] name) {
		return create(this.namespace, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #create(byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param name a name string
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
	 * @param namespace a name space UUID (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(final UUID namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #create(UUID, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a name space UUID (optional)
	 * @param name      a name string
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
	 * @param namespace a name space UUID (optional)
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
	 * @param namespace a name space UUID in string format (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public UUID create(final String namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #create(String, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a name space UUID in string format (optional)
	 * @param name      a name string
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public UUID create(final String namespace, final String name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space UUID in string format (optional)
	 * @param name      a UUID
	 * @return a name-based UUID
	 * @throws InvalidUuidException if the namespace is invalid
	 */
	public UUID create(final String namespace, final UUID name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = bytes(name);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space enumeration (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	public UUID create(final UuidNamespace namespace, final byte[] name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		return create(ns, name);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * The name string is encoded into a sequence of bytes using the UTF-8
	 * {@linkplain java.nio.charset.Charset charset}. If you want another charset,
	 * use {@link #create(UuidNamespace, byte[])} instead.
	 * 
	 * See: UTF-8, a transformation format of ISO 10646
	 * https://tools.ietf.org/html/rfc3629
	 * 
	 * @param namespace a name space enumeration (optional)
	 * @param name      a name string
	 * @return a name-based UUID
	 */
	public UUID create(final UuidNamespace namespace, final String name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = name.getBytes(StandardCharsets.UTF_8);
		return create(ns, n);
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a name space enumeration (optional)
	 * @param name      a UUID
	 * @return a name-based UUID
	 */
	public UUID create(final UuidNamespace namespace, final UUID name) {
		final byte[] ns = namespace == null ? null : bytes(namespace);
		final byte[] n = bytes(name);
		return create(ns, n);
	}

	protected static byte[] bytes(UuidNamespace namespace) {
		return bytes(namespace.getValue());
	}

	protected static byte[] bytes(UUID namespace) {
		return BinaryCodec.INSTANCE.encode(namespace);
	}

	protected static byte[] bytes(String namespace) {
		return BinaryCodec.INSTANCE.encode(StringCodec.INSTANCE.decode(namespace));
	}

	/**
	 * Returns a name-based UUID.
	 * 
	 * @param namespace a byte array of the name space (optional)
	 * @param name      a byte array of the name
	 * @return a name-based UUID
	 */
	private UUID create(final byte[] namespace, final byte[] name) {

		MessageDigest hasher;

		try {
			// No need for a synchronized `MessageDigest`. Just get a new instance, use it,
			// and throw it away. It performs worse than synchronized instances in
			// single-threaded benchmarks, but it performs much better in multi-threaded
			// contexts. The overhead of creation is justifiable.
			hasher = MessageDigest.getInstance(this.algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Message digest algorithm not available: " + this.algorithm, e);
		}

		if (namespace != null) {
			// prepend the namespace
			hasher.update(namespace);
		}
		// Compute the hash of the name
		final byte[] hash = hasher.digest(name);

		// Set the version and variant bits
		return getUuid(hash);
	}
}
