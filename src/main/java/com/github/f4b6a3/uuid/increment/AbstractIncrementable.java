package com.github.f4b6a3.uuid.increment;

/**
 * This abstract class may represent circular a counter or sequence.
 * 
 * If the maximum value is reached the value is reset to the minimum.
 */
public abstract class AbstractIncrementable implements Incrementable {

	protected int value;
	public final int MIN_VALUE;
	public final int MAX_VALUE;

	protected AbstractIncrementable(int min, int max) {
		this.MIN_VALUE = min;
		this.MAX_VALUE = max;
	}

	@Override
	public int getCurrent() {
		return this.value;
	}

	@Override
	public int getNext() {

		if (this.value >= MAX_VALUE) {
			this.value = MIN_VALUE;
		} else {
			this.value++;
		}

		return this.value;
	}

	@Override
	public int getMinValue() {
		return MIN_VALUE;
	}

	@Override
	public int getMaxValue() {
		return MAX_VALUE;
	}

	@Override
	public void reset() {
		this.value = MIN_VALUE;
	}
}
