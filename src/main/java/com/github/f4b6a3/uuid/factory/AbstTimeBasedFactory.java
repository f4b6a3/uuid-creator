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

package com.github.f4b6a3.uuid.factory;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.function.ClockSeqFunction;
import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.TimeFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultClockSeqFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultTimeFunction;
import com.github.f4b6a3.uuid.factory.function.impl.HashNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.MacNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.RandomNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.WindowsTimeFunction;
import com.github.f4b6a3.uuid.util.UuidTime;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;
import com.github.f4b6a3.uuid.util.internal.SettingsUtil;

public abstract class AbstTimeBasedFactory extends UuidFactory implements NoArgsFactory {

	protected TimeFunction timeFunction;
	protected NodeIdFunction nodeidFunction;
	protected ClockSeqFunction clockseqFunction;

	private static final String NODE_MAC = "mac";
	private static final String NODE_HASH = "hash";
	private static final String NODE_RANDOM = "random";

	private static final long EPOCH_TIMESTAMP = TimeFunction.toTimestamp(UuidTime.EPOCH_GREG);

	protected AbstTimeBasedFactory(UuidVersion version, Builder<?> builder) {
		super(version);
		this.timeFunction = builder.getTimeFunction();
		this.nodeidFunction = builder.getNodeIdFunction();
		this.clockseqFunction = builder.getClockSeqFunction();
	}

	/**
	 * Returns a time-based UUID.
	 * 
	 * ### Timestamp
	 * 
	 * The timestamp has 100-nanoseconds resolution, starting from 1582-10-15. It
	 * uses 60 bits from the most significant bits. The the time value rolls over
	 * around AD 5235 (1582 + 2^60 / 365.25 / 24 / 60 / 60 / 10000000).
	 * 
	 * The node identifier can be:
	 * 
	 * - The host MAC address;
	 * 
	 * - The hash of system data;
	 * 
	 * - A random number;
	 * 
	 * - A specific number.
	 * 
	 * The node identifier can be controlled by defining a system property
	 * 'uuidcreator.node' or an environment variable 'UUIDCREATOR_NODE'. Both
	 * property and variable accept one of three options:
	 * 
	 * - The string "mac" for using the MAC address;
	 * 
	 * - The string "hash" for using the system data hash;
	 * 
	 * - The string "random" for using a random number that always changes.
	 * 
	 * - The string representation of a number between 0 and 2^48-1.
	 * 
	 * If no property or variable is defined, the node identifier is randomly chosen
	 * implicitly.
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
	 */
	@Override
	public synchronized UUID create() {

		// (3a) get the timestamp
		final long timestamp = this.timeFunction.getAsLong() - EPOCH_TIMESTAMP;

		// (4a)(5a) get the node identifier
		final long nodeIdentifier = this.nodeidFunction.getAsLong();

		// (5a)(6a) get the sequence value
		final long clockSequence = this.clockseqFunction.applyAsLong(timestamp);

		// (9a) format the most significant bits
		final long msb = this.formatMostSignificantBits(timestamp);

		// (9a) format the least significant bits
		final long lsb = this.formatLeastSignificantBits(nodeIdentifier, clockSequence);

		// (9a) format a UUID from the MSB and LSB
		return new UUID(msb, lsb);
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
		return ((((clockSequence << 48) | (nodeIdentifier & 0x0000ffffffffffffL)) //
				& 0x3fffffffffffffffL) // clear variant bits
				| 0x8000000000000000L); // apply variant bits
	}

