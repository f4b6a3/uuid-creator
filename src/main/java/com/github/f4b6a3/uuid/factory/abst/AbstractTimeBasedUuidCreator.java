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

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.clockseq.ClockSequenceStrategy;
import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.clockseq.FixedClockSequenceStrategy;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.UuidCreatorException;
import com.github.f4b6a3.uuid.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.HardwareAddressNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.RandomNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.FingerprintNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.FixedTimestampStretegy;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.util.SettingsUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

public abstract class AbstractTimeBasedUuidCreator extends AbstractUuidCreator implements NoArgumentsUuidCreator {

	protected TimestampStrategy timestampStrategy;
	protected ClockSequenceStrategy clockSequenceStrategy;
	protected NodeIdentifierStrategy nodeIdentifierStrategy;

	/**
	 * This constructor requires a version number.
	 * 
	 * @param version
	 *            the version number
	 */
	protected AbstractTimeBasedUuidCreator(UuidVersion version) {
		super(version);

		this.timestampStrategy = new DefaultTimestampStrategy();
		this.nodeIdentifierStrategy = new DefaultNodeIdentifierStrategy();

		if (SettingsUtil.isStateEnabled()) {
			long timestamp = this.timestampStrategy.getTimestamp();
			long nodeIdentifier = this.nodeIdentifierStrategy.getNodeIdentifier();
			this.clockSequenceStrategy = new DefaultClockSequenceStrategy(timestamp, nodeIdentifier);
		} else {
			this.clockSequenceStrategy = new DefaultClockSequenceStrategy();
		}
	}

	/**
	 * Returns a new time-based UUID.
	 * 
	 * ### Timestamp
	 * 
	 * The timestamp has 100-nanoseconds resolution, starting from 1582-10-15.
	 * It uses 60 bits from the most significant bits. The the time value rolls
	 * over around AD 5235.
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
	 * The timestamp is a 60-bit value. For UUID version 1, this is represented
	 * by Coordinated Universal Time (UTC) as a count of 100- nanosecond
	 * intervals since 00:00:00.00, 15 October 1582 (the date of Gregorian
	 * reform to the Christian calendar).
	 * 
	 * ### RFC-4122 - 4.1.5. Clock Sequence
	 * 
	 * For UUID version 1, the clock sequence is used to help avoid duplicates
	 * that could arise when the clock is set backwards in time or if the node
	 * ID changes.
	 * 
	 * ### RFC-4122 - 4.1.6. Node
	 * 
	 * For UUID version 1, the node field consists of an IEEE 802 MAC address,
	 * usually the host address. For systems with multiple IEEE 802 addresses,
	 * any available one can be used. The lowest addressed octet (octet number
	 * 10) contains the global/local bit and the unicast/multicast bit, and is
	 * the first octet of the address transmitted on an 802.3 LAN.
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
	 * (3a) Get the current time as a 60-bit count of 100-nanosecond intervals
	 * since 00:00:00.00, 15 October 1582.
	 * 
	 * (4a) Get the current node ID.
	 * 
	 * (5a) If the state was unavailable (e.g., non-existent or corrupted), or
	 * the saved node ID is different than the current node ID, generate a
	 * random clock sequence value.
	 * 
	 * (6a) If the state was available, but the saved timestamp is later than
	 * the current timestamp, increment the clock sequence value.
	 * 
	 * (7a) Save the state (current timestamp, clock sequence, and node ID) back
	 * to the stable store.
	 * 
	 * (8a) Release the global lock.
	 * 
	 * (9a) Format a UUID from the current timestamp, clock sequence, and node
	 * ID values according to the steps in Section 4.2.2.
	 * 
	 * @return {@link UUID} a UUID value
	 * 
	 * @throws UuidCreatorException
	 *             an overrun exception if more than 10 thousand UUIDs are
	 *             requested within the same millisecond
	 */
	public synchronized UUID create() {

		// (3a) get the timestamp
		final long timestamp = this.timestampStrategy.getTimestamp();

		// (4a)(5a) get the node identifier
		final long nodeIdentifier = this.nodeIdentifierStrategy.getNodeIdentifier();

		// (5a)(6a) get the sequence value
		final long clockSequence = this.clockSequenceStrategy.getClockSequence(timestamp, nodeIdentifier);

		// (9a) format the most significant bits
		final long msb = this.formatMostSignificantBits(timestamp);

		// (9a) format the least significant bits
		final long lsb = this.formatLeastSignificantBits(nodeIdentifier, clockSequence);

		// (9a) format a UUID from the MSB and LSB
		return new UUID(msb, lsb);
	}

