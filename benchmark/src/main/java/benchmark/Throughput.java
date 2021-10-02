
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
//import com.github.f4b6a3.uuid.codec.UuidCodec;
//import com.github.f4b6a3.uuid.codec.base.Base16Codec;
//import com.github.f4b6a3.uuid.codec.base.Base32Codec;
//import com.github.f4b6a3.uuid.codec.base.Base36Codec;
//import com.github.f4b6a3.uuid.codec.base.Base58Codec;
//import com.github.f4b6a3.uuid.codec.base.Base62Codec;
//import com.github.f4b6a3.uuid.codec.base.Base64Codec;

@Fork(1)
@Threads(1)
@State(Scope.Thread)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {

	String string = "01234567-89ab-cdef-0123-456789abcdef";
	UUID uuid = UUID.fromString(string);
	private byte[] bytes = "http:www.github.com".getBytes();
	
	/*********** JDK UUID ***********/
	
	@Benchmark
	public String uuid01ToString() {
		return uuid.toString();
	}

	@Benchmark
	public UUID uuid02FromString() {
		return UUID.fromString(string);
	}

	@Benchmark
	public UUID uuid03RandomBased() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID uuid04NameBasedMd5() {
		return UUID.nameUUIDFromBytes(bytes);
	}
	
	/*********** UUID Creator ***********/
	
	@Benchmark
	public String uuidCreator01ToString() {
		return UuidCreator.toString(uuid);
	}

	@Benchmark
	public UUID uuidCreator02FromString() {
		return UuidCreator.fromString(string);
	}

	@Benchmark
	public UUID uuidCreator03RandomBased() {
		return UuidCreator.getRandomBased();
	}

	@Benchmark
	public UUID uuidCreator04PrefixComb() {
		return UuidCreator.getPrefixComb();
	}

	@Benchmark
	public UUID uuidCreator05ShortPrefixComb() {
		return UuidCreator.getShortPrefixComb();
	}

	@Benchmark
	public UUID uuidCreator06NameBasedMd5() {
		return UuidCreator.getNameBasedMd5(bytes);
	}

	@Benchmark
	public UUID uuidCreator07NameBasedSha1() {
		return UuidCreator.getNameBasedSha1(bytes);
	}

	@Benchmark
	public UUID uuidCreator08TimeBased() {
		return UuidCreator.getTimeBased();
	}

	@Benchmark
	public UUID uuidCreator09TimeOrdered() {
		return UuidCreator.getTimeOrdered();
	}
	
	/*********** UUID Codecs ************/
	
//	String base16 = Base16Codec.INSTANCE.encode(uuid);
//	String base32 = Base32Codec.INSTANCE.encode(uuid);
//	String base36 = Base36Codec.INSTANCE.encode(uuid);
//	String base58 = Base58Codec.INSTANCE.encode(uuid);
//	String base62 = Base62Codec.INSTANCE.encode(uuid);
//	String base64 = Base64Codec.INSTANCE.encode(uuid);
//	
//	@Benchmark
//	public UUID base16CodecDecode() {
//		return Base16Codec.INSTANCE.decode(base16);
//	}
//	
//	@Benchmark
//	public UUID base32CodecDecode() {
//		return Base32Codec.INSTANCE.decode(base32);
//	}
//	
//	@Benchmark
//	public UUID base36CodecDecode() {
//		return Base36Codec.INSTANCE.decode(base36);
//	}
//	
//	@Benchmark
//	public UUID base58CodecDecode() {
//		return Base58Codec.INSTANCE.decode(base58);
//	}
//	
//	@Benchmark
//	public UUID base62CodecDecode() {
//		return Base62Codec.INSTANCE.decode(base62);
//	}
//	
//	@Benchmark
//	public UUID base64CodecDecode() {
//		return Base64Codec.INSTANCE.decode(base64);
//	}
//	
//	@Benchmark
//	public String base16CodecEncode() {
//		return Base16Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String base32CodecEncode() {
//		return Base32Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String base36CodecEncode() {
//		return Base36Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String base58CodecEncode() {
//		return Base58Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String base62CodecEncode() {
//		return Base62Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String base64CodecEncode() {
//		return Base64Codec.INSTANCE.encode(uuid);
//	}
}
