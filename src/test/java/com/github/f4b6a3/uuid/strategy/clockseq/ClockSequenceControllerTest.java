package com.github.f4b6a3.uuid.strategy.clockseq;

import org.junit.Test;

import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceController;

import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Random;

public class ClockSequenceControllerTest {

	private static final int CLOCK_SEQUENCE_MAX = 16384; // 2^16
	protected static final int THREAD_TOTAL = availableProcessors();

	private static final int[] TEST_ARRAY = { 11095, 2930, 2724, 4104, 9694, 2582, 9767, 12129, 1520, 11363, 6324, 2000,
			3774, 4166, 7735, 8085, 12625, 10242, 12830, 9389, 3568, 11802, 15169, 1950, 5523, 8799, 12194, 3659, 9046,
			4001, 2047, 4109, 7744, 7684, 13922, 14610, 8953, 10895, 3392, 9622, 4682, 13131, 10762, 14918, 11658,
			13519, 4144, 9746, 11971, 8281, 8233, 3407, 4639, 3272, 4632, 11896, 6555, 7859, 6367, 2863, 13271, 13056,
			2048, 10453, 15206, 2533, 6466, 11108, 12106, 13723, 13236, 10137, 6942, 8197, 12575, 3484, 6880, 10050,
			12873, 11826, 14003, 3343, 3032, 8230, 254, 10790, 11390, 3908, 8850, 1949, 11262, 15259, 4556, 8030, 12627,
			9773, 5979, 16241, 3883, 6881 };

	private static final Random random = new Random();

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	@Test
	public void testClockSequenceController1() {

		ClockSequenceController controller = new ClockSequenceController();
		HashSet<Integer> set = new HashSet<>();

		// Request all values from the pool
		for (int i = 0; i < CLOCK_SEQUENCE_MAX; i++) {
			assertTrue("There are duplicate values", set.add(controller.take(i)));
			assertTrue("Value not used", controller.isUsed(i));
		}

		assertEquals("The collection size should be equal to the pool size", CLOCK_SEQUENCE_MAX, set.size());
		assertEquals("All values from the poll should be used", CLOCK_SEQUENCE_MAX, controller.countUsed());
	}

	@Test
	public void testClockSequenceController2() {

		ClockSequenceController controller = new ClockSequenceController();
		HashSet<Integer> set = new HashSet<>();

		// Request all values from the test array
		for (int i = 0; i < TEST_ARRAY.length; i++) {
			assertTrue("There are duplicate values: " + i, set.add(controller.take(TEST_ARRAY[i])));
			assertTrue("Value not used", controller.isUsed(TEST_ARRAY[i]));
		}

		assertEquals("The collection size should be equal to the test array size", TEST_ARRAY.length, set.size());
		assertEquals("All values from the test array should be used", TEST_ARRAY.length, controller.countUsed());
	}

	@Test
	public void testClockSequenceController3() {

		ClockSequenceController controller = new ClockSequenceController();

		int take = -1;

		// Request all values from the test array
		for (int i = 0; i < TEST_ARRAY.length; i++) {
			take = TEST_ARRAY[i];
			assertEquals(take, controller.take(take));
			assertTrue("The value should be used", controller.isUsed(take));
		}

		assertEquals(TEST_ARRAY.length, controller.countUsed());
	}

	@Test
	public void testClockSequenceController4() {

		ClockSequenceController controller = new ClockSequenceController();

		// Request all values from the pool
		for (int i = 0; i < CLOCK_SEQUENCE_MAX; i++) {
			controller.take(i);
		}

		assertEquals("All values from the poll should be used", CLOCK_SEQUENCE_MAX, controller.countUsed());

		// Request another value from the pool
		controller.take(0);

		assertEquals("The pool should be reset and only one value should be used in the end", 1,
				controller.countUsed());
	}

	@Test
	public void testClockSequenceController5() throws InterruptedException {

		Thread[] threads = new Thread[CLOCK_SEQUENCE_MAX];

		ClockSequenceController controller = new ClockSequenceController();

		// Start threads threads
		for (int i = 0; i < CLOCK_SEQUENCE_MAX; i++) {
			threads[i] = new TestThread(controller, i);
			threads[i].start();
		}
		
		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		HashSet<Integer> unique = new HashSet<>();
		for (Integer i : TestThread.list) {
			unique.add(i);
		}
		// Check if the quantity of unique values is correct
		assertEquals("Duplicate clock sequence", CLOCK_SEQUENCE_MAX, unique.size());
	}

	private static class TestThread extends Thread {

		private int index;
		public static int[] list = new int[CLOCK_SEQUENCE_MAX];

		private ClockSequenceController controller;

		public TestThread(ClockSequenceController controller, int index) {
			this.controller = controller;
			this.index = index;
		}

		@Override
		public void run() {
			int take = random.nextInt(CLOCK_SEQUENCE_MAX);
			list[index] = controller.take(take);
		}
	}
}