	/**
	 * Use an alternate {@link TimestampStrategy} to generate timestamps. The
	 * {@link DefaultTimestampStrategy} has accuracy of milliseconds. If someone
	 * needs a real 100-nanosecond resolution, another implementation of
	 * {@link TimestampStrategy} may be provided via this method.
	 * 
	 * @param timestampStrategy
	 *            a timestamp strategy
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withTimestampStrategy(
			TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Use an alternate {@link NodeIdentifierStrategy} to generate node
	 * identifiers.
	 * 
	 * @param nodeIdentifierStrategy
	 *            a node identifier strategy
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withNodeIdentifierStrategy(
			NodeIdentifierStrategy nodeIdentifierStrategy) {
		this.nodeIdentifierStrategy = nodeIdentifierStrategy;
		return (T) this;
	}

	/**
	 * Use an alternate {@link ClockSequenceStrategy} to generate clock
	 * sequences.
	 * 
	 * @param clockSequenceStrategy
	 *            a clock sequence strategy
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withClockSequenceStrategy(
			ClockSequenceStrategy clockSequenceStrategy) {
		this.clockSequenceStrategy = clockSequenceStrategy;
		return (T) this;
	}

	/**
	 * Set a fixed Instant to generate UUIDs.
	 * 
	 * This method is useful for unit tests.
	 * 
	 * @param instant
	 *            an {@link Instant}
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withInstant(Instant instant) {
		this.timestampStrategy = new FixedTimestampStretegy(TimestampUtil.toTimestamp(instant));
		return (T) this;
	}

	/**
	 * Set a fixed timestamp to generate UUIDs.
	 * 
	 * This method is useful for unit tests.
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withTimestamp(long timestamp) {
		this.timestampStrategy = new FixedTimestampStretegy(timestamp);
		return (T) this;
	}

	/**
	 * Set a fixed Unix Epoch milliseconds value to generate UUIDs.
	 * 
	 * This method is useful for unit tests.
	 * 
	 * @param unixEpochMillis
	 *            a Unix Epoch milliseconds value
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withUnixEpochMilliseconds(long unixEpochMillis) {
		long timestamp = TimestampUtil.toTimestamp(unixEpochMillis);
		this.timestampStrategy = new FixedTimestampStretegy(timestamp);
		return (T) this;
	}

	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * @param nodeIdentifier
	 *            a node identifier
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifierStrategy = new FixedNodeIdentifierStrategy(nodeIdentifier);
		return (T) this;
	}

	/**
	 * Set the node identifier to be a real hardware address of the host
	 * machine.
	 * 
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withHardwareAddressNodeIdentifier() {
		this.nodeIdentifierStrategy = new HardwareAddressNodeIdentifierStrategy();
		return (T) this;
	}

	/**
	 * Set the node identifier to be a system data hash.
	 * 
	 * The node identifier is calculated using these system data: OS + JVM +
	 * network details + system resources. The resulting node identifier is as
	 * unique and mutable as the host machine.
	 * 
	 * Read: https://en.wikipedia.org/wiki/Device_fingerprint
	 * 
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withFingerprintNodeIdentifier() {
		this.nodeIdentifierStrategy = new FingerprintNodeIdentifierStrategy();
		return (T) this;
	}

	/**
	 * Set the node identifier to be a random identifier.
	 * 
	 * Each UUID generated will have a different random node identifier.
	 * 
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withRandomNodeIdentifier() {
		this.nodeIdentifierStrategy = new RandomNodeIdentifierStrategy();
		return (T) this;
	}

	/**
	 * Set the node identifier to be a random identifier.
	 * 
	 * Each UUID generated will have a different random node identifier.
	 * 
	 * @param random
	 *            a random number generator
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withRandomNodeIdentifier(Random random) {
		this.nodeIdentifierStrategy = new RandomNodeIdentifierStrategy(random);
		return (T) this;
	}

	/**
	 * Set a fixed clock sequence value to generate UUIDs.
	 * 
	 * The clock sequence has a range from 0 to 16,383 (0x3fff).
	 * 
	 * @param clockSequence
	 *            a clock sequence
	 * @param <T>
	 *            type parameter
	 * @return {@link AbstractTimeBasedUuidCreator}
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends AbstractTimeBasedUuidCreator> T withClockSequence(int clockSequence) {
		this.clockSequenceStrategy = new FixedClockSequenceStrategy(clockSequence);
		return (T) this;
	}

	/**
	 * Formats the most significant bits of the UUID.
	 * 
	 * @param timestamp
	 *            a timestamp
	 * @return the MSB
	 */
	protected abstract long formatMostSignificantBits(long timestamp);

	/**
	 * Formats the least significant bits of the UUID.
	 * 
	 * ### RFC-4122 - 4.2.2. Generation Details
	 * 
	 * Set the clock_seq_low field to the eight least significant bits (bits
	 * zero through 7) of the clock sequence in the same order of significance.
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
	 * @param nodeIdentifier
	 *            a node identifier
	 * @param clockSequence
	 *            a clock sequence
	 * @return the LSB
	 */
	protected long formatLeastSignificantBits(final long nodeIdentifier, final long clockSequence) {
		return UuidUtil.formatRfc4122LeastSignificantBits(nodeIdentifier, clockSequence);
	}
}