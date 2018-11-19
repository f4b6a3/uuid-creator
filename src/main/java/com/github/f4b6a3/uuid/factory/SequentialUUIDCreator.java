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

package com.github.f4b6a3.uuid.factory;

import com.github.f4b6a3.uuid.factory.abst.AbstractTimeBasedUUIDCreator;

/**
 * Factory that creates time-based UUIDs, version 0.
 * 
 * This version is a 'extension' of the RFC-4122. It leaves the timestamp bits
 * in the 'natural' order, instead of rearranging them as the version 1 does.
 *
 */
public class SequentialUUIDCreator extends AbstractTimeBasedUUIDCreator {

	public SequentialUUIDCreator() {
		super(VERSION_0);
	}

	protected SequentialUUIDCreator(int version) {
		super(version);
	}

	/**
	 * Returns the timestamp bits of the UUID in the 'natural' order of bytes.
	 * 
	 * It's not necessary to set the version bits because they are already ZERO.
	 * 
	 * @param timestamp
	 */
	@Override
	public long getMostSignificantBits(long timestamp) {

		long himid = (timestamp & 0x0ffffffffffff000L) << 4;
		long low = (timestamp & 0x0000000000000fffL);

		return (himid | low);
	}
}
