
UUID Generator
======================================================

Summary
------------------------------------------------------

UUID Generator can generate UUIDs (Universally Unique Identifiers), also known as GUIDs (Globally Unique Identifiers). It provides methods for RFC-4122 versions 1, 3, 4, 5. It also provides methods for a sequential version 0.

These types of UUIDs can be generated:

* __Random__: the pseudo-randomly generated version;
* __Fast Random__: the pseudo-randomly generated version, using a fast RNG;
* __Time-based:__ the time-based version;
* __Time-based MAC:__ the time-based version with hardware address;
* __Sequential:__ a modified time-based version;
* __Sequential MAC:__ a modified time-based version with hardware address;
* __Name-based MD5:__ a base-named version that uses MD5;
* __Name-based SHA1:__ a base-named version that uses SHA-1.
* __DCE Security:__ a modified time-based version that uses local domains and identifiers;

The sequential UUID is a different implementation of the standard time-based UUIDs.

How to Use
------------------------------------------------------

### Version 0: Sequential UUID

The Sequential UUID is a modified time-based UUID. The bytes of timestamp part are arrenged in the 'natural' order. The version number 0 (zero) was chosen to identify Sequential UUIDs. It may be considered as an 'extension' of the RFC-4122.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getSequentialUUID();
System.out.println(uuid.toString());
// Output: 1e879099-a413-0e81-9888-737f8d128eed
```

**Example 2:**

```java
UUID uuid = UUIDGenerator.getSequentialWithHardwareAddressUUID();
System.out.println(uuid.toString());
// Output: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
```

**Example 3:**

```java
// Using a fixed node identifier: 0x111111111111L
UUID uuid = UUIDGenerator.getSequentialUUIDCreator().withNodeIdentifier(0x111111111111L).create();
System.out.println(uuid.toString());
// Output: 1e88c46f-e5df-0550-8e9d-111111111111
```

### Version 1: Time-based UUID

The Time-based UUID embeds a timestamp and may embed a hardware address.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getTimeBasedUUID();
System.out.println(uuid.toString());
// Output: 8caea7c7-7909-11e8-a919-4b43423cffc9
```

**Example 2:**

```java
UUID uuid = UUIDGenerator.getTimeBasedWithHardwareAddressUUID();
System.out.println(uuid.toString());
// Output: 15670d26-8c44-11e8-bda5-47f8xxxxxxxx
```

**Example 3:**

```java
// Using a fixed node identifier: 0x111111111111L
uuid = UUIDGenerator.getTimeBasedUUIDCreator().withNodeIdentifier(0x111111111111L).create();
System.out.println(uuid.toString());
// Output: fe682e80-8c46-11e8-98d5-111111111111
```

### Version 2: DCE Security

The DCE Security is a Time-based UUID that also embeds local domain and local identifier.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getDCESecurityUUID(1, 1701);
System.out.println(uuid.toString());
// Output: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
```

### Version 3: Name-based UUID hashed with MD5

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

**Example 1:**

```java
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5UUID(namespace, name);
System.out.println(uuid.toString());
// Output: 295df05a-2c43-337c-b6b8-4b84826e4a94
```

**Example 2:**

```java
// Without name space.
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5UUID(name);
System.out.println(uuid.toString());
// Output: 008ec445-3ff3-3513-b438-93cba7aa31c8
```

### Version 4: Random UUID

The Random UUID is a simple random array of 16 bytes. The default random generator is SecureRandom, but any one can be used.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getRandomUUID();
System.out.println(uuid.toString());
// Output: 1133483a-df21-47a6-b667-7482d6ceae39
```

**Example 2:**

```java
// With Xorshift fast random generator
UUID uuid = UUIDGenerator.getRandomUUIDCreator().withRandomGenerator(new XorshiftRandom()).create();
System.out.println(uuid.toString());
// Output: d08b9aca-41c9-4e72-bc9d-95bda46c9730
```

### Version 5: Name-based UUID hashed with SHA-1

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name.

**Example 1:**

```java
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1UUID(namespace, name);
System.out.println(uuid.toString());
// Output: 39983165-606c-5d83-abfa-b97af8b1ae8d
```

**Example 2:**

```java
// Without name space
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1UUID(name);
System.out.println(uuid.toString());
// Output: d7b3438d-97f3-55e6-92a5-66a731eea5ac
```

Differences between Time-based UUID and Sequential UUID
------------------------------------------------------

