package com.github.f4b6a3.uuid.factory.abst;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import com.github.f4b6a3.uuid.factory.TimeBasedUUIDCreator;
import com.github.f4b6a3.uuid.increment.ClockSequence;
import com.github.f4b6a3.uuid.increment.TimestampCounter;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.strategy.TimeBasedUUIDStrategy;
import com.github.f4b6a3.uuid.util.ByteUtils;
import com.github.f4b6a3.uuid.util.TimestampUtils;

public abstract class AbstractTimeBasedUUIDCreator extends AbstractUUIDCreator {

	private static final long serialVersionUID = -5359654877586361230L;

	/*
	 * -------------------------
	 * Private fields
	 * -------------------------
	 */
	// State fields
	
	// Strategies for time based UUID
	protected TimeBasedUUIDStrategy timeBasedUUIDStrategy;
	
	// incrementable fields
	protected TimestampCounter timestampCounter;
	protected ClockSequence clockSequence;
	
	// Fixed fields
	protected long timestamp = 0;
	protected long nodeIdentifier = 0;
	
	// Random number generator
	protected Random random;
	
	/* 
	 * -------------------------
	 * Public constructors
	 * -------------------------
	 */
	public AbstractTimeBasedUUIDCreator(int version, TimeBasedUUIDStrategy timeBasedUUIDStrategy) {
		super(version);
		
		this.timeBasedUUIDStrategy = timeBasedUUIDStrategy;
		this.random = new Xorshift128PlusRandom();
		
		this.timestampCounter = new TimestampCounter();
		this.clockSequence = new ClockSequence();
	}
	
	/* 
	 * --------------------------------
	 * Public fluent interface methods
	 * --------------------------------
	 */
	
	public AbstractTimeBasedUUIDCreator withRandomGenerator(Random random) {
		this.random = random;
		return this;
	}
	
	public AbstractTimeBasedUUIDCreator withInstant(Instant instant) {
		this.timestamp = TimestampUtils.toTimestamp(instant);
		return this;
	}
	
