package com.github.f4b6a3.uuid.sequence;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.sequence.AbstractSequence;

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
		
		long value = 0;
		
		for (int i = 0; i < 10; i++) {
			value = testSequence.next();
		}
		value = testSequence.current();
		assertEquals(TestSequence.MIN_TEST + 10, value); // Should be MIN_VALUE + 10
	}
	
	@Test
	public void testReset() {
		TestSequence testSequence = new TestSequence();
		
		long value = 0;
		
		for (int i = 0; i < 10; i++) {
			value = testSequence.next();
		}
		value = testSequence.current();
		assertEquals(TestSequence.MIN_TEST + 10, value); // Should be MIN_VALUE + 10
		
		testSequence.reset();
		value = testSequence.current();
		assertEquals(TestSequence.MIN_TEST, value); // Should be reset to MIN_VALUE
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
	public void testNext_should_increment() {
		
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
	public void testNext_should_be_reset_to_min_value_if_the_max_value_is_reached() {
		
		TestSequence testSequence = new TestSequence();
		
		long value = 0;
		
		for (int i = 0; i < TestSequence.MAX_TEST; i++) {
			value = testSequence.next();
		}
		
		assertEquals(TestSequence.MAX_TEST, value); // Should be MAX_VALUE
		
		value = testSequence.next();
		assertEquals(TestSequence.MIN_TEST, value); // Sould be reset to MIN_VALUE
		
	}

}
