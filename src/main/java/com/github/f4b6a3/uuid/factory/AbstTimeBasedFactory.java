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
import java.util.concurrent.locks.ReentrantLock;

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

/**
 * Abstract factory for creating time-based unique identifiers (UUIDv1, UUIDv2
 * and UUIDv6).
 * <p>
 * The time stamp has 100-nanoseconds resolution, starting from 1582-10-15,
 * which is a date known as Gregorian Epoch. The the time stamp rolls over
 * around AD 5235 (1582 + 2^60 / 365.25 / 24 / 60 / 60 / 10000000).
 * <p>
 * The node identifier can be:
 * <ul>
 * <li>A MAC address;
 * <li>A hash of host name, MAC and IP;
 * <li>A random number that always changes;
 * <li>A specific number chosen by someone.
 * </ul>
 * <p>
 * The node identifier used by this factory can be controlled by defining a
 * system property <code>'uuidcreator.node'</code> or an environment variable
 * <code>'UUIDCREATOR_NODE'</code>. The system property has preference over the
 * environment variable.
 * <p>
 * Options accepted by the system property and the environment variable:
 * <ul>
 * <li>The string "mac" for using the MAC address;
 * <li>The string "hash" for using a hash of host name, MAC and IP;
 * <li>The string "random" for using a random number that always changes;
 * <li>The string representation of a specific number between 0 and 2^48-1.
 * </ul>
 * <p>
 * If a property or variable is defined, all UUIDs generated by this factory
 * will be based on it.
 * <p>
 * Otherwise, if no property or variable is defined, a random node identifier is
 * generated once at instantiation. This is the default.
 * <p>
 * Example of system property definition:
 * 
 * <pre>{@code
 * # Append to VM arguments
 * -Duuidcreator.node="mac"
 * }</pre>
 * <p>
 * Example of environment variable definition:
 * 
 * <pre>{@code
 * # Append to ~/.profile
 * export UUIDCREATOR_NODE="mac"
 * }</pre>
 *
 * @see TimeFunction
 * @see NodeIdFunction
 * @see ClockSeqFunction
 * @see <a href= "https://www.rfc-editor.org/rfc/rfc4122#section-4.2">RFC-4122 -
 *      4.2. Algorithms for Creating a Time-Based UUID</a>
 */
public abstract class AbstTimeBasedFactory extends UuidFactory implements NoArgsFactory {

	/**
	 * The time function.
	 */
	protected TimeFunction timeFunction;
	/**
	 * The node function.
	 */
	protected NodeIdFunction nodeidFunction;
	/**
	 * The clock sequence function.
	 */
	protected ClockSeqFunction clockseqFunction;

	private static final String NODE_MAC = "mac";
	private static final String NODE_HASH = "hash";
	private static final String NODE_RANDOM = "random";

	/**
	 * The reentrant lock for synchronization.
	 */
	protected final ReentrantLock lock = new ReentrantLock();

	private static final long EPOCH_TIMESTAMP = TimeFunction.toUnixTimestamp(UuidTime.EPOCH_GREG);

	/**
	 * A protected constructor that receives a builder object.
	 * 
	 * @param version the version number (1, 2 or 6)
	 * @param builder a builder object
	 */
	protected AbstTimeBasedFactory(UuidVersion version, Builder<?, ?> builder) {
		super(version);
		this.timeFunction = builder.getTimeFunction();
		this.nodeidFunction = builder.getNodeIdFunction();
		this.clockseqFunction = builder.getClockSeqFunction();
	}

	/**
	 * Returns a time-based UUID.
	 * 
	 * @return a time-based UUID
	 */
	@Override
	public UUID create() {
		lock.lock();
		try {

			// Get the time stamp
			final long timestamp = TimeFunction.toExpectedRange(this.timeFunction.getAsLong() - EPOCH_TIMESTAMP);

			// Get the node identifier
			final long nodeIdentifier = NodeIdFunction.toExpectedRange(this.nodeidFunction.getAsLong());

			// Get the clock sequence
			final long clockSequence = ClockSeqFunction.toExpectedRange(this.clockseqFunction.applyAsLong(timestamp));

			// Format the most significant bits
			final long msb = this.formatMostSignificantBits(timestamp);

			// Format the least significant bits
			final long lsb = this.formatLeastSignificantBits(nodeIdentifier, clockSequence);

			return new UUID(msb, lsb);

		} finally {
			lock.unlock();
		}
	}

