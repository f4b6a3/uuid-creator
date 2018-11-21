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

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.sequence.ClockSequence;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.TimestampStrategy;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;

public abstract class AbstractTimeBasedUUIDCreator extends AbstractUUIDCreator {

	// Fixed fields
	protected long timestamp = 0;
	protected long nodeIdentifier = 0;

	protected Random random = new Xorshift128PlusRandom();
	protected ClockSequence clockSequence = new ClockSequence();
	protected TimestampStrategy timestampStrategy = new DefaultTimestampStrategy();

	/**
	 * This constructor requires a version number.
	 * 
	 * @param version
	 */
	protected AbstractTimeBasedUUIDCreator(int version) {
		super(version);
	}

	/**
	 * Returns a new time-based UUID.
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
	 * ### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (4b) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @return {@link UUID}
	 */
	public synchronized UUID create() {

		// (3a) get the timestamp
		long timestamp = this.getTimestamp();

		// (4a)(5a) get the node identifier
		long nodeIdentifier = this.getNodeIdentifier();

		// (5a)(6a) get the sequence value
		long sequence = clockSequence.getNextForTimestamp(timestamp);

		// (9a) format the most significant bits
		long msb = getMostSignificantBits(timestamp);

		// (9a) format the least significant bits
		long lsb = getLeastSignificantBits(nodeIdentifier, sequence);

		// (9a) format a UUID from the MSB and LSB
		return new UUID(msb, lsb);
	}

	/**
	 * Set an alternative random generator to generate UUIDs.
	 * 
	 * By default it uses {@link Xorshift128PlusRandom}, but it can be changed
	 * to any subclass of {@link Random}.
	 * 
	 * @param random
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withRandomGenerator(Random random) {
		this.random = random;
		return (T) this;
	}

	/**
	 * Set an alternative {@link TimestampStrategy} to generate timestamps. The
	 * {@link DefaultTimestampStrategy} has accuracy of milliseconds. If someone
	 * needs a real 100-nanosecond an implementation of
	 * {@link TimestampStrategy} may be provided via this method.
	 * 
	 * @param timestampStrategy
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withTimestampStrategy(TimestampStrategy timestampStrategy) {
		this.timestampStrategy = timestampStrategy;
		return (T) this;
	}

	/**
	 * Set a fixed Instant to generate UUIDs.
	 * 
	 * This method is useful for unit tests.
	 * 
	 * @param instant
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withInstant(Instant instant) {
		this.timestamp = TimestampUtil.toTimestamp(instant);
		return (T) this;
	}

	/**
	 * Set a fixed timestamp to generate UUIDs.
	 * 
	 * This method is useful for unit tests.
	 * 
	 * @param timestamp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return (T) this;
	}

	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Someone may think it's useful in some special case
	 * to use a fixed node identifier other than random value.
	 * 
	 * This method will always set the multicast bit ON to indicate that the
	 * value is NOT a real MAC address.
	 * 
	 * If you want to inform a fixed value that is real MAC address, use the
	 * method {@link TimeBasedUUIDCreator#withUnicastNodeIdentifier(long)}.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withMulticastNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = setMulticastNodeIdentifier(nodeIdentifier);
		return (T) this;
	}

	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * This method will always set the multicast bit OFF to indicate that the
	 * value is a real MAC address.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withUnicastNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = setUnicastNodeIdentifier(nodeIdentifier);
		return (T) this;
	}

	/**
	 * Set the node identifier to be a real hardware address of the host
	 * machine.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Using a real hardware address today is not
	 * recommended anymore. But someone may prefer to use a real MAC address.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withHardwareAddress() {
		this.nodeIdentifier = getHardwareAddress();
		return (T) this;
	}

	/**
	 * Set a fixed initial clock sequence value to generate UUIDs.
	 * 
	 * The sequence has a range from 0 to 16,383 (0x3fff).
	 * 
	 * Every time a factory is instantiated a random value is set to the
	 * sequence by default. This method allows someone to change this value with
	 * a desired one. It is called "initial" because during the lifetime of the
	 * factory instance, this value may be incremented or even replaced by
	 * another random value to avoid repetition of UUIDs.
	 * 
	 * @param sequence
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withInitialClockSequence(int sequence) {
		this.clockSequence = new ClockSequence(sequence);
		return (T) this;
	}

	/**
	 * @see {@link ClockSequence#setOverrunException(boolean)}
	 * 
	 * @param enabled
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTimeBasedUUIDCreator> T withOverrunException(boolean enabled) {
		this.clockSequence.setOverrunException(enabled);
		return (T) this;
	}

	/**
	 * Format the MSB UUID from the current timestamp.
	 * 
	 * @param timestamp
	 */
	public abstract long getMostSignificantBits(long timestamp);

	/**
	 * Returns the least significant bits of the UUID.
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
	 * @param clockSequence
	 */
	public long getLeastSignificantBits(final long nodeIdentifier, final long clockSequence) {

		long seq = clockSequence << 48;
		long nod = nodeIdentifier & 0x0000ffffffffffffL;

		return setVariantBits(seq | nod);
	}

	/**
	 * Get the node identifier.
	 * 
	 * ### RFC-4122 - 4.1.6. Node
	 * 
	 * (2) For systems with no IEEE address, a randomly or pseudo-randomly
	 * generated value may be used; see Section 4.5. The multicast bit must be
	 * set in such addresses, in order that they will never conflict with
	 * addresses obtained from network cards.
	 * 
	 * @return
	 */
	protected long getNodeIdentifier() {

		if (this.nodeIdentifier == 0) {
			this.nodeIdentifier = AbstractTimeBasedUUIDCreator.setMulticastNodeIdentifier(random.nextLong());
		}

		return this.nodeIdentifier;
	}

	/**
	 * Get the current timestamp.
	 * 
	 * @return
	 */
	protected long getTimestamp() {

		if (this.timestamp != 0) {
			return this.timestamp;
		}

		return timestampStrategy.getCurrentTimestamp();
	}

	/**
	 * Get the machine address.
	 * 
	 * It returns ZERO if no address is found.
	 * 
	 * @return
	 */
	protected long getHardwareAddress() {
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return ByteUtil.toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			return 0;
		}
		return 0;
	}

	/**
	 * Sets the the multicast bit ON to indicate that it's NOT a real MAC
	 * address.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected static long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}

	/**
	 * Sets the the multicast bit OFF to indicat that it's a MAC address.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected static long setUnicastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier & 0xFFFFFEFFFFFFFFFFL;
	}
}