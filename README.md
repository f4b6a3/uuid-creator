


UUID Generator
======================================================

Summary
------------------------------------------------------

UUID Generator can generate UUIDs (Universally Unique Identifiers) <sup>[1]</sup>, also known as GUIDs (Globally Unique Identifiers). It provides methods for RFC-4122 <sup>[2]</sup> versions 1, 2, 3, 4, 5. It also provides methods for creating non-standard ordered UUIDs. <sup>[4]</sup> <sup>[5]</sup>

These types of UUIDs can be generated:

* __Random__: the pseudo-randomly generated version;
* __Fast Random__: the pseudo-randomly generated version, using a fast RNG;
* __Time-based:__ the time-based version;
* __Time-based with MAC:__ the time-based version with hardware address;
* __Ordered:__ a modified time-based version;
* __Ordered with MAC:__ a modified time-based version with hardware address;
* __Name-based MD5:__ a base-named version that uses MD5;
* __Name-based SHA1:__ a base-named version that uses SHA-1.
* __DCE Security:__ a modified time-based version that uses local domains and identifiers;

How to Use
------------------------------------------------------

### Version 0: Ordered (extension)

The ordered UUID is a modified time-based UUID. The timestamp bits in this version are not rearranged as in the time-based version 1. The version number 0 (zero) was chosen to identify  UUIDs. It may be considered as an 'extension' of the RFC-4122.

```java
UUID uuid = UUIDGenerator.getOrdered();
System.out.println(uuid.toString());
// Output: 1e879099-a413-0e81-9888-737f8d128eed
```

```java
// With hardware address
UUID uuid = UUIDGenerator.getOrderedWithMac();
System.out.println(uuid.toString());
// Output: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
```

```java
// Using a fixed node identifier instead of MAC address, for example, a device identifier
long nodeIdentifier = 0x111111111111L;
UUID uuid = UUIDGenerator.getOrderedCreator().withNodeIdentifier(nodeIdentifier).create();
System.out.println(uuid.toString());
// Output: 1e88c46f-e5df-0550-8e9d-111111111111
```

### Version 1: Time-based

The Time-based UUID embeds a timestamp and may embed a hardware address.

```java
UUID uuid = UUIDGenerator.getTimeBased();
System.out.println(uuid.toString());
// Output: 8caea7c7-7909-11e8-a919-4b43423cffc9
```

```java
// With hardware address
UUID uuid = UUIDGenerator.getTimeBasedWithMac();
System.out.println(uuid.toString());
// Output: 15670d26-8c44-11e8-bda5-47f8xxxxxxxx
```

```java
// Using a fixed node identifier instead of MAC address, for example, a device identifier
long nodeIdentifier = 0x111111111111L;
UUID uuid = UUIDGenerator.getTimeBasedCreator().withNodeIdentifier(nodeIdentifier).create();
System.out.println(uuid.toString());
// Output: fe682e80-8c46-11e8-98d5-111111111111
```

### Version 2: DCE Security

The DCE Security is a Time-based UUID that also embeds local domain and local identifier.

```java
byte localDomain = DceSecurityCreator.LOCAL_DOMAIN_GROUP; // POSIX Group ID domain (1)
int localIdentifier = 1701; // Group ID
UUID uuid = UUIDGenerator.getDceSecurity(localDomain, localIdentifier);
System.out.println(uuid.toString());
// Output: 000006a5-f043-21e8-a900-a99b08980e52
```

```java
// With hardware address
byte localDomain = DceSecurityCreator.LOCAL_DOMAIN_GROUP; // POSIX Group ID domain (1)
int localIdentifier = 1701; // Group ID
UUID uuid = UUIDGenerator.getDceSecurityWithMac(localDomain, localIdentifier);
System.out.println(uuid.toString());
// Output: 000006a5-f043-21e8-a900-47f8xxxxxxxx
```

