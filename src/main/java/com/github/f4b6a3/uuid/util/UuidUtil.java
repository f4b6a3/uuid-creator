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

package com.github.f4b6a3.uuid.util;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.commons.util.ByteUtil;
import com.github.f4b6a3.uuid.enums.UuidVariant;
import com.github.f4b6a3.uuid.enums.UuidVersion;

public class UuidUtil {

	public static final String UUID_PATTERN_STRICT = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";
	public static final String UUID_PATTERN_LOOSE = "^(\\{?[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\}?|[0-9a-fA-F]{32})$";

	private static final String NOT_DCE_SECURITY = "Not a DCE Security UUID: %s.";

	private UuidUtil() {
	}

	/**
	 * Get the UUID version.
	 * 
	 * @param uuid a UUID
	 * @return a {@link UuidVersion}
	 */
	public UuidVersion getVersion(UUID uuid) {
		return UuidVersion.getVersion(uuid.version());
	}

	/**
	 * Checks whether the UUID is equal to the Nil UUID.
	 * 
	 * The nil UUID is special UUID that has all 128 bits set to zero.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an RFC4122 variant
	 */
	public static boolean isNil(UUID uuid) {
		return uuid.getMostSignificantBits() == 0L && uuid.getLeastSignificantBits() == 0L;
	}

