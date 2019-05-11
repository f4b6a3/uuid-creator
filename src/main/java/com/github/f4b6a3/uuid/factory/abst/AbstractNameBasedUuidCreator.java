/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.factory.abst;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;

import static com.github.f4b6a3.uuid.util.ByteUtil.*;

/**
 * Factory that creates name-based UUIDs versions 3 and 5.
 */
public abstract class AbstractNameBasedUuidCreator extends AbstractUuidCreator {

	private UUID namespace;
	private MessageDigest md = null;

	protected static final String MESSAGE_DIGEST_MD5 = "MD5";
	protected static final String MESSAGE_DIGEST_SHA1 = "SHA-1";
	protected static final String MESSAGE_DIGEST_SHA256 = "SHA-256";

	/**
	 * This constructor receives the name of a message digest.
	 * 
	 * In this implementation it's possible to use ANY message digest that Java
	 * supports, but only MD5 and SHA-1 are used by the the RFC-4122.
	 * 
	 * Someone can implement a non-standard name-based factory that uses a
	 * better message digest, by extending this abstract class.
	 * 
	 * @param version the version number
	 * @param messageDigest a message digest
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
	 * Sets a fixed namespace with in a fluent way.
	 * 
	 * @param namespace a namespace UUID
	 * @param <T> the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractNameBasedUuidCreator> T withNamespace(UUID namespace) {
		this.namespace = namespace;
		return (T) this;
	}

	/**
	 * Sets a fixed namespace with in a fluent way.
	 * 
	 * The namespace string is converted to namespace UUID.
	 * 
	 * @param namespace a namespace string
	 * @param <T> the type parameter
	 * @return {@link AbstractNameBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractNameBasedUuidCreator> T withNamespace(String namespace) {
		UUID namespaceUUID = create(namespace);
		this.namespace = namespaceUUID;
		System.out.println(this.namespace.toString());
		return (T) this;
	}

	/**
	 * Returns a name-based UUID without namespace.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, String)}
	 * 
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public UUID create(String name) {
		return create((UUID) null, name);
	}

	/**
	 * Returns a name-based UUID with a namespace and a name.
	 * 
	 * The namespace string is converted to namespace UUID.
	 * 
	 * {@link AbstractNameBasedUuidCreator#create(UUID, String)}
	 * 
	 * @param namespace a namespace string
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public UUID create(String namespace, String name) {
		UUID namespaceUUID = create(namespace);
		return create(namespaceUUID, name);
	}

	/**
	 * Returns a name-based UUID with a namespace and a name.
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
	 * @param namespace a namespace UUID
	 * @param name a name string
	 * @return a name-based UUID
	 */
	public synchronized UUID create(UUID namespace, String name) {

		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;

		byte[] namespaceBytes = null;
		byte[] nameBytes = null;
		byte[] bytes = null;
		byte[] hash = null;

		nameBytes = name.getBytes();

		if (namespace != null) {
			namespaceBytes = toBytes(namespace.getMostSignificantBits());
			namespaceBytes = concat(namespaceBytes, toBytes(namespace.getLeastSignificantBits()));
		} else if (this.namespace != null) {
			namespaceBytes = toBytes(this.namespace.getMostSignificantBits());
			namespaceBytes = concat(namespaceBytes, toBytes(this.namespace.getLeastSignificantBits()));
		} else {
			namespaceBytes = new byte[0];
		}

		bytes = concat(namespaceBytes, nameBytes);

		hash = md.digest(bytes);

		msb = toNumber(copy(hash, 0, 8));
		lsb = toNumber(copy(hash, 8, 16));

		lsb = setVariantBits(lsb);
		msb = setVersionBits(msb);

		return new UUID(msb, lsb);
	}
}
