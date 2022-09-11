
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
//import com.github.f4b6a3.uuid.codec.base.Base16Codec;
//import com.github.f4b6a3.uuid.codec.base.Base32Codec;
//import com.github.f4b6a3.uuid.codec.base.Base58BtcCodec;
//import com.github.f4b6a3.uuid.codec.base.Base62Codec;
//import com.github.f4b6a3.uuid.codec.base.Base64UrlCodec;

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
	private byte[] bytes = "http:www.github.com".getBytes();

	/*********** JDK UUID ***********/

	@Benchmark
	public String jdk_uuid_01_to_string() {
		return uuid.toString();
	}

	@Benchmark
	public UUID jdk_uuid_02_from_string() {
		return UUID.fromString(string);
	}

	@Benchmark
	public UUID jdk_uuid_03_random_based() {
		return UUID.randomUUID();
	}

	@Benchmark
	public UUID jdk_uuid_04_name_based_md5() {
		return UUID.nameUUIDFromBytes(bytes);
	}

	/*********** UUID Creator ***********/

	@Benchmark
	public String uuid_creator_01_to_string() {
		return UuidCreator.toString(uuid);
	}

	@Benchmark
	public UUID uuid_creator_02_from_string() {
		return UuidCreator.fromString(string);
	}

	@Benchmark
	public UUID uuid_creator_03_random_based() {
		return UuidCreator.getRandomBased();
	}

	@Benchmark
	public UUID uuid_creator_04_prefix_comb() {
		return UuidCreator.getPrefixComb();
	}

	@Benchmark
	public UUID uuid_creator_05_short_prefix_comb() {
		return UuidCreator.getShortPrefixComb();
	}

	@Benchmark
	public UUID uuid_creator_06_name_based_md5() {
		return UuidCreator.getNameBasedMd5(bytes);
	}

	@Benchmark
	public UUID uuid_creator_07_name_based_sha1() {
		return UuidCreator.getNameBasedSha1(bytes);
	}

	@Benchmark
	public UUID uuid_creator_08_time_based() {
		return UuidCreator.getTimeBased();
	}

	@Benchmark
	public UUID uuid_creator_09_time_ordered() {
		return UuidCreator.getTimeOrdered();
	}

	@Benchmark
	public UUID uuid_creator_10_time_ordered_epoch() {
		return UuidCreator.getTimeOrderedEpoch();
	}

	@Benchmark
	public UUID uuid_creator_11_time_ordered_epoch_plus1() {
		return UuidCreator.getTimeOrderedEpochPlus1();
	}

	@Benchmark
	public UUID uuid_creator_12_time_ordered_epoch_plusn() {
		return UuidCreator.getTimeOrderedEpochPlusN();
	}

	/*********** UUID Codecs ************/

//	String base16 = Base16Codec.INSTANCE.encode(uuid);
//	String base32 = Base32Codec.INSTANCE.encode(uuid);
//	String base58 = Base58BtcCodec.INSTANCE.encode(uuid);
//	String base62 = Base62Codec.INSTANCE.encode(uuid);
//	String base64 = Base64UrlCodec.INSTANCE.encode(uuid);
//
//	@Benchmark
//	public UUID uuid_codec_base_16_decode() {
//		return Base16Codec.INSTANCE.decode(base16);
//	}
//
//	@Benchmark
//	public UUID uuid_codec_base_32_decode() {
//		return Base32Codec.INSTANCE.decode(base32);
//	}
//
//	@Benchmark
//	public UUID uuid_codec_base_58_decode() {
//		return Base58BtcCodec.INSTANCE.decode(base58);
//	}
//
//	@Benchmark
//	public UUID uuid_codec_base_62_decode() {
//		return Base62Codec.INSTANCE.decode(base62);
//	}
//
//	@Benchmark
//	public UUID uuid_codec_base_64_decode() {
//		return Base64UrlCodec.INSTANCE.decode(base64);
//	}
//
//	@Benchmark
//	public String uuid_codec_base_16_encode() {
//		return Base16Codec.INSTANCE.encode(uuid);
//	}
//
//	@Benchmark
//	public String uuid_codec_base_32_encode() {
//		return Base32Codec.INSTANCE.encode(uuid);
//	}
//
//	@Benchmark
//	public String uuid_codec_base_58_encode() {
//		return Base58BtcCodec.INSTANCE.encode(uuid);
//	}
//
//	@Benchmark
//	public String uuid_codec_base_62_encode() {
//		return Base62Codec.INSTANCE.encode(uuid);
//	}
//
//	@Benchmark
//	public String uuid_codec_base_64_encode() {
//		return Base64UrlCodec.INSTANCE.encode(uuid);
//	}
}
