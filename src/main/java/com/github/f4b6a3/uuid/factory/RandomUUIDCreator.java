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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Factory that creates random UUIDs version 4.
 * 
 * @author fabiolimace
 *
 */
public class RandomUUIDCreator extends UUIDCreator {

	private static final long serialVersionUID = -5686288990673774098L;
	
	/*
	 * -------------------------
	 * Private fields
	 * -------------------------
	 */
	private Random random;
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	public RandomUUIDCreator() {
		super(UUIDCreator.VERSION_4);
	}

	/* 
	 * -------------------------
	 * Public methods
	 * -------------------------
	 */
	@Override
	public UUID create() {
		
		if(random == null) {
			random = getSecureRandom();
		}
		
		long msb = 0x0000000000000000L;
		long lsb = 0x0000000000000000L;
		
		msb = this.random.nextLong();
		lsb = this.random.nextLong();
		
		lsb = setVariantBits(lsb);
		msb = setVersionBits(msb);
		
		return new UUID(msb, lsb);
	}
	
	/* 
	 * -------------------------
	 * Public fluent interface methods
	 * -------------------------
	 */
	/**
	 * Change the default random generator in a fluent way to another that extends {@link Random}.
	 * 
	 * The default random generator is {@link java.security.SecureRandom} with SHA1PRNG algorithm.
	 * 
	 * {@link Random}.
	 * 
	 * @param random {@link Random}
	 */
	public RandomUUIDCreator withRandomGenerator(Random random) {
		this.random = random;
		return this;
	}
	
	/* 
	 * -------------------------
	 * Private static methods
	 * -------------------------
	 */
	/**
	 * Create a secure random instance with SHA1PRNG algorithm.
	 *
	 * If this algorithm is not present, it uses JVM's default.
	 */
	private static SecureRandom getSecureRandom() {
		try {
			return SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			return new SecureRandom();
		}
	}
}
