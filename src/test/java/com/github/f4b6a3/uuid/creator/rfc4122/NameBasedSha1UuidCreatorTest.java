package com.github.f4b6a3.uuid.creator.rfc4122;

import org.junit.Test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.creator.AbstractUuidCreatorTest;
import com.github.f4b6a3.uuid.creator.rfc4122.NameBasedSha1UuidCreator;
import com.github.f4b6a3.uuid.enums.UuidVersion;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

public class NameBasedSha1UuidCreatorTest extends AbstractUuidCreatorTest {

	private static final int DEFAULT_LOOP_MAX = 100;

	/**
	 * 
	 * Source of examples: https://www.randomlists.com
	 * 
	 * Commands used:
	 * 
	 * uuidgen --sha1 --namespace @dns --name "www.example.com"
	 * 
	 * uuidgen --sha1 --namespace @url --name "https://www.example.com/"
	 * 
	 * uuidgen --sha1 --namespace "3c20a4dd-157c-4e22-865a-89632154b825" --name "The
	 * Movie"
	 * 
	 */
	private static final String[][] LIST_DNS = { //
			{ "d47fb1f9-9829-580f-887a-212253f600fc", "www.amazon.de" }, //
			{ "755c52b7-cb5d-591e-998e-c839ea45664d", "www.aol.com" }, //
			{ "ec602edd-a876-5ceb-bc0d-47d23a3cf0a0", "www.dell.com" }, //
			{ "066e11fb-16d1-5ebc-90d3-da3af0e0f461", "www.dot.gov" }, //
			{ "2ed6657d-e927-568b-95e1-2665a8aea6a2", "www.example.com" }, //
			{ "48adeaa0-d5c5-5ed1-8d01-d1d9243080c2", "www.home.pl" }, //
			{ "a44d4f2d-0d59-5b73-94c6-f0d58544d9ef", "www.naver.com" }, //
			{ "85d2dbee-39e0-5a27-92bc-8a7a1bf3158e", "www.opensource.org" }, //
			{ "1eaf0cb1-fb36-5fe1-988b-fc95e4068ad9", "www.privacy.gov.au" }, //
			{ "fceee119-7b23-5d08-90e8-01f3747b58f7", "www.samsung.com" }, //
			{ "5197121f-8af2-5e9a-9d45-b8595036512f", "www.wunderground.com" }, //
			{ "3ea285aa-a9bb-5edb-994f-0b8445e804ef", "www.zimbio.com" }, //
	};

	private static final String[][] LIST_URL = { //
			{ "b87fc5de-0f35-5c4e-aa98-09af1104ae6a", "http://anger.example.com/border/blade.html" }, //
			{ "148c9b87-5ee8-5642-a594-f6f211e560fe", "http://basket.example.com/" }, //
			{ "6bf7b75e-1b31-5f37-9178-e16c6c20e15b", "http://bikes.example.org/?art=books" }, //
			{ "0a300ee9-f9e4-5697-a51a-efc7fafaba67", "http://example.com/" }, //
			{ "554d83e9-bb4d-57d5-b6e8-c60a5054d662", "https://advertisement.example.com/amount/basketball" }, //
			{ "52cdf6ec-c0d9-5a9f-844b-d2228d85dfa5", "https://www.example.com/?beds=bedroom&bear=animal" }, //
			{ "c0e4d795-dbfc-5340-9e5b-fbdf69176183", "https://www.example.com/?bridge=bear" }, //
			{ "3a46d9e4-53c3-5c6e-8dc9-52abd8a5e09e", "https://www.example.edu/bead#bell" }, //
			{ "ae9b57c4-a56a-5607-adcb-c069724bd1f7", "https://www.example.net/" }, //
			{ "911b3d40-f52e-57fd-a7e6-f11f861ab1ec", "https://www.example.net/aftermath?bait=arithmetic" }, //
			{ "88c2936c-68e4-5770-b910-7ea30fdecae1", "http://www.example.com/angle/beginner.php" }, //
			{ "39ee08dc-e9aa-5e91-a00e-fc9f5fbee4d0", "http://www.example.com/#apparel" }, //
	};

