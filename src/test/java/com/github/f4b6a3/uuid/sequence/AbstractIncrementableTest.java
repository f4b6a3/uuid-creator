package com.github.f4b6a3.uuid.sequence;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.f4b6a3.uuid.sequence.AbstractSequence;

public class AbstractIncrementableTest {
	
	private static class TestIncrementable extends AbstractSequence {

		protected static int MIN_TEST = 1111;
		protected static int MAX_TEST = 2222;
		
		protected TestIncrementable() {
			super(TestIncrementable.MIN_TEST, TestIncrementable.MAX_TEST);
		}
		
	}
	
	@Test
	public void testGetCurrent() {
		
		TestIncrementable testIncrementable = new TestIncrementable();
		
		long value = 0;
		
		for (int i = 0; i < TestIncrementable.MIN_TEST + 10; i++) {
			value = testIncrementable.getNext();
		}
		value = testIncrementable.getCurrent();
		assertEquals(TestIncrementable.MIN_TEST + 10, value); // Should be MIN_VALUE + 10
	}
	
	@Test
	public void testReset() {
		TestIncrementable testIncrementable = new TestIncrementable();
		
		long value = 0;
		
		for (int i = 0; i < TestIncrementable.MIN_TEST + 10; i++) {
			value = testIncrementable.getNext();
		}
		value = testIncrementable.getCurrent();
		assertEquals(TestIncrementable.MIN_TEST + 10, value); // Should be MIN_VALUE + 10
		
		testIncrementable.reset();
		value = testIncrementable.getCurrent();
		assertEquals(TestIncrementable.MIN_TEST, value); // Should be reset to MIN_VALUE
	}
	
	@Test
	public void testGetMinValue() {
		
		TestIncrementable testIncrementable = new TestIncrementable();
		assertEquals(TestIncrementable.MIN_TEST, testIncrementable.getMinValue());
		
	}
	
	@Test
	public void testGetMaxValue() {
		
		TestIncrementable testIncrementable = new TestIncrementable();
		assertEquals(TestIncrementable.MAX_TEST, testIncrementable.getMaxValue());
		
	}
	
	@Test
	public void testNext_should_increment() {
		
		TestIncrementable testIncrementable = new TestIncrementable();
		long old_value = testIncrementable.getCurrent();
		long new_value = testIncrementable.getNext();
		assertEquals(old_value + 1, new_value);
		
		testIncrementable = new TestIncrementable();
		old_value = testIncrementable.getCurrent();
		new_value = testIncrementable.getNext();
		new_value = testIncrementable.getNext();
		new_value = testIncrementable.getNext();
		assertEquals(old_value + 3, new_value);
	}
	
	@Test
	public void testNext_should_be_reset_to_min_value_if_the_max_value_is_reached() {
		
		TestIncrementable testIncrementable = new TestIncrementable();
		
		long value = 0;
		
		for (int i = 0; i < TestIncrementable.MAX_TEST - 1; i++) {
			value = testIncrementable.getNext();
		}
		assertEquals(TestIncrementable.MAX_TEST - 1, value); // Should be MAX_VALUE - 1
		
		value = testIncrementable.getNext();
		assertEquals(TestIncrementable.MAX_TEST, value); // Should be MAX_VALUE
		
		value = testIncrementable.getNext();
		assertEquals(TestIncrementable.MIN_TEST, value); // Sould be reset to MIN_VALUE
		
	}

}
