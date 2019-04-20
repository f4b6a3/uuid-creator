
UUID Creator
======================================================

UUID Creator generates standard <sup>[2]</sup> and non-standard UUIDs <sup>[4]</sup> <sup>[5]</sup> <sup>[7]</sup> <sup>[8]</sup>.

Standard UUIDs:

* __Random__: the pseudo-randomly generated version, using a secure RNG;
* __Time-based:__ the time-based version;
* __Time-based with MAC:__ the time-based version with hardware address;
* __Name-based MD5:__ the base-named version that uses MD5;
* __Name-based SHA1:__ the base-named version that uses SHA-1;
* __DCE Security:__ the time-based version that embeds local domains and identifiers.

Non-standard UUIDs:

* __Sequential:__ a modified time-based UUID that doesn't change the timestamp byte order;
* __Sequential with MAC:__ a sequential UUID with hardware address;
* __Fast Random__: a pseudo-randomly generated version, using a fast RNG;
* __MSSQL Guid:__ a modified time-based UUID that changes the timestamp byte order for MSSQL Server database;
* __COMB Guid:__ a modified random UUID that replaces the last 6 bytes with a Unix epoch milliseconds for MSSQL Server database.

How to Use
------------------------------------------------------

### Maven dependency

Add these lines to your `pom.xml`.

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>0.9.1</version>
</dependency>
```
See more options [here](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Sequential (version 0, non-standard)

The sequential UUID is a modified time-based UUID. The timestamp bits in this version are not rearranged as in the time-based version 1. The version number 0 (zero) was chosen to identify  UUIDs. It may be considered as an 'extension' of the RFC-4122.

```java
// Sequential
// Result: 1e879099-a413-0e81-9888-737f8d128eed
UUID uuid = UuidCreator.getSequential();
```

```java
// Sequential with hardware address
// Result: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
UUID uuid = UuidCreator.getSequentialWithMac();
```

### Time-based (version 1)

The Time-based UUID has a timestamp and may have a hardware address.

```java
// Time-based
// Result: 8caea7c7-7909-11e8-a919-4b43423cffc9
UUID uuid = UuidCreator.getTimeBased();
```

```java
// Time-based with hardware address
// Result: 15670d26-8c44-11e8-bda5-47f8xxxxxxxx
UUID uuid = UuidCreator.getTimeBasedWithMac();
```

### DCE Security (version 2)

The DCE Security is a Time-based UUID that also has local domain and local identifier.

```java
// DCE Security
// Local domain: POSIX Group ID domain (1)
// Local identifier: Group ID
// Result: 000006a5-f043-21e8-a900-a99b08980e52
byte localDomain = DceSecurityCreator.LOCAL_DOMAIN_GROUP;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
```

```java
// DCE Security with hardware address
// DCE Security
// Local domain: POSIX Group ID domain (1)
// Local identifier: Group ID
// Result: 000006a5-f043-21e8-a900-47f8xxxxxxxx
byte localDomain = DceSecurityCreator.LOCAL_DOMAIN_GROUP;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurityWithMac(localDomain, localIdentifier);
```

### Name-based using MD5 (version 3)

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

```java
// Name-based using MD5
// Namespace: URL
// Name: https://github.com/
// Result: 295df05a-2c43-337c-b6b8-4b84826e4a94
UUID namespace = UuidCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(namespace, name);
```

```java
// Name-based using MD5 without name space
// Namespace: NULL
// Name: https://github.com/
// Result: 008ec445-3ff3-3513-b438-93cba7aa31c8
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(name);
```

### Random (version 4)

The Random UUID is a simple random array of 16 bytes. The default random generator is SecureRandom, but any one can be used.

```java
// Random using the default SecureRandom generator
// Result: 1133483a-df21-47a6-b667-7482d6ceae39
UUID uuid = UuidCreator.getRandom();
```

```java
// Random using the fast Xorshift128Plus generator
// Result: d08b9aca-41c9-4e72-bc9d-95bda46c9730
UUID uuid = UuidCreator.getFastRandom();
```

### Name-based using SHA-1 (version 5)

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name.

```java
// Name-based using SHA-1
// Namespace: URL
// Name: https://github.com/
// Result: 39983165-606c-5d83-abfa-b97af8b1ae8d
UUID namespace = UuidCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(namespace, name);
```

```java
// Name-based using SHA-1 without name space
// Namespace: URL
// Name: https://github.com/
// Result: d7b3438d-97f3-55e6-92a5-66a731eea5ac
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(name);
```

### MSSQL GUID (non-standard)

The MSSQL GUID is a modified time-based UUID that changes the timestamp byte order for MSSQL Server database.

```java
// MSSQL GUID
// Result: b0effeb5-bdfd-e811-97b9-d568a81bd675
UUID uuid = UuidCreator.getMssqlGuid();
```

### COMB GUID (non-standard)

The COMB GUID is a modified random UUID that replaces the last 6 bytes with Unix epoch milliseconds for MSSQL Server database.

```java
// COMB GUID
// Result: 990676e9-5bb8-1a3d-b17c-0167a0739235
UUID uuid = UuidCreator.getCombGuid();
```

Fluent interface
------------------------------------------------------

The UUID factories are configurable via fluent interface methods. This section lists a series of examples for each version of UUID.


#### Time-based

All the examples in this subsection are also valid for Sequential and DCE Security UUIDs.

##### Fixed values


```java

