package com.github.f4b6a3.uuid.bench;

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
import com.github.f4b6a3.uuid.UUIDGenerator;
import com.github.f4b6a3.uuid.factory.abst.AbstractNameBasedUUIDCreator;

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
	
	@Setup
	public void setUp() {
		
		name = "https://github.com/";
		bytes = name.getBytes();

		jugNameBasedGenerator = Generators.nameBasedGenerator();
		jugTimeBasedGenerator = Generators.timeBasedGenerator();
		jugTimeBasedMACGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
		jugRandomGenerator = Generators.randomBasedGenerator();
	}

	/*
	 * Java UUID
	 */

	@Benchmark
	public UUID Java_Random() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID Java_nameBased() {
		return UUID.nameUUIDFromBytes(bytes);
	}

	/*
	 * EAIO
	 */
	
    @Benchmark
    public com.eaio.uuid.UUID EAIO_TimeAndEthernetBased() {
        return new com.eaio.uuid.UUID();
    }
    
	/*
	 * JUG
	 */

	@Benchmark
	public UUID JUG_NameBased() {
		return jugNameBasedGenerator.generate(bytes);
	}

	@Benchmark
	public UUID JUG_TimeBased() {
		return jugTimeBasedGenerator.generate();
	}

	@Benchmark
	public UUID JUG_TimeAndEthernetBased() {
		return jugTimeBasedMACGenerator.generate();
	}

	@Benchmark
	public UUID JUG_Random() {
		return jugRandomGenerator.generate();
	}

	/*
	 * UUID Generator
	 */

	@Benchmark
	public UUID My_Random() {
		return UUIDGenerator.getRandomUUID();
	}

	@Benchmark
	public UUID My_FastRandom() {
		return UUIDGenerator.getFastRandomUUID();
	}

	@Benchmark
	public UUID My_DCESecurity() {
		return UUIDGenerator.getDCESecurityUUID(1701);
	}

	@Benchmark
	public UUID My_NameBasedMD5() {
		return UUIDGenerator.getNameBasedMD5UUID(name);
	}

	@Benchmark
	public UUID My_NameBasedSHA1() {
		return UUIDGenerator.getNameBasedSHA1UUID(name);
	}

	@Benchmark
	public UUID My_Sequential() {
		return UUIDGenerator.getSequentialUUID();
	}

	@Benchmark
	public UUID My_SequentialMAC() {
		return UUIDGenerator.getSequentialWithHardwareAddressUUID();
	}

	@Benchmark
	public UUID My_TimeBased() {
		return UUIDGenerator.getTimeBasedUUID();
	}

	@Benchmark
	public UUID My_TimeBasedMAC() {
		return UUIDGenerator.getTimeBasedWithHardwareAddressUUID();
	}

	public static void main(String[] args) throws Exception {
		org.openjdk.jmh.Main.main(args);
	}
}