	/**
	 * Checks whether the UUID variant is the one defined by the RFC-4122.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an RFC4122 variant
	 */
	public static boolean isRfc4122(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RFC4122);
	}

	/**
	 * Checks whether the UUID variant is reserved NCS.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved NCS variant
	 */
	public static boolean isReservedNcs(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_NCS);
	}

	/**
	 * Checks whether the UUID variant is reserved Microsoft.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved Microsoft variant
	 */
	public static boolean isReservedMicrosoft(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_MICROSOFT);
	}

	/**
	 * Checks whether the UUID variant is reserved future.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is an reserved future variant
	 */
	public static boolean isReservedFuture(UUID uuid) {
		return isVariant(uuid, UuidVariant.VARIANT_RESERVED_FUTURE);
	}

	private static boolean isVariant(UUID uuid, UuidVariant variant) {
		return (uuid.variant() == variant.getValue());
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * The validation mode is not strict.
	 * 
	 * See {@link UuidUtil#isValid(String, boolean)}
	 * 
	 * @param uuid a UUID string
	 * @return boolean true if valid
	 */
	protected static boolean isValid(String uuid) {
		return isValid(uuid, false);
	}

	/**
	 * Checks if the UUID string is valid.
	 * 
	 * <pre>
	 * Strict validation: accepts only the RFC-4122 format:
	 * 
	 * 12345678-abcd-1bcd-abcd-123456789abcd (36 hexadecimal chars, case insensitive, with dash and RFC-4122 version and variant)
	 * 
	 * Loose validation: accepts UUID strings similar to this examples:
	 * 
	 * 12345678abcdabcdabcd123456789abcd       (32 hexadecimal chars, lower case and without dash)
	 * 12345678ABCDABCDABCD123456789ABCD       (32 hexadecimal chars, UPPER CASE and without dash)
	 * 12345678-abcd-abcd-abcd-123456789abcd   (36 hexadecimal chars, lower case and with dash)
	 * 12345678-ABCD-ABCD-ABCD-123456789ABCD   (36 hexadecimal chars, UPPER CASE and with dash)
	 * {12345678-abcd-abcd-abcd-123456789abcd} (38 hexadecimal chars, lower case, with dash and curly braces)
	 * {12345678-ABCD-ABCD-ABCD-123456789ABCD} (38 hexadecimal chars, UPPER CASE, with dash and curly braces)
	 * </pre>
	 * 
	 * @param uuid   a UUID string
	 * @param strict true for strict validation, false for loose validation
	 * @return boolean true if valid
	 */
	public static boolean isValid(String uuid, boolean strict) {
		if (strict) {
			return uuid != null && uuid.matches(UUID_PATTERN_STRICT);
		}
		return uuid != null && uuid.matches(UUID_PATTERN_LOOSE);
	}

	/**
	 * Checks whether the UUID version 4.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a random UUID
	 */
	public static boolean isRandomBased(UUID uuid) {
		return isVersion(uuid, UuidVersion.RANDOM_BASED);
	}

	/**
	 * Checks whether the UUID version 3.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a name-based UUID
	 */
	public static boolean isNameBasedMd5(UUID uuid) {
		return isVersion(uuid, UuidVersion.NAME_BASED_MD5);
	}

	/**
	 * Checks whether the UUID version 5.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a name-based UUID
	 */
	public static boolean isNameBasedSha1(UUID uuid) {
		return isVersion(uuid, UuidVersion.NAMBE_BASED_SHA1);
	}

	/**
	 * Checks whether the UUID version 1.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a time-based UUID
	 */
	public static boolean isTimeBased(UUID uuid) {
		return isVersion(uuid, UuidVersion.TIME_BASED);
	}

	/**
	 * Checks whether the UUID version 0.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a sequential UUID
	 */
	public static boolean isSequential(UUID uuid) {
		return isVersion(uuid, UuidVersion.SEQUENTIAL);
	}

	/**
	 * Checks whether the UUID version 2.
	 * 
	 * @param uuid a UUID
	 * @return boolean true if it is a DCE Security UUID
	 */
	public static boolean isDceSecurity(UUID uuid) {
		return isVersion(uuid, UuidVersion.DCE_SECURITY);
	}

	private static boolean isVersion(UUID uuid, UuidVersion version) {
		return isRfc4122(uuid) && (uuid.version() == version.getValue());
	}

	/**
	 * Get the node identifier from a time-based UUID.
	 *
	 * @param uuid a UUID
	 * @return long the node identifier
	 */
	public static long extractNodeIdentifier(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid) || UuidUtil.isSequential(uuid) || UuidUtil.isDceSecurity(uuid))) {
			throw new UuidUtilException(
					String.format("Not a time-based, sequential or DCE Security UUID: %s.", uuid.toString()));
		}

		return uuid.getLeastSignificantBits() & 0x0000ffffffffffffL;
	}

	/**
	 * Get the clock sequence from a time-based UUID.
	 *
	 * @param uuid a UUID
	 * @return int the clock sequence
	 */
	public static int extractClockSequence(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid) || UuidUtil.isSequential(uuid))) {
			throw new UuidUtilException(String.format("Not a time-based or sequential UUID: %s.", uuid.toString()));
		}

		return (int) ((uuid.getLeastSignificantBits() >>> 48) & 0x0000000000003fffL);
	}

	/**
	 * Get the instant from a time-based UUID.
	 *
	 * @param uuid a UUID
	 * @return {@link Instant}
	 */
	public static Instant extractInstant(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return UuidTimeUtil.toInstant(timestamp);
	}

	/**
	 * Get the Unix epoch milliseconds from a time-based UUID.
	 *
	 * The value returned by this method is the number of milliseconds since
	 * 1970-01-01 (Unix epoch).
	 * 
	 * @param uuid a UUID
	 * @return Unix milliseconds
	 */
	public static long extractUnixMilliseconds(UUID uuid) {
		long timestamp = extractTimestamp(uuid);
		return UuidTimeUtil.toUnixMilliseconds(timestamp);
	}

	/**
	 * Get the timestamp from a time-based UUID.
	 *
	 * The value returned by this method is the number of 100-nanos since 1582-10-15
	 * (Gregorian epoch).
	 *
	 * @param uuid a UUID
	 * @return long the timestamp
	 */
	public static long extractTimestamp(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid) || UuidUtil.isSequential(uuid))) {
			throw new UuidUtilException(String.format("Not a time-based or sequential UUID: %s.", uuid.toString()));
		}

		if (uuid.version() == 1) {
			return extractTimeBasedTimestamp(uuid.getMostSignificantBits());
		} else {
			return extractSequentialTimestamp(uuid.getMostSignificantBits());
		}
	}

	/**
	 * Get the timestamp from a sequential UUID.
	 *
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractSequentialTimestamp(long msb) {

		long himid = (msb & 0xffffffffffff0000L) >>> 4;
		long low = (msb & 0x0000000000000fffL);

		return (himid | low);
	}

	/**
	 * Get the timestamp from a standard time-based UUID.
	 *
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractTimeBasedTimestamp(long msb) {

		long hii = (msb & 0xffffffff00000000L) >>> 32;
		long mid = (msb & 0x00000000ffff0000L) << 16;
		long low = (msb & 0x0000000000000fffL) << 48;

		return (hii | mid | low);
	}

	/**
	 * Get the timestamp from a MSSQL Guid.
	 * 
	 * @param msb a long value that has the "Most Significant Bits" of the UUID.
	 * @return the timestamp
	 */
	private static long extractMssqlGuidTimestamp(long msb) {

		long ts = 0x0000000000000000L;
		// high bits
		ts |= (msb & 0xff000000_0000_0000L) >>> 24;
		ts |= (msb & 0x00ff0000_0000_0000L) >>> 8;
		ts |= (msb & 0x0000ff00_0000_0000L) << 8;
		ts |= (msb & 0x000000ff_0000_0000L) << 24;
		// mid bits
		ts |= (msb & 0x00000000_ff00_0000L) >>> 8;
		ts |= (msb & 0x00000000_00ff_0000L) << 8;
		// low bits
		ts |= (msb & 0x00000000_0000_ff00L) >>> 8;
		ts |= (msb & 0x00000000_0000_00ffL) << 8;

		long hii = (ts & 0xffffffff00000000L) >>> 32;
		long mid = (ts & 0x00000000ffff0000L) << 16;
		long low = (ts & 0x000000000000ffffL) << 48;

		return (hii | mid | low);
	}

	/**
	 * Get the local domain number from a DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return the local domain
	 */
	public static byte extractDceSecurityLocalDomain(UUID uuid) {

		if (!UuidUtil.isDceSecurity(uuid)) {
			throw new UuidUtilException(String.format(NOT_DCE_SECURITY, uuid.toString()));
		}

		return (byte) ((uuid.getLeastSignificantBits() & 0x00ff000000000000L) >> 48);
	}

	/**
	 * Get the local identifier number from a DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return the local identifier
	 */
	public static int extractDceSecurityLocalIdentifier(UUID uuid) {

		if (!UuidUtil.isDceSecurity(uuid)) {
			throw new UuidUtilException(String.format(NOT_DCE_SECURITY, uuid.toString()));
		}

		return (int) (uuid.getMostSignificantBits() >> 32);
	}

	/**
	 * Get the timestamp from a DCE Security UUID.
	 *
	 * The value returned by this method is the number of 100-nanos since 1582-10-15
	 * (Gregorian epoch).
	 * 
	 * @param uuid a UUID
	 * @return the timestamp
	 */
	public static long extractDceSecurityTimestamp(UUID uuid) {
		if (!UuidUtil.isDceSecurity(uuid)) {
			throw new UuidUtilException(String.format(NOT_DCE_SECURITY, uuid.toString()));
		}

		return extractTimeBasedTimestamp((uuid.getMostSignificantBits() & 0x00000000ffffffffL));
	}

	/**
	 * Get the instant from a DCE Security UUID.
	 *
	 * @param uuid a UUID
	 * @return {@link Instant}
	 */
	public static Instant extractDceSecurityInstant(UUID uuid) {
		long timestamp = extractDceSecurityTimestamp(uuid);
		return UuidTimeUtil.toInstant(timestamp);
	}

	/**
	 * Get the array of bytes from a UUID.
	 * 
	 * @param uuid a UUID
	 * @return an array of bytes
	 */
	public static byte[] fromUuidToBytes(UUID uuid) {
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();
		byte[] msbBytes = ByteUtil.toBytes(msb);
		byte[] lsbBytes = ByteUtil.toBytes(lsb);
		return ByteUtil.concat(msbBytes, lsbBytes);
	}

	/**
	 * Get a UUID from an array of bytes;
	 * 
	 * @param bytes an array of bytes
	 * @return a UUID
	 */
	public static UUID fromBytesToUuid(byte[] bytes) {
		byte[] msbBytes = ByteUtil.copy(bytes, 0, 8);
		byte[] lsbBytes = ByteUtil.copy(bytes, 8, 16);
		long msb = ByteUtil.toNumber(msbBytes);
		long lsb = ByteUtil.toNumber(lsbBytes);
		return new UUID(msb, lsb);
	}

	/**
	 * Convert a sequential UUID to a time-based UUID.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	public static UUID fromSequentialUuidToTimeBasedUuid(UUID uuid) {

		if (!(UuidUtil.isSequential(uuid))) {
			throw new UuidUtilException(String.format("Not a sequential UUID: %s.", uuid.toString()));
		}

		long timestamp = extractTimestamp(uuid);

		long msb = formatTimeBasedMostSignificantBits(timestamp);
		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a time-based UUID to a sequential UUID.
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	public static UUID fromTimeBasedUuidToSequentialUuid(UUID uuid) {

		if (!(UuidUtil.isTimeBased(uuid))) {
			throw new UuidUtilException(String.format("Not a time-based UUID: %s.", uuid.toString()));
		}

		long timestamp = extractTimestamp(uuid);

		long msb = formatSequentialMostSignificantBits(timestamp);
		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a UUID to a MSSQL Server 'friendly' GUID.
	 * 
	 * {@link UuidUtil#formatMssqlMostSignificantBits(long)}
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	public static UUID fromUuidToMssqlGuid(UUID uuid) {

		long temp = uuid.getMostSignificantBits();
		long hii = (temp & 0xffffffff00000000L) >>> 32;
		long mid = (temp & 0x00000000ffff0000L) << 16;
		long low = (temp & 0x000000000000ffffL) << 48;
		long timestamp = (hii | mid | low);

		long msb = formatMssqlMostSignificantBits(timestamp);
		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert a MSSQL Server 'friendly' GUID to a UUID.
	 * 
	 * {@link UuidUtil#formatMssqlMostSignificantBits(long)}
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	public static UUID fromMssqlGuidToUuid(UUID uuid) {

		long timestamp = extractMssqlGuidTimestamp(uuid.getMostSignificantBits());

		long msb = 0x0000000000000000L;
		msb |= (timestamp & 0xffff_0000_00000000L) >>> 48;
		msb |= (timestamp & 0x0000_ffff_00000000L) >>> 16;
		msb |= (timestamp & 0x0000_0000_ffffffffL) << 32;

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Returns the timestamp bits of the UUID in the 'natural' order of bytes.
	 * 
	 * It's not necessary to set the version bits because they are already ZERO.
	 * 
	 * @param timestamp a timestamp
	 * @return the MSB
	 */
	public static long formatSequentialMostSignificantBits(final long timestamp) {
		return ((timestamp & 0x0ffffffffffff000L) << 4) | (timestamp & 0x0000000000000fffL);
	}

	/**
	 * Returns the timestamp bits of the UUID in the order defined in the RFC-4122.
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
	public static long formatTimeBasedMostSignificantBits(final long timestamp) {
		return ((timestamp & 0x0fff_0000_00000000L) >>> 48) | ((timestamp & 0x0000_ffff_00000000L) >>> 16)
				| ((timestamp & 0x0000_0000_ffffffffL) << 32) | 0x00000000_0000_1000L;
	}

	/**
	 * Format most significant bits for MSSQL Server.
	 * 
	 * ### References
	 * 
	 * * How to Generate Sequential GUIDs for SQL Server in .NET
	 * 
	 * https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net/
	 * 
	 * * UUID Binary encoding
	 * 
	 * https://en.wikipedia.org/wiki/Universally_unique_identifier#Encoding
	 * 
	 * * Newsequentialid (Histrory/Benefits and Implementation)
	 * 
	 * https://blogs.msdn.microsoft.com/sqlprogrammability/2006/03/23/newsequentialid-histrorybenefits-and-implementation/
	 * 
	 * * NEWSEQUENTIALID (Transact-SQL)
	 * 
	 * https://docs.microsoft.com/en-us/sql/t-sql/functions/newsequentialid-transact-sql?view=sql-server-2017
	 * 
	 * * How are GUIDs sorted by SQL Server?
	 * 
	 * http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx
	 * 
	 * * SqlGuid.CompareTo Method
	 * 
	 * https://docs.microsoft.com/en-us/dotnet/api/system.data.sqltypes.sqlguid.compareto?view=netframework-4.7.2#System_Data_SqlTypes_SqlGuid_CompareTo_System_Data_SqlTypes_SqlGuid_
	 * 
	 * * Comparing GUID and uniqueidentifier Values
	 * 
	 * https://docs.microsoft.com/pt-br/dotnet/framework/data/adonet/sql/comparing-guid-and-uniqueidentifier-values
	 * 
	 * @param timestamp a timestamp
	 * @return the MSB
	 */
	public static long formatMssqlMostSignificantBits(final long timestamp) {

		long ts1 = 0x0000000000000000L;
		ts1 |= (timestamp & 0xffff_0000_00000000L) >>> 48;
		ts1 |= (timestamp & 0x0000_ffff_00000000L) >>> 16;
		ts1 |= (timestamp & 0x0000_0000_ffffffffL) << 32;

		long ts2 = 0x0000000000000000L;
		// high bits
		ts2 |= (ts1 & 0xff000000_0000_0000L) >>> 24;
		ts2 |= (ts1 & 0x00ff0000_0000_0000L) >>> 8;
		ts2 |= (ts1 & 0x0000ff00_0000_0000L) << 8;
		ts2 |= (ts1 & 0x000000ff_0000_0000L) << 24;
		// mid bits
		ts2 |= (ts1 & 0x00000000_ff00_0000L) >>> 8;
		ts2 |= (ts1 & 0x00000000_00ff_0000L) << 8;
		// low bits
		ts2 |= (ts1 & 0x00000000_0000_ff00L) >>> 8;
		ts2 |= (ts1 & 0x00000000_0000_00ffL) << 8;

		return ts2;
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
	public static long formatRfc4122LeastSignificantBits(final long nodeIdentifier, final long clockSequence) {
		return ((clockSequence << 48) | (nodeIdentifier & 0x0000ffffffffffffL) | 0x8000000000000000L);
	}

	public static class UuidUtilException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UuidUtilException(String message) {
			super(message);
		}
	}
}
