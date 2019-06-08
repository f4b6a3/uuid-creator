
UUID Creator
======================================================

UUID Creator is a library that generates standard <sup>[2]</sup> and non-standard UUIDs <sup>[4]</sup> <sup>[5]</sup> <sup>[7]</sup> <sup>[8]</sup>.

Standard UUIDs:

* __Random__: the pseudo-randomly generated version that uses a secure RNG;
* __Time-based:__ the time-based version;
* __Time-based with MAC:__ the time-based version with hardware address;
* __Name-based MD5:__ the base-named version that uses MD5;
* __Name-based SHA-1:__ the base-named version that uses SHA-1;
* __DCE Security:__ the time-based version that embeds local domains and identifiers.

Non-standard UUIDs:

* __Fast Random__: a pseudo-randomly generated version that uses a fast RNG;
* __Sequential:__ a modified time-based version that is also known as Ordered UUID;
* __Sequential with MAC:__ a sequential version with hardware address;
* __Name-based SHA-256:__ a base-named version that uses SHA-256;
* __MSSQL Guid:__ a modified time-based version that changes the timestamp byte order for MSSQL Server database;
* __COMB Guid:__ a modified random version that replaces the last 6 bytes with Unix epoch milliseconds for MSSQL Server database.

How to Use
------------------------------------------------------

### No time to read

This library is very simple, but this document is too long. So this section shows what most people are looking for.

If you just want a random UUID, which is the most common case, use this single line of code:

```java
UUID uuid = UuidCreator.getRandom();
```

Or if you want a UUID that is based on date and time, use this line of code:

```java
UUID uuid = UuidCreator.getTimeBased();
```
Or if you want a UUID that is also based on date and time, but looks like a sequential number, use this line:

```java
UUID uuid = UuidCreator.getSequential();
```

You can read the rest of this document if you have enough time.

### Maven dependency

