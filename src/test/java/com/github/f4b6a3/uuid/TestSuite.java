
package com.github.f4b6a3.uuid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.codec.base.BaseNCodec1Test;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec2SamplesTest;
import com.github.f4b6a3.uuid.codec.base.BaseNCodec3SamplesTest;
import com.github.f4b6a3.uuid.codec.base.BaseNTest;
import com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderDecoderTest;
import com.github.f4b6a3.uuid.codec.base.function.BaseNRemainderEncoderTest;
import com.github.f4b6a3.uuid.codec.other.DotNetGuid1CodecTest;
import com.github.f4b6a3.uuid.codec.other.DotNetGuid4CodecTest;
import com.github.f4b6a3.uuid.codec.other.NcnameCodecTest;
import com.github.f4b6a3.uuid.codec.other.SlugCodecTest;
import com.github.f4b6a3.uuid.codec.other.TimeOrderedCodecTest;
import com.github.f4b6a3.uuid.factory.AbstTimeBasedFactoryTest;
import com.github.f4b6a3.uuid.factory.function.ClockSeqPoolTest;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultClockSeqFunctionTest;
import com.github.f4b6a3.uuid.factory.function.impl.DefaultTimeFunctionTest;
import com.github.f4b6a3.uuid.factory.function.impl.WindowsTimeFunctionTest;
import com.github.f4b6a3.uuid.factory.nonstandard.PrefixCombFactoryTest;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortPrefixCombFactoryTest;
import com.github.f4b6a3.uuid.factory.nonstandard.ShortSuffixCombFactoryTest;
import com.github.f4b6a3.uuid.factory.nonstandard.SuffixCombFactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.DceSecurityFactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.NameBasedMd5FactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.NameBasedSha1FactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.RandomBasedFactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeBasedFactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedEpochFactoryTest;
import com.github.f4b6a3.uuid.factory.rfc4122.TimeOrderedFactoryTest;
import com.github.f4b6a3.uuid.codec.BinaryCodecTest;
import com.github.f4b6a3.uuid.codec.StringCodecTest;
import com.github.f4b6a3.uuid.codec.UriCodecTest;
import com.github.f4b6a3.uuid.codec.UrnCodecTest;
import com.github.f4b6a3.uuid.util.internal.ByteUtilTest;
import com.github.f4b6a3.uuid.util.internal.SettingsUtilTest;
import com.github.f4b6a3.uuid.util.CombUtilTest;
import com.github.f4b6a3.uuid.util.UuidComparatorTest;
import com.github.f4b6a3.uuid.util.UuidTimeTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;
import com.github.f4b6a3.uuid.util.UuidValidatorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	AbstTimeBasedFactoryTest.class,
	BaseNCodec1Test.class,
	BaseNCodec2SamplesTest.class,
	BaseNCodec3SamplesTest.class,
	BaseNRemainderDecoderTest.class,
	BaseNRemainderEncoderTest.class,
	BaseNTest.class,
	BinaryCodecTest.class,
	ByteUtilTest.class,
	ClockSeqPoolTest.class,
	CombUtilTest.class,
	DceSecurityFactoryTest.class,
	DefaultClockSeqFunctionTest.class,
	DefaultTimeFunctionTest.class,
	DotNetGuid1CodecTest.class,
	DotNetGuid4CodecTest.class,
	NameBasedMd5FactoryTest.class,
	NameBasedSha1FactoryTest.class,
	NcnameCodecTest.class,
	PrefixCombFactoryTest.class,
	RandomBasedFactoryTest.class,
	SettingsUtilTest.class,
	ShortPrefixCombFactoryTest.class,
	ShortSuffixCombFactoryTest.class,
	SlugCodecTest.class,
	StringCodecTest.class,
	SuffixCombFactoryTest.class,
	TimeBasedFactoryTest.class,
	TimeOrderedCodecTest.class,
	TimeOrderedEpochFactoryTest.class,
	TimeOrderedFactoryTest.class,
	UriCodecTest.class,
	UrnCodecTest.class,
	UuidComparatorTest.class,
	UuidTimeTest.class,
	UuidUtilTest.class,
	UuidValidatorTest.class,
	WindowsTimeFunctionTest.class,
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