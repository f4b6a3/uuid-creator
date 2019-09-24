/*
 * MIT License
 * 
 * Copyright (c) 2018-2019 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.uuid.distrib;

import com.github.f4b6a3.uuid.util.RandomUtil;

/**
 * This class hands out numbers in a range of values so that the first value is
 * random and the rest values won't repeat.
 *
 * The range is treated as the perimeter of a circle. Each value is a point in
 * this perimeter. The first point handed out is always random. The next values
 * are calculated in iterations or cycles. Each cycle has a 2^n number of values
 * to hand out. All the values are equidistant from the other values of the same
 * circle.
 * 
 * Example:
 * 
 * Say the range is 360, similar to the 360 degrees of a circle. The first
 * handed out is random, and for coincidence it is ZERO. The second value handed
 * out is 180 degrees away from the first point. The third is at 270 degrees far
 * from the first one. The forth 90 degrees. And so on...
 * 
 * This algorithm is very simple, but it's easier to understand it watching it
 * running. There's an animation in the `doc` directory that shows the algorithm
 * in action. Each point drawn in the circle of the animation is like a value
 * been handed out. Each value is at the same distance of the others in the same
 * iteration or cycle.
 * 
 **/
public class CyclicDistributor implements Distributor {

	private double offset = -1;
	private double perimeter;
	private double iteration;
	private double remaining;
	private double arc;

	public CyclicDistributor(int range) {
		this.perimeter = range;
		this.reset();
	}

	protected synchronized void reset() {
		this.iteration = 0;
		this.remaining = 0;
		this.arc = 0;
	}

	protected synchronized long first() {
		this.reset();
		this.offset = RandomUtil.nextInt((int) this.perimeter);
		return (long) this.offset;
	}

	@Override
	public synchronized long handOut() {

		if (this.offset == -1) {
			return this.first();
		}

		if (this.remaining == 0) {
			this.remaining = (float) Math.pow(2.0, this.iteration);

			this.arc = (this.perimeter / this.remaining);
			this.iteration++;
			if (this.remaining > this.perimeter / 2.0) {
				this.offset = (this.offset + (this.arc / 2.0)) % this.perimeter;
				this.reset();
				return (int) this.offset;
			}
		}

		return (int) ((this.offset + (this.arc * --this.remaining) + (this.arc / 2.0)) % this.perimeter);
	}
}