Add these lines to your `pom.xml`.

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>1.2.6</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator) and [mvnrepository.com](https://mvnrepository.com/artifact/com.github.f4b6a3/uuid-creator).

### Sequential (version 0, non-standard)

The sequential UUID is a modified time-based UUID that is also known as Ordered UUID <sup>[4]</sup> <sup>[5]</sup>. The timestamp bits in this version are not rearranged as in the time-based version 1. It is an extension of the [RFC-4122](https://tools.ietf.org/html/rfc4122). 

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

Examples of sequential UUID:

```text
1e96c51a-960b-0970-a083-4546d757de20
1e96c51a-960b-0971-a083-4546d757de20
1e96c51a-960b-0972-a083-4546d757de20
1e96c51a-960b-0973-a083-4546d757de20
1e96c51a-960b-0974-a083-4546d757de20
1e96c51a-960b-0975-a083-4546d757de20
1e96c51a-960b-0976-a083-4546d757de20
1e96c51a-960b-0977-a083-4546d757de20
1e96c51a-960b-0978-a083-4546d757de20
1e96c51a-960b-0979-a083-4546d757de20
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

Examples of time-based UUID:

```text
a9601d3a-6c51-11e9-9711-4546d757de20
a9601d3b-6c51-11e9-9711-4546d757de20
a9601d3c-6c51-11e9-9711-4546d757de20
a9601d3d-6c51-11e9-9711-4546d757de20
a9601d3e-6c51-11e9-9711-4546d757de20
a9601d3f-6c51-11e9-9711-4546d757de20
a9601d40-6c51-11e9-9711-4546d757de20
a9601d41-6c51-11e9-9711-4546d757de20
a9601d42-6c51-11e9-9711-4546d757de20
a9601d43-6c51-11e9-9711-4546d757de20
```

### DCE security (version 2)

The DCE Security is a time-based UUID that also has a local domain and a local identifier.

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

The Random UUID is a simple random array of 16 bytes. The default random generator is `SecureRandom`, but any one can be used.

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

Examples of random-based UUID:

```text
ba1059e8-42ce-4a17-8c43-53c83353e23e
6658b049-44f2-4db6-862a-7e8a3d45a41d
4b6c87c3-dfba-40b3-8865-69a4432721e9
9b0872a9-2f84-4b4d-8480-c81741e6b730
bbe05770-3ed8-4064-a400-287ea1869585
dfff7f23-2ba4-4ad1-bcae-0e17b2c261de
d2b42ff7-2c79-4a8e-aa23-e0af1f1fb4e2
9c498979-39f8-4f8f-8ab7-312e51ff0c38
4b4a8b3b-9371-4a2a-b7de-55d070de8604
ca4de6d2-63b8-4a49-8ab6-bfccd32a27e8
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

Examples of MSSQL GUID:

```text
a02e61a9-516c-e911-9bd6-4546d757de20
a12e61a9-516c-e911-9bd6-4546d757de20
a22e61a9-516c-e911-9bd6-4546d757de20
a32e61a9-516c-e911-9bd6-4546d757de20
a42e61a9-516c-e911-9bd6-4546d757de20
a52e61a9-516c-e911-9bd6-4546d757de20
a62e61a9-516c-e911-9bd6-4546d757de20
a72e61a9-516c-e911-9bd6-4546d757de20
b05561a9-516c-e911-9bd6-4546d757de20
b15561a9-516c-e911-9bd6-4546d757de20
```

### COMB GUID (non-standard)

The COMB GUID is a modified random UUID that replaces the last 6 bytes with Unix epoch milliseconds for MSSQL Server database.

```java
// COMB GUID
// Result: 990676e9-5bb8-1a3d-b17c-0167a0739235
UUID uuid = UuidCreator.getCombGuid();
```

Examples of COMB GUID:

```text
cc6347bb-bd7a-5904-945e-016a75228e8e
967426cb-89c4-fcb5-945f-016a75228e8e
28e86a61-085b-1385-9460-016a75228e8e
ab831b05-8ff0-fc80-9461-016a75228e8e
e260f564-3ae0-8396-efa6-016a75228e8f
1cc5a7cc-4016-f39c-efa7-016a75228e8f
bc84674c-406c-e2de-efa8-016a75228e8f
109c9408-9ff0-34b3-efa9-016a75228e8f
a9c86b5f-4d97-c767-efaa-016a75228e8f
1777eb4e-20f7-09e7-efab-016a75228e8f
```

System properties and environment variables
------------------------------------------------------

### Node identifier

The `nodeid` property is used by the `DefaultNodeIdentifierStrategy`. If this property or variable exists, its value is returned. Otherwise, a system data hash is returned. In the default strategy the `nodeid` property is a *preferred value* for the node identifier, that is, it overrides the system data hash generated by the node identifier strategy. It may be useful if you want to identify each single machine by yourself, instead of allowing the algorithm do it for you. It accepts 12 hexadecimal chars.

* Using system property:

```bash
// append to VM arguments
-Duuidcreator.nodeid="c0da06157723"
```

* Using environment variable:

```bash
// append to /etc/environment or ~/.profile
export UUIDCREATOR_NODEID="c0da06157723"
```

### Node identifier salt

The `nodeid.salt` property is used by the `DefaultNodeIdentifierStrategy`. If this property or variable exists, its value is used to calculate the system data hash. It may be any string.

* Using system property:

```bash
// append to VM arguments
-Duuidcreator.nodeid.salt="this is my salt"
```

* Using environment variable:

```bash
// append to /etc/environment or ~/.profile
export UUIDCREATOR_NODEID_SALT="this is my salt"
```

### State enabled

The `state.enabled` property is used by the `AbstractTimeBasedUuidCreator`. When this property is `true`, it tells the factory to save the state file in the file system when the program exits. The state file is used in the next time the library is loaded into memory. The state has three key-value pairs: `timestamp`, `nodeid` and `cloqseq`. These values are used by the `DefaultClockSequenceStrategy` to decide when to reset or increment the clock sequence. The state file is disabled by default. It accepts `true` or `false`.

Don't enable the state file if you want to use many instances of UUID generators running at the same time.

* Using system property:

```bash
// append to VM arguments
-Duuidcreator.state.enabled="true"
```

* Using environment variable:

```bash
// append to /etc/environment or ~/.profile
export UUIDCREATOR_STATE_ENABLED="true"
```

### State directory

The `state.directory` property is used by the `AbstractTimeBasedUuidCreator`. If this property or variable exists, its value is returned. Otherwise, the default directory for temporary files is returned. It may be used to set a different directory other than the "/tmp" directory on Linux, which may not keep its files after reboot. It accepts a file system path without trailing slash.

* Using system property:

```bash
// append to VM arguments
-Duuidcreator.state.directory="/var/tmp"
```

* Using environment variable:

```bash
// append to /etc/environment or ~/.profile
export UUIDCREATOR_STATE_DIRECTORY="/var/tmp"
```


Implementation
------------------------------------------------------

### Format and representation

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
m: variant number (sharing bits with the clock-sequence)
```

#### Representation

The `java.util.UUID`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html) class represents a UUID with two `long` fields, called most significant bits (MSB) and least significant bits (LSB). The MSB has the version number. The LSB has the variant number.

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

The timestamp is a value that represents date and time. It has 4 subparts: low timestamp, middle timestamp, high timestamp and version number.

```
Standard timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---||3--|

1: timestamp low      *
2: timestamp mid     ***
3: timestamp high   *****
```

In the standard the bytes of the timestamp are rearranged so that the highest bits are put in the end of the array of bits and the lowest ones in the beginning. The standard _timestamp resolution_ is 1 second divided by 10,000,000. The timestamp is the amount of 100 nanoseconds intervals since 1582-10-15. Since the timestamp has 60 bits, the greatest date and time that can be represented is 5236-03-31T21:21:00.684Z.

In this implementation, the timestamp has milliseconds accuracy, that is, it uses `System.currentTimeMillis()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#currentTimeMillis()) to get the current milliseconds. An internal counter is used to _simulate_ the standard timestamp resolution of 10 million intervals per second. The counter range is from 0 to 9,999. Every time a request is made at the same millisecond, the counter is increased by 1. Each counter value corresponds to a 100 nanosecond interval. The timestamp is calculated with this formula: MILLISECONDS * 10,000 + COUNTER. The reason this strategy is used is that the JVM may not guarantee[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#nanoTime()) a resolution higher than milliseconds. 

Two alternate strategies are provided in the case that the default timestamp strategy is not desired: nanoseconds strategy and delta strategy. 

The nanoseconds strategy uses `Instant.getNano()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#getNano--). You can use the `NanosecondTimestampStrategy` if your machine provides high time resolution. My personal development PC doesn't.

The delta strategy uses `System.nanoTime()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#nanoTime()). The `DeltaTimestampStrategy` calculates the difference in nanoseconds between two calls to the `getTimestamp()` method. This difference is used to figure out the current timestamp in nanoseconds. As you can see, it is not precise, but it's an alternate option if you really need nanoseconds resolution.

You can create any strategy that implements the `TimestampStrategy` in the case that none of the strategies provided suffices.

#### Clock sequence

The clock sequence helps to avoid duplicates. It comes in when the system clock is backwards or when the node identifier changes. It also expands the amount of UUIDs that can be created at the same second. 

The first bits of the clock sequence are multiplexed with the variant number of the RFC-4122. Because of that, it has a range from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards. 

With the incrementing clock sequence, the _theoretical limit_ of UUIDs created per second rises to 163,840,000,000 at a single host machine, since the clock sequence range is 16,384 and the timestamp resolution is 10,000,000. So the theoretical fastest generation rate is 163 billion UUIDs per second per node identifier.

This implementation generates _well distributed clock sequences_ in the case that there are many instances of the `AbstractTimeBasedUuidCreator` running in parallel. This is done to avoid more than one instance using the same clock sequence. To assure that the clock sequence values won't be repeated and will be distant enough from the other values, it uses an algorithm created for this purpose: the `CyclicDistributor`. We could just use a random clock sequence every time a generator is instantiated, but there's a small risk of the same clock sequence being given to more than one generator. The cyclic distributor reduces this risk to ZERO with up to 16 thousand parallel generators. Besides, there's a risk of an incremented clock sequence conflicting with the clock sequence of another generator. The cyclic distributor hands out a value as distant as possible from the other values to minimize the risk related to incrementing.

You can create any strategy that implements the `ClockSequenceStrategy` in the case that none of the strategies are good for you.

##### Cyclic distributor

The cyclic distributor is just a simple algorithm that hands out values. It's like a sequence, but the first number is random, and the rest is calculated as points of a circle. I don't know if this algorithm already exists. I call it "cyclic distributor" because it hands out or _distributes_ values as if they were on the perimeter of a _circle_ and does it in iterations or _cycles_.

The class `CyclicDistributor` hands out numbers in a range of values, so that the first value is always random and the rest of values won't repeat. The range is treated as the perimeter of a circle. Each value is a point in this perimeter. The first point handed out is random. The next values are calculated in iterations or cycles. Each cycle has a 2^n number of values to hand out. All the values are at the same distance from the other values of the same iteration.

For example, say a range is 360, similar to the 360 degrees of a circle. The first number handed out is random, and for coincidence it is ZERO. The second value handed out is 180 degrees away from the first point. The third is at 270 degrees far from the first one. The fourth is at 90 degrees far. And so on...

This algorithm is very simple, but it's easier to understand it watching it running. There's an animation in the `doc` directory that shows the algorithm in action. Each point drawn in the circle of the animation is like a value being handed out. Each value is at the same distance of the others in the same iteration or cycle.

##### State file

The state file is a simple file that keeps three key-value pairs: previous timestamp, previous node identifier and previous clock sequence. This file is read by the `DefaultClockSequenceStrategy` to decide what is the next clock sequence to use. 

The directory in which the file `uuidcreator.state` lies is configured by the system property `uuidcreator.state.directory` or environment variable `UUIDCREATOR_STATE_DIRECTORY`. This is one example of path for the state file in Linux systems: "/var/tmp/uuidcreator.state".

Since the state file is disabled by default, it can be enabled by the system property `uuidcreator.state.enabled` or the environment variable `UUIDCREATOR_STATE_ENABLED`.

The key-value pairs of the state file has effect in these factories: `TimeBasedUuidCreator`, `SequentialUuidCreator`, `DceSecurityUuidCreator` and `MssqlGuidCreator`. The the other factories ignore the state file.

Don't enable the state file if you want to use multiple instances of UUID generators in parallel. If you do it, all instances of UUID generators will use the same clock sequence. Just let the algorithm generate different clock sequences for each generator in a well distributed way.


#### Node identifier

The node identifier is an IEEE 802 MAC address, usually the host machine address. But the standard allows the usage of a random generated value or a system data hash if no address is available, or if the usage of MAC address is not desired. 

In this implementation, the default behavior is to use a _system data hash_ (SHA-256). The data used are: operating system, java virtual machine, network and resources available. It's like a device identifier based on its hardware and software configurations. The specification recommends to accumulate _as many sources as possible_ into a buffer to generate the hash value. Note that if one of these data changes, the node identifier also changes. So _the resulting node identifier is as unique and mutable as the host machine settings_.

It's possible to use a `salt` to change the system data hash by setting the system property `uuidcreator.nodeid.salt`  or the environment variable `UUIDCREATOR_NODEID_SALT`. It accepts any string as salt value.

It's also possible to use a _preferred node identifier_ by setting a system property or environment variable. The system property is `uuidcreator.nodeid` and the environment variable is `UUIDCREATOR_NODEID`. All UUIDs created will use the value present in the property or variable.

Other strategies are provided by this implementation if the default strategy is not desired: random generator strategy and machine address strategy. If you really need to identify the UUIDs with MAC address, just use the `MacNodeIdentifierStrategy`. Or if you feel comfortable with random generated node identifiers, use the `RandomNodeIdentifierStrategy`.

You can also create your own strategy that implements the `NodeIdentifierStrategy`.

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

The DCE Security UUID inherits the same characteristics of the time-based UUID. The standard doesn't describe the algorithm for generating this kind of UUID. These instructions are in the document "DCE 1.1: Authentication and Security Services", available in the Internet. <sup>[6]</sup>

The difference is that it also contains information of local domain and local identifier. A half of the timestamp is replaced by a local identifier number. And half of the clock sequence is replaced by a local domain number.

### Name-based

There are two types of name-based UUIDs: MD5 and SHA-1. The MD5 is registered as version 3. And the SHA-1 is version 5.

Two parameters are needed to generate a name-based UUID: a name space and a name.

The name space is a UUID object. But a string may be passed as argument. The factory converts it to UUID. The name space is optional.

The name in the standard is an array of bytes. But a string may also be passed as argument.

### Random

The random-based factory uses `java.security.SecureRandom`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html) to get 'cryptographic quality random' numbers as the standard requires.

This implementation also provides a factory that uses a fast random number generator. The default fast RNG used is  `Xorshift128Plus`[<sup>&#x2197;</sup>](https://en.wikipedia.org/wiki/Xorshift), that is used by the main web browsers. Other generators of the `Xorshift` family are also provided.

If the `SecureRandom` and the `Xorshift128Plus` are not desired, any other RNG can be passed as parameter to the factory, since it extends the class `java.util.Random`.

Fluent interface
------------------------------------------------------

The UUID factories are configurable via [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) methods. This section lists a lot of examples for each UUID version.


#### Time-based

All the examples in this subsection are also valid for [Sequential](#sequential-version-0-non-standard) and [DCE Security](#dce-security-version-2) UUIDs.

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

// with default node identifier strategy (SHA-256 of OS+JVM+Network+Resources+Salt)
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

All the examples in this subsection are also valid for SHA-1 and SHA-256 UUIDs.

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

Benchmark
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH. This implementation is compared to other implementations.

```text
---------------------------------------------------------------------------------
Benchmark                                       Mode  Cnt   Score   Error  Units
---------------------------------------------------------------------------------
BenchmarkRunner.EaioTimeBasedWithMac             ss  100   6,946 ± 0,533  ms/op
BenchmarkRunner.JavaNameBased                    ss  100  52,486 ± 9,778  ms/op
BenchmarkRunner.JavaRandom                       ss  100  54,846 ± 4,672  ms/op
BenchmarkRunner.JugNameBased                     ss  100  39,635 ± 3,455  ms/op
BenchmarkRunner.JugRandom                        ss  100  55,042 ± 4,761  ms/op
BenchmarkRunner.JugTimeBased                     ss  100   7,815 ± 0,970  ms/op
BenchmarkRunner.JugTimeBasedWithMAC              ss  100   7,827 ± 0,951  ms/op
BenchmarkRunner.UuidCreatorCombGuid              ss  100  48,273 ± 4,948  ms/op
BenchmarkRunner.UuidCreatorDceSecurity           ss  100   8,101 ± 1,327  ms/op
BenchmarkRunner.UuidCreatorDceSecurityWithMac    ss  100   8,358 ± 1,333  ms/op
BenchmarkRunner.UuidCreatorFastRandom            ss  100   2,973 ± 0,540  ms/op
BenchmarkRunner.UuidCreatorMssqlGuid             ss  100   8,111 ± 0,967  ms/op
BenchmarkRunner.UuidCreatorNameBasedMd5          ss  100  42,855 ± 3,826  ms/op
BenchmarkRunner.UuidCreatorNameBasedSha1         ss  100  52,575 ± 4,795  ms/op
BenchmarkRunner.UuidCreatorNameBasedSha256       ss  100  71,507 ± 3,846  ms/op
BenchmarkRunner.UuidCreatorRandom                ss  100  45,711 ± 2,752  ms/op
BenchmarkRunner.UuidCreatorSequential            ss  100   7,406 ± 0,995  ms/op
BenchmarkRunner.UuidCreatorSequentialWithMac     ss  100   7,526 ± 1,056  ms/op
BenchmarkRunner.UuidCreatorTimeBased             ss  100   7,568 ± 1,026  ms/op
BenchmarkRunner.UuidCreatorTimeBasedWithMac      ss  100   7,591 ± 0,995  ms/op
---------------------------------------------------------------------------------
Total time: 00:02:02
---------------------------------------------------------------------------------
```

This benchmark was executed in a machine Intel i5-3330 with 8GB RAM.

You can find the benchmark source code at [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark).

Related projects
------------------------------------------------------

* [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark).

* [uuid-creator-database](https://github.com/fabiolimace/uuid-creator-database).

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

* [Sequential UUID based entity key generator - COMB GUID](https://sites.google.com/site/rikkus/sequential-uuid-based-entity-key-generator-comb-guid)

* [UUID Collisions](https://softwareengineering.stackexchange.com/questions/130261/uuid-collisions)

* [Has there ever been a UUID collision?](https://www.quora.com/Has-there-ever-been-a-UUID-collision)

* [The article contains a footnote about UUIDs as primary keys](https://news.ycombinator.com/item?id=17135430)

* [Software engineer — from monolith to cloud: Auto Increment to UUID](https://coder.today/tech/2017-10-04_software-engineerfrom-monolith-to-cloud-auto-increment-to-uuid/)

* [When are you truly forced to use UUID as part of the design?](https://stackoverflow.com/questions/703035/when-are-you-truly-forced-to-use-uuid-as-part-of-the-design/786541)

* [How unique is UUID? - StackOverflow](https://stackoverflow.com/questions/1155008/how-unique-is-uuid)

* [When does it make sense in a RDBMS to make your primary key a UUID rather than an int?](https://www.quora.com/When-does-it-make-sense-in-a-RDBMS-to-make-your-primary-key-a-UUID-rather-than-an-int)

* [Why aren't programmers comfortable with the uniqueness guarantee of UUID version 4?](https://www.quora.com/Why-arent-programmers-comfortable-with-the-uniqueness-guarantee-of-UUID-version-4)

* [Is there any difference between a GUID and a UUID?](https://stackoverflow.com/questions/246930/is-there-any-difference-between-a-guid-and-a-uuid)

* [UUID1 vs UUID4](https://www.sohamkamani.com/blog/2016/10/05/uuid1-vs-uuid4/)

* [Probability of GUID collisions with different versions](https://news.ycombinator.com/item?id=10924343)

* [Are UUIDs really unique?](https://towardsdatascience.com/are-uuids-really-unique-57eb80fc2a87)
