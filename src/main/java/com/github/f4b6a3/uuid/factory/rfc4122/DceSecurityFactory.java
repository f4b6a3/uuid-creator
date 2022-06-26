/*
 * MIT License
 * 
 * Copyright (c) 2018-2022 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.rfc4122;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;

/**
 * 
 * Factory that creates DCE Security UUIDs (version 2).
 * 
 * <pre>
 * Local domains predefined by DCE 1.1 Authentication and Security Services (Chapter 11):
 * 
 * - LOCAL_DOMAIN_PERSON: 0 (interpreted as POSIX UID domain);
 * - LOCAL_DOMAIN_GROUP: 1 (interpreted as POSIX GID domain);
 * - LOCAL_DOMAIN_ORG: 2.
 * </pre>
 * 
 * Sources:
 * 
 * DCE 1.1: Authentication and Security Services (Chapter 05 - Version 2 UUIDs):
 * https://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01
 * 
 * DCE 1.1: Authentication and Security Services (Chapter 11 - domain names):
 * https://pubs.opengroup.org/onlinepubs/9696989899/chap11.htm#tagcjh_14_05_01_01
 * 
 * Distributed Computing Environment (DCE):
 * https://en.wikipedia.org/wiki/Distributed_Computing_Environment
 * 
 * Unix User identifier (UID): https://en.wikipedia.org/wiki/User_identifier
 * 
 * Unix group identifier (GID): https://en.wikipedia.org/wiki/Group_identifier
 */
public final class DceSecurityFactory extends AbstTimeBasedFactory {

	private AtomicInteger counter;

	private final byte localDomain;

	public DceSecurityFactory() {
		this(builder());
	}

