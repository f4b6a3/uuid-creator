package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUUIDCreator;

public class SHA1UUIDCreator extends AbstractNameBasedUUIDCreator {

	private static final long serialVersionUID = 8563020852501403342L;

	public SHA1UUIDCreator() {
		super(VERSION_5, MESSAGE_DIGEST_SHA1);
	}	
}