// with fixed instant (now)
// Result: b9c7f4c0-fa7f-11e8-8cb2-af51de005196
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withInstant(Instant.now())
    .create();

// with fixed timestamp (now as timestamp)
// Result: b9c7f4c0-fa7f-11e8-898a-65c735295f15
long timestamp = TimestampUtil.toTimestamp(Instant.now());
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestamp(timestamp)
    .create();

// with fixed clock sequence (0x8888)
// Result: b9c81bd0-fa7f-11e8-8888-c3393cfe8550
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withClockSequence(0x8888)
    .create();

// with fixed node identifier (0x111111111111L)
// Result: b9c81bd0-fa7f-11e8-b717-111111111111
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifier(0x111111111111L)
    .create();

// with hardware address (first MAC found)
// Result: b9c81bd0-fa7f-11e8-ba0b-5254xxxxxxxx
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withHardwareAddress()
    .create();

```

##### Timestamp strategy

```java

// with default timestamp strategy (System.currentTimeMillis() + counter)
// Result: b9c81bd0-fa7f-11e8-93c4-1f0d5328f95f
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(new DefaultTimestampStrategy())
    .create();

// with nanoseconds timestamp strategy (Instant.getNano())
// Result: b9c842e0-fa7f-11e8-9168-0b57beda5626
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(new NanosecondTimestampStrategy())
    .create();

// with delta timestamp strategy (diff of subsequent System.nanoTime())
// Result: b9c86aa6-fa7f-11e8-a299-27ce0b9c1a91
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(new DeltaTimestampStrategy())
    .create();

```

##### Node identifier strategy

```java

// with default node identifier strategy (SHA-256 of hostname+MAC+IP+OS+JVM)
// Result: b9c869f0-fa7f-11e8-a04e-cbb1015db3ad
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new DefaultNodeIdentifierStrategy())
    .create();

// with random node identifier strategy (random number generated once)
// Result: b9c869f0-fa7f-11e8-8dba-0b2212ac1723
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new RandomNodeIdentifierStrategy())
    .create();

// with hardware address node identifier strategy (first MAC found)
// Result: b9c89100-fa7f-11e8-bf97-5254xxxxxxxx
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new MacNodeIdentifierStrategy())
    .create();

```

#### Name-based

All the examples in this subsection are also valid for SHA1 UUIDs.

```java

// with fixed and custom namespace as string (USERS)
// Result: a69660c2-481e-3114-b91b-7f0c7727cc72
UUID uuid = UuidCreator.getNameBasedMd5Creator()
    .withNamespace("USERS")
    .create("Paul");

// with fixed and standard namespace as UUID (standard DNS namespace)
// Result: 2c02fba1-0794-3c12-b62b-578ec5f03908
UUID uuid = UuidCreator.getNameBasedMd5Creator()
    .withNamespace(NameBasedMd5UuidCreator.NAMESPACE_DNS)
    .create("www.github.com");

