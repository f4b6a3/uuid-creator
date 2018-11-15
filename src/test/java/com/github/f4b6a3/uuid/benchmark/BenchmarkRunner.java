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

import com.github.f4b6a3.uuid.UUIDGenerator;
import com.github.f4b6a3.uuid.factory.NameBasedUUIDCreator;

@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BenchmarkRunner {

	/*
	private UUID namespace;
    private String name;

    @Setup
    public void setUp() {
    	namespace = NameBasedUUIDCreator.NAMESPACE_URL;
        name = "https://github.com/";
    }
    
    @Benchmark
    public UUID javaUtilUUIDRandom() {
        return UUID.randomUUID();
    }
    
    @Benchmark
    public UUID random() {
        return UUIDGenerator.getRandomUUID();
    }
    
    @Benchmark
    public UUID randomFast() {
        return UUIDGenerator.getRandomFastUUID();
    }
    
    @Benchmark
    public UUID nameBasedMD5() {
        return UUIDGenerator.getNameBasedMD5UUID(namespace, name);
    }
    
    @Benchmark
    public UUID nameBasedSHA1() {
        return UUIDGenerator.getNameBasedSHA1UUID(namespace, name);
    }
      
    @Benchmark
    public UUID timeBasedMAC() {
        return UUIDGenerator.getTimeBasedWithHardwareAddressUUID();
    }
    
    @Benchmark
    public UUID sequentialMAC() {
    	return UUIDGenerator.getSequentialWithHardwareAddressUUID();
    }

    @Benchmark
    public UUID sequential() {
    	return UUIDGenerator.getSequentialUUID();
    } 
    */
	
    @Benchmark
    public UUID timeBased() {
        return UUIDGenerator.getTimeBasedUUID();
    }
    
    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