	private DceSecurityFactory(Builder builder) {
		super(UuidVersion.VERSION_DCE_SECURITY, builder);
		this.localDomain = builder != null ? builder.localDomain : 0;
		this.counter = new AtomicInteger();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends AbstTimeBasedFactory.Builder<DceSecurityFactory> {

		private byte localDomain;

		public Builder withLocalDomain(UuidLocalDomain localDomain) {
			this.localDomain = localDomain.getValue();
			return this;
		}

		public Builder withLocalDomain(byte localDomain) {
			this.localDomain = localDomain;
			return this;
		}

		@Override
		public DceSecurityFactory build() {
			return new DceSecurityFactory(this);
		}
	}

	/**
	 * Returns a DCE Security UUID.
	 * 
	 * A DCE Security UUID (version 2) is a modified Time-based one (version 1).
	 * 
	 * Steps of creation:
	 * 
	 * (1a) Create a Time-based UUID (version 1);
	 * 
	 * (2a) Replace the least significant 8 bits of the clock sequence with the
	 * local domain;
	 * 
	 * (3a) Replace the least significant 32 bits of the timestamp with the local
	 * identifier.
	 * 
	 * 
	 * ### DCE 1.1: Authentication and Security Services Security-Version (Version
	 * 2) UUIDs
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * These security-version UUIDs are specified exactly as in Appendix A, except
	 * that they have the following special properties and interpretations:
	 * 
	 * (1b) The version number is 2;
	 * 
	 * (2) The clock_seq_low field (which represents an integer in the range [0,
	 * 2^8-1]) is interpreted as a local domain (as represented by sec_rgy_domain_t;
	 * see sec_rgy_domain_t ); that is, an identifier domain meaningful to the local
	 * host. (Note that the data type sec_rgy_domain_t can potentially hold values
	 * outside the range [0, 28-1]; however, the only values currently registered
	 * are in the range [0, 2], so this type mismatch is not significant.) In the
	 * particular case of a POSIX host, the value sec_rgy_domain_person is to be
	 * interpreted as the "POSIX UID domain", and the value sec_rgy_domain_group is
	 * to be interpreted as the "POSIX GID domain".
	 * 
	 * (3) The time_low field (which represents an integer in the range [0, 2^32-1])
	 * is interpreted as a local-ID; that is, an identifier (within the domain
	 * specified by clock_seq_low) meaningful to the local host. In the particular
	 * case of a POSIX host, when combined with a POSIX UID or POSIX GID domain in
	 * the clock_seq_low field (above), the time_low field represents a POSIX UID or
	 * POSIX GID, respectively.
	 * 
	 * #### SEC_RGY_DOMAIN_T:
	 * 
	 * The following values are currently registered (for sec_rgy_domain_t):
	 * 
	 * 1) sec_rgy_domain_person: The principal domain. Its associated stringname is
	 * person.
	 * 
	 * 2) sec_rgy_domain_group: The group domain. Its associated stringname is
	 * group.
	 * 
	 * 3) sec_rgy_domain_org: The organization domain. Its associated stringname is
	 * org.
	 * 
	 * #### Trade off (from Wikipedia)
	 * 
	 * The ability to include a 40-bit domain/identifier in the UUID comes with a
	 * tradeoff. On the one hand, 40 bits allow about 1 trillion domain/identifier
	 * values per node id. On the other hand, with the clock value truncated to the
	 * 28 most significant bits, compared to 60 bits in version 1, the clock in a
	 * version 2 UUID will "tick" only once every 429.49 seconds, a little more than
	 * 7 minutes, as opposed to every 100 nanoseconds for version 1. And with a
	 * clock sequence of only 6 bits, compared to 14 bits in version 1, only 64
	 * unique UUIDs per node/domain/identifier can be generated per 7 minute clock
	 * tick, compared to 16,384 clock sequence values for version 1.[12] Thus,
	 * Version 2 may not be suitable for cases where UUIDs are required, per
	 * node/domain/identifier, at a rate exceeding about 1 per 7 seconds.
	 * 
	 * @param localDomain     a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public synchronized UUID create(byte localDomain, int localIdentifier) {

		// (1a) Create a Time-based UUID (version 1)
		UUID uuid = super.create();

		// (2a) Insert the local identifier bits
		long msb = setLocalIdentifierBits(uuid.getMostSignificantBits(), localIdentifier);

		// (3a) Insert the local domain bits
		long lsb = setLocalDomainBits(uuid.getLeastSignificantBits(), localDomain, this.counter.incrementAndGet());

		// (1b) set version 2
		return getUuid(msb, lsb);
	}

	/**
	 * Returns a DCE Security UUID.
	 * 
	 * See {@link DceSecurityFactory#create(byte, int)}
	 * 
	 * @param localDomain     a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public synchronized UUID create(UuidLocalDomain localDomain, int localIdentifier) {
		return create(localDomain.getValue(), localIdentifier);
	}

	/**
	 * Returns a DCE Security UUID.
	 * 
	 * The local domain is fixed using
	 * {@code DceSecurityFactory.builder().withLocalDomain(UuidLocalDomain).build()}
	 * 
	 * See {@link DceSecurityFactory#create(byte, int)}
	 * 
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public synchronized UUID create(int localIdentifier) {
		return create(this.localDomain, localIdentifier);
	}

	/**
	 * Throws an exception.
	 * 
	 * Overrides the create() method from {@link AbstTimeBasedFactory}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public synchronized UUID create() {
		throw new UnsupportedOperationException("Unsuported operation for DCE Security UUID factory");
	}

	/**
	 * Inserts the local identifier bits into the most significant bits.
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * (3) The time_low field (which represents an integer in the range [0, 2^32-1])
	 * is interpreted as a local-ID; that is, an identifier (within the domain
	 * specified by clock_seq_low) meaningful to the local host. In the particular
	 * case of a POSIX host, when combined with a POSIX UID or POSIX GID domain in
	 * the clock_seq_low field (above), the time_low field represents a POSIX UID or
	 * POSIX GID, respectively.
	 * 
	 * @param msb             the MSB
	 * @param localIdentifier the local identifier
	 * @return the updated MSB
	 */
	private static long setLocalIdentifierBits(long msb, int localIdentifier) {
		return (msb & 0x00000000ffffffffL) // clear time_low bits
				| ((localIdentifier & 0x00000000ffffffffL) << 32);
	}

	/**
	 * Inserts the local domain bits into the least significant bits.
	 * 
	 * #### Security-Version (Version 2) UUIDs
	 * 
	 * (2) The clock_seq_low field (which represents an integer in the range [0,
	 * 2^8-1]) is interpreted as a local domain (as represented by sec_rgy_domain_t;
	 * see sec_rgy_domain_t ); that is, an identifier domain meaningful to the local
	 * host. (Note that the data type sec_rgy_domain_t can potentially hold values
	 * outside the range [0, 2^8-1]; however, the only values currently registered
	 * are in the range [0, 2], so this type mismatch is not significant.) In the
	 * particular case of a POSIX host, the value sec_rgy_domain_person is to be
	 * interpreted as the "POSIX UID domain", and the value sec_rgy_domain_group is
	 * to be interpreted as the "POSIX GID domain".
	 * 
	 * @param lsb         the LSB
	 * @param localDomain a local domain
	 * @param counter     a counter value
	 * @return the updated LSB
	 */
	private static long setLocalDomainBits(long lsb, byte localDomain, long counter) {
		return (lsb & 0x0000ffffffffffffL) // clear clock_seq bits
				| ((localDomain & 0x00000000000000ffL) << 48) //
				| ((counter & 0x00000000000000ffL) << 56);
	}
}