```java
// Using a fixed node identifier instead of MAC address, for example, a device identifier
byte localDomain = DceSecurityCreator.LOCAL_DOMAIN_GROUP; // POSIX Group ID domain (1)
int localIdentifier = 1701; // Group ID
long nodeIdentifier = 0x111111111111L;
UUID uuid = UUIDGenerator.getDceSecurityCreator().withNodeIdentifier(nodeIdentifier).create();
System.out.println(uuid.toString());
// Output: 000006a5-f043-21e8-a900-111111111111
```

### Version 3: Name-based using MD5

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

```java
UUID namespace = UuidCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMd5(namespace, name);
System.out.println(uuid.toString());
// Output: 295df05a-2c43-337c-b6b8-4b84826e4a94
```

```java
// Without name space.
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMd5(name);
System.out.println(uuid.toString());
// Output: 008ec445-3ff3-3513-b438-93cba7aa31c8
```

### Version 4: Random

The Random UUID is a simple random array of 16 bytes. The default random generator is SecureRandom, but any one can be used.

```java
// With the default SecureRandom generator
UUID uuid = UUIDGenerator.getRandom();
System.out.println(uuid.toString());
// Output: 1133483a-df21-47a6-b667-7482d6ceae39
```

```java
// With the fast Xorshift128Plus generator
UUID uuid = UUIDGenerator.getFastRandom();
System.out.println(uuid.toString());
// Output: d08b9aca-41c9-4e72-bc9d-95bda46c9730
```

### Version 5: Name-based using SHA-1

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name.

```java
UUID namespace = UuidCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSha1(namespace, name);
System.out.println(uuid.toString());
// Output: 39983165-606c-5d83-abfa-b97af8b1ae8d
```

```java
// Without name space
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSha1(name);
System.out.println(uuid.toString());
// Output: d7b3438d-97f3-55e6-92a5-66a731eea5ac
```

Fluent interface
------------------------------------------------------

The UUID factories are configurable via fluent interface methods. This section lists a series of examples for each version of UUID.


#### Time-based

All the examples in this subsection are also valid for Ordered and DCE Security UUIDs.

##### Fixed values


```java

// with fixed instant (now)
UUID uuid = UuidGenerator.getTimeBasedCreator().withInstant(Instant.now()).create();
// Result: b9c7f4c0-fa7f-11e8-8cb2-af51de005196

// with fixed timestamp (now as timestamp)
UUID uuid = UuidGenerator.getTimeBasedCreator().withTimestamp(TimestampUtil.toTimestamp(Instant.now())).create();
// Result: b9c7f4c0-fa7f-11e8-898a-65c735295f15

// with fixed clock sequence (0x8888)
UUID uuid = UuidGenerator.getTimeBasedCreator().withClockSequence(0x8888).create();
// Result: b9c81bd0-fa7f-11e8-8888-c3393cfe8550

// with fixed node identifier (0x111111111111L)
UUID uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifier(0x111111111111L).create();
// Result: b9c81bd0-fa7f-11e8-b717-111111111111

// with hardware address (first MAC found)
UUID uuid = UuidGenerator.getTimeBasedCreator().withHardwareAddress().create();
// Result: b9c81bd0-fa7f-11e8-ba0b-5254xxxxxxxx

```

##### Timestamp strategy

```java

// with default timestamp strategy (System.currentTimeMillis() + counter)
UUID uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new DefaultTimestampStrategy()).create();
// Result: b9c81bd0-fa7f-11e8-93c4-1f0d5328f95f

// with nanoseconds timestamp strategy (Instant.getNano())
UUID uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new NanosecondTimestampStrategy()).create();
// Result: b9c842e0-fa7f-11e8-9168-0b57beda5626

// with delta timestamp strategy (diff of subsequent System.nanoTime())
UUID uuid = UuidGenerator.getTimeBasedCreator().withTimestampStrategy(new DeltaTimestampStrategy()).create();
// Result: b9c86aa6-fa7f-11e8-a299-27ce0b9c1a91

```

##### Node identifier strategy

