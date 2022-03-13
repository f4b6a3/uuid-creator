
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
//import com.github.f4b6a3.uuid.codec.base.Base58BtcCodec;
//import com.github.f4b6a3.uuid.codec.base.Base62Codec;
//import com.github.f4b6a3.uuid.codec.base.Base64Codec;

@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
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
	public String UUID01_toString() {
		return uuid.toString();
	}

	@Benchmark
	public UUID UUID02_fromString() {
		return UUID.fromString(string);
	}

	@Benchmark
	public UUID UUID03_RandomBased() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID UUID04_NameBasedMd5() {
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

	/*********** UUID Codecs ************/

//	String base16 = Base16Codec.INSTANCE.encode(uuid);
//	String base32 = Base32Codec.INSTANCE.encode(uuid);
//	String base58 = Base58BtcCodec.INSTANCE.encode(uuid);
//	String base62 = Base62Codec.INSTANCE.encode(uuid);
//	String base64 = Base64Codec.INSTANCE.encode(uuid);
//	
//	@Benchmark
//	public UUID Base16Codec_decode() {
//		return Base16Codec.INSTANCE.decode(base16);
//	}
//	
//	@Benchmark
//	public UUID Base32Codec_decode() {
//		return Base32Codec.INSTANCE.decode(base32);
//	}
//	
//	@Benchmark
//	public UUID Base58Codec_decode() {
//		return Base58BtcCodec.INSTANCE.decode(base58);
//	}
//	
//	@Benchmark
//	public UUID Base62Codec_decode() {
//		return Base62Codec.INSTANCE.decode(base62);
//	}
//	
//	@Benchmark
//	public UUID Base64Codec_decode() {
//		return Base64Codec.INSTANCE.decode(base64);
//	}
//	
//	@Benchmark
//	public String Base16Codec_encode() {
//		return Base16Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String Base32Codec_encode() {
//		return Base32Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String Base58Codec_encode() {
//		return Base58BtcCodec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String Base62Codec_encode() {
//		return Base62Codec.INSTANCE.encode(uuid);
//	}
//	
//	@Benchmark
//	public String Base64Codec_encode() {
//		return Base64Codec.INSTANCE.encode(uuid);
//	}
}
