
package benchmark;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.alt.GUID;

@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {

	String string = "01234567-89ab-cdef-0123-456789abcdef";
	UUID uuid = UUID.fromString(string);
	GUID guid = new GUID(uuid);
	private byte[] bytes = "http:www.github.com".getBytes();

	/*********** JDK UUID ***********/

	@Benchmark
	public String jdkUUIDToString() {
		return uuid.toString();
	}

	@Benchmark
	public UUID jdkUUIDFromString() {
		return UUID.fromString(string);
	}

	@Benchmark
	public UUID jdkUUIDv4() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID jdkUUIDv3() {
		return UUID.nameUUIDFromBytes(bytes);
	}

	/*********** UUID Creator ***********/

	@Benchmark
	public String uuidCreatorToString() {
		return UuidCreator.toString(uuid);
	}

	@Benchmark
	public UUID uuidCreatorFromString() {
		return UuidCreator.fromString(string);
	}

	@Benchmark
	public UUID uuidCreatorV1() {
		return UuidCreator.getTimeBased();
	}

	@Benchmark
	public UUID uuidCreatorV3() {
		return UuidCreator.getNameBasedMd5(bytes);
	}

	@Benchmark
	public UUID uuidCreatorV4() {
		return UuidCreator.getRandomBased();
	}

	@Benchmark
	public UUID uuidCreatorV5() {
		return UuidCreator.getNameBasedSha1(bytes);
	}

	@Benchmark
	public UUID uuidCreatorV6() {
		return UuidCreator.getTimeOrdered();
	}

	@Benchmark
	public UUID uuidCreatorV7() {
		return UuidCreator.getTimeOrderedEpoch();
	}

	/*********** GUID ***********/

	@Benchmark
	public String altGUIDToString() {
		return guid.toString();
	}

	@Benchmark
	public GUID altGUIDFromString() {
		return new GUID(string);
	}
	
	@Benchmark
	public GUID altGUIDv1() {
		return GUID.v1();
	}

	@Benchmark
	public GUID altGUIDv3() {
		return GUID.v3(null, string);
	}

	@Benchmark
	public GUID altGUIDv4() {
		return GUID.v4();
	}

	@Benchmark
	public GUID altGUIDv5() {
		return GUID.v5(null, string);
	}

	@Benchmark
	public GUID altGUIDv6() {
		return GUID.v6();
	}

	@Benchmark
	public GUID altGUIDv7() {
		return GUID.v7();
	}
}
