package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedMd5UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

public class NameBasedMd5UuidCreatorTest extends AbstractUuidCreatorTest {

	private static final int DEFAULT_LOOP_MAX = 100;

	/**
	 * 
	 * Source of examples: https://www.randomlists.com
	 * 
	 * Commands used:
	 * 
	 * uuidgen --md5 --namespace @dns --name "www.example.com"
	 * 
	 * uuidgen --md5 --namespace @url --name "https://www.example.com/"
	 * 
	 * uuidgen --md5 --namespace "3c20a4dd-157c-4e22-865a-89632154b825" --name "The
	 * Movie"
	 * 
	 */
	private static final String[][] LIST_DNS = { //
			{ "817f86bc-5b24-3b96-b81b-9e96fb58b7c0", "www.cnet.com" }, //
			{ "c5adc751-6fa1-30e0-890b-bcca69242cee", "www.examiner.com" }, //
			{ "c91331ef-10fa-3991-8f39-4b0c10645a9d", "www.gov.uk" }, //
			{ "dd2e5195-e0d1-394f-8ba9-307aa2ab4962", "www.intel.com" }, //
			{ "cc26cd3c-61d1-3430-8aa7-b02f014a488c", "www.mit.edu" }, //
			{ "dcab659f-ad0a-34cc-8f80-7862c85206de", "www.prnewswire.com" }, //
			{ "569966b7-1d05-3478-a83d-1689d1707b7b", "www.sitemeter.com" }, //
			{ "ad27aa10-e243-348b-b9a7-7b43a089c5c1", "www.skype.com" }, //
			{ "5b68cc4b-ac2a-3a30-90f7-2de5d7223a24", "www.taobao.com" }, //
			{ "80123f96-d6ae-3cc2-8c4d-f987fdb660cf", "www.tinyurl.com" }, //
			{ "ac2692dc-0410-313a-8483-de8be07f45d8", "www.tuttocitta.it" }, //
			{ "871298a0-027a-3f9a-9bda-c339e3e0b8e6", "www.yale.edu" }, //
	};

	private static final String[][] LIST_URL = { //
			{ "6198f7e6-5154-3ea4-bb9b-ef38628d4789", "http://amusement.example.org/basket" }, //
			{ "773536a8-4b7b-383d-9106-697d4d366254", "http://example.com/" }, //
			{ "0440df8e-c162-3ac0-8b80-64e50016f1ae", "http://example.com/aftermath" }, //
			{ "df2746ca-ad20-3d68-bd5a-74acd87c5211", "http://example.com/bird/bead.html" }, //
			{ "dd3d862d-9bbf-3d8a-880d-d601af871379", "http://example.com/boot/bomb" }, //
			{ "45792131-d809-3c19-b49c-901c9aee7865",
					"https://birth.example.net/bottle/achiever?alarm=bell&amusement=aftermath" }, //
			{ "74a61761-a3d2-3db3-a78f-b6e3a6412b02", "https://example.com/bee/airport#arch" }, //
			{ "7fed185f-0864-319f-875b-a3d5458e30ac", "https://www.example.com/" }, //
			{ "88039255-62dd-3d11-8f2e-c7962cb49984", "https://www.example.com/?achiever=bell" }, //
			{ "aa5bd769-4d61-3b5f-b8ab-57a515c9e6f8", "https://www.example.com/#books" }, //
			{ "1a8add93-a98f-36df-abfc-98b18af0fce4", "https://www.example.net/" }, //
			{ "556cf76b-3b36-3ae6-85f9-50424b369b50", "http://www.example.com/" }, //
	};

