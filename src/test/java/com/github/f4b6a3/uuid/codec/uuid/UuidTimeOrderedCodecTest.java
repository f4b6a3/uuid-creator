package com.github.f4b6a3.uuid.codec.uuid;

import static com.github.f4b6a3.uuid.util.UuidUtil.isTimeBased;
import static com.github.f4b6a3.uuid.util.UuidUtil.isTimeOrdered;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.codec.UuidCodec;

public class UuidTimeOrderedCodecTest {

	private static final int DEFAULT_LOOP_LIMIT = 1_000;

	private static final UuidCodec<UUID> CODEC = new UuidTimeOrderedCodec();

	@Test
	public void testEncode() {
		// Using samples
		String sample1 = "5714f720-1268-11e7-a24b-96d95aa38c32"; // UUID v1
		String sample2 = "1e712685-714f-6720-a24b-96d95aa38c32"; // UUID v6
		UUID uuidv1 = UUID.fromString(sample1);
		UUID uuidv6 = UUID.fromString(sample2);
		UUID result = CODEC.encode(uuidv1);
		assertEquals(uuidv6, result);

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv6 = UuidCreator.getTimeOrdered();
			uuidv1 = CODEC.decode(uuidv6);
			result = CODEC.encode(uuidv1);
			assertEquals(uuidv6, result);
		}
	}

	@Test
	public void testDecode() {

		// Using samples
		String sample1 = "1e71269a-b116-6740-a694-68c004266291"; // UUID v6
		String sample2 = "ab116740-1269-11e7-a694-68c004266291"; // UUID v1
		UUID uuidv6 = UUID.fromString(sample1);
		UUID uuidv1 = UUID.fromString(sample2);
		UUID result = CODEC.decode(uuidv6);
		assertEquals(uuidv1, result);

		// Using loop
		for (int i = 0; i < DEFAULT_LOOP_LIMIT; i++) {
			uuidv1 = UuidCreator.getTimeBased();
			uuidv6 = CODEC.encode(uuidv1);
			result = CODEC.decode(uuidv6);
			assertEquals(uuidv1, result);
		}
	}

	@Test
	public void testEncodeIsTimeOrderedUuid() {
		UUID uuid1 = UuidCreator.getTimeBased();
		UUID uuid2 = CODEC.encode(uuid1);
		assertTrue(isTimeOrdered(uuid2));
	}

	@Test
	public void testDecodeIsTimeBasedAfter() {
		UUID uuid1 = UuidCreator.getTimeOrdered();
		UUID uuid2 = CODEC.decode(uuid1);
		assertTrue(isTimeBased(uuid2));
	}
}