	private static final UUID NAMESPACE_MOVIES = UUID.fromString("3c20a4dd-157c-4e22-865a-89632154b825");
	private static final String[][] LIST_MOVIES = { //
			{ "4e841bcd-a125-5692-9469-d9c537cb1a79", "Ava" }, //
			{ "bd6b30c1-5616-5f4d-b35c-baedf4327133", "Black and Blue" }, //
			{ "1079f2f2-f231-5cb8-bc33-363710c90d93", "Centigrade" }, //
			{ "99028b51-709a-5d99-8380-254491f4d25b", "Deathstroke: Knights & Dragons - The Movie" }, //
			{ "48a0cb3d-5e88-5b92-b727-d6a7644394ee", "Fast & Furious Presents: Hobbs & Shaw" }, //
			{ "2ef8e877-92ab-51b7-bfc4-a083c124c36b", "Fat Ass Zombies" }, //
			{ "49262659-f891-5cf3-a48c-99bc281c2fb2", "Infamous" }, //
			{ "ecf6779f-c99a-510d-b7fc-3f66c1fd060e", "Lamp Life" }, //
			{ "406690a8-0bfc-5e06-a776-e2ae2fb4c581", "Ready Player One" }, //
			{ "7b516eb5-d7e1-54f6-9941-9008592f35c9", "The Flu" }, //
			{ "e824f17c-360e-5eab-836e-e879853f1310", "The Pale Door" }, //
			{ "0c5576e8-2975-5880-be15-7b260027fb05", "Toxic" }, //
	};

	@Test
	public void testNameBasedSha1() {

		UUID[] list = new UUID[DEFAULT_LOOP_MAX];
		NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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
		checkVersion(list, UuidVersion.VERSION_NAME_BASED_SHA1.getValue());
	}

	@Test
	public void testNameBasedSha1WithNamespaceDns() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_DNS.length; i++) {

			String uuid = LIST_DNS[i][0];
			String name = LIST_DNS[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS, name);
			UUID actual2 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS.getValue(), name);
			UUID actual4 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS.getValue(), name.getBytes());
			UUID actual5 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS.getValue().toString(), name);
			UUID actual6 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_DNS.getValue().toString(),
					name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedSha1UuidCreator
		for (int i = 0; i < LIST_DNS.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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

		// Test methods of the factory NameBasedSha1UuidCreator with fixed namespace
		for (int i = 0; i < LIST_DNS.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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
	public void testNameBasedSha1WithNamespaceUrl() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_URL.length; i++) {

			String uuid = LIST_URL[i][0];
			String name = LIST_URL[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL, name);
			UUID actual2 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL.getValue(), name);
			UUID actual4 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL.getValue(), name.getBytes());
			UUID actual5 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL.getValue().toString(), name);
			UUID actual6 = UuidCreator.getNameBasedSha1(UuidCreator.NAMESPACE_URL.getValue().toString(),
					name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
			assertEquals(expected, actual5);
			assertEquals(expected, actual6);
		}

		// Test methods of the factory NameBasedSha1UuidCreator
		for (int i = 0; i < LIST_URL.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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

		// Test methods of the factory NameBasedSha1UuidCreator with fixed namespace
		for (int i = 0; i < LIST_URL.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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
	public void testNameBasedSha1WithNamespaceMovies() {

		// Test methods of the facade UuidCreator
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			String uuid = LIST_MOVIES[i][0];
			String name = LIST_MOVIES[i][1];

			UUID expected = UUID.fromString(uuid);
			UUID actual1 = UuidCreator.getNameBasedSha1(NAMESPACE_MOVIES, name);
			UUID actual2 = UuidCreator.getNameBasedSha1(NAMESPACE_MOVIES, name.getBytes());
			UUID actual3 = UuidCreator.getNameBasedSha1(NAMESPACE_MOVIES.toString(), name);
			UUID actual4 = UuidCreator.getNameBasedSha1(NAMESPACE_MOVIES.toString(), name.getBytes());

			assertEquals(expected, actual1);
			assertEquals(expected, actual2);
			assertEquals(expected, actual3);
			assertEquals(expected, actual4);
		}

		// Test methods of the factory NameBasedSha1UuidCreator
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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

		// Test methods of the factory NameBasedSha1UuidCreator with fixed namespace
		for (int i = 0; i < LIST_MOVIES.length; i++) {

			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();

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
	public void testGetNameBasedSha1InParallel() throws InterruptedException {

		Thread[] threads = new Thread[THREAD_TOTAL];
		NameBasedTestThread.clearHashSet();

		// Instantiate and start many threads
		for (int i = 0; i < THREAD_TOTAL; i++) {
			NameBasedSha1UuidCreator creator = UuidCreator.getNameBasedSha1Creator();
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
}
