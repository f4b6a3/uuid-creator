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

package com.github.f4b6a3.uuid.creator;

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
import com.github.f4b6a3.uuid.strategy.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.UuidFormatter;

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
	 * {@link MacNodeIdentifierStrategy}.
	 * 
	 * @param <T> type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withHardwareAddressNodeIdentifier() {
		this.nodeIdentifierStrategy = new MacNodeIdentifierStrategy();
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
	 * Disables the overrun exception.
	 * 
	 * An exception thrown when more than 10 thousand requests are made within the
	 * same millisecond. This method disables the exception.
	 * 
	 * This method replaces the current {@link TimestampStrategy} with the
	 * {@link DefaultTimestampStrategy}. So don't use this method if you want the
	 * creator to use another {@link TimestampStrategy}.
	 * 
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withoutOverrunException() {
		this.timestampStrategy = new DefaultTimestampStrategy(/* enableOverrunException */ false);
		return (T) this;
	}

	/**
	 * Formats the most significant bits of the UUID.
	 * 
	 * This method must be implemented by all subclasses.
	 * 
	 * @param timestamp a timestamp
	 * @return the MSB
	 */
	protected abstract long formatMostSignificantBits(long timestamp);

	/**
	 * Formats the least significant bits of the UUID.
	 * 
	 * See: {@link UuidFormatter#formatRfc4122LeastSignificantBits(long, long)}
	 * 
	 * @param nodeIdentifier a node identifier
	 * @param clockSequence  a clock sequence
	 * @return the LSB
	 */
	protected long formatLeastSignificantBits(final long nodeIdentifier, final long clockSequence) {
		return UuidFormatter.formatRfc4122LeastSignificantBits(nodeIdentifier, clockSequence);
	}
}