/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.standard;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactory;

/**
 * 
 * Concrete factory for creating DCE Security unique identifiers (UUIDv2).
 * 
 * @see UuidLocalDomain
 * @see <a href=
 *      "https://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01">DCE
 *      Security UUIDs</a>
 */
public final class DceSecurityFactory extends AbstTimeBasedFactory {

	private AtomicInteger counter;

	private final byte localDomain;

	/**
	 * Default constructor.
	 */
	public DceSecurityFactory() {
		this(builder());
	}

	private DceSecurityFactory(Builder builder) {
		super(UuidVersion.VERSION_DCE_SECURITY, builder);
		this.localDomain = builder.localDomain;
		this.counter = new AtomicInteger();
	}

	/**
	 * Returns a builder of DCE Security factory.
	 * 
	 * @return a builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Concrete builder for creating a DCE Security factory.
	 * 
	 * @see AbstTimeBasedFactory.Builder
	 */
	public static class Builder extends AbstTimeBasedFactory.Builder<DceSecurityFactory, Builder> {

		private byte localDomain;

		/**
		 * Set the local domain.
		 * 
		 * @param localDomain the local domain
		 * @return the builder
		 */
		public Builder withLocalDomain(UuidLocalDomain localDomain) {
			this.localDomain = localDomain.getValue();
			return this;
		}

		/**
		 * Set the local domain.
		 * 
		 * @param localDomain the local domain
		 * @return the builder
		 */
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
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * A DCE Security UUID is a modified UUIDv1.
	 * <p>
	 * Steps of creation:
	 * <ol>
	 * <li>Create a Time-based UUIDv1;
	 * <li>Replace the least significant 8 bits of the clock sequence with the local
	 * domain;
	 * <li>Replace the least significant 32 bits of the time stamp with the local
	 * identifier.
	 * </ol>
	 * 
	 * @param localDomain     a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public UUID create(byte localDomain, int localIdentifier) {

		// Create a UUIDv1
		UUID uuid = super.create();

		// Embed the local domain bits
		final long lsb = embedLocalDomain(uuid.getLeastSignificantBits(), localDomain, this.counter.incrementAndGet());

		// Embed the local identifier bits
		final long msb = emgedLocalIdentifier(uuid.getMostSignificantBits(), localIdentifier);

		return toUuid(msb, lsb);
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * 
	 * @param localDomain     a local domain
	 * @param localIdentifier a local identifier
	 * @return a DCE Security UUID
	 */
	public UUID create(UuidLocalDomain localDomain, int localIdentifier) {
		return create(localDomain.getValue(), localIdentifier);
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * <p>
	 * The local domain is local domain used by this method defined by builder:
	 * 
	 * <pre>{@code
	 * DceSecurityFactory factory = DceSecurityFactory.builder().withLocalDomain(UuidLocalDomain).build();
	 * }</pre>
	 * 
	 * @param localIdentifier a local identifier
	 * @return a UUIDv2
	 */
	public UUID create(int localIdentifier) {
		return create(this.localDomain, localIdentifier);
	}

	/**
	 * Always throws an exception.
	 * <p>
	 * Overrides the method {@link AbstTimeBasedFactory#create()} to throw an
	 * exception instead of returning a UUID.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public UUID create() {
		throw new UnsupportedOperationException("Unsuported operation for DCE Security UUID factory");
	}

	/**
	 * Returns a DCE Security unique identifier (UUIDv2).
	 * 
	 * @return a UUIDv2
	 */
	@Override
	public UUID create(Parameters parameters) {
		return create(parameters.getLocalDomain(), parameters.getLocalIdentifier());
	}

	/**
	 * Embeds the local identifier in into the most significant bits.
	 * 
	 * @param msb             the MSB
	 * @param localIdentifier the local identifier
	 * @return the updated MSB
	 */
	private static long emgedLocalIdentifier(long msb, int localIdentifier) {
		return (msb & 0x00000000ffffffffL) // clear time_low bits
				| ((localIdentifier & 0x00000000ffffffffL) << 32);
	}

	/**
	 * Embeds the local domain bits in the least significant bits.
	 * 
	 * @param lsb         the LSB
	 * @param localDomain a local domain
	 * @param counter     a counter value
	 * @return the updated LSB
	 */
	private static long embedLocalDomain(long lsb, byte localDomain, long counter) {
		return (lsb & 0x0000ffffffffffffL) // clear clock_seq bits
				| ((localDomain & 0x00000000000000ffL) << 48) //
				| ((counter & 0x00000000000000ffL) << 56);
	}
}