```java

// with default node identifier strategy (random number generated once)
UUID uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new DefaultNodeIdentifierStrategy()).create();
// Result: b9c869f0-fa7f-11e8-a04e-cbb1015db3ad

// with random node identifier strategy (random number generated every time)
UUID uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new RandomNodeIdentifierStrategy()).create();
// Result: b9c869f0-fa7f-11e8-8dba-0b2212ac1723

// with hardware address node identifier strategy (first MAC found)
UUID uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new MacNodeIdentifierStrategy()).create();
// Result: b9c89100-fa7f-11e8-bf97-5254xxxxxxxx

// with system node identifier strategy (hash of hostname + MAC + IP + OS + JVM)
UUID uuid = UuidGenerator.getTimeBasedCreator().withNodeIdentifierStrategy(new SystemNodeIdentifierStrategy()).create();
// Result: b9c89100-fa7f-11e8-9573-678d16c4a97a

```

#### Name-based

All the examples in this subsection are also valid for SHA1 UUIDs.

```java

// with fixed namespace as string (USERS)
UUID uuid = UuidGenerator.getNameBasedMd5Creator().withNamespace("USERS").create("Paul");
// Result: a69660c2-481e-3114-b91b-7f0c7727cc72

// with fixed namespace as UUID (standard DNS namespace)
UUID uuid = UuidGenerator.getNameBasedMd5Creator().withNamespace(NameBasedMd5UuidCreator.NAMESPACE_DNS).create("www.github.com");
// Result: 2c02fba1-0794-3c12-b62b-578ec5f03908

```

#### Random

```java

// with java random generator (java.util.Random)
UUID uuid = UuidGenerator.getRandomCreator().withRandomGenerator(new Random()).create();
// Result: 8586fdd9-f654-4d28-a1a4-0d76c29d1455

// with fast random generator (Xorshift128Plus)
UUID uuid = UuidGenerator.getRandomCreator().withFastRandomGenerator().create();
// Result: 2cc034e7-d51e-4eca-adb2-c0da06157723

```

#### DCE Security

```java

// with fixed local domain (standard POSIX User ID)
UUID uuid = UuidGenerator.getDceSecurityCreator().withLocalDomain(DceSecurityUuidCreator.LOCAL_DOMAIN_PERSON).create(1701);
// Result: 000006a5-fa7f-21e8-8d00-5ba01f846124

```


Implementation
------------------------------------------------------

### Format and representation

A UUID is simply a 128-bit value that may be represented as:
* An array of 2 longs; or
* An array of 16 bytes; or
* An array of 32 chars (without dashes).

#### Format

The canonical format has 5 groups separated by dashes.

```
Canonical format

 00000000-0000-v000-m000-000000000000
|1-------|2---|3---|4---|5-----------|

1: 8 chars
2: 4 chars
3: 4 chars
4: 4 chars
5: 12 chars

v: version number
m: variant number (sharing bits with clock-sequence)
```

#### Representation

The `java.util.UUID`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html) class represents a UUID with two `long` fields, called Most Significant Bits (MSB) and Least Significant Bits (LSB). The MSB contains the version number. The LSB contains the variant number.

```
Representation in java.util.UUID

 00000000-0000-v000-m000-000000000000
|msb---------------|lsb--------------|

msb: Most Significant Bits
lsb: Least Significant Bits
```

###  Time-based

The Time-based UUID has three main parts: timestamp, clock-sequence and node identifier.

```
Time-based UUID structure

 00000000-0000-v000-m000-000000000000
|1-----------------|2---|3-----------|

1: timestamp
2: clock-sequence
3: node identifier

v: version number
m: variant number (sharing bits with clock-sequence)
```
#### Timestamp

The timestamp part has 4 subparts: timestamp low, timestamp mid, timestamp high and version number.

```
Standard timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---||3--|

1: timestamp low      *
2: timestamp mid     ***
3: timestamp high   *****
```

In the standard the bytes of the timestamp are rearranged so that the highest bits are put in the end of the array of bits and the lowest in the beginning of the resulting array of bits.