	private static final UUID NAMESPACE_MOVIES = UUID.fromString("3c20a4dd-157c-4e22-865a-89632154b825");
	private static final String[][] LIST_MOVIES = { //
			{ "4c466e80-59cc-3ccb-a5f8-79b80c136261", "Animal Crackers" }, //
			{ "d1d5c821-ead5-3c08-aab2-0ee89700991d", "Black Water: Abyss" }, //
			{ "0776d513-8af3-3a9f-a512-fb11542d63ac", "Dino King: Journey to Fire Mountain" }, //
			{ "49a351f0-c725-3d90-b4fb-43e79e2b0a66", "Fast & Furious Presents: Hobbs & Shaw" }, //
			{ "50129117-4b42-39a1-b0de-1ca6eb67a3b2", "Force of Nature" }, //
			{ "5036bdf2-f888-3383-912d-0a01c94461a7", "Host" }, //
			{ "65af000a-0491-3aed-822b-cc164afb2e4c", "Mulan" }, //
			{ "c763d65b-b65a-3b3e-80a9-ebbbb4307852", "Rambo: Last Blood" }, //
			{ "eead91c1-1371-38c6-86b6-21a829cc1011", "Ready Player One" }, //
			{ "ca43ca01-fa48-3303-a914-e95e4f1cc59f", "The Candy Witch" }, //
			{ "aff9b8f5-bd5f-3c3a-bdc9-ef89e70c6c9e", "The Immortal Wars: Resurgence" }, //
			{ "4c89193e-d988-306a-9fe1-616d11d4f7af", "The Rising Hawk" }, //
	};

	@Test
	public void testNameBasedMd5() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

