package com.github.f4b6a3.uuid.state;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;

public class UuidStateTest {

	private MockUuidState state;

	private static final int CLOCKSEQ = 0x1111;
	private static final long NODEID = 0x111111111111L;
	
	private long timestamp;
	
	@Before
	public void before() {
		
		DefaultTimestampStrategy timestampStrategy = new DefaultTimestampStrategy();
		this.timestamp = timestampStrategy.getTimestamp();
		
		SettingsUtil.setStateEnabled(true);
		this.state = new MockUuidState();
		this.state.setValid(true);
	}
	
	@After
	public void after() {
		SettingsUtil.setStateEnabled(false);
	}
	
	@Test
	public void clockSequenceShouldIncrementIfTimestampIsBackwards() {
		
		long oneSecond = 10_000_000;
		
		this.state.setTimestamp(timestamp + oneSecond);
		this.state.setClockSequence(CLOCKSEQ);
		this.state.setNodeIdentifier(NODEID);
		
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, NODEID, this.state);
		long clockseq = clockSequenceStrategy.current();
		assertEquals(ByteUtil.toHexadecimal(CLOCKSEQ + 1L), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldIncrementIfNodeIdentifierHasChanged() {
		
		long oneSecond = 10_000_000;
		
		this.state.setTimestamp(timestamp - oneSecond);
		this.state.setClockSequence(CLOCKSEQ);
		this.state.setNodeIdentifier(NODEID + 1);
		
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, NODEID, this.state);
		long clockseq = clockSequenceStrategy.current();
		assertEquals(ByteUtil.toHexadecimal(CLOCKSEQ + 1L), ByteUtil.toHexadecimal(clockseq));
	}

	@Test
	public void clockSequenceShouldNotBeIqualToZeroIfStateFileIsInvalid() {
		
		this.state.setValid(false);
		
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, NODEID, this.state);
		long clockseq = clockSequenceStrategy.current();
		
		assertNotEquals(ByteUtil.toHexadecimal(0), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldNotBeIqualToZeroIfStateFileIsDesabled() {
		
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, NODEID, this.state);
		long clockseq = clockSequenceStrategy.current();
		
		assertNotEquals(ByteUtil.toHexadecimal(0), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldBeIqualToPreviousClockSequence() {
		
		long oneSecond = 10_000_000;
		
		this.state.setTimestamp(timestamp - oneSecond);
		this.state.setClockSequence(CLOCKSEQ);
		this.state.setNodeIdentifier(NODEID);
		
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, NODEID, this.state);
		long clockseq = clockSequenceStrategy.current();
		
		assertEquals(ByteUtil.toHexadecimal(CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
}
