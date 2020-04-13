package com.github.f4b6a3.uuid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceControllerTest;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategyTest;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.SettingsUtilTest;
import com.github.f4b6a3.uuid.util.UuidTimeUtilTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;
import com.github.f4b6a3.uuid.util.sequence.AbstractSequenceTest;
import com.github.f4b6a3.uuid.util.UuidValidatorTest;
import com.github.f4b6a3.uuid.util.UuidConverterTest;
import com.github.f4b6a3.uuid.creator.nonstandard.AltCombGuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.CombGuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AbstractSequenceTest.class,
   DefaultClockSequenceStrategyTest.class,
   DefaultTimestampStrategyTest.class,
   UuidTimeUtilTest.class,
   UuidUtilTest.class,
   SettingsUtilTest.class,
   ClockSequenceControllerTest.class,
   UuidValidatorTest.class,
   UuidConverterTest.class,
   DceSecurityUuidCreatorTest.class,
   CombGuidCreatorTest.class,
   AltCombGuidCreatorTest.class,
   RandomBasedUuidCreatorTest.class,
   NameBasedMd5UuidCreatorTest.class,
   NameBasedSha1UuidCreatorTest.class,
   TimeBasedUuidCreatorTest.class,
   TimeOrderedUuidCreatorTest.class,
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