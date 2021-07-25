
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

@Fork(1)
@Threads(1)
@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {

	UUID uuid = UUID.randomUUID();
	String string = uuid.toString();
	private byte[] bytes = "http://www.github.com".getBytes();
	
	/*********** JDK UUID ***********/
	
	@Benchmark
	public String Uuid01_toString() {
		return uuid.toString();
	}

	@Benchmark
	public UUID Uuid02_fromString() {
		return UUID.fromString(string);
	}

	@Benchmark
	public UUID Uuid03_RandomBased() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID Uuid04_NameBasedMd5() {
		return UUID.nameUUIDFromBytes(bytes);
	}
	
	/*********** UUID Creator ***********/
	
	@Benchmark
	public String UuidCreator01_toString() {
		return UuidCreator.toString(uuid);
	}

	@Benchmark
	public UUID UuidCreator02_fromString() {
		return UuidCreator.fromString(string);
	}

	@Benchmark
	public UUID UuidCreator03_RandomBased() {
		return UuidCreator.getRandomBased();
	}

	@Benchmark
	public UUID UuidCreator04_PrefixComb() {
		return UuidCreator.getPrefixComb();
	}

	@Benchmark
	public UUID UuidCreator05_ShortPrefixComb() {
		return UuidCreator.getShortPrefixComb();
	}

	@Benchmark
	public UUID UuidCreator06_NameBasedMd5() {
		return UuidCreator.getNameBasedMd5(bytes);
	}

	@Benchmark
	public UUID UuidCreator07_NameBasedSha1() {
		return UuidCreator.getNameBasedSha1(bytes);
	}

	@Benchmark
	public UUID UuidCreator08_TimeBased() {
		return UuidCreator.getTimeBased();
	}

	@Benchmark
	public UUID UuidCreator09_TimeOrdered() {
		return UuidCreator.getTimeOrdered();
	}
}
