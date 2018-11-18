package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUUIDCreator;

public class NameBasedMD5UUIDCreator extends AbstractNameBasedUUIDCreator {

	private static final long serialVersionUID = -3521943502220060680L;
	
	/**
	 * Facoty that creates name based UUIDs, version 3.
	 */
	public NameBasedMD5UUIDCreator() {
		super(VERSION_3, MESSAGE_DIGEST_MD5);
	}
}
