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


import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;

/**
 * Abstract class for subclasses that create {@link UUID} objects.
 */
public abstract class AbstractUuidCreator {
	
	protected final UuidVersion version;
	
	// UUIDs objects defined by RFC-4122
	public static final UUID NIL_UUID = new UUID(0x0000000000000000L, 0x0000000000000000L);
	
	// Values to be used in bitwise operations
	public static final long RFC4122_VARIANT_BITS = 0x8000000000000000L;
	protected static final long[] RFC4122_VERSION_BITS = {
			0x0000000000000000L, 0x0000000000001000L, 
			0x0000000000002000L, 0x0000000000003000L, 
			0x0000000000004000L, 0x0000000000005000L };
	
	public AbstractUuidCreator(UuidVersion version) {
		this.version = version;
	}

	public UuidVersion getVersion() {
		return this.version;
	}
	
	/**
	 * Check if the {@link UUID} to been created is valid.
	 * 
	 * It checks whether the variant and version are correct.
	 * 
	 * @param msb the MSB
	 * @param lsb the LSB
	 * @return boolean true if valid
	 */
	public boolean valid(long msb, long lsb) {
		long variantBits = getVariantBits(lsb);
		long versionBits = getVersionBits(msb);
		return variantBits == RFC4122_VARIANT_BITS && versionBits == RFC4122_VERSION_BITS[version.getValue()];
	}

	/**
	 * Returns the variant bits from the "Least Significant Bits".
	 * 
	 * @param lsb the LSB
	 * @return long the variant number bits
	 */
	protected long getVariantBits(long lsb) {
		return (lsb & 0xc000000000000000L);
	}
	
	/**
	 * Set UUID variant bits into the "Least Significant Bits".
	 * 
	 * @param lsb the LSB
	 */
	protected long setVariantBits(long lsb) {
		return (lsb & 0x3fffffffffffffffL) | RFC4122_VARIANT_BITS;
	}
	
	/**
	 * Returns the version bits from the "Most Significant Bits".
	 * 
	 * @param msb the MSB
	 * @return long version number bits
	 */
	protected long getVersionBits(long msb) {
		return (msb & 0x000000000000f000L);
	}
	
	/**
	 * Set UUID version bits into the "Most Significant Bits".
	 * 
	 * @param msb the MSB
	 * @return the MSB
	 */
	protected long setVersionBits(long msb) {
		return (msb & 0xffffffffffff0fffL) | RFC4122_VERSION_BITS[this.version.getValue()];
	}
}
