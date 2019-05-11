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

import com.github.f4b6a3.uuid.enums.UuidVersion;
import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUuidCreator;

public class NameBasedSha256UuidCreator extends AbstractNameBasedUuidCreator {

	/**
	 * Factory that creates name based UUIDs with SHA-256, version 4 (number
	 * borrowed from random version).
	 */
	public NameBasedSha256UuidCreator() {
		super(UuidVersion.RANDOM_BASED, MESSAGE_DIGEST_SHA256);
	}	
}