	/**
	 * Returns the most significant bits of the UUID.
	 * <p>
	 * It implements the algorithm for generating UUIDv1.
	 * 
	 * @param timestamp the number of 100-nanoseconds since 1970-01-01 (Unix epoch)
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
	 * 
	 * @return a node function
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

	/**
	 * Abstract builder for creating a time-based factory.
	 */
	public abstract static class Builder<T, B extends Builder<T, B>> {

		/**
		 * The time function.
		 */
		protected TimeFunction timeFunction;
		/**
		 * The node function.
		 */
		protected NodeIdFunction nodeidFunction;
		/**
		 * The clock sequence function.
		 */
		protected ClockSeqFunction clockseqFunction;

		/**
		 * Get the time function.
		 * 
		 * @return a function
		 */
		protected TimeFunction getTimeFunction() {
			if (this.timeFunction == null) {
				this.timeFunction = selectTimeFunction();
			}
			return this.timeFunction;
		}

		/**
		 * Get the node function.
		 * 
		 * @return a function
		 */
		protected NodeIdFunction getNodeIdFunction() {
			if (this.nodeidFunction == null) {
				this.nodeidFunction = selectNodeIdFunction();
			}
			return this.nodeidFunction;
		}

		/**
		 * Get the clock sequence function.
		 * 
		 * @return a function
		 */
		protected ClockSeqFunction getClockSeqFunction() {
			if (this.clockseqFunction == null) {
				this.clockseqFunction = new DefaultClockSeqFunction();
			}
			return this.clockseqFunction;
		}

		/**
		 * Set the time function.
		 * 
		 * @param timeFunction a function
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withTimeFunction(TimeFunction timeFunction) {
			this.timeFunction = timeFunction;
			return (B) this;
		}

		/**
		 * Set the node function
		 * 
		 * @param nodeidFunction a function
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withNodeIdFunction(NodeIdFunction nodeidFunction) {
			this.nodeidFunction = nodeidFunction;
			return (B) this;
		}

		/**
		 * Set the clock sequence function
		 * 
		 * @param clockseqFunction a function
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withClockSeqFunction(ClockSeqFunction clockseqFunction) {
			this.clockseqFunction = clockseqFunction;
			return (B) this;
		}

		/**
		 * Set the fixed instant.
		 * 
		 * @param instant an instant
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withInstant(Instant instant) {
			final long timestamp = TimeFunction.toUnixTimestamp(instant);
			this.timeFunction = () -> timestamp;
			return (B) this;
		}

		/**
		 * Set the fixed clock sequence.
		 * 
		 * @param clockseq a clock sequence
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withClockSeq(long clockseq) {
			final long clockSequence = ClockSeqFunction.toExpectedRange(clockseq);
			this.clockseqFunction = x -> clockSequence;
			return (B) this;
		}

		/**
		 * Set a fixed clock sequence.
		 * 
		 * @param clockseq a clock sequence
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withClockSeq(byte[] clockseq) {
			final long clockSequence = ClockSeqFunction.toExpectedRange(ByteUtil.toNumber(clockseq));
			this.clockseqFunction = x -> clockSequence;
			return (B) this;
		}

		/**
		 * Set a fixed node.
		 * 
		 * @param nodeid a node
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withNodeId(long nodeid) {
			final long nodeIdentifier = NodeIdFunction.toExpectedRange(nodeid);
			this.nodeidFunction = () -> nodeIdentifier;
			return (B) this;
		}

		/**
		 * Set a fixed node
		 * 
		 * @param nodeid a node
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withNodeId(byte[] nodeid) {
			final long nodeIdentifier = NodeIdFunction.toExpectedRange(ByteUtil.toNumber(nodeid));
			this.nodeidFunction = () -> nodeIdentifier;
			return (B) this;
		}

		/**
		 * Set the node function to MAC strategy.
		 * 
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withMacNodeId() {
			this.nodeidFunction = new MacNodeIdFunction();
			return (B) this;
		}

		/**
		 * Set the node function to hash strategy.
		 * 
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withHashNodeId() {
			this.nodeidFunction = new HashNodeIdFunction();
			return (B) this;
		}

		/**
		 * Set the node function to random strategy.
		 * 
		 * @return the builder
		 */
		@SuppressWarnings("unchecked")
		public B withRandomNodeId() {
			this.nodeidFunction = new RandomNodeIdFunction();
			return (B) this;
		}

		/**
		 * Finish the factory building.
		 * 
		 * @return the built factory
		 */
		public abstract T build();
	}
}