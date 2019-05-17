package com.github.f4b6a3.uuid.distrib;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class CyclicDistributorTest {
	
	@Test
	public void testCyclicDistributor_TheValuesHandedOutShouldFollowThePrecalculatedSequence () {

		int[] list = { 0, 180, 270, 90, 315, 225, 135, 45, 338, 293, 248, 203, 158, 113, 68, 23, 349, 326, 304, 281,
				259, 236, 214, 191, 169, 146, 124, 101, 79, 56, 34, 11, 354, 343, 332, 321, 309, 298, 287, 276, 264,
				253, 242, 231, 219, 208, 197, 186, 174, 163, 152, 141, 129, 118, 107, 96, 84, 73, 62, 51, 39, 28, 17, 6,
				357, 352, 346, 340, 335, 329, 323, 318, 312, 307, 301, 295, 290, 284, 278, 273, 267, 262, 256, 250, 245,
				239, 233, 228, 222, 217, 211, 205, 200, 194, 188, 183, 177, 172, 166, 160, 155, 149, 143, 138, 132, 127,
				121, 115, 110, 104, 98, 93, 87, 82, 76, 70, 65, 59, 53, 48, 42, 37, 31, 25, 20, 14, 8, 3, 359, 356, 353,
				350, 347, 345, 342, 339, 336, 333, 330, 328, 325, 322, 319, 316, 314, 311, 308, 305, 302, 300, 297, 294,
				291, 288, 285, 283, 280, 277, 274, 271, 269, 266, 263, 260, 257, 255, 252, 249, 246, 243, 240, 238, 235,
				232, 229, 226, 224, 221, 218, 215, 212, 210, 207, 204, 201, 198, 195, 193, 190, 187, 184, 181, 179, 176,
				173, 170, 167, 165, 162, 159, 156, 153, 150, 148, 145, 142, 139, 136, 134, 131, 128, 125, 122, 120, 117,
				114, 111, 108, 105, 103, 100, 97, 94, 91, 89, 86, 83, 80, 77, 75, 72, 69, 66, 63, 60, 58, 55, 52, 49,
				46, 44, 41, 38, 35, 32, 30, 27, 24, 21, 18, 15, 13, 10, 7, 4, 1 };

		int degrees = 360;
		Distributor distributor = new CyclicDistributor(degrees);

		int first = distributor.handOut();
		for (int i = 1; i < list.length; i++) {
			assertEquals((list[i] + first) % degrees, distributor.handOut());
		}
	}

	@Test
	public void testCyclicDistributor_TheValuesHandedOutShouldNotRepeat() {

		int loopMax = 0x3fff; // 16383
		HashSet<Integer> set = new HashSet<>();
		Distributor distributor = new CyclicDistributor(loopMax + 1);

		for (int i = 0; i < loopMax; i++) {
			assertTrue("There are duplicate values", set.add(distributor.handOut()));
		}

		assertEquals(loopMax, set.size());
	}
}
