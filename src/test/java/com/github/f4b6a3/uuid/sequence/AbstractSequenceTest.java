package com.github.f4b6a3.uuid.sequence;

import static org.junit.Assert.*;

import org.junit.Test;

public class AbstractSequenceTest {

	private static class TestSequence extends AbstractSequence {

		protected static int MIN_TEST = 0;
		protected static int MAX_TEST = 1000;

		protected TestSequence() {
			super(TestSequence.MIN_TEST, TestSequence.MAX_TEST);
		}
	}

	@Test
	public void testGetCurrent() {

		TestSequence testSequence = new TestSequence();

		for (int i = 0; i < 10; i++) {
			testSequence.next();
		}

		// Should be MIN_VALUE + 10
		assertEquals(TestSequence.MIN_TEST + 10L, testSequence.current());
	}

	@Test
	public void testReset() {
		TestSequence testSequence = new TestSequence();
		
		for (int i = 0; i < 10; i++) {
			testSequence.next();
		}
		
		// Should be MIN_VALUE + 10
		assertEquals(TestSequence.MIN_TEST + 10L, testSequence.current()); 

		// Should be reset to MIN_VALUE
		testSequence.reset();
		assertEquals(TestSequence.MIN_TEST, testSequence.current()); 
	}

	@Test
	public void testGetMinValue() {

		TestSequence testSequence = new TestSequence();
		assertEquals(TestSequence.MIN_TEST, testSequence.min());

	}

	@Test
	public void testGetMaxValue() {

		TestSequence testSequence = new TestSequence();
		assertEquals(TestSequence.MAX_TEST, testSequence.max());

	}

	@Test
	public void testNext_TheSequenceShouldIncrement() {

		TestSequence testSequence = new TestSequence();
		long old_value = testSequence.current();
		long new_value = testSequence.next();
		assertEquals(old_value + 1, new_value);

		testSequence = new TestSequence();
		old_value = testSequence.current();
		new_value = testSequence.next();
		new_value = testSequence.next();
		new_value = testSequence.next();
		assertEquals(old_value + 3, new_value);
	}

	@Test
	public void testNext_TheSequenceShouldBeResetToTheMinimumValueIfTheMaximumValueIsReached() {

		TestSequence testSequence = new TestSequence();

		long value = 0;

		for (int i = 0; i < TestSequence.MAX_TEST; i++) {
			value = testSequence.next();
		}

		assertEquals(TestSequence.MAX_TEST, value); // Should be MAX_VALUE

		value = testSequence.next();
		assertEquals(TestSequence.MIN_TEST, value); // Should be reset to
													// MIN_VALUE

	}

}
