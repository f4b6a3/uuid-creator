package com.github.f4b6a3.uuid;

import java.util.UUID;

public class UUIDBuilder {

	private long msb;
	private long lsb;
	
	private long version = VERSION_0;
	
	public static final long VERSION_0 = 0x0000000000000000L;
	public static final long VERSION_1 = 0x0000000000001000L;
	public static final long VERSION_2 = 0x0000000000002000L;
	public static final long VERSION_3 = 0x0000000000003000L;
	public static final long VERSION_4 = 0x0000000000004000L;
	public static final long VERSION_5 = 0x0000000000005000L;
	
	public static UUIDBuilder getUUIDBuilder(long version) {
		return new UUIDBuilder(0L, 0L, version);
	}
	
	public UUIDBuilder(long msb, long lsb, long version) {
		this.msb = msb;
		this.lsb = lsb;
		this.version = version;
	}
	
	public void setMsb(long msb) {
		this.msb = msb;
	}

	public void setLsb(long lsb) {
		this.lsb = lsb;
	}

	public void setVersion(long version) {
		this.version = version;
	}
	
	public long getMsb() {
		return msb;
	}
	
	public long getLsb() {
		return lsb;
	}
	
	public long getVersion() {
		return this.version;
	}
	
	public UUID getUUID() {
		// Set version before returning a UUID object.
		this.msb = (this.msb & 0xffffffffffff0fffL) | this.version;
		return new UUID(this.msb, this.lsb);
	}
	
	@Override
	public UUIDBuilder clone() {
		return new UUIDBuilder(this.msb, this.lsb, this.version);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		return (other != null)
			&& (other instanceof UUIDBuilder)
			&& (this.msb == ((UUIDBuilder) other).getMsb())
			&& (this.lsb == ((UUIDBuilder) other).getLsb())
			&& (this.version == ((UUIDBuilder) other).getVersion());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lsb ^ (lsb >>> 32));
		result = prime * result + (int) (msb ^ (msb >>> 32));
		result = prime * result + (int) (version ^ (version >>> 32));
		return result;
	}
	
	public UUIDBuilder setTimestamp(long timestamp) {
		if(this.version == VERSION_1) {
			return setStandardTimestamp(timestamp);
		} else {
			return setSequentialTimestamp(timestamp);
		}
	}
	
	public UUIDBuilder setSequentialTimestamp(long timestamp) {
		
		long hii = (timestamp & 0x0fff000000000000L) << 4;
		long mid = (timestamp & 0x0000ffff00000000L) << 4;
		long lo1 = (timestamp & 0x00000000fffff000L) << 4;
		long lo2 = (timestamp & 0x0000000000000fffL);
		
		this.msb = (hii | mid | lo1 | lo2);
		setVersion(VERSION_0);
		
		return this;
	}
	
	public UUIDBuilder setStandardTimestamp(long timestamp) {
		
		long hii = (timestamp & 0x0fff000000000000L) >>> 48;
		long mid = (timestamp & 0x0000ffff00000000L) >>> 16;
		long low = (timestamp & 0x00000000ffffffffL) << 32;

		this.msb = (low | mid | hii);
		setVersion(VERSION_1);
		
		return this;
	}
	
	public UUIDBuilder setClockSeq1(long clockSeq1) {
		
		long lsb = this.lsb;
		long cs1 = clockSeq1 << 48;
		
		lsb &= 0x0000ffffffffffffL;
		cs1 &= 0xffff000000000000L;

		this.lsb = (lsb | cs1);
		
		return this;
	}
	
//	public UUIDBuilder setClockSeq2(long clockSeq2) {
//		
//		short version = getVersion();
//		
//		if (version == 0) {
//			
//		} else if (version == 1) {
//			
//		} else {
//			return this;
//		}
//		
//		this.msb = (this.msb + clockSeq2);
//		return this;
//	}
	
	public UUIDBuilder setNode(long node) {
		
		long lsb = this.lsb;
		long nod = node;
		
		lsb &= 0xffff000000000000L;
		nod &= 0x0000ffffffffffffL;

		this.lsb = (lsb | nod);
		
		return this;
	}
}