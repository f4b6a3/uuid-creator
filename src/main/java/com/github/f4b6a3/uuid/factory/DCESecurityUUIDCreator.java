package com.github.f4b6a3.uuid.factory;

import java.util.UUID;

import com.github.f4b6a3.uuid.factory.abst.AbstractUUIDCreator;
import com.github.f4b6a3.uuid.increment.AbstractIncrementable;

public class DCESecurityUUIDCreator extends AbstractUUIDCreator {

	private static final long serialVersionUID = -5263854356470674635L;

	public static final byte LOCAL_DOMAIN_PERSON = 0; // POSIX UID domain
	public static final byte LOCAL_DOMAIN_GROUP = 1; // POSIX GID domain
	public static final byte LOCAL_DOMAIN_ORG = 2;

	protected TimeBasedUUIDCreator timeBasedUUIDCreator;
	protected DCESTimestampCounter timestampCounter;

	protected byte localDomain;

	public DCESecurityUUIDCreator() {
		super(VERSION_2);
		timeBasedUUIDCreator = new TimeBasedUUIDCreator();
		timestampCounter = new DCESTimestampCounter();
	}

	/**
	 * 
	 * Returns a new DCE Security UUID.
	 * 
	 * A DCE Security (version 2) is a modified Time-based (version 1). It
	 * embeds a local domain (8 bits) and a local identifier (32 bits).
	 * 
	 * Steps of creation:
	 * 
	 * 1) Create a Time-based UUID (version 1);
	 * 
	 * 2) Replace the least significant 8 bits of the clock sequence with the
	 * local domain;
	 * 
	 * 3) Replace the least significant 32 bits of the timestamp with the local
	 * identifier..
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
	 * 1) The version number is 2;
	 * 
	 * 2) The clock_seq_low field (which represents an integer in the range [0,
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
	 * 3) The time_low field (which represents an integer in the range [0,
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
	 * @param localDomain
	 * @param localIdentifier
	 * @return
	 */
	public synchronized UUID create(byte localDomain, int localIdentifier) {

		UUID uuid = timeBasedUUIDCreator.create();
		int counter = timestampCounter.getNext();
		long msb = setLocalIdentifierBits(uuid.getMostSignificantBits(), localIdentifier);
		long lsb = setLocalDomainBits(uuid.getLeastSignificantBits(), localDomain, counter);
		
		return new UUID(msb, lsb);
	}

	public UUID create(int localIdentifier) {
		return create(this.localDomain, localIdentifier);
	}

	protected static long setLocalIdentifierBits(long msb, long localIdentifier) {
		return (msb & 0x00000000ffffffffL) | localIdentifier;
	}

	protected static long setLocalDomainBits(long lsb, long localDomain, int counter) {
		return lsb | (localDomain << 48) | (counter << 56);
	}
	
	
	public DCESecurityUUIDCreator withLocalDomain(byte localDomain) {
		this.localDomain = localDomain;
		return this;
	}

	public DCESecurityUUIDCreator withPosixUserDomain() {
		this.localDomain = LOCAL_DOMAIN_PERSON;
		return this;
	}

	public DCESecurityUUIDCreator withPosixGroupDomain() {
		this.localDomain = LOCAL_DOMAIN_GROUP;
		return this;
	}
	
	protected class DCESTimestampCounter extends AbstractIncrementable {

		private long timestamp = 0;

		private static final int COUNTER_MIN = 0;
		private static final int COUNTER_MAX = 63; // 2^64

		public DCESTimestampCounter() {
			super(COUNTER_MIN, COUNTER_MAX);
		}

		public int getNextFor(long timestamp) {
			if (timestamp <= this.timestamp) {
				this.timestamp = timestamp;
				return this.getNext();
			}
			return this.getCurrent();
		}
	}

}