The standard resolution of timestamps is a second divided by 10,000,000. The timestamp is the count of 100-nanos since 1582-10-15. In this implementation, the timestamp has milliseconds accuracy. It uses `System.currentTimeMillis()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#currentTimeMillis()) to get the current milliseconds. An internal counter is used to simulate the standard resolution. The counter range is from 0 to 10,000. Every time a request is made at the same timestamp, the counter is increased by 1. Each counter value corresponds to a 100-nanosecond interval. Before returning, the counter value is added to the timestamp value. The reason why this strategy is used is that the JVM may not guarantee[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#nanoTime()) a resolution higher than milliseconds.

If the default timestamp strategy is not desired, other two strategies are provided: nanoseconds strategy and delta strategy. The nanoseconds strategy uses `Instant.getNano()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#getNano--). The delta strategy uses `System.nanoTime()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#nanoTime()). Any strategy that implements `TimestampStrategy` interface may be used, if none of the strategies provided suffices.

#### Clock sequence

The clock sequence is used to help avoid duplicates. The first bits of the clock sequence part are multiplexed with the variant number of the RFC-4122. Because of that, the clock sequence always starts with one of this hexadecimal chars: `8`, `9`, `a` or `b`. In this implementation, every instance of a time-based factory has it's own clock sequence started with a random value from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards. If the the system requests more than 16383 UUIDs at the same timestamp, an exception is thrown to conform the standard.

There's no 'non-volatile storage' in this implementation. That's why the clock sequence is always initialized to a random number, as recommended by the standard.

#### Node identifier

The node identifier part consists of an IEEE 802 MAC address, usually the host address. But the standard allows the usage of random generated number if no address is available, or if its use is not desired. In this implementation, the default behavior is to use a random node identifier for each instance of a time-based factory. 

But this implementation provides other strategies: alternative random strategy, machine address strategy and system information strategy.

###  Ordered

The ordered UUID inherits the same characteristics of the time-based UUID. The only difference is that the timestamp bits are not rearranged as the standard requires. <sup>[4]</sup> <sup>[5]</sup>

```
Ordered timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---||3--|

1: timestamp high   *****
2: timestamp mid     ***
3: timestamp low      *
```

###  DCE Security

The DCE Security UUID inherits the same characteristics of the time-based UUID. The standard doesn't describe the algorithm for generating this kind of UUID. These instructions are in the document "DCE 1.1: Authentication and Security Services", available in the internet. <sup>[6]</sup>

The difference is that it also contains information of local domain and local identifier. A half of the timestamp is replaced by a local identifier number. And half of the clock sequence is replaced by a local domain number.

### Name-based

There are two types of name-based UUIDs: MD5 and SHA-1. The MD5 is registered as version 3. And the SHA-1 is version 5.

Two parameters are needed to generate a name-based UUID: a namespace and a name. 

The namespace is a UUID object. But a string may be passed as argument. The factory converts it to UUID. The namespace is optional.

The name in the standard is an array of bytes. But a string may also be passed as argument.

### Random

The random-based factory uses `java.security.SecureRandom`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html) to get 'cryptographic quality random' numbers as the standard requires.

