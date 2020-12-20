package com.github.f4b6a3.uuid.creator;

import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.HashNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.RandomNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.util.internal.UuidCreatorSettings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

public class AbstractTimeBasedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testSelectNodeIdentifierStrategy() {

		NodeIdentifierStrategy strategy;
		Random random = new Random();

		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, "mac");
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof MacNodeIdentifierStrategy)) {
			fail("It should use MAC node identifier strategy");
		}

		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, "hash");
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof HashNodeIdentifierStrategy)) {
			fail("It should use Hash node identifier strategy");
		}
		
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, "random");
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof RandomNodeIdentifierStrategy)) {
			fail("It should use Random node identifier strategy");
		}

		Long number = random.nextLong() & 0x0000ffffffffffffL;
		UuidCreatorSettings.setProperty(UuidCreatorSettings.PROPERTY_NODE, number.toString());
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		assertEquals(number.longValue(), strategy.getNodeIdentifier());
		if (!(strategy instanceof FixedNodeIdentifierStrategy)) {
			fail("It should use Fixed node identifier strategy");
		}

		UuidCreatorSettings.clearProperty(UuidCreatorSettings.PROPERTY_NODE);
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof DefaultNodeIdentifierStrategy)) {
			fail("It should use Default node identifier strategy");
		}
	}
}
