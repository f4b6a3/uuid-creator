package com.github.f4b6a3.uuid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceControllerTest;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategyTest;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.UuidCreatorSettingsTest;
import com.github.f4b6a3.uuid.util.UuidTimeTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;
import com.github.f4b6a3.uuid.util.UuidValidatorTest;
import com.github.f4b6a3.uuid.util.UuidConverterTest;
import com.github.f4b6a3.uuid.util.ByteUtilTest;
import com.github.f4b6a3.uuid.creator.AbstractTimeBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.PrefixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.SuffixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortPrefixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortSuffixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DefaultClockSequenceStrategyTest.class,
   DefaultTimestampStrategyTest.class,
   UuidTimeTest.class,
   UuidUtilTest.class,
   UuidCreatorSettingsTest.class,
   ClockSequenceControllerTest.class,
   UuidValidatorTest.class,
   UuidConverterTest.class,
   DceSecurityUuidCreatorTest.class,
   SuffixCombCreatorTest.class,
   PrefixCombCreatorTest.class,
   ShortSuffixCombCreatorTest.class,
   ShortPrefixCombCreatorTest.class,
   RandomBasedUuidCreatorTest.class,
   NameBasedMd5UuidCreatorTest.class,
   NameBasedSha1UuidCreatorTest.class,
   AbstractTimeBasedUuidCreatorTest.class,
   TimeBasedUuidCreatorTest.class,
   TimeOrderedUuidCreatorTest.class,
   ByteUtilTest.class,
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