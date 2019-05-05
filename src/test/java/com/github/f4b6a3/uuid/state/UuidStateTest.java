package com.github.f4b6a3.uuid.state;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;
import com.github.f4b6a3.uuid.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.util.ByteUtil;
import com.github.f4b6a3.uuid.util.SettingsUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class UuidStateTest {

	private MockUuidState state;

	private static final int PREV_CLOCKSEQ = 0x1111;
	private static final int PROP_CLOCKSEQ = 0x2222;
	
	private long timestamp;
	private long nodeid;
	
	@Before
	public void before() {
		
		DefaultTimestampStrategy timestampStrategy = new DefaultTimestampStrategy();
		this.timestamp = timestampStrategy.getTimestamp();
		
		DefaultNodeIdentifierStrategy nodeIdentifierStrategy = new DefaultNodeIdentifierStrategy();
		this.nodeid = nodeIdentifierStrategy.getNodeIdentifier();
		
		this.state = new MockUuidState();
		this.state.setTimestamp(timestamp - 1);
		this.state.setNodeIdentifier(nodeid);
		this.state.setClockSequence(PREV_CLOCKSEQ);
		
		SettingsUtil.setStateEnabled(true);
		System.clearProperty(SettingsUtil.PROPERTY_NODEID);
		System.clearProperty(SettingsUtil.PROPERTY_CLOCKSEQ);
	}
	
	@Test
	public void clockSequenceShouldIncrementIfTimestampIsBackwards() {
		this.state.setTimestamp(timestamp + 1);
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, this.nodeid, this.state);
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(clockSequenceStrategy);
		UUID uuid = creator.create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertEquals(ByteUtil.toHexadecimal(PREV_CLOCKSEQ + 1), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldIncrementIfNodeIdentifierHasChanged() {
		this.state.setNodeIdentifier(nodeid + 1);
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, this.nodeid, this.state);
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(clockSequenceStrategy);
		UUID uuid = creator.create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertEquals(ByteUtil.toHexadecimal(PREV_CLOCKSEQ + 1), ByteUtil.toHexadecimal(clockseq));
	}

	@Test
	public void clockSequenceShouldBeIqualToPreviousClockSequence() {
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, this.nodeid, this.state);
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(clockSequenceStrategy);
		UUID uuid = creator.create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertEquals(ByteUtil.toHexadecimal(PREV_CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldBeIqualToSystemProperty() {
		this.state.setClockSequence(0);
		SettingsUtil.setClockSequence(PROP_CLOCKSEQ);
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, this.nodeid, this.state);
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(clockSequenceStrategy);
		UUID uuid = creator.create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertEquals(ByteUtil.toHexadecimal(PROP_CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
	
	@Test
	public void clockSequenceShouldNotBeIqualToPreviousClockSequenceOrSystemProperty() {
		this.state.setClockSequence(0);
		SettingsUtil.setClockSequence(0);
		DefaultClockSequenceStrategy clockSequenceStrategy = new DefaultClockSequenceStrategy(this.timestamp, this.nodeid, this.state);
		TimeBasedUuidCreator creator = UuidCreator.getTimeBasedCreator().withClockSequenceStrategy(clockSequenceStrategy);
		UUID uuid = creator.create();
		long clockseq = UuidUtil.extractClockSequence(uuid);
		assertNotEquals(ByteUtil.toHexadecimal(PROP_CLOCKSEQ), ByteUtil.toHexadecimal(clockseq));
	}
}
