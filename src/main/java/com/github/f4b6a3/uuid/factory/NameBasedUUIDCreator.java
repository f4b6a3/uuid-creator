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
	private UUID namespace;
	private String name;
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
	 * Sets the namespace with in a fluent way.
	 * 
	 * @param namespace
	 * @return
	 */
	public NameBasedUUIDCreator withNamespace(UUID namespace) {
		this.namespace = namespace;
		return this;
	}

	/**
	 * Sets the name with in a fluent way.
	 * 
	 * @param name
	 * @return
	 */
	public NameBasedUUIDCreator withName(String name) {
		this.name = name;
		return this;
	}
	
	/* 
	 * -------------------------
	 * Public methods
	 * -------------------------
	 */
	@Override
	public UUID create() {
		
		byte[] namespaceBytes = null;
		byte[] nameBytes = null;
		byte[] bytes = null;
		byte[] hash = null;
		
		if(namespace != null) {
			namespaceBytes = toBytes(namespace.getMostSignificantBits());
			namespaceBytes = concat(namespaceBytes, toBytes(namespace.getLeastSignificantBits()));
		}
		
		nameBytes = name.getBytes();
		bytes = concat(namespaceBytes, nameBytes);
		
		hash = md.digest(bytes);
		
		this.msb = toNumber(copy(hash, 0, 8));
		this.lsb = toNumber(copy(hash, 8, 16));
		
		setVariantBits();
		setVersionBits();
		return super.create();
	}
}
