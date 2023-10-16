
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
@Threads(4)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
//@Warmup(iterations = 5, time = 1)
//@Measurement(iterations = 5, time = 3)
@Warmup(iterations = 5, time = 6)
@Measurement(iterations = 5, time = 12)
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
	public UUID getTimeOrderedEpoch() {
		return UuidCreator.getTimeOrderedEpoch();
	}

//  JDK 8 (synchronized)
//	Benchmark                       Mode  Cnt      Score     Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  10968,052 ± 439,404  ops/ms

//  JDK 8 (ReentrantLock)
//	Benchmark                       Mode  Cnt     Score      Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  8478,789 ± 1724,910  ops/ms

//  JDK 11 (synchronized)
//	Benchmark                       Mode  Cnt      Score     Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  11208,180 ± 585,846  ops/ms

//  JDK 11 (ReentrantLock)
//	Benchmark                       Mode  Cnt      Score      Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  10734,693 ± 1148,196  ops/ms

//  JDK 17 (synchronized)
//	Benchmark                       Mode  Cnt      Score      Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  11822,357 ± 2584,825  ops/ms

//  JDK 17 (ReentrantLock)
//	Benchmark                       Mode  Cnt      Score      Error   Units
//	Throughput.uuidCreatorV7plus1  thrpt    5  11722,471 ± 1398,280  ops/ms

//	@Benchmark
//	public UUID uuidCreatorWrapper() {
//		return UuidCreatorWrapper.getUUID();
//	}

//  JDK 17 (synchronized + ReentrantLock Wrapper)
//	Benchmark                       Mode  Cnt     Score     Error   Units
//	Throughput.uuidCreatorWrapper  thrpt    5  9730,139 ± 722,465  ops/ms

}
