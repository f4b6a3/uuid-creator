
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
import com.github.f4b6a3.uuid.UuidCreatorWrapper;
import com.github.f4b6a3.uuid.alt.GUID;

@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
//@Warmup(iterations = 5, time = 6)
//@Measurement(iterations = 5, time = 12)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {
	
	@Benchmark
	public UUID getTimeOrderedEpochPlus1() {
		return UuidCreator.getTimeOrderedEpochPlus1();
	}
	
	@Benchmark
	public UUID getTimeOrderedEpochPlusN() {
		return UuidCreator.getTimeOrderedEpochPlusN();
	}
	

	@Benchmark
	public UUID getRandomUUID() {
		return UUID.randomUUID();
	}
	
	@Benchmark
	public UUID getTimeOrderedEpoch() {
		return UuidCreator.getTimeOrderedEpoch();
	}

//	Benchmark                             Mode  Cnt      Score      Error   Units
//	Throughput.getTimeOrderedEpoch       thrpt    5   2107,234 ±  103,967  ops/ms
//	Throughput.getTimeOrderedEpochPlus1  thrpt    5  12064,203 ± 1961,822  ops/ms
//	Throughput.getTimeOrderedEpochPlusN  thrpt    5   2256,414 ±  619,088  ops/ms


}
