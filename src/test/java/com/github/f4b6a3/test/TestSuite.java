package com.github.f4b6a3.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.github.f4b6a3.uuid.UUIDGeneratorTest;
import com.github.f4b6a3.uuid.factory.abst.AbstractUUIDCreatorTest;
import com.github.f4b6a3.uuid.sequence.AbstractSequenceTest;
import com.github.f4b6a3.uuid.sequence.ClockSequenceTest;
import com.github.f4b6a3.uuid.time.DefaultTimestampStrategyTest;
import com.github.f4b6a3.uuid.util.ByteUtilTest;
import com.github.f4b6a3.uuid.util.TimestampUtilTest;
import com.github.f4b6a3.uuid.util.UUIDUtilTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   UUIDGeneratorTest.class,
   AbstractUUIDCreatorTest.class,
   AbstractSequenceTest.class,
   ClockSequenceTest.class,
   DefaultTimestampStrategyTest.class,
   ByteUtilTest.class,
   TimestampUtilTest.class,
   UUIDUtilTest.class
})

public class TestSuite {   
}  	