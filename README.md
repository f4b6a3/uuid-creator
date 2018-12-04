
UUID Generator
======================================================

Summary
------------------------------------------------------

UUID Generator can generate UUIDs (Universally Unique Identifiers), also known as GUIDs (Globally Unique Identifiers). It provides methods for RFC-4122 versions 1, 2, 3, 4, 5. It also provides methods for creating non-standard ordered UUIDs.

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

The ordered UUID is a different implementation of the standard time-based UUIDs.

How to Use
------------------------------------------------------

### Version 0: Ordered (extension)

The ordered UUID is a modified time-based UUID. The bytes of timestamp part are rearranged in the 'natural' order. The version number 0 (zero) was chosen to identify  UUIDs. It may be considered as an 'extension' of the RFC-4122. 

```java
UUID uuid = UUIDGenerator.getOrdered();
System.out.println(uuid.toString());
// Output: 1e879099-a413-0e81-9888-737f8d128eed
```

```java
// With hardware address
UUID uuid = UUIDGenerator.getOrderedWithMAC();
System.out.println(uuid.toString());
// Output: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
```

```java
// Using a fixed node identifier instead of the hardware address
UUID uuid = UUIDGenerator.getOrderedUUIDCreator().withMulticastNodeIdentifier(0x111111111111L).create();
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
UUID uuid = UUIDGenerator.getTimeBasedWithMAC();
System.out.println(uuid.toString());
// Output: 15670d26-8c44-11e8-bda5-47f8xxxxxxxx
```

```java
// Using a fixed node identifier instead of the hardware address
uuid = UUIDGenerator.getTimeBasedUUIDCreator().withMulticastNodeIdentifier(0x111111111111L).create();
System.out.println(uuid.toString());
// Output: fe682e80-8c46-11e8-98d5-111111111111
```

### Version 2: DCE Security

The DCE Security is a Time-based UUID that also embeds local domain and local identifier.

```java
byte localDomain = DCESecurityCreator.LOCAL_DOMAIN_GROUP; // POSIX Group ID domain (1)
int localIdentifier = 1701; // Group ID
UUID uuid = UUIDGenerator.getDCESecurity(localDomain, localIdentifier);
System.out.println(uuid.toString());
// Output: 000006a5-f043-21e8-a900-a99b08980e52
```

```java
// With hardware address
byte localDomain = DCESecurityCreator.LOCAL_DOMAIN_GROUP; // POSIX Group ID domain (1)
int localIdentifier = 1701; // Group ID
UUID uuid = UUIDGenerator.getDCESecurityWithMAC(localDomain, localIdentifier);
System.out.println(uuid.toString());
// Output: 000006a5-f043-21e8-a900-47f8xxxxxxxx
```

### Version 3: Name-based using MD5

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

```java
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5(namespace, name);
System.out.println(uuid.toString());
// Output: 295df05a-2c43-337c-b6b8-4b84826e4a94
```

```java
// Without name space.
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5(name);
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
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1(namespace, name);
System.out.println(uuid.toString());
// Output: 39983165-606c-5d83-abfa-b97af8b1ae8d
```

```java
// Without name space
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1(name);
System.out.println(uuid.toString());
// Output: d7b3438d-97f3-55e6-92a5-66a731eea5ac
```

Implementation
------------------------------------------------------

### Format and representation

A UUID is simply a 128-bit value that may be represented as:
* An array of 2 longs; or
* An array of 16 bytes; or
* An array of 32 chars (without dashes).

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
m: variant number (multiplexed)
```

The `java.util.UUID` represents a UUID with two `long` fields, called Most Significant Bits (MSB) and Least Significant Bits (LSB). The MSB contains the version number. The LSB contains the variant number.

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
m: variant number (multiplexed with clock-sequence)
```
The timestamp part has 4 subparts: timestamp low, timestamp mid, timestamp high and version number.

```
Standard timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---||3--|

1: timestamp low
2: timestamp mid
3: timestamp high *
```

In the standard the bytes of the timestamp are rearranged so that the highest bits are put in the end of the array of bits and the lowest in the beginning of the resulting array of bits.

The standard resolution of timestamps is a second divided by 10,000,000. The timestamp is the count of 100-nanos since 1582-10-15. In this implementation, the timestamp has milliseconds accuracy. It uses `System.currentTimeMillis()` to get the current milliseconds. An internal counter is used to simulate the standard resolution. The counter range is from 0 to 10,000. Every time a request is made at the same timestamp, the counter is increased by 1. Each incremented of the counter corresponds to a 100-nanosecond. Before returning, the counter value is added to the timestamp value. The reason why this strategy is used is that the JVM may not guarantee a resolution higher than milliseconds.

If the default timestamp strategy is not desired, other two strategies are provided: nanoseconds strategy and delta strategy. The nanoseconds strategy uses `Instant.getNano()`, that as said before may not have nanoseconds precision guarantee by the JVM. The delta strategy uses `System.nanoTime()`. Any strategy that implements `TimestampStrategy` interface may be used, if none of the strategies provided suffices.