```

#### Random

```java

// with java random generator (java.util.Random)
// Result: 8586fdd9-f654-4d28-a1a4-0d76c29d1455
UUID uuid = UuidCreator.getRandomCreator()
    .withRandomGenerator(new Random())
    .create();

// with fast random generator (Xorshift128Plus)
// Result: 2cc034e7-d51e-4eca-adb2-c0da06157723
UUID uuid = UuidCreator.getRandomCreator()
    .withFastRandomGenerator()
    .create();

```

#### DCE Security

```java

// with fixed local domain (standard POSIX User ID)
// Result: 000006a5-fa7f-21e8-8d00-5ba01f846124
UUID uuid = UuidCreator.getDceSecurityCreator()
    .withLocalDomain(DceSecurityUuidCreator.LOCAL_DOMAIN_PERSON)
    .create(1701);

```

System properties and environment variables
------------------------------------------------------

Some UUID parts can be set by system properties or environment variables. 

Currently only clock sequence and node identifier values can be set. These properties are used to replace the values generated by the default strategies for clock sequence and node identifier.

The algorithm tries to find a property in the system properties. If no property is found, it tries to find in the environment variables. If no variable is found, it proceeds to return the value generated by the strategy.

### Clock sequence

The `clockseq` property is used by `DefaultClockSequenceStrategy`. If this property or variable exists, it's value is returned. If not, a random clock sequence is returned. 

* Using system property:

```bash
-Duuidcreator.clockseq="9abc"
```

* Using environment variable:

```bash
UUIDCREATOR_CLOCKSEQ="9abc"
```

### Node identifier

The `nodeid` property is used by `DefaultNodeIdentifierStrategy`. If this property or variable exists, it's value is returned. If not, a system information hash is returned.

* Using system property:

```bash
-Duuidcreator.nodeid="c0da06157723"
```

* Using environment variable:

```bash
UUIDCREATOR_NODEID="c0da06157723"
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

v: version number
m: variant number (sharing bits with clock-sequence)
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

The clock sequence is used to help avoid duplicates. The first bits of the clock sequence part are multiplexed with the variant number of the RFC-4122. Because of that, the clock sequence always starts with one of these hexadecimal chars: `8`, `9`, `a` or `b`. In this implementation, every instance of a time-based factory has it's own clock sequence started with a random value from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards.

There's no 'non-volatile storage' in this implementation. That's why the clock sequence is always initialized to a random number.

#### Node identifier

The node identifier part consists of an IEEE 802 MAC address, usually the host address. But the standard allows the usage of a random generated value or a system information hash if no address is available, or if its use is not desired. In this implementation, the default behavior is to use a system information hash (SHA-256). The system information used are: host name, IP, MAC, Operating System and Java Virtual Machine.

But this implementation provides other strategies: random generator strategy and machine address strategy.

###  Sequential

The sequential UUID inherits the same characteristics of the time-based UUID. The only difference is that the timestamp bits are not rearranged as the standard requires. <sup>[4]</sup> <sup>[5]</sup>

