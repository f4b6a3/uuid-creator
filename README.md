
UUID Generator
======================================================

Summary
------------------------------------------------------

UUID Generator can generate UUIDs (Universally Unique Identifiers), also known as GUIDs (Globally Unique Identifiers). It provides methods for RFC-4122 versions 1, 2, 3, 4, 5. It also provides methods for creating non-standard sequential UUIDs.

These types of UUIDs can be generated:

* __Random__: the pseudo-randomly generated version;
* __Fast Random__: the pseudo-randomly generated version, using a fast RNG;
* __Time-based:__ the time-based version;
* __Time-based with MAC:__ the time-based version with hardware address;
* __Sequential:__ a modified time-based version;
* __Sequential with MAC:__ a modified time-based version with hardware address;
* __Name-based MD5:__ a base-named version that uses MD5;
* __Name-based SHA1:__ a base-named version that uses SHA-1.
* __DCE Security:__ a modified time-based version that uses local domains and identifiers;

The sequential UUID is a different implementation of the standard time-based UUIDs.

How to Use
------------------------------------------------------

### Version 0: Sequential (extension)

The Sequential UUID is a modified time-based UUID. The bytes of timestamp part are arrenged in the 'natural' order. The version number 0 (zero) was chosen to identify Sequential UUIDs. It may be considered as an 'extension' of the RFC-4122.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getSequential();
System.out.println(uuid.toString());
// Output: 1e879099-a413-0e81-9888-737f8d128eed
```

**Example 2:**

```java
UUID uuid = UUIDGenerator.getSequentialWithMAC();
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

### Version 1: Time-based

The Time-based UUID embeds a timestamp and may embed a hardware address.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getTimeBased();
System.out.println(uuid.toString());
// Output: 8caea7c7-7909-11e8-a919-4b43423cffc9
```

**Example 2:**

```java
UUID uuid = UUIDGenerator.getTimeBasedWithMAC();
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
UUID uuid = UUIDGenerator.getDCESecurity(1, 1701);
System.out.println(uuid.toString());
// Output: 1e88c441-5711-0fa2-aaac-bf66xxxxxxxx
```

### Version 3: Name-based using MD5

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

**Example 1:**

```java
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5(namespace, name);
System.out.println(uuid.toString());
// Output: 295df05a-2c43-337c-b6b8-4b84826e4a94
```

**Example 2:**

```java
// Without name space.
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5(name);
System.out.println(uuid.toString());
// Output: 008ec445-3ff3-3513-b438-93cba7aa31c8
```

### Version 4: Random

The Random UUID is a simple random array of 16 bytes. The default random generator is SecureRandom, but any one can be used.

**Example 1:**

```java
UUID uuid = UUIDGenerator.getRandom();
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

### Version 5: Name-based using SHA-1

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name.

**Example 1:**

```java
UUID namespace = UUIDCreator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1(namespace, name);
System.out.println(uuid.toString());
// Output: 39983165-606c-5d83-abfa-b97af8b1ae8d
```

**Example 2:**

```java
// Without name space
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1(name);
System.out.println(uuid.toString());
// Output: d7b3438d-97f3-55e6-92a5-66a731eea5ac
```

Benchmark using JMH
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH. This implementation is compared to other implementations.

```text
Benchmark                                        Mode  Cnt   Score   Error  Units
BenchmarkRunner.EAIO_TimeBasedWithMAC              ss  100   7,331 ± 0,551  ms/op
BenchmarkRunner.JUG_NameBased                      ss  100  40,412 ± 4,297  ms/op
BenchmarkRunner.JUG_Random                         ss  100  54,952 ± 4,435  ms/op
BenchmarkRunner.JUG_TimeBasedWithMAC               ss  100   7,994 ± 1,030  ms/op
BenchmarkRunner.JUG_TimeBased                      ss  100   7,950 ± 0,975  ms/op
BenchmarkRunner.Java_Random                        ss  100  55,014 ± 4,848  ms/op
BenchmarkRunner.Java_NameBased                     ss  100  51,648 ± 9,532  ms/op
BenchmarkRunner.UUIDGenerator_DCESecurity          ss  100  14,947 ± 2,875  ms/op
BenchmarkRunner.UUIDGenerator_FastRandom           ss  100   3,441 ± 0,587  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedMD5         ss  100  41,536 ± 3,365  ms/op
BenchmarkRunner.UUIDGenerator_NameBasedSHA1        ss  100  51,539 ± 5,157  ms/op
BenchmarkRunner.UUIDGenerator_Random               ss  100  45,951 ± 2,979  ms/op
BenchmarkRunner.UUIDGenerator_Sequential           ss  100   7,613 ± 1,026  ms/op
BenchmarkRunner.UUIDGenerator_SequentialWithMAC    ss  100   7,480 ± 0,980  ms/op
BenchmarkRunner.UUIDGenerator_TimeBased            ss  100   7,524 ± 0,982  ms/op
BenchmarkRunner.UUIDGenerator_TimeBasedWithMAC     ss  100   7,705 ± 0,959  ms/op
Total time: 00:01:32
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

