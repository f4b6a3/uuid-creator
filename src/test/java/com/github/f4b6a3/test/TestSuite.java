package com.github.f4b6a3.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.UuidGeneratorTest;
import com.github.f4b6a3.uuid.factory.abst.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.sequence.AbstractSequenceTest;
import com.github.f4b6a3.uuid.sequence.ClockSequenceTest;
import com.github.f4b6a3.uuid.time.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.ByteUtilTest;
import com.github.f4b6a3.uuid.util.TimestampUtilTest;
import com.github.f4b6a3.uuid.util.UuidUtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UuidGeneratorTest.class,
   AbstractUuidCreatorTest.class,
   AbstractSequenceTest.class,
   ClockSequenceTest.class,
   DefaultTimestampStrategyTest.class,
   ByteUtilTest.class,
   TimestampUtilTest.class,
   UuidUtilTest.class
})

public class TestSuite {   
}  	