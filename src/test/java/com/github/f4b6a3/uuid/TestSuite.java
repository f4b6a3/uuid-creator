package com.github.f4b6a3.uuid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceControllerTest;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategyTest;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.SettingsUtilTest;
import com.github.f4b6a3.uuid.util.UuidTimeUtilTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;
import com.github.f4b6a3.uuid.util.sequence.AbstractSequenceTest;
import com.github.f4b6a3.uuid.util.UuidValidatorTest;
import com.github.f4b6a3.uuid.util.UuidConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UuidCreatorTest.class,
   AbstractUuidCreatorTest.class,
   AbstractSequenceTest.class,
   DefaultClockSequenceStrategyTest.class,
   DefaultTimestampStrategyTest.class,
   UuidTimeUtilTest.class,
   UuidUtilTest.class,
   SettingsUtilTest.class,
   ClockSequenceControllerTest.class,
   UuidValidatorTest.class,
   UuidConverterTest.class,
})

/**
 * 
 * It bundles all JUnit test cases.
 * 
 * Also see {@link UniquenesTest}. 
 *
 */
public class TestSuite {
}