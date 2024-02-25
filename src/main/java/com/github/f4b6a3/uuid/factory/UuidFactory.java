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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import com.github.f4b6a3.uuid.codec.BinaryCodec;
import com.github.f4b6a3.uuid.codec.StringCodec;
import com.github.f4b6a3.uuid.enums.UuidLocalDomain;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.exception.InvalidUuidException;

/**
 * Abstract factory that is base for all UUID factories.
 */
public abstract class UuidFactory {

	/**
	 * Version number.
	 */
	protected final UuidVersion version;

	/**
	 * Version bit mask.
	 */
	protected final long versionMask;

	/**
	 * Default Constructor.
	 * 
	 * The version used is {@link UuidVersion#VERSION_UNKNOWN}.
	 */
	public UuidFactory() {
		this.version = UuidVersion.VERSION_UNKNOWN;
		this.versionMask = (long) version.getValue() << 12;
	}

	/**
	 * Constructor with a version number.
	 * 
	 * @param version a version number
	 */
	public UuidFactory(UuidVersion version) {
		this.version = version;
		this.versionMask = (long) version.getValue() << 12;
	}

	/**
	 * Returns the version number for this factory.
	 * 
	 * @return the version number
	 */
	public UuidVersion getVersion() {
		return this.version;
	}

	/**
	 * Create a UUID
	 * 
	 * @return a UUID
	 */
	public abstract UUID create();

	/**
	 * Creates a UUID using parameters.
	 * 
	 * @param parameters parameters object
	 * @return a UUID
	 */
	public abstract UUID create(Parameters parameters);

	/**
	 * Parameters object to be used with a {@link UuidFactory#create(Parameters)}.
	 */
	public static class Parameters {

		/**
		 * Name space byte array.
		 */
		private final byte[] namespace;

		/**
		 * Name byte array.
		 */
		private final byte[] name;

		/**
		 * Local domain byte.
		 */
		private final byte localDomain;

		/**
		 * Local identifier number.
		 */
		private final int localIdentifier;

		/**
		 * Constructor using a builder.
		 * 
		 * @param builder a builder
		 */
		public Parameters(Builder builder) {
			Objects.requireNonNull(builder);
			this.namespace = builder.namespace;
			this.name = builder.name;
			this.localDomain = builder.localDomain;
			this.localIdentifier = builder.localIdentifier;
		}

		/**
		 * Get the name space bytes
		 * 
		 * @return a byte array
		 */
		public byte[] getNamespace() {
			return this.namespace;
		}

		/**
		 * Get the name bytes
		 * 
		 * @return a byte array
		 */
		public byte[] getName() {
			return this.name;
		}

		/**
		 * Get the local domain.
		 * 
		 * @return the local domain
		 */
		public byte getLocalDomain() {
			return this.localDomain;
		}

		/**
		 * Get the local identifier.
		 * 
		 * @return the local identifier
		 */
		public int getLocalIdentifier() {
			return this.localIdentifier;
		}

		/**
		 * Returns a new builder.
		 * 
		 * @return a builder
		 */
		public static Builder builder() {
			return new Builder();
		}

		/**
		 * Parameters builder.
		 */
		public static class Builder {

			/**
			 * Name space byte array.
			 */
			private byte[] namespace = null;

			/**
			 * Name byte array.
			 */
			private byte[] name = null;

			/**
			 * Local domain byte.
			 */
			private byte localDomain;

			/**
			 * Local identifier number.
			 */
			private int localIdentifier;

			private Builder() {
			}

			/**
			 * Use the name space UUID.
			 * 
			 * @param namespace a name space
			 * @return the builder
			 */
			public Builder withNamespace(UUID namespace) {
				this.namespace = namespaceBytes(namespace);
				return this;
			}

			/**
			 * Use the name space string.
			 * 
			 * @param namespace a name space
			 * @return the builder
			 */
			public Builder withNamespace(String namespace) {
				this.namespace = namespaceBytes(namespace);
				return this;
			}

