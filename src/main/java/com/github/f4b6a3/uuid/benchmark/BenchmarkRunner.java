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

package com.github.f4b6a3.uuid.benchmark;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.github.f4b6a3.uuid.UuidGenerator;
import com.github.f4b6a3.uuid.exception.OverrunException;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.factory.OrderedUuidCreator;
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator;

/**
 * A simple benchmark that compares this implementation to others.
 * 
 * If the computer is too fast, {@link OverrunException} may be thrown,
 * increasing some scores.
 *
 */
@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkRunner {

	private String name;
	private byte[] bytes;

	private NameBasedGenerator jugNameBasedGenerator;
	private TimeBasedGenerator jugTimeBasedGenerator;
	private TimeBasedGenerator jugTimeBasedMACGenerator;
	private RandomBasedGenerator jugRandomGenerator;

	private OrderedUuidCreator orderedCreator;
	private TimeBasedUuidCreator timeBasedCreator;
	private OrderedUuidCreator orderedMacCreator;
	private TimeBasedUuidCreator timeBasedMacCreator;
	private DceSecurityUuidCreator dceSecurityCreator;
	private DceSecurityUuidCreator dceSecurityWithMacCreator;

	@Setup
	public void setUp() {

		name = "https://github.com/";
		bytes = name.getBytes();

		jugNameBasedGenerator = Generators.nameBasedGenerator();
		jugTimeBasedGenerator = Generators.timeBasedGenerator();
		jugTimeBasedMACGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
		jugRandomGenerator = Generators.randomBasedGenerator();

		orderedCreator = UuidGenerator.getOrderedCreator();
		timeBasedCreator = UuidGenerator.getTimeBasedCreator();
		orderedMacCreator = UuidGenerator.getOrderedCreator().withHardwareAddress();
		timeBasedMacCreator = UuidGenerator.getTimeBasedCreator().withHardwareAddress();
		dceSecurityCreator = UuidGenerator.getDceSecurityCreator();
		dceSecurityWithMacCreator = UuidGenerator.getDceSecurityCreator().withHardwareAddress();
	}

	// Java UUID

	@Benchmark
	public UUID Java_Random() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID Java_NameBased() {
		return UUID.nameUUIDFromBytes(bytes);
	}

	// EAIO

	@Benchmark
	public com.eaio.uuid.UUID EAIO_TimeBasedWithMac() {
		return new com.eaio.uuid.UUID();
	}

	// JUG

	@Benchmark
	public UUID JUG_NameBased() {
		return jugNameBasedGenerator.generate(bytes);
	}

	@Benchmark
	public UUID JUG_TimeBased() {
		return jugTimeBasedGenerator.generate();
	}

	@Benchmark
	public UUID JUG_TimeBasedWithMAC() {
		return jugTimeBasedMACGenerator.generate();
	}

	@Benchmark
	public UUID JUG_Random() {
		return jugRandomGenerator.generate();
	}

	// UUID Generator

	@Benchmark
	public UUID UUIDGenerator_Random() {
		return UuidGenerator.getRandom();
	}

	@Benchmark
	public UUID UUIDGenerator_FastRandom() {
		return UuidGenerator.getFastRandom();
	}

	@Benchmark
	public UUID UUIDGenerator_DceSecurity() {
		return dceSecurityCreator.create((byte) 1, 1701);
	}

	@Benchmark
	public UUID UUIDGenerator_DceSecurityWithMac() {
		return dceSecurityWithMacCreator.create((byte) 1, 1701);
	}

	@Benchmark
	public UUID UUIDGenerator_NameBasedMd5() {
		return UuidGenerator.getNameBasedMd5(name);
	}

	@Benchmark
	public UUID UUIDGenerator_NameBasedSha1() {
		return UuidGenerator.getNameBasedSha1(name);
	}

	@Benchmark
	public UUID UUIDGenerator_Ordered() {
		return orderedCreator.create();
	}

	@Benchmark
	public UUID UUIDGenerator_OrderedWithMac() {
		return orderedMacCreator.create();
	}

	@Benchmark
	public UUID UUIDGenerator_TimeBased() {
		return timeBasedCreator.create();
	}

	@Benchmark
	public UUID UUIDGenerator_TimeBasedWithMac() {
		return timeBasedMacCreator.create();
	}

	public static void main(String[] args) throws Exception {
		org.openjdk.jmh.Main.main(args);
	}
}
