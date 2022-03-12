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

package com.github.f4b6a3.uuid.util;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.ToIntBiFunction;

import com.github.f4b6a3.uuid.UuidCreator;

/**
 * Comparator for UUIDs.
 * 
 * The default static method compares two time-based UUIDs by comparing the
 * timestamps first and then comparing the least significant bits as unsigned
 * 64-bit integers. If both UUIDs are not time-based then it compares them as
 * unsigned 128-bit integers.
 * 
 * The opaque static method compares two UUIDs as unsigned 128-bit integers.
 * It's the same as lexicographic sorting of UUID canonical strings.
 */
public final class UuidComparator implements Comparator<UUID> {

	private final ToIntBiFunction<UUID, UUID> comparator;

	private static final UuidComparator INSTANCE_DEFAULT = new UuidComparator(UuidComparator::defaultCompare);
	private static final UuidComparator INSTANCE_OPAQUE = new UuidComparator(UuidComparator::opaqueCompare);

	private UuidComparator(ToIntBiFunction<UUID, UUID> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Creates a default implementation of {@code UuidComparator}.
	 * 
	 * @see {@link UuidComparator#defaultCompare(UUID, UUID)}
	 * 
	 * @return a {@code UuidComparator}
	 */
	public UuidComparator() {
		this(UuidComparator::defaultCompare);
	}

	/**
	 * Returns a default implementation of {@code UuidComparator}.
	 * 
	 * @see {@link UuidComparator#defaultCompare(UUID, UUID)}
	 * 
	 * @return a {@code UuidComparator}
	 */
	public static UuidComparator getDefaultInstance() {
		return INSTANCE_DEFAULT;
	}

	/**
	 * Returns an opaque implementation of {@code UuidComparator}.
	 * 
	 * @see {@link UuidComparator#opaqueCompare(UUID, UUID)}
	 * 
	 * @return a opaque {@code UuidComparator}
	 */
	public static UuidComparator getOpaqueInstance() {
		return INSTANCE_OPAQUE;
	}

	/**
	 * Compares two UUIDs.
	 * 
	 * The default static method compares two time-based UUIDs by comparing the
	 * timestamps first and then comparing the least significant bits as unsigned
	 * 64-bit integers. If both UUIDs are not time-based then it compares them as
	 * unsigned 128-bit integers.
	 * 
	 * The first of two UUIDs is greater than the second if the timestamp is greater
	 * for the first UUID. If the timestamps are equal, the first of two UUIDs is
	 * greater than the second if the most significant byte in which they differ is
	 * greater for the first UUID.
	 * 
	 * It can be useful for these reasons:
	 * 
	 * 1. {@code UUID#compareTo(UUID)} doesn't work well for time-based UUIDs;
	 * 
	 * 2. {@code UUID#compareTo(UUID)} can lead to unexpected behavior due to signed
	 * {@code long} comparison;
	 * 
	 * 3. {@code UUID#compareTo(UUID)} throws {@code NullPointerException} if a
	 * {@code null} UUID is given.
	 * 
	 * @param uuid1 a {@code UUID}
	 * @param uuid2 another {@code UUID}
	 * @return -1, 0 or 1 as {@code u1} is less than, equal to, or greater than
	 *         {@code u2}
	 */
	public static int defaultCompare(UUID uuid1, UUID uuid2) {

		UUID u1 = uuid1 != null ? uuid1 : UuidCreator.getNil();
		UUID u2 = uuid2 != null ? uuid2 : UuidCreator.getNil();

		// time-based comparison is done by timestamp first
		if (isTimeBased(u1) && isTimeBased(u2)) {
			UUID rearranged1 = new UUID(u1.timestamp(), u1.getLeastSignificantBits());
			UUID rearranged2 = new UUID(u2.timestamp(), u2.getLeastSignificantBits());
			return opaqueCompare(rearranged1, rearranged2);
		}

		// unsigned 128 bit integers
		return opaqueCompare(u1, u2);
	}

	/**
	 * Compares two UUIDs.
	 * 
	 * The opaque static method compares two UUIDs as unsigned 128-bit integers.
	 * It's the same as lexicographic sorting of UUID canonical strings.
	 * 
	 * The first of two UUIDs is greater than the second if the most significant
	 * byte in which they differ is greater for the first UUID.
	 * 
	 * The opaque method is faster than the default method as it does not check the
	 * UUID version.
	 * 
	 * It's referred to as "opaque" just because it works like a "blind byte-to-byte
	 * comparison".
	 * 
	 * It can be useful for these reasons:
	 * 
	 * 1. {@code UUID#compareTo(UUID)} can lead to unexpected behavior due to signed
	 * {@code long} comparison;
	 * 
	 * 2. {@code UUID#compareTo(UUID)} throws {@code NullPointerException} if a
	 * {@code null} UUID is given.
	 * 
	 * @param uuid1 a {@code UUID}
	 * @param uuid2 another {@code UUID}
	 * @return -1, 0 or 1 as {@code u1} is less than, equal to, or greater than
	 *         {@code u2}
	 */
	public static int opaqueCompare(UUID uuid1, UUID uuid2) {

		UUID u1 = uuid1 != null ? uuid1 : UuidCreator.getNil();
		UUID u2 = uuid2 != null ? uuid2 : UuidCreator.getNil();

		final long mask = 0xffffffffL;

		final long msb1 = u1.getMostSignificantBits();
		final long lsb1 = u1.getLeastSignificantBits();
		final long msb2 = u2.getMostSignificantBits();
		final long lsb2 = u2.getLeastSignificantBits();

		final long[] a = { msb1 >>> 32, msb1 & mask, lsb1 >>> 32, lsb1 & mask };
		final long[] b = { msb2 >>> 32, msb2 & mask, lsb2 >>> 32, lsb2 & mask };

		for (int i = 0; i < a.length; i++) {
			if (a[i] > b[i]) {
				return 1;
			} else if (a[i] < b[i]) {
				return -1;
			}
		}

		return 0;
	}

	/**
	 * Compares two UUIDs.
	 * 
	 * @see {@link UuidComparator#defaultCompare(UUID, UUID)}
	 * 
	 * @param uuid1 a {@code UUID}
	 * @param uuid2 another {@code UUID}
	 * @return -1, 0 or 1 as {@code u1} is less than, equal to, or greater than
	 *         {@code u2}
	 */
	@Override
	public int compare(UUID uuid1, UUID uuid2) {
		return this.comparator.applyAsInt(uuid1, uuid2);
	}

	private static boolean isTimeBased(UUID uuid) {
		return uuid.version() == 1 && uuid.variant() == 2;
	}
}
