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
 * This abstract class represents a circular a counter or sequence.
 * 
 * If the maximum value is reached the value is reset to the minimum.
 */
public abstract class AbstractSequence implements Sequence {

	protected long value;
	public final long minValue;
	public final long maxValue;

	protected AbstractSequence(long min, long max) {
		this.minValue = min;
		this.maxValue = max;
		this.value = minValue;
	}

	@Override
	public long current() {
		return this.value;
	}

	@Override
	public long next() {
		if (this.value >= maxValue) {
			this.value = minValue;
			return this.value;
		}
		return ++this.value;
	}

	@Override
	public long min() {
		return minValue;
	}

	@Override
	public long max() {
		return maxValue;
	}

	@Override
	public void reset() {
		this.value = minValue;
	}
	
	@Override
	public void set(final long value) {
		if (value < minValue || value > maxValue) {
			this.reset();
		}
		this.value = value;
	}
}
