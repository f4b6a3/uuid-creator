package com.github.f4b6a3.uuid.codec.uuid;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class UuidMsGuidCodec implements UuidCodec<UUID> {

	/**
	 * Convert a time-based UUID to a MS GUID.
	 * 
	 * This method is only useful for MS SQL Server.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The Microsoft GUID format stores the most significant bytes as little-endian,
	 * while the least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	@Override
	public UUID encode(UUID uuid) {
		if (!UuidUtil.isTimeBased(uuid)) {
			throw new IllegalArgumentException(String.format("Not a time-based UUID: %s.", uuid.toString()));
		}
		return toAndFromMsGuid(uuid);
	}

	/**
	 * Convert a MS GUID to a time-based UUID.
	 * 
	 * This method is only useful for MS SQL Server.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The Microsoft GUID format stores the most significant bytes as little-endian,
	 * while the least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	@Override
	public UUID decode(UUID uuid) {
		UUID uuidv1 = toAndFromMsGuid(uuid);
		if (!UuidUtil.isTimeBased(uuidv1)) {
			throw new IllegalArgumentException(String.format("Not a time-based UUID: %s.", uuidv1.toString()));
		}
		return uuidv1;
	}

	/**
	 * Convert a UUID to and from a MS GUID.
	 * 
	 * This method is only useful for MS SQL Server.
	 * 
	 * It rearranges the most significant bytes from big-endian to little-endian,
	 * and vice-versa.
	 * 
	 * The Microsoft GUID format stores the most significant bytes as little-endian,
	 * while the least significant bytes are stored as big-endian (network order).
	 * 
	 * @param uuid a UUID
	 * @return another UUID
	 */
	private static UUID toAndFromMsGuid(UUID uuid) {

		long msb = toAndFromMsGuidMostSignificantBits(uuid.getMostSignificantBits());
		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	/**
	 * Convert to and from MS GUID most significant bits.
	 * 
	 * ### References
	 * 
	 * * How to Generate Sequential GUIDs for SQL Server in .NET
	 * 
	 * https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net/
	 * 
	 * * How are GUIDs sorted by SQL Server?
	 * 
	 * http://sqlblog.com/blogs/alberto_ferrari/archive/2007/08/31/how-are-guids-sorted-by-sql-server.aspx
	 * 
	 * @param msb the most significant bits
	 * @return the MSB
	 */
	private static long toAndFromMsGuidMostSignificantBits(final long msb) {

		long bits = 0x0000000000000000L;
		// high bits
		bits |= (msb & 0xff000000_0000_0000L) >>> 24;
		bits |= (msb & 0x00ff0000_0000_0000L) >>> 8;
		bits |= (msb & 0x0000ff00_0000_0000L) << 8;
		bits |= (msb & 0x000000ff_0000_0000L) << 24;
		// mid bits
		bits |= (msb & 0x00000000_ff00_0000L) >>> 8;
		bits |= (msb & 0x00000000_00ff_0000L) << 8;
		// low bits
		bits |= (msb & 0x00000000_0000_ff00L) >>> 8;
		bits |= (msb & 0x00000000_0000_00ffL) << 8;

		return bits;
	}
}