The clock sequence exists to avoid UUID duplication by generating more than one UUID in the same timestamp. The first bits of the clock sequence part are multiplexed with the variant number of the RFC-4122. Because of that, the clock sequence always starts with one of this hexadecimal chars: `8`, `9`, `a` or `b`. In this implementation, every instance of a time-based factory has it's own clock sequence started with a random value from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards. If the the system requests more than 16383 UUIDs at the same timestamp, an exception is thrown to conform the standard.

There's no 'non-volatile storage' in this implementation. That's why the clock sequence is always started with a random number, as recommended by the standard.

The node identifier part consists of an IEEE 802 MAC address, usually the host address. But the standard allows the usage of random generated number if no address is available, or if its use is not desired. In this implementation, the default behavior is to use a random node identifier for each instance of a time-based factory. 

###  Ordered

The ordered UUID inherits the same characteristics of the time-based UUID. The only difference is that the timestamp bits are not rearranged as the standard requires.

```
Timestamp arrangement for ordered time-based UUID

 00000000-0000-v000-m000-000000000000
|1-------|2---||3--|

1: timestamp high *
2: timestamp mid
3: timestamp low
```

###  DCE Security

The DCE Security UUID inherits the same characteristics of the time-based UUID. The standard doesn't describe the algorithm for generating this kind of UUID. These instructions are in the document "DCE 1.1: Authentication and Security Services", available in the internet.

The difference is that it also contains information of local domain and local identifier. A half of the timestamp is replaced by a local identifier number. And half of the clock sequence is replaced by a local domain number.

### Name-based

There are two types of name-based UUIDs: MD5 and SHA-1. The MD5 is registered as version 3. And the SHA-1 is version 5.

Two parameters are needed to generate a name-based UUID: a namespace and a name. 

The namespace is a UUID object. But in this implementation, a string may be passed as argument. The factory internally converts it to a UUID. The namespace is optional.

The name in the standard is an array of bytes. But a string may also be passed as argument.

### Random

The random-based factory uses `java.security.SecureRandom` to get 'cryptographic quality random' numbers as the standard requires.

This implementation also provides a factory that uses a fast random number generator. The default fast RNG used is `Xorshift128Plus`, that is used by the main web browsers. Other generators of the `Xorshift` family are also provided.

If the `SecureRandom` and the `Xorshift128Plus` are not desired, any other RNG can be passed as parameter to the factory, since it extends the class `java.util.Random`.

Benchmark
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH. This implementation is compared to other implementations.

```text
-----------------------------------------------------------------------------------
Benchmark                                         Mode  Cnt   Score    Error  Units
-----------------------------------------------------------------------------------
BenchmarkRunner.EAIO_TimeBasedWithMAC               ss  100   7,260 ± 0,571  ms/op
BenchmarkRunner.JUG_NameBased                       ss  100  39,894 ± 3,442  ms/op
BenchmarkRunner.JUG_Random                          ss  100  54,840 ± 4,728  ms/op
BenchmarkRunner.JUG_TimeBased                       ss  100   8,235 ± 0,996  ms/op
BenchmarkRunner.JUG_TimeBasedWithMAC                ss  100   7,875 ± 0,904  ms/op
BenchmarkRunner.Java_NameBased                      ss  100  51,574 ± 9,393  ms/op
BenchmarkRunner.Java_Random                         ss  100  54,762 ± 4,922  ms/op
BenchmarkRunner.UUIDGenerator_DCESecurity           ss  100   7,919 ± 1,326  ms/op
BenchmarkRunner.UUIDGenerator_DCESecurityWithMAC    ss  100   7,963 ± 1,305  ms/op
BenchmarkRunner.UUIDGenerator_FastRandom            ss  100   3,576 ± 0,704  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedMD5          ss  100  41,836 ± 3,830  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedSHA1         ss  100  52,006 ± 4,921  ms/op
BenchmarkRunner.UUIDGenerator_Ordered               ss  100   7,130 ± 0,968  ms/op
BenchmarkRunner.UUIDGenerator_OrderedWithMAC        ss  100   7,363 ± 0,958  ms/op
BenchmarkRunner.UUIDGenerator_Random                ss  100  46,433 ± 2,535  ms/op
BenchmarkRunner.UUIDGenerator_TimeBased             ss  100   7,391 ± 0,980  ms/op
BenchmarkRunner.UUIDGenerator_TimeBasedWithMAC      ss  100   7,494 ± 1,107  ms/op
-----------------------------------------------------------------------------------
Total time: 00:01:36
-----------------------------------------------------------------------------------
```

These are the configurations used to run this benchmark:

```java
@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000 )
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
```

The method `getRandom()` uses the SecureRandom (java.security.SecureRandom) random generator.

The method `getFastRandom()` uses the [Xorshift128Plus](https://en.wikipedia.org/wiki/Xorshift) random generator, which is also used by many web browsers.

This benchmark was executed in a machine Intel i5-3330 with 8GB RAM.

