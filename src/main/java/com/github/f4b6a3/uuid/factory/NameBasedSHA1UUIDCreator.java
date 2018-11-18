package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUUIDCreator;

public class NameBasedSHA1UUIDCreator extends AbstractNameBasedUUIDCreator {

	private static final long serialVersionUID = 8563020852501403342L;

	/**
	 * Facoty that creates name based UUIDs, version 5.
	 */
	public NameBasedSHA1UUIDCreator() {
		super(VERSION_5, MESSAGE_DIGEST_SHA1);
	}	
}
