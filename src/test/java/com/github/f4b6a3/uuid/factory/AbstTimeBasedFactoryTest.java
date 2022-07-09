package com.github.f4b6a3.uuid.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.HashNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.MacNodeIdFunction;
import com.github.f4b6a3.uuid.factory.function.impl.RandomNodeIdFunction;
import com.github.f4b6a3.uuid.util.internal.SettingsUtil;

public class AbstTimeBasedFactoryTest extends UuidFactoryTest {

	@Test
	public void testSelectNodeIdentifierStrategy() {

		NodeIdFunction supplier;

		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, "mac");
		supplier = AbstTimeBasedFactory.selectNodeIdFunction();
		if (!(supplier instanceof MacNodeIdFunction)) {
			fail("It should use MAC node identifier supplier");
		}

		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, "hash");
		supplier = AbstTimeBasedFactory.selectNodeIdFunction();
		if (!(supplier instanceof HashNodeIdFunction)) {
			fail("It should use Hash node identifier supplier");
		}

		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, "random");
		supplier = AbstTimeBasedFactory.selectNodeIdFunction();
		if (!(supplier instanceof RandomNodeIdFunction)) {
			fail("It should use Random node identifier supplier");
		}

		Long number = ThreadLocalRandom.current().nextLong() & 0x0000ffffffffffffL;
		SettingsUtil.setProperty(SettingsUtil.PROPERTY_NODE, number.toString());
		supplier = AbstTimeBasedFactory.selectNodeIdFunction();
		assertEquals(number.longValue(), supplier.getAsLong());
		if ((supplier instanceof DefaultNodeIdFunction) || (supplier instanceof MacNodeIdFunction)
				|| (supplier instanceof HashNodeIdFunction) || (supplier instanceof RandomNodeIdFunction)) {
			fail("It should use a static node identifier supplier");
		}

		SettingsUtil.clearProperty(SettingsUtil.PROPERTY_NODE);
		supplier = AbstTimeBasedFactory.selectNodeIdFunction();
		if (!(supplier instanceof DefaultNodeIdFunction)) {
			fail("It should use Default node identifier supplier");
		}
	}
}
