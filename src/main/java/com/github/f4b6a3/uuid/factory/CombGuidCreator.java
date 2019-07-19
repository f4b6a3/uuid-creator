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

import java.util.UUID;

/**
 * Factory that creates COMB UUIDs.
 * 
 * This implementation is derived from the ULID specification. The only
 * difference is that the millisecond bits are moved to the end of the GUID. See
 * the {@link LexicalOrderGuidCreator}.
 * 
 * The Cost of GUIDs as Primary Keys (COMB GUID inception):
 * http://www.informit.com/articles/article.aspx?p=25862
 * 
 * ULID specification: https://github.com/ulid/spec
 * 
 */
public class CombGuidCreator extends LexicalOrderGuidCreator {

	/**
	 * Return a COMB GUID.
	 * 
	 * See {@link LexicalOrderGuidCreator#create()}
	 */
	@Override
	public synchronized UUID create() {

		final long timestamp = this.getTimestamp();

		final long msb = (high << 48) | (low >> 16);
		final long lsb = (low << 48) | timestamp;

		return new UUID(msb, lsb);
	}
}
