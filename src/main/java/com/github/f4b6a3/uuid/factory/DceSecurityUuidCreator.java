/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
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

import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.sequence.AbstractSequence;

/**
 * 
 * Factory that creates DCE Security UUIDs.
 * 
 * RFC-4122 version: 2.
 * 
 * <pre>
 * Standard local domains: 
 * - Local Domain Person (POSIX UserID domain): 0
 * - Local Domain Group (POSIX GroupID domain): 1
 * - Local Domain Org: 2
 * </pre>
 * 
 */
public class DceSecurityUuidCreator extends TimeBasedUuidCreator {

	public static final byte LOCAL_DOMAIN_PERSON = 0; // POSIX UID domain
	public static final byte LOCAL_DOMAIN_GROUP = 1; // POSIX GID domain
	public static final byte LOCAL_DOMAIN_ORG = 2;

	protected TimeBasedUuidCreator timeBasedUUIDCreator;
	protected DCESTimestampCounter timestampCounter;

	protected byte localDomain;

	/**
	 * Facoty that creates DCE Security UUIDs, version 2.
	 */
	public DceSecurityUuidCreator() {
		super(UuidVersion.DCE_SECURITY);
		timestampCounter = new DCESTimestampCounter();
	}
	
	@Override
	public synchronized UUID create() {
		throw new UuidCreatorException("Not implemented.");
	}

	/**
	 * 
	 * Returns a DCE Security UUID based in a local domain and a local
	 * identifier.
	 * 
	 * A DCE Security UUID (version 2) is a modified Time-based one (version 1).
	 * It embeds a local domain (8 bits) and a local identifier (32 bits).
	 * 
	 * Steps of creation:
	 * 
	 * (1a) Create a Time-based UUID (version 1);
	 * 
	 * (2a) Replace the least significant 8 bits of the clock sequence with the
	 * local domain;
	 * 
	 * (3a) Replace the least significant 32 bits of the timestamp with the
	 * local identifier.
	 * 
	 * 
	 * ### DCE 1.1: Authentication and Security Services Security-Version
	 * (Version 2) UUIDs
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * These security-version UUIDs are specified exactly as in Appendix A,
	 * except that they have the following special properties and
	 * interpretations:
	 * 
	 * (1b) The version number is 2;
	 * 
	 * (2) The clock_seq_low field (which represents an integer in the range [0,
	 * 2^8-1]) is interpreted as a local domain (as represented by
	 * sec_rgy_domain_t; see sec_rgy_domain_t ); that is, an identifier domain
	 * meaningful to the local host. (Note that the data type sec_rgy_domain_t
	 * can potentially hold values outside the range [0, 28-1]; however, the
	 * only values currently registered are in the range [0, 2], so this type
	 * mismatch is not significant.) In the particular case of a POSIX host, the
	 * value sec_rgy_domain_person is to be interpreted as the "POSIX UID
	 * domain", and the value sec_rgy_domain_group is to be interpreted as the
	 * "POSIX GID domain".
	 * 
	 * (3) The time_low field (which represents an integer in the range [0,
	 * 2^32-1]) is interpreted as a local-ID; that is, an identifier (within the
	 * domain specified by clock_seq_low) meaningful to the local host. In the
	 * particular case of a POSIX host, when combined with a POSIX UID or POSIX
	 * GID domain in the clock_seq_low field (above), the time_low field
	 * represents a POSIX UID or POSIX GID, respectively.
	 * 
	 * #### SEC_RGY_DOMAIN_T:
	 * 
	 * The following values are currently registered (for sec_rgy_domain_t):
	 * 
	 * 1) sec_rgy_domain_person: The principal domain. Its associated stringname
	 * is person.
	 * 
	 * 2) sec_rgy_domain_group: The group domain. Its associated stringname is
	 * group.
	 * 
	 * 3) sec_rgy_domain_org: The organization domain. Its associated stringname
	 * is org.
	 * 
	 * #### Trade off (from Wikipedia)
	 * 
	 * The ability to include a 40-bit domain/identifier in the UUID comes with
	 * a tradeoff. On the one hand, 40 bits allow about 1 trillion
	 * domain/identifier values per node id. On the other hand, with the clock
	 * value truncated to the 28 most significant bits, compared to 60 bits in
	 * version 1, the clock in a version 2 UUID will "tick" only once every
	 * 429.49 seconds, a little more than 7 minutes, as opposed to every 100
	 * nanoseconds for version 1. And with a clock sequence of only 6 bits,
	 * compared to 14 bits in version 1, only 64 unique UUIDs per
	 * node/domain/identifier can be generated per 7 minute clock tick, compared
	 * to 16,384 clock sequence values for version 1.[12] Thus, Version 2 may
	 * not be suitable for cases where UUIDs are required, per
	 * node/domain/identifier, at a rate exceeding about 1 per 7 seconds.
	 * 
	 * @param localDomain a local domain
	 * @param localIdentifier a local identifier
	 * @return a UUID
	 */
	public synchronized UUID create(byte localDomain, int localIdentifier) {

		// (1a) Create a Time-based UUID (version 1)
		UUID uuid = super.create();

		// (2a) Insert de local identifier bits
		long msb = setLocalIdentifierBits(uuid.getMostSignificantBits(), localIdentifier);

		// (3a) Insert the local domain bits
		long counter = timestampCounter.next();
		long lsb = setLocalDomainBits(uuid.getLeastSignificantBits(), localDomain, counter);
		
		// (1b) set version 2
		return new UUID(setVersionBits(msb), setVariantBits(lsb));
	}

