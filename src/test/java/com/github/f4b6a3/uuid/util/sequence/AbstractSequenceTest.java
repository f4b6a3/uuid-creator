package com.github.f4b6a3.uuid.util.sequence;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.util.sequence.AbstractSequence;

public class AbstractSequenceTest {

	private static class TestSequence extends AbstractSequence {

		protected static int minTest = 0;
		protected static int maxTest = 1000;

		protected TestSequence() {
			super(TestSequence.minTest, TestSequence.maxTest);
		}
	}

	@Test
	public void testGetCurrent() {

		TestSequence testSequence = new TestSequence();

		for (int i = 0; i < 10; i++) {
			testSequence.next();
		}

		// Should be MIN_VALUE + 10
		assertEquals(TestSequence.minTest + 10L, testSequence.current());
	}

	@Test
	public void testReset() {
		TestSequence testSequence = new TestSequence();
		
		for (int i = 0; i < 10; i++) {
			testSequence.next();
		}
		
		// Should be MIN_VALUE + 10
		assertEquals(TestSequence.minTest + 10L, testSequence.current()); 

		// Should be reset to MIN_VALUE
		testSequence.reset();
		assertEquals(TestSequence.minTest, testSequence.current()); 
	}

	@Test
	public void testGetMinValue() {

		TestSequence testSequence = new TestSequence();
		assertEquals(TestSequence.minTest, testSequence.min());

	}

	@Test
	public void testGetMaxValue() {

		TestSequence testSequence = new TestSequence();
		assertEquals(TestSequence.maxTest, testSequence.max());

	}

	@Test
	public void testNextTheSequenceShouldIncrement() {

		TestSequence testSequence = new TestSequence();
		long oldValue = testSequence.current();
		assertEquals(oldValue + 1, testSequence.next());

		testSequence = new TestSequence();
		oldValue = testSequence.current();
		testSequence.next();
		testSequence.next();
		assertEquals(oldValue + 3, testSequence.next());
	}

	@Test
	public void testNextTheSequenceShouldBeResetToTheMinimumValueIfTheMaximumValueIsReached() {

		TestSequence testSequence = new TestSequence();

		long value = 0;

		for (int i = 0; i < TestSequence.maxTest; i++) {
			value = testSequence.next();
		}

		assertEquals(TestSequence.maxTest, value); // Should be MAX_VALUE

		value = testSequence.next();
		assertEquals(TestSequence.minTest, value); // Should be reset to MIN_VALUE

	}

}
