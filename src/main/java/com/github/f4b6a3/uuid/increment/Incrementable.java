package com.github.f4b6a3.uuid.increment;

public interface Incrementable {
	int getCurrent();
	int getNext();
	int getMinValue();
	int getMaxValue();
	void reset();
}
