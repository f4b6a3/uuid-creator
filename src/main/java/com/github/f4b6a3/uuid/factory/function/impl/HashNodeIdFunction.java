/*
 * MIT License
 * 
 * Copyright (c) 2018-2025 Fabio Lima
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

package com.github.f4b6a3.uuid.factory.function.impl;

import com.github.f4b6a3.uuid.factory.function.NodeIdFunction;
import com.github.f4b6a3.uuid.util.MachineId;
import com.github.f4b6a3.uuid.util.internal.ByteUtil;

/**
 * Function that returns a hash of host name, MAC and IP.
 * <p>
 * The hash is calculated once during instantiation.
 * 
 * @see NodeIdFunction
 * @see MachineId
 */
public final class HashNodeIdFunction implements NodeIdFunction {

	private final long nodeIdentifier;

	/**
	 * Default constructor.
	 */
	public HashNodeIdFunction() {
		final byte[] hash = MachineId.getMachineHash();
		final long number = ByteUtil.toNumber(hash, 0, 6);
		this.nodeIdentifier = NodeIdFunction.toMulticast(number);
	}

	@Override
	public long getAsLong() {
		return this.nodeIdentifier;
	}
}