	public AbstractTimeBasedUUIDCreator withTimestamp(long timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	/**
	 * Set a fixed node identifier to generate UUIDs.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Someone may think it's useful in some special case
	 * to use a fixed node identifier other than random value.
	 * 
	 * This method will always set the multicast bit to indicate that the value
	 * is not a real MAC address.
	 * 
	 * If you want to inform a fixed value that is real MAC address, use the
	 * method {@link TimeBasedUUIDCreator#withMulticastNodeIdentifier(long)}, which
	 * doesn't change the multicast bit.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	public AbstractTimeBasedUUIDCreator withMulticastNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = setMulticastNodeIdentifier(nodeIdentifier);
		return this;
	}
	
	public AbstractTimeBasedUUIDCreator withUnicastNodeIdentifier(long nodeIdentifier) {
		this.nodeIdentifier = setUnicastNodeIdentifier(nodeIdentifier);
		return this;
	}
	
	/**
	 * Set the node identifier to be a real hardware address of the host
	 * machine.
	 * 
	 * Every time a factory is instantiated a random value is set to the node
	 * identifier by default. Today to use a real hardware address is
	 * not recommended anymore. But someone may prefer to use a real MAC
	 * address.
	 * 
	 * @return
	 */
	public AbstractTimeBasedUUIDCreator withHardwareAddressNodeIdentifier() {
		this.nodeIdentifier = getHardwareAddress();
		return this;
	}
	
	/**
	 * Set a fixed initial sequence value to generate UUIDs.
	 * 
	 * The sequence has a range from 0 to 16,383.
	 * 
	 * Every time a factory is instantiated a random value is set to the sequence
	 * by default. This method allows someone to change this value with a
	 * desired one. It is called "initial" because during the lifetime of the
	 * factory instance, this value may be incremented or even replaced by
	 * another random value to avoid repetition of UUIDs.
	 * 
	 * @param sequence
	 * @return
	 */
	public AbstractTimeBasedUUIDCreator withInitialClockSequence(int sequence) {
		this.clockSequence = new ClockSequence(sequence);
		return this;
	}
	
	/* 
	 * --------------------------------
	 * 
	 * --------------------------------
	 */
	
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
	 * @return {@link UUID}
	 */
	public synchronized UUID create() {
		
		// (3a) get the timestamp
		long timestamp = this.getTimestamp();

		// (4b) get the node identifier
		long nodeIdentifier = this.getNodeIdentifier();
		
		// (4b) get the counter value
		long counter = timestampCounter.getNextFor(timestamp);
		
		// (5a)(6a) get the sequence value
		long sequence = clockSequence.getNextFor(timestamp, nodeIdentifier);
		
		// (9a) format the most significant bits
		long msb = getMostSignificantBits(timestamp, counter);
		
		// (9a) format the least significant bits
		long lsb = getLeastSignificantBits(nodeIdentifier, sequence);
		
		return new UUID(msb, lsb);
	}
	
	
	/* 
	 * --------------------------------
	 * 
	 * --------------------------------
	 */
	
	
	/**
	 * Returns the timestamp bits of the UUID in the order defined in the
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
	 * Set the time_mid field equal to bits 32 through 47 from the timestamp in
	 * the same order of significance.
	 * 
	 * Set the 12 least significant bits (bits zero through 11) of the
	 * time_hi_and_version field equal to bits 48 through 59 from the timestamp
	 * in the same order of significance.
	 * 
	 * Set the four most significant bits (bits 12 through 15) of the
	 * time_hi_and_version field to the 4-bit version number corresponding to
	 * the UUID version being created, as shown in the table above."
	 * 
	 * @param timestamp
	 */
	
	/**
	 * Returns the most significant bits of the UUID.
	 * 
	 * It is a extension suggested by the RFC-4122.
	 * 
	 * {@link TimeBasedUUIDCreator#getStandardTimestampBits(long)}
	 * 
	 * #### RFC-4122 - 4.2.1.2. System Clock Resolution
	 * 
	 * (4) A high resolution timestamp can be simulated by keeping a count of
	 * the number of UUIDs that have been generated with the same value of the
	 * system time, and using it to construct the low order bits of the
	 * timestamp. The count will range between zero and the number of
	 * 100-nanosecond intervals per system time interval.
	 * 
	 * @param counter
	 */
	protected long getMostSignificantBits(final long timestamp, final long counter) {
		long msb = this.timeBasedUUIDStrategy.getMostSignificantBits(timestamp + counter);
		return setVersionBits(msb);
	}
	
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
	 */
	public long getLeastSignificantBits(final long nodeIdentifier, final long sequence) {
		
		long seq = sequence << 48;
		seq = setVariantBits(seq);
		
		long nod = nodeIdentifier & 0x0000ffffffffffffL;
		
		return (seq | nod);
	}
	

	protected long getNodeIdentifier() {
		
		if (this.nodeIdentifier == 0) {
			this.nodeIdentifier = setMulticastNodeIdentifier(random.nextLong());
		}
		
		return this.nodeIdentifier;
	}
	
	protected long getTimestamp() {
		
		if(this.timestamp != 0) {
			return this.timestamp;
		}
		
		return TimestampUtils.getCurrentTimestamp();
	}
	
	protected long getHardwareAddress() {
		try {
			NetworkInterface nic = NetworkInterface.getNetworkInterfaces().nextElement();
			byte[] realHardwareAddress = nic.getHardwareAddress();
			if (realHardwareAddress != null) {
				return ByteUtils.toNumber(realHardwareAddress);
			}
		} catch (SocketException | NullPointerException e) {
			return 0;
		}
		return 0;
	}
	
	/**
	 * Sets the the multicast bit on.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected long setMulticastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier | 0x0000010000000000L;
	}
	
	/**
	 * Sets the the multicast bit off.
	 * 
	 * @param nodeIdentifier
	 * @return
	 */
	protected long setUnicastNodeIdentifier(long nodeIdentifier) {
		return nodeIdentifier & 0xFFFFFEFFFFFFFFFFL;
	}
}