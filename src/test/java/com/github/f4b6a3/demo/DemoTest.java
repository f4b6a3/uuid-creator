package com.github.f4b6a3.demo;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.junit.Ignore;

import com.github.f4b6a3.other.RandomImage;
import com.github.f4b6a3.other.SimpleBenchmark;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidNamespace;
import com.github.f4b6a3.uuid.factory.DceSecurityUuidCreator;
import com.github.f4b6a3.uuid.nodeid.RandomNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.MacNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.nodeid.DefaultNodeIdentifierStrategy;
import com.github.f4b6a3.uuid.random.Xoroshiro128PlusRandom;
import com.github.f4b6a3.uuid.random.Xorshift128PlusRandom;
import com.github.f4b6a3.uuid.random.XorshiftRandom;
import com.github.f4b6a3.uuid.random.XorshiftStarRandom;
import com.github.f4b6a3.uuid.timestamp.DefaultTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.DeltaTimestampStrategy;
import com.github.f4b6a3.uuid.timestamp.NanosecondTimestampStrategy;
import com.github.f4b6a3.uuid.util.LogUtil;
import com.github.f4b6a3.uuid.util.SystemDataUtil;
import com.github.f4b6a3.uuid.util.TimestampUtil;
import com.github.f4b6a3.uuid.util.UuidUtil;

public class DemoTest {

	private static final String HORIZONTAL_LINE = "----------------------------------------";

	@Ignore
	public void testPrintList() {
		int max = 100;
		
		LogUtil.log();
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("Print list of UUIDs");
		LogUtil.log(HORIZONTAL_LINE);

		LogUtil.log();
		LogUtil.log("### Random UUID");

		for (int i = 0; i < max; i++) {
			LogUtil.log(UuidCreator.getFastRandom());
		}

		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("### Time-based UUID");

		for (int i = 0; i < max; i++) {
			LogUtil.log(UuidCreator.getTimeBased());
		}

		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("### Sequential UUID");

		for (int i = 0; i < max; i++) {
			LogUtil.log(UuidCreator.getSequential());
		}

		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("### MSSQL GUID");

		for (int i = 0; i < max; i++) {
			LogUtil.log(UuidCreator.getMssqlGuid());
		}

		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("### COMB GUID");

		for (int i = 0; i < max; i++) {
			LogUtil.log(UuidCreator.getCombGuid());
		}

		LogUtil.log(HORIZONTAL_LINE);
	}

	/**
	 * This method only prints average running times.
	 */
	@Ignore
	public void testEstimateRunningTimes() {
		long loopMax = 100_000;
		long nano = 1_000_000;
		long randomUUID = (SimpleBenchmark.run(null, UUID.class, "randomUUID", loopMax) * loopMax) / nano;
		long getRandomUUID = (SimpleBenchmark.run(null, UuidCreator.class, "getRandom", loopMax) * loopMax) / nano;
		long getTimeBasedUUID = (SimpleBenchmark.run(null, UuidCreator.class, "getTimeBased", loopMax) * loopMax)
				/ nano;
		long getSequentialUUID = (SimpleBenchmark.run(null, UuidCreator.class, "getSequential", loopMax) * loopMax)
				/ nano;
		long javaNextLong = (SimpleBenchmark.run(new Random(), null, "nextLong", loopMax) * loopMax) / nano;
		long xorshiftNextLong = (SimpleBenchmark.run(new XorshiftRandom(), null, "nextLong", loopMax) * loopMax) / nano;

		LogUtil.log();
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log(String.format("Average generation times for %,d UUIDs", loopMax));
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log(String.format("* java.util.UUID.randomUUID():       %s ms", randomUUID));
		LogUtil.log(String.format("* UUIDGenerator.getRandomUUID():     %s ms", getRandomUUID));
		LogUtil.log(String.format("* UUIDGenerator.getTimeBasedUUID():  %s ms", getTimeBasedUUID));
		LogUtil.log(String.format("* UUIDGenerator.getSequentialUUID(): %s ms", getSequentialUUID));
		LogUtil.log(String.format("* java.util.Random.nextLong(): %s ms", javaNextLong));
		LogUtil.log(String.format("* XorshiftRandom.nextLong(): %s ms", xorshiftNextLong));
		LogUtil.log(HORIZONTAL_LINE);
	}

	/**
	 * Just prints UUIDs generated to a specific instant.
	 */
	@Ignore
	public void testDemoDifferenceBetweenTimeBasedAndSequentialUUID() {

		Instant instant = Instant.now();
		String timeBasedUUID = UuidCreator.getTimeBasedCreator().withInstant(instant).create().toString();
		String sequentialUUID = UuidCreator.getSequentialCreator().withInstant(instant).create().toString();

		Logger.getAnonymousLogger().info("");
		LogUtil.log();
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("Demonstration of time-baed UUIDs");
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("- TimeBased UUID:          " + timeBasedUUID);
		LogUtil.log("- Sequential UUID:         " + sequentialUUID);
		LogUtil.log("- Original instant:        " + instant.toString());
		LogUtil.log("- TimeBased UUID instant:  " + UuidUtil.extractInstant(UUID.fromString(timeBasedUUID)));
		LogUtil.log("- Sequential UUID instant: " + UuidUtil.extractInstant(UUID.fromString(sequentialUUID)));
		LogUtil.log(HORIZONTAL_LINE);
	}

