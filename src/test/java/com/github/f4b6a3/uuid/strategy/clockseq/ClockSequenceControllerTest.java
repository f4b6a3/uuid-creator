package com.github.f4b6a3.uuid.strategy.clockseq;

import org.junit.Test;

import com.github.f4b6a3.uuid.strategy.clockseq.ClockSequenceController;

import static org.junit.Assert.*;
import java.util.HashSet;

public class ClockSequenceControllerTest {

	private static final int LOOP_MAX = 0x3fff + 1; // 16384

	private static final int[] TEST_ARRAY = { 11095, 2930, 2724, 4104, 9694, 2582, 9767, 12129, 1520, 11363, 6324, 2000,
			3774, 4166, 7735, 8085, 12625, 10242, 12830, 9389, 3568, 11802, 15169, 1950, 5523, 8799, 12194, 3659, 9046,
			4001, 2047, 4109, 7744, 7684, 13922, 14610, 8953, 10895, 3392, 9622, 4682, 13131, 10762, 14918, 11658,
			13519, 4144, 9746, 11971, 8281, 8233, 3407, 4639, 3272, 4632, 11896, 6555, 7859, 6367, 2863, 13271, 13056,
			2048, 10453, 15206, 2533, 6466, 11108, 12106, 13723, 13236, 10137, 6942, 8197, 12575, 3484, 6880, 10050,
			12873, 11826, 14003, 3343, 3032, 8230, 254, 10790, 11390, 3908, 8850, 1949, 11262, 15259, 4556, 8030, 12627,
			9773, 5979, 16241, 3883, 6881 };

	@Test
	public void testClockSequenceController1() {

		ClockSequenceController controller = new ClockSequenceController();
		HashSet<Integer> set = new HashSet<>();

		// Request all values from the pool
		for (int i = 0; i < LOOP_MAX; i++) {
			assertTrue("There are duplicate values", set.add(controller.borrow(-1, i)));
			assertTrue("Value not used", controller.isUsed(i));
		}

		assertEquals("The collection size should be equal to the pool size", LOOP_MAX, set.size());
		assertEquals("All values from the poll should be used", LOOP_MAX, controller.countUsed());
	}

	@Test
	public void testClockSequenceController2() {

		ClockSequenceController controller = new ClockSequenceController();
		HashSet<Integer> set = new HashSet<>();

		// Request all values from the test array
		for (int i = 0; i < TEST_ARRAY.length; i++) {
			assertTrue("There are duplicate values: " + i, set.add(controller.borrow(-1, TEST_ARRAY[i])));
			assertTrue("Value not used", controller.isUsed(TEST_ARRAY[i]));
		}

		assertEquals("The collection size should be equal to the test array size", TEST_ARRAY.length, set.size());
		assertEquals("All values from the test array should be used", TEST_ARRAY.length, controller.countUsed());
	}

	@Test
	public void testClockSequenceController3() {

		ClockSequenceController controller = new ClockSequenceController();

		int give = -1;
		int take = -1;

		// Request all values from the test array
		for (int i = 0; i < TEST_ARRAY.length; i++) {
			give = take;
			take = TEST_ARRAY[i];
			assertEquals(take, controller.borrow(give, take));
			assertTrue("The value should be free", controller.isFree(give));
			assertTrue("The value should be used", controller.isUsed(take));
		}

		assertEquals("Only one value should be used in the end", 1, controller.countUsed());
		assertTrue("The value used should be equal to the last test array item",
				controller.isUsed(TEST_ARRAY[TEST_ARRAY.length - 1]));
	}

	@Test
	public void testClockSequenceController4() {

		ClockSequenceController controller = new ClockSequenceController();

		// Request all values from the pool
		for (int i = 0; i < LOOP_MAX; i++) {
			controller.borrow(-1, i);
		}

		assertEquals("All values from the poll should be used", LOOP_MAX, controller.countUsed());

		// Request another value from the pool
		controller.borrow(-1, 0);

		assertEquals("The pool should be reset and only one value should be used in the end", 1,
				controller.countUsed());
	}

	@Test
	public void testClockSequenceController5() {

		ClockSequenceController controller = new ClockSequenceController();

		// UUID Creator 1
		assertEquals(1000, controller.borrow(-1, 1000));

		// UUID Creator 2
		assertEquals(998, controller.borrow(-1, 998));
		assertEquals(999, controller.borrow(998, 999));
		// It should increment to '1001' because '1000' is not free
		assertEquals(1001, controller.borrow(999, 1000));

		assertTrue(controller.isFree(998));
		assertTrue(controller.isFree(999));
		assertTrue(controller.isUsed(1000));
		assertTrue(controller.isUsed(1001));
	}
}