This implementation also provides a factory that uses a fast random number generator. The default fast RNG used is  `Xorshift128Plus`[<sup>&#x2197;</sup>](https://en.wikipedia.org/wiki/Xorshift), that is used by the main web browsers. Other generators of the `Xorshift` family are also provided.

If the `SecureRandom` and the `Xorshift128Plus` are not desired, any other RNG can be passed as parameter to the factory, since it extends the class `java.util.Random`.

Benchmark
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH. This implementation is compared to other implementations.

```text
-----------------------------------------------------------------------------------
Benchmark                                         Mode  Cnt   Score    Error  Units
-----------------------------------------------------------------------------------
BenchmarkRunner.EAIO_TimeBasedWithMac               ss  100   7,410 ± 0,619  ms/op
BenchmarkRunner.JUG_NameBased                       ss  100  39,787 ± 2,959  ms/op
BenchmarkRunner.JUG_Random                          ss  100  55,029 ± 4,540  ms/op
BenchmarkRunner.JUG_TimeBased                       ss  100   7,889 ± 1,010  ms/op
BenchmarkRunner.JUG_TimeBasedWithMAC                ss  100   7,789 ± 0,972  ms/op
BenchmarkRunner.Java_NameBased                      ss  100  51,255 ± 8,995  ms/op
BenchmarkRunner.Java_Random                         ss  100  54,613 ± 4,852  ms/op
BenchmarkRunner.UUIDGenerator_DceSecurity           ss  100   8,124 ± 1,254  ms/op
BenchmarkRunner.UUIDGenerator_DceSecurityWithMac    ss  100   8,358 ± 1,248  ms/op
BenchmarkRunner.UUIDGenerator_FastRandom            ss  100   3,410 ± 0,560  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedMd5          ss  100  41,841 ± 4,234  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedSha1         ss  100  50,936 ± 4,486  ms/op
BenchmarkRunner.UUIDGenerator_Ordered               ss  100   7,559 ± 1,008  ms/op
BenchmarkRunner.UUIDGenerator_OrderedWithMac        ss  100   7,539 ± 0,988  ms/op
BenchmarkRunner.UUIDGenerator_Random                ss  100  46,713 ± 2,853  ms/op
BenchmarkRunner.UUIDGenerator_TimeBased             ss  100   7,471 ± 1,007  ms/op
BenchmarkRunner.UUIDGenerator_TimeBasedWithMac      ss  100   7,730 ± 1,008  ms/op
-----------------------------------------------------------------------------------
Total time: 00:01:38
-----------------------------------------------------------------------------------
```

These are the configurations used to run this benchmark:

```java
@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
```

This benchmark was executed in a machine Intel i5-3330 with 8GB RAM.

References
------------------------------------------------------

[1]. Universally unique identifier. Wikipedia.

[2]. A Universally Unique IDentifier (UUID). RFC-4122.

[3]. To UUID or not to UUID?

[4]. Store UUID in an optimized way.

[5]. UUID "Version 6": the version RFC 4122 forgot.

[6]. DCE 1.1: Security-Version (Version 2) UUIDs. The Open Group.

[1]: https://en.wikipedia.org/wiki/Universally_unique_identifier
[2]: https://tools.ietf.org/html/rfc4122
[3]: https://www.percona.com/blog/2007/03/13/to-uuid-or-not-to-uuid
[4]: https://www.percona.com/blog/2014/12/19/store-uuid-optimized-way
[5]: https://bradleypeabody.github.io/uuidv6
[6]: http://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01


External links
------------------------------------------------------

* [A brief history of the UUID](https://segment.com/blog/a-brief-history-of-the-uuid)

* [How is a Time-based UUID / GUID made](https://www.famkruithof.net/guid-uuid-timebased.html)

* [The Cost of GUIDs as Primary Keys](http://www.informit.com/articles/printerfriendly/25862)

* [Sequential UUID Generators](https://blog.2ndquadrant.com/sequential-uuid-generators/)

* [Be Careful with UUID or GUID as Primary Keys](https://news.ycombinator.com/item?id=14523523)

* [How to Generate Sequential GUIDs for SQL Server in .NET](https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net/)

* [Ordered-uuid - npmjs package](https://www.npmjs.com/package/ordered-uuid)

* [To UUID or not to UUID](http://stereobooster.github.io/to-uuid-or-not-to-uuid)

* [Primary Keys: IDs versus GUIDs](https://blog.codinghorror.com/primary-keys-ids-versus-guids/)

* [GUIDs are globally unique, but substrings of GUIDs aren’t](https://blogs.msdn.microsoft.com/oldnewthing/20080627-00/?p=21823)

* [MySQL Performance When Using UUID For Primary Key](https://blog.programster.org/mysql-performance-when-using-uuid-for-primary-key)