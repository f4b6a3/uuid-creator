/*
 * MIT License
 * 
 * Copyright (c) 2018-2020 Fabio Lima
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

package com.github.f4b6a3.uuid.creator;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.strategy.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.TimestampStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.clockseq.FixedClockSequenceStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.HashNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidTimeUtil;

public abstract class AbstractTimeBasedUuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	protected TimestampStrategy timestampStrategy;
	protected ClockSequenceStrategy clockSequenceStrategy;
	protected NodeIdentifierStrategy nodeIdentifierStrategy;

	protected AbstractTimeBasedUuidCreator(UuidVersion version) {
		super(version);
		this.timestampStrategy = new DefaultTimestampStrategy();
		this.nodeIdentifierStrategy = new DefaultNodeIdentifierStrategy();
		this.clockSequenceStrategy = new DefaultClockSequenceStrategy();
	}

	/**
	 * Returns a time-based UUID.
	 * 
	 * ### Timestamp
	 * 
	 * The timestamp has 100-nanoseconds resolution, starting from 1582-10-15. It
	 * uses 60 bits from the most significant bits. The the time value rolls over
	 * around AD 5235.
	 * 
	 * <pre>
	 *   s = 10_000_000
	 *   m = 60 * s
	 *   h = 60 * m
	 *   d = 24 * h
	 *   y = 365.25 * d
	 *   2^60 / y = ~3653
	 *   3653 + 1582 = 5235
	 * </pre>
	 * 
	 * 
	 * ### RFC-4122 - 4.1.4. Timestamp
	 * 
	 * The timestamp is a 60-bit value. For UUID version 1, this is represented by
	 * Coordinated Universal Time (UTC) as a count of 100- nanosecond intervals
	 * since 00:00:00.00, 15 October 1582 (the date of Gregorian reform to the
	 * Christian calendar).
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * For UUID version 1, the clock sequence is used to help avoid duplicates that
	 * could arise when the clock is set backwards in time or if the node ID
	 * changes.
	 * 
	 * ### RFC-4122 - 4.1.6. Node
	 * 
	 * For UUID version 1, the node field consists of an IEEE 802 MAC address,
	 * usually the host address. For systems with multiple IEEE 802 addresses, any
	 * available one can be used. The lowest addressed octet (octet number 10)
	 * contains the global/local bit and the unicast/multicast bit, and is the first
	 * octet of the address transmitted on an 802.3 LAN.
	 * 
	 * For systems with no IEEE address, a randomly or pseudo-randomly generated
	 * value may be used; see Section 4.5. The multicast bit must be set in such
	 * addresses, in order that they will never conflict with addresses obtained
	 * from network cards.
	 * 
	 * ### RFC-4122 - 4.2.1. Basic Algorithm
	 * 
	 * (1a) Obtain a system-wide global lock
	 * 
	 * (2a) From a system-wide shared stable store (e.g., a file), read the UUID
	 * generator state: the values of the timestamp, clock sequence, and node ID
	 * used to generate the last UUID.
	 * 
	 * (3a) Get the current time as a 60-bit count of 100-nanosecond intervals since
	 * 00:00:00.00, 15 October 1582.
	 * 
	 * (4a) Get the current node ID.
	 * 
	 * (5a) If the state was unavailable (e.g., non-existent or corrupted), or the
	 * saved node ID is different than the current node ID, generate a random clock
	 * sequence value.
	 * 
	 * (6a) If the state was available, but the saved timestamp is later than the
	 * current timestamp, increment the clock sequence value.
	 * 
	 * (7a) Save the state (current timestamp, clock sequence, and node ID) back to
	 * the stable store.
	 * 
	 * (8a) Release the global lock.
	 * 
	 * (9a) Format a UUID from the current timestamp, clock sequence, and node ID
	 * values according to the steps in Section 4.2.2.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException an overrun exception if more than 10 thousand
	 *                              UUIDs are requested within the same millisecond
	 */
	public synchronized UUID create() {

		// (3a) get the timestamp
		final long timestamp = this.timestampStrategy.getTimestamp();

		// (4a)(5a) get the node identifier
		final long nodeIdentifier = this.nodeIdentifierStrategy.getNodeIdentifier();

		// (5a)(6a) get the sequence value
		final long clockSequence = this.clockSequenceStrategy.getClockSequence(timestamp);

		// (9a) format the most significant bits
		final long msb = this.formatMostSignificantBits(timestamp);

		// (9a) format the least significant bits
		final long lsb = this.formatLeastSignificantBits(nodeIdentifier, clockSequence);

		// (9a) format a UUID from the MSB and LSB
		return new UUID(msb, lsb);
	}

	/**
	 * Returns a time-based UUID.
	 * 
	 * This method overrides some or all parts of the new UUID.
	 * 
	 * For example, If you need to replace the default node identifier, you can pass
	 * another node identifier as argument.
	 * 
	 * The timestamp has 100-nanos resolution, but its accuracy depends on the
	 * argument passed. In some JVMs the {@link java.time.Instant} accuracy may be
	 * limited to milliseconds, for example, in the OpenJDK-8.
	 * 
	 * The node identifier range is from 0 to 281474976710655 (2^48 - 1). If the
	 * value passed by argument is out of range, the result of MOD 2^48 is used
	 * instead. The multicast bit is set to ONE automatically.
	 * 
	 * The clock sequence range is from 0 to 16383 (2^14 - 1). If the value passed
	 * by argument is out of range, the result of MOD 2^14 is used instead.
	 * 
	 * The null arguments are ignored. If all arguments are null, this method works
	 * exactly as the method {@link AbstractTimeBasedUuidCreator#create()}.
	 * 
	 * @param instant  an alternate instant
	 * @param nodeid   an alternate node (0 to 2^48)
	 * @param clockseq an alternate clock sequence (0 to 16,383)
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException an overrun exception if more than 10 thousand
	 *                              UUIDs are requested within the same millisecond
	 */
	public UUID create(final Instant instant, final Long nodeid, final Integer clockseq) {

		final long timestamp;
		final long nodeIdentifier;
		final long clockSequence;

		// (3a) get the timestamp
		if (instant != null) {
			timestamp = UuidTimeUtil.toTimestamp(instant);
		} else {
			timestamp = this.timestampStrategy.getTimestamp();
		}

		// (4a)(5a) get the node identifier
		if (nodeid != null) {
			nodeIdentifier = NodeIdentifierStrategy.setMulticastNodeIdentifier(nodeid);
		} else {
			nodeIdentifier = this.nodeIdentifierStrategy.getNodeIdentifier();
		}

		// (5a)(6a) get the sequence value
		if (clockseq != null) {
			clockSequence = clockseq & 0x00003fffL;
		} else {
			clockSequence = this.clockSequenceStrategy.getClockSequence(timestamp);
		}

		// (9a) format the most significant bits
		final long msb = this.formatMostSignificantBits(timestamp);

		// (9a) format the least significant bits
		final long lsb = this.formatLeastSignificantBits(nodeIdentifier, clockSequence);

		// (9a) format a UUID from the MSB and LSB
		return new UUID(msb, lsb);
	}

	/**
	 * Replaces the default {@link TimestampStrategy} with another one.
	 * 
	 * The {@link DefaultTimestampStrategy} has accuracy of milliseconds.
	 * 
	 * If someone needs, for example, a real 100-nanosecond resolution, a custom
	 * implementation of {@link TimestampStrategy} may be provided via this method.
	 * 
	 * @param timestampStrategy a timestamp strategy
	 * @param <T>               type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withTimestampStrategy(
			TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default {@link NodeIdentifierStrategy} with another one.
	 * 
	 * @param nodeIdentifierStrategy a node identifier strategy
	 * @param <T>                    type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withNodeIdentifierStrategy(
			NodeIdentifierStrategy nodeIdentifierStrategy) {
		this.nodeIdentifierStrategy = nodeIdentifierStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default {@link ClockSequenceStrategy} with another one.
	 * 
	 * @param clockSequenceStrategy a clock sequence strategy
	 * @param <T>                   type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withClockSequenceStrategy(
			ClockSequenceStrategy clockSequenceStrategy) {
		this.clockSequenceStrategy = clockSequenceStrategy;
		return (T) this;
	}

	/**
	 * Replaces the default {@link NodeIdentifierStrategy} with the
	 * {@link FixedNodeIdentifierStrategy}.
	 * 
	 * The input node identifier has it's multicast bit set to 1 automatically.
	 * 
	 * @param nodeIdentifier a node identifier
	 * @param <T>            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifierStrategy = new FixedNodeIdentifierStrategy(nodeIdentifier);
		return (T) this;
	}

	/**
	 * Replaces the default {@link NodeIdentifierStrategy} with the
	 * {@link FixedNodeIdentifierStrategy}.
	 * 
	 * The input node identifier has it's multicast bit set to 1 automatically.
	 * 
	 * @param nodeIdentifier a node identifier
	 * @param <T>            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withNodeIdentifier(byte[] nodeIdentifier) {
		this.nodeIdentifierStrategy = new FixedNodeIdentifierStrategy(nodeIdentifier);
		return (T) this;
	}

	/**
	 * Replaces the default {@link NodeIdentifierStrategy} with the
	 * {@link MacNodeIdentifierStrategy}.
	 * 
	 * @param <T> type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withMacNodeIdentifier() {
		this.nodeIdentifierStrategy = new MacNodeIdentifierStrategy();
		return (T) this;
	}
	
	/**
	 * Replaces the default {@link NodeIdentifierStrategy} with the
	 * {@link HashNodeIdentifierStrategy}.
	 * 
	 * @param <T> type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withHashNodeIdentifier() {
		this.nodeIdentifierStrategy = new HashNodeIdentifierStrategy();
		return (T) this;
	}

	/**
	 * Replaces the default {@link ClockSequenceStrategy} with the
	 * {@link FixedClockSequenceStrategy}.
	 * 
	 * The input clock sequence is truncated to fit the range 0 and 16,383 (0x3fff).
	 * 
	 * @param clockSequence a clock sequence
	 * @param <T>           type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withClockSequence(int clockSequence) {
		this.clockSequenceStrategy = new FixedClockSequenceStrategy(clockSequence);
		return (T) this;
	}

	/**
	 * Replaces the default {@link ClockSequenceStrategy} with the
	 * {@link FixedClockSequenceStrategy}.
	 * 
	 * The input clock sequence is truncated to fit the range 0 and 16,383 (0x3fff).
	 * 
	 * @param clockSequence a clock sequence
	 * @param <T>           type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withClockSequence(byte[] clockSequence) {
		this.clockSequenceStrategy = new FixedClockSequenceStrategy(clockSequence);
		return (T) this;
	}

	/**
	 * Returns the timestamp bits of the UUID version 1 in the order defined in the
	 * RFC-4122.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Determine the values for the UTC-based timestamp and clock sequence to be
	 * used in the UUID, as described in Section 4.2.1.
	 * 
	 * For the purposes of this algorithm, consider the timestamp to be a 60-bit
	 * unsigned integer and the clock sequence to be a 14-bit unsigned integer.
	 * Sequentially number the bits in a field, starting with zero for the least
	 * significant bit.
	 * 
	 * "Set the time_low field equal to the least significant 32 bits (bits zero
	 * through 31) of the timestamp in the same order of significance.
	 * 
	 * Set the time_mid field equal to bits 32 through 47 from the timestamp in the
	 * same order of significance.
	 * 
	 * Set the 12 least significant bits (bits zero through 11) of the
	 * time_hi_and_version field equal to bits 48 through 59 from the timestamp in
	 * the same order of significance.
	 * 
	 * Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the 4-bit version number corresponding to the
	 * UUID version being created, as shown in the table above."
	 * 
	 * @param timestamp a timestamp
	 * @return the MSB
	 */
	protected long formatMostSignificantBits(final long timestamp) {
		return ((timestamp & 0x0fff_0000_00000000L) >>> 48) //
				| ((timestamp & 0x0000_ffff_00000000L) >>> 16) //
				| ((timestamp & 0x0000_0000_ffffffffL) << 32) //
				| 0x0000000000001000L; // apply version 1
	}

	/**
	 * Returns the least significant bits of the UUID.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Set the clock_seq_low field to the eight least significant bits (bits zero
	 * through 7) of the clock sequence in the same order of significance.
	 * 
	 * Set the 6 least significant bits (bits zero through 5) of the
	 * clock_seq_hi_and_reserved field to the 6 most significant bits (bits 8
	 * through 13) of the clock sequence in the same order of significance.
	 * 
	 * Set the two most significant bits (bits 6 and 7) of the
	 * clock_seq_hi_and_reserved to zero and one, respectively.
	 * 
	 * Set the node field to the 48-bit IEEE address in the same order of
	 * significance as the address.
	 * 
	 * @param nodeIdentifier a node identifier
	 * @param clockSequence  a clock sequence
	 * @return the LSB
	 */
	protected long formatLeastSignificantBits(final long nodeIdentifier, final long clockSequence) {
		return (((clockSequence << 48) //
				| (nodeIdentifier & 0x0000ffffffffffffL)) //
				& 0x3fffffffffffffffL // clear variant bits
				| 0x8000000000000000L); // apply variant bits
	}
}