			/**
			 * Use the name space enum.
			 * 
			 * @param namespace a name space
			 * @return the builder
			 */
			public Builder withNamespace(UuidNamespace namespace) {
				this.namespace = namespaceBytes(namespace);
				return this;
			}

			/**
			 * Use the name byte array.
			 * 
			 * It makes a copy of the input byte array.
			 * 
			 * @param name a name
			 * @return the builder
			 */
			public Builder withName(byte[] name) {
				this.name = nameBytes(name);
				return this;
			}

			/**
			 * Use the name string.
			 * 
			 * The string is encoded into UTF-8 byte array.
			 * 
			 * @param name a name
			 * @return the builder
			 */
			public Builder withName(String name) {
				this.name = nameBytes(name);
				return this;
			}

			/**
			 * Use the local domain.
			 * 
			 * @param localDomain the local domain
			 * @return the builder
			 */
			public Builder withLocalDomain(UuidLocalDomain localDomain) {
				this.localDomain = localDomain.getValue();
				return this;
			}

			/**
			 * Use the local domain.
			 * 
			 * @param localDomain the local domain
			 * @return the builder
			 */
			public Builder withLocalDomain(byte localDomain) {
				this.localDomain = localDomain;
				return this;
			}

			/**
			 * Use the local identifier.
			 * 
			 * @param localIdentifier the local identifier
			 * @return the builder
			 */
			public Builder withLocalIdentifier(int localIdentifier) {
				this.localIdentifier = localIdentifier;
				return this;
			}

			/**
			 * Finishes the parameters build.
			 * 
			 * @return the build parameters.
			 */
			public Parameters build() {
				return new Parameters(this);
			}
		}
	}

	/**
	 * Returns a copy of the input byte array.
	 * 
	 * @param name a name string
	 * @return a byte array
	 * @throws IllegalArgumentException if the input is null
	 */
	protected static byte[] nameBytes(byte[] name) {
		Objects.requireNonNull(name, "Null name");
		return Arrays.copyOf(name, name.length);
	}

	/**
	 * Converts a name string into a byte array.
	 * 
	 * @param name a name string
	 * @return a byte array
	 * @throws IllegalArgumentException if the input is null
	 */
	protected static byte[] nameBytes(String name) {
		Objects.requireNonNull(name, "Null name");
		return name.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Converts a name space enumeration into a byte array.
	 * 
	 * @param namespace a name space enumeration
	 * @return a byte array
	 */
	protected static byte[] namespaceBytes(UuidNamespace namespace) {
		if (namespace != null) {
			return namespaceBytes(namespace.getValue());
		}
		return null; // the name space can be null
	}

	/**
	 * Converts a name space UUID into a byte array.
	 * 
	 * @param namespace a name space UUID
	 * @return a byte array
	 */
	protected static byte[] namespaceBytes(UUID namespace) {
		if (namespace != null) {
			return BinaryCodec.INSTANCE.encode(namespace);
		}
		return null; // the name space can be null
	}

	/**
	 * Converts a name space string into a byte array.
	 * 
	 * @param namespace a name space string
	 * @return a byte array
	 * @throws InvalidUuidException if the name space is invalid
	 */
	protected static byte[] namespaceBytes(String namespace) {
		if (namespace != null) {
			return BinaryCodec.INSTANCE.encode(StringCodec.INSTANCE.decode(namespace));
		}
		return null; // the name space can be null
	}

	/**
	 * Creates a UUID from a pair of numbers.
	 * <p>
	 * It applies the version and variant numbers to the resulting UUID.
	 * 
	 * @param msb the most significant bits
	 * @param lsb the least significant bits
	 * @return a UUID
	 */
	protected UUID toUuid(final long msb, final long lsb) {
		final long msb0 = (msb & 0xffffffffffff0fffL) | this.versionMask; // set version
		final long lsb0 = (lsb & 0x3fffffffffffffffL) | 0x8000000000000000L; // set variant
		return new UUID(msb0, lsb0);
	}
}
