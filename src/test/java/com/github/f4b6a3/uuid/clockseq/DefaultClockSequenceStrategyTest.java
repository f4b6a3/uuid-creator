package com.github.f4b6a3.uuid.clockseq;

import org.junit.Test;

import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy;
import com.github.f4b6a3.uuid.clockseq.DefaultClockSequenceStrategy.Balancer;
import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.util.TimestampUtil;

import static org.junit.Assert.*;

import java.util.HashSet;

public class DefaultClockSequenceStrategyTest {

	@Test
	public void testNextFor_should_increment_if_the_new_timestamp_is_lower_or_equal_to_the_old_timestamp() {

		// It should increment if the new timestamp is LOWER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 999;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		long new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence + 1, new_sequence);

		// It should increment if the new timestamp is EQUAL TO the old
		// timestamp
		old_timestamp = 1000;
		new_timestamp = 1000;
		clockSequence = new DefaultClockSequenceStrategy();
		old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence + 1, new_sequence);
	}

	@Test
	public void testNextFor_should_not_increment_if_the_new_timestamp_is_equal_to_the_old_timestamp() {

		// It should NOT increment if the new timestamp is GREATER THAN the old
		// timestamp
		long old_timestamp = 1000;
		long new_timestamp = 1001;
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();
		long old_sequence = clockSequence.getClockSequence(old_timestamp, 0);
		long new_sequence = clockSequence.getClockSequence(new_timestamp, 0);
		assertEquals(old_sequence, new_sequence);
	}

	@Test()
	public void testNextForTimestamp_the_last_value_should_be_equal_to_the_first_value_minus_one() {

		int first = 0;
		int last = 0;
		long timestamp = TimestampUtil.getCurrentTimestamp();
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();

		first = clockSequence.getClockSequence(timestamp, 0);
		for (int i = 0; i < DefaultClockSequenceStrategy.SEQUENCE_MAX; i++) {
			last = clockSequence.getClockSequence(timestamp, 0);
		}

		assertEquals(first - 1, last);
	}

	@Test(expected = OverrunException.class)
	public void testNextForTimestamp_should_throw_overrun_exception() {

		long timestamp = TimestampUtil.getCurrentTimestamp();
		DefaultClockSequenceStrategy clockSequence = new DefaultClockSequenceStrategy();

		int i = 0;
		try {
			// Generate MAX values
			for (i = 0; i <= DefaultClockSequenceStrategy.SEQUENCE_MAX; i++) {
				clockSequence.getClockSequence(timestamp, 0);
			}
		} catch (OverrunException e) {
			// fail if the exception is thrown before the maximum value
			fail(String.format("Overrun exception thrown before the maximum value is reached."));
		}

		// It should throw an exception now
		clockSequence.getClockSequence(timestamp, 0);

	}

	@Test
	public void testClockSequenceBalancer_should_follow_the_precalculated_sequence() {

		int[] list = { 0, 180, 270, 90, 315, 225, 135, 45, 337, 292, 247, 202, 157, 112, 67, 22, 348, 326, 303, 281,
				258, 236, 213, 191, 168, 146, 123, 101, 78, 56, 33, 11, 354, 343, 331, 320, 309, 298, 286, 275, 264,
				253, 241, 230, 219, 208, 196, 185, 174, 163, 151, 140, 129, 118, 106, 95, 84, 73, 61, 50, 39, 28, 16, 5,
				357, 351, 345, 340, 334, 329, 323, 317, 312, 306, 300, 295, 289, 284, 278, 272, 267, 261, 255, 250, 244,
				239, 233, 227, 222, 216, 210, 205, 199, 194, 188, 182, 177, 171, 165, 160, 154, 149, 143, 137, 132, 126,
				120, 115, 109, 104, 98, 92, 87, 81, 75, 70, 64, 59, 53, 47, 42, 36, 30, 25, 19, 14, 8, 2, 358, 355, 352,
				350, 347, 344, 341, 338, 336, 333, 330, 327, 324, 322, 319, 316, 313, 310, 307, 305, 302, 299, 296, 293,
				291, 288, 285, 282, 279, 277, 274, 271, 268, 265, 262, 260, 257, 254, 251, 248, 246, 243, 240, 237, 234,
				232, 229, 226, 223, 220, 217, 215, 212, 209, 206, 203, 201, 198, 195, 192, 189, 187, 184, 181, 178, 175,
				172, 170, 167, 164, 161, 158, 156, 153, 150, 147, 144, 142, 139, 136, 133, 130, 127, 125, 122, 119, 116,
				113, 111, 108, 105, 102, 99, 97, 94, 91, 88, 85, 82, 80, 77, 74, 71, 68, 66, 63, 60, 57, 54, 52, 49, 46,
				43, 40, 37, 35, 32, 29, 26, 23, 21, 18, 15, 12, 9, 7, 4, 1 };

		int degrees = 360;
		Balancer balancer = new DefaultClockSequenceStrategy.Balancer(degrees);

		int first = balancer.first();
		for (int i = 1; i < list.length; i++) {
			assertEquals((list[i] + first) % degrees, balancer.next());
		}
		
		// Restart the sequence
		first = balancer.next();
		for (int i = 1; i < list.length; i++) {
			assertEquals((list[i] + first) % degrees, balancer.next());
		}
	}

	@Test
	public void testClockSequenceBalancer_should_not_repeat_values() {

		int max = 100_000;
		HashSet<Integer> set = new HashSet<>();
		Balancer balancer = new DefaultClockSequenceStrategy.Balancer(max * 2);

		for (int i = 0; i < max; i++) {
			set.add(balancer.next());
		}
		
		assertEquals(max, set.size());
	}
}
