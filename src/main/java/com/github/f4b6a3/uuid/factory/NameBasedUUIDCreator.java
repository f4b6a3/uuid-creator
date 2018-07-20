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

package com.github.f4b6a3.uuid.factory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import static com.github.f4b6a3.uuid.util.ByteUtils.*;

/**
 * Factory that creates name-based UUIDs versions 3 and 5.
 * 
 * @author fabiolimace
 *
 */
public class NameBasedUUIDCreator extends UUIDCreator {

	private static final long serialVersionUID = -1626930139360985025L;

	/*
	 * ------------------------- 
	 * Private fields 
	 * -------------------------
	 */
	private UUID fixedNamespace;
	private MessageDigest md = null;

	/*
	 * ------------------------- 
	 * Public constructors 
	 * -------------------------
	 */
	public NameBasedUUIDCreator(int version) {
		super(version);
		try {
			if (this.version == VERSION_3) {
				this.md = MessageDigest.getInstance("MD5");
			} else {
				this.md = MessageDigest.getInstance("SHA-1");
			}
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("Message digest algorithm not supported.", e);
		}
	}

	/*
	 * -------------------------------- 
	 * Public fluent interface methods
	 * --------------------------------
	 */

	/**
	 * Sets a fixed namespace with in a fluent way.
	 * 
	 * @param namespace
	 * @return
	 */
	public NameBasedUUIDCreator withFixedNamespace(UUID namespace) {
		this.fixedNamespace = namespace;
		return this;
	}

	/*
	 * ------------------------- 
	 * Public methods 
	 * -------------------------
	 */

	/**
	 * Return a name-based UUID.
	 * 
	 * @see {@link NameBasedUUIDCreator#create(UUID, String)}
	 * 
	 * @param name
	 * @return
	 */
	public UUID create(String name) {
		return create(this.fixedNamespace, name);
	}

	/**
	 * Return a name-based UUID.
	 * 
	 * ### RFC-4122 - 4.3.  Algorithm for Creating a Name-Based UUID
	 * 
	 * (1) Allocate a UUID to use as a "name space ID" for all UUIDs generated from
	 * names in that name space; see Appendix C for some pre-defined values.
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
	 * (12) Set octets zero through five of the node field to octets 10 through 15 of
	 * the hash.
	 * 
	 * (13) Convert the resulting UUID to local byte order.
	 * 
	 * @param namespace
	 * @param name
	 * @return
	 */
	public UUID create(UUID namespace, String name) {

		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;

		byte[] namespaceBytes = null;
		byte[] nameBytes = null;
		byte[] bytes = null;
		byte[] hash = null;

		nameBytes = name.getBytes();
		
		if (this.fixedNamespace != null) {
			namespaceBytes = toBytes(this.fixedNamespace.getMostSignificantBits());
			namespaceBytes = concat(namespaceBytes, toBytes(this.fixedNamespace.getLeastSignificantBits()));
		} else if (namespace != null) {
			namespaceBytes = toBytes(namespace.getMostSignificantBits());
			namespaceBytes = concat(namespaceBytes, toBytes(namespace.getLeastSignificantBits()));
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
