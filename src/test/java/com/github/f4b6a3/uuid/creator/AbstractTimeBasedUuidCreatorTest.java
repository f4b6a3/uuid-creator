package com.github.f4b6a3.uuid.creator;

import com.github.f4b6a3.uuid.strategy.NodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.FixedNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.HashNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.strategy.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.util.UuidSettings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

public class AbstractTimeBasedUuidCreatorTest extends AbstractUuidCreatorTest {

	@Test
	public void testSelectNodeIdentifierStrategy() {

		NodeIdentifierStrategy strategy;
		Random random = new Random();

		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, "mac");
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof MacNodeIdentifierStrategy)) {
			fail("It should use MAC node identifier strategy");
		}

		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, "hash");
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof HashNodeIdentifierStrategy)) {
			fail("It should use Hash node identifier strategy");
		}

		Long number = random.nextLong() & 0x0000ffffffffffffL;
		UuidSettings.setProperty(UuidSettings.PROPERTY_NODE, number.toString());
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		assertEquals(number.longValue(), strategy.getNodeIdentifier());
		if (!(strategy instanceof FixedNodeIdentifierStrategy)) {
			fail("It should use Fixed node identifier strategy");
		}

		UuidSettings.clearProperty(UuidSettings.PROPERTY_NODE);
		strategy = AbstractTimeBasedUuidCreator.selectNodeIdentifierStrategy();
		if (!(strategy instanceof DefaultNodeIdentifierStrategy)) {
			fail("It should use Default node identifier strategy");
		}
	}
}