UUID Sequential is a modified implementation of the Time-based UUID. All octets dedicated to the timestamp in the Sequential UUID are in the natural order. In the default Time-based UUID the timestamp octets are arranged differently (RFC-4122, section 4.2).

The UUID has this following structure:

`
TTTTTTTT-TTTT-VTTT-RSS-MMMMMMMMMMMM
`

The dashes separate the UUID in parts called "fields". The first tree fields contain timestamp bytes. The forth field contains a clock sequence number generated to avoid duplications. And the last field contains bytes of the machine hardware, but it is generally desired to fill this field with random bytes instead of the actual MAC.

In Time-based UUID the bytes are arranged in this way:

* `TTTTTTTT`: this field contains the lowest bytes of the timestamp;
* `TTTT`: this field contains the middle bytes of the timestamp;
* `VTTT`: in this field, "V" is the version (always 1) and the "TTT" are the highest bytes of timestamp;
* `RSS`: in this field, "R" is the variant and "SSS" are a clock sequence;
* `MMMMMMMMMMMM`: this field contains the hardware address or node (MAC)

In Sequential UUID the bytes are arranged in this way:

* `TTTTTTTT`: this field contains the **highest** bytes of the timestamp;
* `TTTT`: this field contains the middle bytes of the timestamp;
* `VTTT`: in this field, "V" is the version (always 0) and the "TTT" are the **lowest** bytes of timestamp;
* `RSS`: in this field, "R" is the variant and "SSS" are a clock sequence;
* `MMMMMMMMMMMM`: this field contains the hardware address or node (MAC)

To understand the difference between Time-based UUID and Sequential UUID, look thise two practical examples of UUID generated at the same instant.

* Time-based UUID: 79592ca7-4d7f-11e8-b946-3bababbf5f8b
* Sequential UUID: 1e84d7f7-9592-0ca7-b947-3bababbf5f8b

Note that the byte order of the first three fields are different in both examples. But both have the same bytes of a single instant, that is 2018-05-01T20:37:32.687274310Z.

Now see the three fields that contain timestamp information separated from the other fields. The lowest bytes of the timestamp are highlighted (the "V" is for version).

* Time-based UUID: **79592ca7**4d7f11e8
* Sequential UUID: 1e84d7f**79592**0**ca7**

In short, that is the is the difference between both.

Benchmark using JMH
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH. My implementation is compared to Java, JUG and EAIO implementations.

```text
Benchmark                                  Mode  Cnt   Score   Error  Units
BenchmarkRunner.EAIO_TimeAndEthernetBased    ss  100   7,420 ± 0,607  ms/op
BenchmarkRunner.JUG_NameBased                ss  100  39,994 ± 3,404  ms/op
BenchmarkRunner.JUG_Random                   ss  100  55,310 ± 4,542  ms/op
BenchmarkRunner.JUG_TimeAndEthernetBased     ss  100   7,911 ± 0,971  ms/op
BenchmarkRunner.JUG_TimeBased                ss  100   7,965 ± 0,995  ms/op
BenchmarkRunner.Java_Random                  ss  100  56,029 ± 4,891  ms/op
BenchmarkRunner.Java_nameBased               ss  100  52,180 ± 9,047  ms/op
BenchmarkRunner.UUIDGenerator_DCESecurity    ss  100   9,727 ± 1,116  ms/op
BenchmarkRunner.UUIDGenerator_FastRandom     ss  100   3,333 ± 0,606  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedMD5   ss  100  47,711 ± 4,385  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedSHA1  ss  100  57,742 ± 5,205  ms/op
BenchmarkRunner.UUIDGenerator_Sequential     ss  100   7,719 ± 0,926  ms/op
BenchmarkRunner.UUIDGenerator_SequentialMAC  ss  100   7,880 ± 1,013  ms/op
BenchmarkRunner.UUIDGenerator_TimeBased      ss  100   7,799 ± 1,034  ms/op
BenchmarkRunner.UUIDGenerator_TimeBasedMAC   ss  100   7,583 ± 1,050  ms/op
BenchmarkRunner.UUIDGenerator_Random         ss  100  47,358 ± 2,550  ms/op
Run complete. Total time: 00:01:33
```

These are the configurations used to run this benchmark:

```java
@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000 )
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
```

The method getRandomUUID() uses SecureRandom (java.security.SecureRandom).

The method getFastRandomUUID() uses a [Xorshift](https://en.wikipedia.org/wiki/Xorshift) random generator.

The machine used to do this test is an Intel i5-3330 with 16GB RAM.

