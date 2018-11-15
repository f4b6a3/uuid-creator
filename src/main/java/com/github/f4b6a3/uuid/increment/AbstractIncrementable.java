package com.github.f4b6a3.uuid.increment;

public abstract class AbstractIncrementable implements Incrementable {

	protected int value;
	protected final int MIN_VALUE;
	protected final int MAX_VALUE;

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
		this.value = ++this.value % MAX_VALUE;
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
