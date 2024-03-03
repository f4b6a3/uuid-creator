package com.github.f4b6a3.uuid.util;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

/**
 * A UUID builder.
 * <p>
 * Usage:
 * 
 * <pre>{@code
 * Random random = new Random();
 * UUID uuid = new UuidBuilder(4) // sets version 4 (random-based)
 * 		.put(random.nextLong()) // put the most significant 64 bits
 * 		.put(random.nextLong()) // put the least significant 64 bits
 * 		.build(); // return the built UUID
 * }</pre>
 */
public class UuidBuilder {

	private int version;

	// newly-created byte buffers are always BIG_ENDIAN
	private ByteBuffer buffer = ByteBuffer.allocate(16);

	/**
	 * Instantiates a new builder.
	 * 
	 * @param version a value between 0 and 15
	 */
	public UuidBuilder(int version) {
		if (version < 0x00L || version > 0xfL) {
			throw new IllegalArgumentException("Invalid version number");
		}
		this.version = version;
	}

	/**
	 * Puts 8 bytes containing the given long.
	 *
	 * @param value a long value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 8 bytes remaining
	 */
	public synchronized UuidBuilder put(long value) {
		buffer.putLong(value);
		return this;
	}

	/**
	 * Puts 4 bytes containing the given int.
	 *
	 * @param value an int value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 4 bytes remaining
	 */
	public synchronized UuidBuilder put(int value) {
		buffer.putInt(value);
		return this;
	}

	/**
	 * Puts 2 bytes containing the given short.
	 *
	 * @param value a short value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 2 bytes remaining
	 */
	public synchronized UuidBuilder put(short value) {
		buffer.putShort(value);
		return this;
	}

	/**
	 * Puts the given byte.
	 *
	 * @param value a byte value
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer than 1 bytes remaining
	 */
	public synchronized UuidBuilder put(byte value) {
		buffer.put(value);
		return this;
	}

	/**
	 * Puts the given byte array.
	 *
	 * @param value a byte array
	 *
	 * @return This buffer
	 *
	 * @throws BufferOverflowException If there are fewer bytes remaining than the
	 *                                 array length
	 */
	public synchronized UuidBuilder put(byte[] array) {
		buffer.put(array);
		return this;
	}

	/**
	 * Builds a UUID after all 16 bytes are filled.
	 * <p>
	 * This method ends the use of a builder.
	 * <p>
	 * Successive calls will always return the same UUID value.
	 * <p>
	 * Note: this method overrides bits 48 through 51 (version field) and bits 52
	 * through 63 (variant field), 6 bits total, to comply the UUID specification.
	 * 
	 * @throws BufferOverflowException If there are bytes remaining to be filled
	 */
	public synchronized UUID build() {

		validate();
		buffer.rewind();

		// set the 4 most significant bits of the 7th byte (version field)
		final long msb = (buffer.getLong() & 0xffff_ffff_ffff_0fffL) | (version & 0xfL) << 12;
		// set the 2 most significant bits of the 9th byte to 1 and 0 (variant field)
		final long lsb = (buffer.getLong() & 0x3fff_ffff_ffff_ffffL) | 0x8000_0000_0000_0000L;

		return new UUID(msb, lsb);
	}

	private synchronized void validate() {
		if (buffer.hasRemaining()) {
			throw new BufferUnderflowException();
		}
	}

	public static void main(String[] args) {

		for (int i = 0; i < 100; i++) {

			Random random = new Random();

			UUID uuid = new UuidBuilder(4) // sets version 4 (random-based)
					.put(random.nextLong()) // puts the most significant 64 bits
					.put(random.nextLong()) // puts the least significant 64 bits
					.build(); // returns the built UUID

			System.out.println(uuid);
		}
	}
}
