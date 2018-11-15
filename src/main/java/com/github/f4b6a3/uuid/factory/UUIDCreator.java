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


import java.io.Serializable;
import java.util.UUID;

/**
 * Abstract class for subclasses that create {@link UUID} objects.
 * 
 * @author fabiolimace
 *
 */
public abstract class UUIDCreator implements IUUIDCreator, Serializable {

	private static final long serialVersionUID = 174136732581039569L;
	
	/*
	 * -------------------------
	 * Private fields
	 * -------------------------
	 */
	protected int version; // intended version to be created
	
	/*
	 * -------------------------
	 * Public constants
	 * -------------------------
	 */
	// UUID variants defined by RFC-4122
	public static final int VARIANT_RESERVED_NCS = 0;
	public static final int VARIANT_RFC4122 = 2;
	public static final int VARIANT_RESERVED_MICROSOFT = 6;
	public static final int VARIANT_RESERVED_FUTURE = 7;
	// UUID versions defined by RFC-4122, plus an extension (zero)
	public static final int VERSION_0 = 0;
	public static final int VERSION_1 = 1;
	public static final int VERSION_2 = 2;
	public static final int VERSION_3 = 3;
	public static final int VERSION_4 = 4;
	public static final int VERSION_5 = 5;
	// UUIDs objects defined by RFC-4122
	public static final UUID NIL_UUID = new UUID(0x0000000000000000L, 0x0000000000000000L);
	public static final UUID NAMESPACE_DNS = new UUID(0x6ba7b8109dad11d1L, 0x80b400c04fd430c8L);
	public static final UUID NAMESPACE_URL = new UUID(0x6ba7b8119dad11d1L, 0x80b400c04fd430c8L);
	public static final UUID NAMESPACE_OID = new UUID(0x6ba7b8129dad11d1L, 0x80b400c04fd430c8L);
	public static final UUID NAMESPACE_X500 = new UUID(0x6ba7b8149dad11d1L, 0x80b400c04fd430c8L);
	
	/*
	 * -------------------------
	 * Private constants
	 * -------------------------
	 */
	// Values to be used in bitwise operations
	public static final long RFC4122_VARIANT_BITS = 0x8000000000000000L;
	public static final long[] VERSION_BITS_ARRAY = {
			0x0000000000000000L, 0x0000000000001000L, 
			0x0000000000002000L, 0x0000000000003000L, 
			0x0000000000004000L, 0x0000000000005000L };
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	public UUIDCreator(int version) {
		this.version = version;
	}
	
	/* 
	 * -------------------------
	 * Public methods
	 * -------------------------
	 */
	
	/**
	 * Check if the {@link UUID} to been created is valid.
	 * 
	 * It checks whether the variant and version are correct.
	 * 
	 * @return boolean
	 */
	public boolean valid(long msb, long lsb) {
		long variantBits = getVariantBits(lsb);
		long versionBits = getVersionBits(msb);
		return variantBits == RFC4122_VARIANT_BITS && versionBits == VERSION_BITS_ARRAY[version];
	}

	/**
	 * Returns the variant bits from the "Least Significant Bits".
	 * 
	 * @return long
	 */
	protected long getVariantBits(long lsb) {
		return (lsb & 0xc000000000000000L);
	}
	
	/**
	 * Set UUID variant bits into the "Least Significant Bits".
	 */
	protected long setVariantBits(long lsb) {
		return (lsb & 0x3fffffffffffffffL) | RFC4122_VARIANT_BITS;
	}
	
	/**
	 * Returns the version bits from the "Most Significant Bits".
	 * 
	 * @return long
	 */
	protected long getVersionBits(long msb) {
		return (msb & 0x000000000000f000L);
	}
	
	/**
	 * Set UUID version bits into the "Most Significant Bits".
	 */
	protected long setVersionBits(long msb) {
		return (msb & 0xffffffffffff0fffL) | VERSION_BITS_ARRAY[this.version];
	}
}
