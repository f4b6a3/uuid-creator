package com.github.f4b6a3.uuid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.codec.base.UuidBaseNCodecTest;
// import com.github.f4b6a3.uuid.codec.name.UuidNcnameCodecTest;
import com.github.f4b6a3.uuid.codec.slug.UuidSlugCodecTest;
// import com.github.f4b6a3.uuid.codec.uri.UuidUriCodecTest;
import com.github.f4b6a3.uuid.codec.UuidBytesCodecTest;
import com.github.f4b6a3.uuid.codec.UuidStringCodecTest;
import com.github.f4b6a3.uuid.creator.AbstractTimeBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.PrefixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortPrefixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.ShortSuffixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.nonstandard.SuffixCombCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.DceSecurityUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.RandomBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeBasedUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.TimeOrderedUuidCreatorTest;
import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceControllerTest;
import com.github.f4b6a3.uuid.strategy.clockseq.DefaultClockSequenceStrategyTest;
import com.github.f4b6a3.uuid.strategy.timestamp.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.internal.ByteUtilTest;
import com.github.f4b6a3.uuid.util.internal.UuidCreatorSettingsTest;
import com.github.f4b6a3.uuid.util.UuidTimeTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;
import com.github.f4b6a3.uuid.util.UuidValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AbstractTimeBasedUuidCreatorTest.class,
	ByteUtilTest.class,
	ClockSequenceControllerTest.class,
	DceSecurityUuidCreatorTest.class,
	DefaultClockSequenceStrategyTest.class,
	DefaultTimestampStrategyTest.class,
	NameBasedMd5UuidCreatorTest.class,
	NameBasedSha1UuidCreatorTest.class,
	PrefixCombCreatorTest.class,
	RandomBasedUuidCreatorTest.class,
	ShortPrefixCombCreatorTest.class,
	ShortSuffixCombCreatorTest.class,
	SuffixCombCreatorTest.class,
	TimeBasedUuidCreatorTest.class,
	TimeOrderedUuidCreatorTest.class,
	UuidBaseNCodecTest.class,
	UuidBytesCodecTest.class,
	UuidCreatorSettingsTest.class,
//	UuidNcnameCodecTest.class,
	UuidSlugCodecTest.class,
	UuidStringCodecTest.class,
	UuidTimeTest.class,
//	UuidUriCodecTest.class,
	UuidUtilTest.class,
	UuidValidatorTest.class,
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