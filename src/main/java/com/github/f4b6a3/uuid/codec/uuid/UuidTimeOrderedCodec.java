package com.github.f4b6a3.uuid.codec.uuid;

import java.util.UUID;

import com.github.f4b6a3.uuid.codec.UuidCodec;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class UuidTimeOrderedCodec implements UuidCodec<UUID> {

	@Override
	public UUID encode(UUID uuid) {
		if (!UuidUtil.isTimeBased(uuid)) {
			throw new IllegalArgumentException(String.format("Not a time-based UUID: %s.", uuid.toString()));
		}

		long timestamp = UuidUtil.extractTimestamp(uuid);

		long msb = ((timestamp & 0x0ffffffffffff000L) << 4) //
				| (timestamp & 0x0000000000000fffL) //
				| 0x0000000000006000L; // set version 6

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	@Override
	public UUID decode(UUID uuid) {

		if (!UuidUtil.isTimeOrdered(uuid)) {
			throw new IllegalArgumentException(String.format("Not a time-ordered UUID: %s.", uuid.toString()));
		}

		long timestamp = UuidUtil.extractTimestamp(uuid);

		long msb = ((timestamp & 0x0fff_0000_00000000L) >>> 48) //
				| ((timestamp & 0x0000_ffff_00000000L) >>> 16) //
				| ((timestamp & 0x0000_0000_ffffffffL) << 32) //
				| 0x0000000000001000L; // set version 1

		long lsb = uuid.getLeastSignificantBits();

		return new UUID(msb, lsb);
	}
}