	/**
	 * Select the node identifier function.
	 * 
	 * This method reads the system property 'uuidcreator.node' and the environment
	 * variable 'UUIDCREATOR_NODE' to decide what node identifier function must be
	 * used.
	 * 
	 * 1. If it finds the string "mac", the generator will use the MAC address.
	 * 
	 * 2. If it finds the string "hash", the generator will use the system data
	 * hash.
	 * 
	 * 3. If it finds the string "random", the generator will use a random number
	 * that always changes.
	 * 
	 * 4. If it finds the string representation of a specific number in octal,
	 * hexadecimal or decimal format, the generator will use the number represented.
	 * 
	 * 5. Else, a random number will be used by the generator.
	 */
	protected static NodeIdFunction selectNodeIdFunction() {

		String string = SettingsUtil.getProperty(SettingsUtil.PROPERTY_NODE);

		if (NODE_MAC.equalsIgnoreCase(string)) {
			return new MacNodeIdFunction();
		}

		if (NODE_HASH.equalsIgnoreCase(string)) {
			return new HashNodeIdFunction();
		}

		if (NODE_RANDOM.equalsIgnoreCase(string)) {
			return new RandomNodeIdFunction();
		}

		Long number = SettingsUtil.getNodeIdentifier();
		if (number != null) {
			final long nodeid = NodeIdFunction.toExpectedRange(number);
			return () -> nodeid;
		}

		return new DefaultNodeIdFunction();
	}

	/**
	 * Select the time function.
	 * 
	 * If the operating system is WINDOWS, it returns a function that is more
	 * efficient for its typical time granularity (15.6ms). Otherwise, it returns
	 * the default time function.
	 * 
	 * @return a time function
	 */
	protected static TimeFunction selectTimeFunction() {

		// check if the operating system is WINDOWS
		final String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().startsWith("win")) {
			return new WindowsTimeFunction();
		}

		return new DefaultTimeFunction();
	}

	public abstract static class Builder<T> {

		protected TimeFunction timeFunction;
		protected NodeIdFunction nodeidFunction;
		protected ClockSeqFunction clockseqFunction;

		protected TimeFunction getTimeFunction() {
			if (this.timeFunction == null) {
				this.timeFunction = selectTimeFunction();
			}
			return this.timeFunction;
		}

		protected NodeIdFunction getNodeIdFunction() {
			if (this.nodeidFunction == null) {
				this.nodeidFunction = selectNodeIdFunction();
			}
			return this.nodeidFunction;
		}

		protected ClockSeqFunction getClockSeqFunction() {
			if (this.clockseqFunction == null) {
				this.clockseqFunction = new DefaultClockSeqFunction();
			}
			return this.clockseqFunction;
		}

		public Builder<T> withTimeFunction(TimeFunction timeFunction) {
			this.timeFunction = timeFunction;
			return this;
		}

		public Builder<T> withNodeIdFunction(NodeIdFunction nodeidFunction) {
			this.nodeidFunction = nodeidFunction;
			return this;
		}

		public Builder<T> withClockSeqFunction(ClockSeqFunction clockseqFunction) {
			this.clockseqFunction = clockseqFunction;
			return this;
		}

		public Builder<T> withInstant(Instant instant) {
			final long timestamp = TimeFunction.toTimestamp(instant);
			this.timeFunction = () -> timestamp;
			return this;
		}

		public Builder<T> withClockSeq(long clockseq) {
			final long clockSequence = ClockSeqFunction.toExpectedRange(clockseq);
			this.clockseqFunction = x -> clockSequence;
			return this;
		}

		public Builder<T> withClockSeq(byte[] clockseq) {
			final long clockSequence = ClockSeqFunction.toExpectedRange(ByteUtil.toNumber(clockseq));
			this.clockseqFunction = x -> clockSequence;
			return this;
		}

		public Builder<T> withNodeId(long nodeid) {
			final long nodeIdentifier = NodeIdFunction.toExpectedRange(nodeid);
			this.nodeidFunction = () -> nodeIdentifier;
			return this;
		}

		public Builder<T> withNodeId(byte[] nodeid) {
			final long nodeIdentifier = NodeIdFunction.toExpectedRange(ByteUtil.toNumber(nodeid));
			this.nodeidFunction = () -> nodeIdentifier;
			return this;
		}

		public Builder<T> withMacNodeId() {
			this.nodeidFunction = new MacNodeIdFunction();
			return this;
		}

		public Builder<T> withHashNodeId() {
			this.nodeidFunction = new HashNodeIdFunction();
			return this;
		}

		public Builder<T> withRandomNodeId() {
			this.nodeidFunction = new RandomNodeIdFunction();
			return this;
		}

		public abstract T build();
	}
}