	/**
	 * Generate images with random pixels.
	 */
	@Ignore
	public void testCreateRandomImage() {

		LogUtil.log();
		LogUtil.log(HORIZONTAL_LINE);
		LogUtil.log("Creating images from random numbers...");
		LogUtil.log(HORIZONTAL_LINE);

		RandomImage.createRandomImageFile("/tmp/java.util.Random.png", new Random(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/java.security.SecureRandom.png", new SecureRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/XorshiftRandom.png", new XorshiftRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/XorshiftStarRandom.png", new XorshiftStarRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/Xorshift128PlusRandom.png", new Xorshift128PlusRandom(), 0, 0);
		RandomImage.createRandomImageFile("/tmp/Xoroshiro128PlusRandom.png", new Xoroshiro128PlusRandom(), 0, 0);

		LogUtil.log(HORIZONTAL_LINE);
	}

	@Ignore
	public void testStrategiesUsingFlowInterface() {

		UUID uuid = null;

		LogUtil.log("\n#### Time-based");

		LogUtil.log("\n##### Fixed values");

		// @formatter:off
		uuid = UuidCreator.getTimeBasedCreator().withInstant(Instant.now()).create();
		LogUtil.log(String.format("%s // with fixed instant (now)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withTimestamp(TimestampUtil.toTimestamp(Instant.now())).create();
		LogUtil.log(String.format("%s // with fixed timestamp (now as timestamp)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withClockSequence(0x8888).create();
		LogUtil.log(String.format("%s // with fixed clock sequence (0x8888)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withNodeIdentifier(0x111111111111L).create();
		LogUtil.log(String.format("%s // with fixed node identifier (0x111111111111L)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withHardwareAddress().create();
		LogUtil.log(String.format("%s // with hardware address (first MAC found)", uuid));

		LogUtil.log("\n##### Timestamp strategy");

		uuid = UuidCreator.getTimeBasedCreator().withTimestampStrategy(new DefaultTimestampStrategy()).create();
		LogUtil.log(String.format("%s // with default timestamp strategy (System.currentTimeMillis() + counter)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withTimestampStrategy(new NanosecondTimestampStrategy()).create();
		LogUtil.log(String.format("%s // with nanoseconds timestamp strategy (Instant.getNano())", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withTimestampStrategy(new DeltaTimestampStrategy()).create();
		LogUtil.log(String.format("%s // with delta timestamp strategy (diff of subsequent System.nanoTime())", uuid));

		LogUtil.log("\n##### Node identifier strategy");

		uuid = UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(new DefaultNodeIdentifierStrategy()).create();
		LogUtil.log(String.format("%s // with default node identifier strategy (host name, IP, MAC, OS and JVM)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(new RandomNodeIdentifierStrategy()).create();
		LogUtil.log(String.format("%s // with random node identifier strategy (random number generated once)", uuid));

		uuid = UuidCreator.getTimeBasedCreator().withNodeIdentifierStrategy(new MacNodeIdentifierStrategy()).create();
		LogUtil.log(String.format("%s // with hardware address node identifier strategy (first MAC found)", uuid));

		LogUtil.log("\n#### Name-based");

		uuid = UuidCreator.getNameBasedMd5Creator().withNamespace("USERS").create("Paul");
		LogUtil.log(String.format("%s // with fixed namespace as string (USERS)", uuid));

		uuid = UuidCreator.getNameBasedMd5Creator().withNamespace(UuidNamespace.NAMESPACE_DNS.getValue()).create("www.github.com");
		LogUtil.log(String.format("%s // with fixed namespace as UUID (standard DNS namespace)", uuid));

		LogUtil.log("\n#### Random");

		uuid = UuidCreator.getRandomCreator().withRandomGenerator(new Random()).create();
		LogUtil.log(String.format("%s // with java random generator (java.util.Random)", uuid));

		uuid = UuidCreator.getRandomCreator().withFastRandomGenerator().create();
		LogUtil.log(String.format("%s // with fast random generator (Xorshift128Plus)", uuid));

		LogUtil.log("\n#### DCE Security");

		uuid = UuidCreator.getDceSecurityCreator().withLocalDomain(DceSecurityUuidCreator.LOCAL_DOMAIN_PERSON).create(1701);
		LogUtil.log(String.format("%s // with fixed local domain (standard POSIX User ID)", uuid));
		// @formatter:on
	}
	
	@Ignore
	public void testGetNetworkData() {
		
		long startTime = 0;
		long endTime = 0;
		
		startTime = System.currentTimeMillis();
		
		for(int i = 0; i < 100; i++) {
			SystemDataUtil.getNetworkData();
		}
		
		endTime = System.currentTimeMillis();
		
		LogUtil.log("get network data: " + (endTime - startTime) + " ms");
		
		startTime = System.currentTimeMillis();
		
		for(int i = 0; i < 100; i++) {
			SystemDataUtil.getNetworkDataList();
		}
		
		endTime = System.currentTimeMillis();
		
		LogUtil.log("get network data list: " + (endTime - startTime) + " ms");
	}
	

}