```
Sequential timestamp arrangement

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
---------------------------------------------------------------------------------
Benchmark                                       Mode  Cnt   Score   Error  Units
---------------------------------------------------------------------------------
BenchmarkRunner.EAIO_TimeBasedWithMac             ss  100   7,233 ± 0,599  ms/op
BenchmarkRunner.JUG_NameBased                     ss  100  39,290 ± 3,412  ms/op
BenchmarkRunner.JUG_Random                        ss  100  54,861 ± 4,903  ms/op
BenchmarkRunner.JUG_TimeBased                     ss  100   7,802 ± 0,947  ms/op
BenchmarkRunner.JUG_TimeBasedWithMAC              ss  100   7,724 ± 0,890  ms/op
BenchmarkRunner.Java_NameBased                    ss  100  51,565 ± 9,172  ms/op
BenchmarkRunner.Java_Random                       ss  100  54,664 ± 4,848  ms/op
BenchmarkRunner.UuidCreator_CombGuid              ss  100  47,247 ± 5,193  ms/op
BenchmarkRunner.UuidCreator_DceSecurity           ss  100   7,890 ± 1,214  ms/op
BenchmarkRunner.UuidCreator_DceSecurityWithMac    ss  100   7,992 ± 1,227  ms/op
BenchmarkRunner.UuidCreator_FastRandom            ss  100   3,346 ± 0,591  ms/op
BenchmarkRunner.UuidCreator_MssqlGuid             ss  100   8,047 ± 0,865  ms/op
BenchmarkRunner.UuidCreator_NameBasedMd5          ss  100  42,992 ± 4,813  ms/op
BenchmarkRunner.UuidCreator_NameBasedSha1         ss  100  52,856 ± 5,514  ms/op
BenchmarkRunner.UuidCreator_Random                ss  100  46,959 ± 2,392  ms/op
BenchmarkRunner.UuidCreator_Sequential            ss  100   7,249 ± 0,931  ms/op
BenchmarkRunner.UuidCreator_SequentialWithMac     ss  100   7,218 ± 0,967  ms/op
BenchmarkRunner.UuidCreator_TimeBased             ss  100   7,441 ± 0,995  ms/op
BenchmarkRunner.UuidCreator_TimeBasedWithMac      ss  100   7,434 ± 0,997  ms/op
---------------------------------------------------------------------------------
Total time: 00:01:49
---------------------------------------------------------------------------------
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

[7]. The Cost of GUIDs as Primary Keys (COMB Algorithm)

[8]. How to Generate Sequential GUIDs for SQL Server in .NET

[1]: https://en.wikipedia.org/wiki/Universally_unique_identifier
[2]: https://tools.ietf.org/html/rfc4122
[3]: https://www.percona.com/blog/2007/03/13/to-uuid-or-not-to-uuid
[4]: https://www.percona.com/blog/2014/12/19/store-uuid-optimized-way
[5]: https://bradleypeabody.github.io/uuidv6
[6]: http://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01
[7]: http://www.informit.com/articles/printerfriendly/25862
[8]: https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net

External links
------------------------------------------------------

* [A brief history of the UUID](https://segment.com/blog/a-brief-history-of-the-uuid)

* [How is a Time-based UUID / GUID made](https://www.famkruithof.net/guid-uuid-timebased.html)

* [Sequential UUID Generators](https://blog.2ndquadrant.com/sequential-uuid-generators/)

* [Be Careful with UUID or GUID as Primary Keys](https://news.ycombinator.com/item?id=14523523)

* [Ordered-uuid - npmjs package](https://www.npmjs.com/package/ordered-uuid)

* [To UUID or not to UUID](http://stereobooster.github.io/to-uuid-or-not-to-uuid)

* [Primary Keys: IDs versus GUIDs](https://blog.codinghorror.com/primary-keys-ids-versus-guids/)

* [GUIDs are globally unique, but substrings of GUIDs aren’t](https://blogs.msdn.microsoft.com/oldnewthing/20080627-00/?p=21823)

* [MySQL Performance When Using UUID For Primary Key](https://blog.programster.org/mysql-performance-when-using-uuid-for-primary-key)

* [Auto increment keys vs. UUID](https://medium.com/@Mareks_082/auto-increment-keys-vs-uuid-a74d81f7476a)

* [GUIDs as fast primary keys under multiple databases](https://www.codeproject.com/Articles/388157/GUIDs-as-fast-primary-keys-under-multiple-database)

* [Do you really need a UUID/GUID?](https://rclayton.silvrback.com/do-you-really-need-a-uuid-guid)

* [Oracle Universal Unique Identifier](https://oracle-base.com/articles/9i/uuid-9i)

* [Oracle Reference 12c - SYS_GUID](https://docs.oracle.com/database/121/SQLRF/functions202.htm#SQLRF06120)

* [Why Auto Increment Is A Terrible Idea](https://www.clever-cloud.com/blog/engineering/2015/05/20/why-auto-increment-is-a-terrible-idea/)