		// Generate a list of UUIDs
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			String name = ("name" + i);
			list[i] = creator.create(name);
		}

		// Check if the same inputs generate the same outputs
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			String name = ("name" + i);
			assertEquals(list[i], creator.create(name));
		}

		// Check the list
		checkNotNull(list);
		checkUniqueness(list);
		checkVersion(list, UuidVersion.VERSION_NAME_BASED_MD5.getValue());
	}

	@Test
	public void testNameBasedMd5NamespaceDns() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_DNS.length; i++) {

			String uuid = LIST_DNS[i][0];
			String name = LIST_DNS[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS, name);
			UUID actual2 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS.getValue(), name);
			UUID actual4 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS.getValue(), name.getBytes());
			UUID actual5 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS.getValue().toString(), name);
			UUID actual6 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_DNS.getValue().toString(),
					name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedMd5UuidCreator
		for (int i = 0; i < LIST_DNS.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_DNS[i][0];
			String name = LIST_DNS[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.create(UuidCreator.NAMESPACE_DNS, name);
			UUID actual2 = creator.create(UuidCreator.NAMESPACE_DNS, name.getBytes());
			UUID actual3 = creator.create(UuidCreator.NAMESPACE_DNS.getValue(), name);
			UUID actual4 = creator.create(UuidCreator.NAMESPACE_DNS.getValue(), name.getBytes());
			UUID actual5 = creator.create(UuidCreator.NAMESPACE_DNS.getValue().toString(), name);
			UUID actual6 = creator.create(UuidCreator.NAMESPACE_DNS.getValue().toString(), name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedMd5UuidCreator with fixed namespace
		for (int i = 0; i < LIST_DNS.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_DNS[i][0];
			String name = LIST_DNS[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.withNamespace(UuidCreator.NAMESPACE_DNS).create(name);
			UUID actual2 = creator.withNamespace(UuidCreator.NAMESPACE_DNS).create(name.getBytes());
			UUID actual3 = creator.withNamespace(UuidCreator.NAMESPACE_DNS.getValue()).create(name);
			UUID actual4 = creator.withNamespace(UuidCreator.NAMESPACE_DNS.getValue()).create(name.getBytes());
			UUID actual5 = creator.withNamespace(UuidCreator.NAMESPACE_DNS.getValue().toString()).create(name);
			UUID actual6 = creator.withNamespace(UuidCreator.NAMESPACE_DNS.getValue().toString())
					.create(name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}
	}

	@Test
	public void testNameBasedMd5NamespaceUrl() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_URL.length; i++) {

			String uuid = LIST_URL[i][0];
			String name = LIST_URL[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL, name);
			UUID actual2 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL.getValue(), name);
			UUID actual4 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL.getValue(), name.getBytes());
			UUID actual5 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL.getValue().toString(), name);
			UUID actual6 = UuidCreator.getNameBasedMd5(UuidCreator.NAMESPACE_URL.getValue().toString(),
					name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedMd5UuidCreator
		for (int i = 0; i < LIST_URL.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_URL[i][0];
			String name = LIST_URL[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.create(UuidCreator.NAMESPACE_URL, name);
			UUID actual2 = creator.create(UuidCreator.NAMESPACE_URL, name.getBytes());
			UUID actual3 = creator.create(UuidCreator.NAMESPACE_URL.getValue(), name);
			UUID actual4 = creator.create(UuidCreator.NAMESPACE_URL.getValue(), name.getBytes());
			UUID actual5 = creator.create(UuidCreator.NAMESPACE_URL.getValue().toString(), name);
			UUID actual6 = creator.create(UuidCreator.NAMESPACE_URL.getValue().toString(), name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedMd5UuidCreator with fixed namespace
		for (int i = 0; i < LIST_URL.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_URL[i][0];
			String name = LIST_URL[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.withNamespace(UuidCreator.NAMESPACE_URL).create(name);
			UUID actual2 = creator.withNamespace(UuidCreator.NAMESPACE_URL).create(name.getBytes());
			UUID actual3 = creator.withNamespace(UuidCreator.NAMESPACE_URL.getValue()).create(name);
			UUID actual4 = creator.withNamespace(UuidCreator.NAMESPACE_URL.getValue()).create(name.getBytes());
			UUID actual5 = creator.withNamespace(UuidCreator.NAMESPACE_URL.getValue().toString()).create(name);
			UUID actual6 = creator.withNamespace(UuidCreator.NAMESPACE_URL.getValue().toString())
					.create(name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}
	}

	@Test
	public void testNameBasedMd5NamespaceMovies() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			String uuid = LIST_MOVIES[i][0];
			String name = LIST_MOVIES[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedMd5(NAMESPACE_MOVIES, name);
			UUID actual2 = UuidCreator.getNameBasedMd5(NAMESPACE_MOVIES, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedMd5(NAMESPACE_MOVIES.toString(), name);
			UUID actual4 = UuidCreator.getNameBasedMd5(NAMESPACE_MOVIES.toString(), name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
		}

		// Test methods of the factory NameBasedMd5UuidCreator
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_MOVIES[i][0];
			String name = LIST_MOVIES[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.create(NAMESPACE_MOVIES, name);
			UUID actual2 = creator.create(NAMESPACE_MOVIES, name.getBytes());
			UUID actual3 = creator.create(NAMESPACE_MOVIES.toString(), name);
			UUID actual4 = creator.create(NAMESPACE_MOVIES.toString(), name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
		}

		// Test methods of the factory NameBasedMd5UuidCreator with fixed namespace
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

			String uuid = LIST_MOVIES[i][0];
			String name = LIST_MOVIES[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = creator.withNamespace(NAMESPACE_MOVIES).create(name);
			UUID actual2 = creator.withNamespace(NAMESPACE_MOVIES).create(name.getBytes());
			UUID actual3 = creator.withNamespace(NAMESPACE_MOVIES.toString()).create(name);
			UUID actual4 = creator.withNamespace(NAMESPACE_MOVIES.toString()).create(name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
		}
	}

	@Test
	public void testGetNameBasedMd5InParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		NameBasedTestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();
			threads[i] = new NameBasedTestThread(creator, DEFAULT_LOOP_MAX);
			threads[i].start();
		}

		// Wait all the threads to finish
		for (Thread thread : threads) {
			thread.join();
		}

		// Check if the quantity of unique UUIDs is correct
		assertEquals(DUPLICATE_UUID_MSG, (DEFAULT_LOOP_MAX * THREAD_TOTAL), NameBasedTestThread.hashSet.size());
	}

	@Test
	public void testGetNameBasedMd5CheckCompatibility() {

		NameBasedMd5UuidCreator creator = UuidCreator.getNameBasedMd5Creator();

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {

			String random = UUID.randomUUID().toString();

			String name = random;
			byte[] bytes = random.getBytes();

			UUID expected = UUID.nameUUIDFromBytes(bytes);
			UUID actual1 = creator.create(name);
			UUID actual2 = creator.create(bytes);

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
		}
	}
}
