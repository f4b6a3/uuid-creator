/**
 * Copyright 2018 Fabio Lima <br/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); <br/>
 * you may not use this file except in compliance with the License. <br/>
 * You may obtain a copy of the License at <br/>
 *
 * http://www.apache.org/licenses/LICENSE-2.0 <br/>
 *
 * Unless required by applicable law or agreed to in writing, software <br/>
 * distributed under the License is distributed on an "AS IS" BASIS, <br/>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br/>
 * See the License for the specific language governing permissions and <br/>
 * limitations under the License. <br/>
 *
 */

package com.github.f4b6a3.uuid.sequence;

/**
 * This abstract class may represent circular a counter or sequence.
 * 
 * If the maximum value is reached the value is reset to the minimum.
 */
public abstract class AbstractSequence implements Sequence {

	protected int value;
	public final int MIN_VALUE;
	public final int MAX_VALUE;

	protected AbstractSequence(int min, int max) {
		this.MIN_VALUE = min;
		this.MAX_VALUE = max;
		this.value = MIN_VALUE;
	}

	@Override
	public int current() {
		return this.value;
	}

	@Override
	public int next() {
		if (this.value >= MAX_VALUE) {
			this.value = MIN_VALUE;
			return this.value;
		}
		return ++this.value;
	}

	@Override
	public int min() {
		return MIN_VALUE;
	}

	@Override
	public int max() {
		return MAX_VALUE;
	}

	@Override
	public void reset() {
		this.value = MIN_VALUE;
	}
	
	@Override
	public void set(int value) {
		if (value < MIN_VALUE || value > MAX_VALUE) {
			this.reset();
		}
		this.value = value;
	}
}