	/**
	 * Returns a DCE Security UUID based in a local identifier.
	 * 
	 * The default local domain is POSIX User ID.
	 * 
	 * {@link DceSecurityUuidCreator#create(byte, int)}
	 * 
	 * @param localIdentifier a local identifier
	 * @return a UUID
	 */
	public synchronized UUID create(int localIdentifier) {
		return create(LOCAL_DOMAIN_PERSON, localIdentifier);
	}

	/**
	 * Insert the local identifier bits in the most significant bits.
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * (3) The time_low field (which represents an integer in the range [0,
	 * 2^32-1]) is interpreted as a local-ID; that is, an identifier (within the
	 * domain specified by clock_seq_low) meaningful to the local host. In the
	 * particular case of a POSIX host, when combined with a POSIX UID or POSIX
	 * GID domain in the clock_seq_low field (above), the time_low field
	 * represents a POSIX UID or POSIX GID, respectively.
	 * 
	 * @param msb the MSB
	 * @param localIdentifier the local identifier
	 * @return the updated MSB
	 */
	protected static long setLocalIdentifierBits(long msb, int localIdentifier) {
		return (msb & 0x00000000ffffffffL) | (((long) localIdentifier) << 32);
	}

	/**
	 * Insert the local domain bits in the most significant bits.
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * (2) The clock_seq_low field (which represents an integer in the range [0,
	 * 2^8-1]) is interpreted as a local domain (as represented by
	 * sec_rgy_domain_t; see sec_rgy_domain_t ); that is, an identifier domain
	 * meaningful to the local host. (Note that the data type sec_rgy_domain_t
	 * can potentially hold values outside the range [0, 2^8-1]; however, the
	 * only values currently registered are in the range [0, 2], so this type
	 * mismatch is not significant.) In the particular case of a POSIX host, the
	 * value sec_rgy_domain_person is to be interpreted as the "POSIX UID
	 * domain", and the value sec_rgy_domain_group is to be interpreted as the
	 * "POSIX GID domain".
	 * 
	 * @param lsb the LSB
	 * @param localDomain a local domain
	 * @param counter a counter value
	 * @return the updated LSB
	 */
	protected static long setLocalDomainBits(long lsb, byte localDomain, long counter) {
		return ((lsb & 0xff00ffffffffffffL) | (long) localDomain << 48) | (counter << 56);
	}

	/**
	 * Set a fixed local domain.
	 * 
	 * @param localDomain a local domain
	 * @return {@link DceSecurityUuidCreator}
	 */
	public synchronized DceSecurityUuidCreator withLocalDomain(byte localDomain) {
		this.localDomain = localDomain;
		return this;
	}

	/**
	 * Class used to keep a counter to simulate minimize repetition.
	 */
	protected class DCESTimestampCounter extends AbstractSequence {
		
		// COUNTER_MAX: 2^6 (14 bits of the clock sequence minus 8 bytes)
		private static final long COUNTER_MIN = 0;
		private static final long COUNTER_MAX = 63;

		protected DCESTimestampCounter() {
			super(COUNTER_MIN, COUNTER_MAX);
		}
	}
}
