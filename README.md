
UUID Creator
======================================================

A Java library for generating and handling RFC-4122 UUIDs and non-standard UUIDs/GUIDs.

#### RFC-4122 UUIDs:

* __Random__: the pseudo-randomly generated version that uses a secure RNG;
* __Time-based:__ the time-based version;
* __Time-based with MAC:__ the time-based version with hardware address;
* __Time-based with fingerprint:__ the time-based version with a host fingerprint <sup>[10]</sup>;
* __Name-based MD5:__ the name-based version that uses MD5;
* __Name-based SHA-1:__ the name-based version that uses SHA-1;
* __DCE security:__ the time-based version that embeds local domains and identifiers.

#### Non-standard UUIDs/GUIDs:

* __Fast Random__: a pseudo-randomly generated version that uses a fast RNG;
* __Sequential:__ a modified time-based version that is also known as Ordered UUID <sup>[4]</sup>;
* __Sequential with MAC:__ a sequential version with hardware address;
* __Sequential with fingerprint:__ a sequential version with a host fingerprint <sup>[10]</sup>;
* __COMB Guid:__ a GUID with 80 bits for randomness and 48 bits for Unix milliseconds <sup>[7]</sup>;

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
  <version>1.4.5</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator) and [mvnrepository.com](https://mvnrepository.com/artifact/com.github.f4b6a3/uuid-creator).

### Sequential (version 0, non-standard)

The sequential UUID is a modified time-based UUID that is also known as Ordered UUID <sup>[4]</sup> <sup>[5]</sup>. The timestamp bits in this version are not rearranged as in the time-based version 1. It is an extension of the [RFC-4122](https://tools.ietf.org/html/rfc4122).

```java
// Sequential
UUID uuid = UuidCreator.getSequential();
```

```java
// Sequential with hardware address
UUID uuid = UuidCreator.getSequentialWithMac();
```

```java
// Sequential with host fingerprint (hash of system properties)
UUID uuid = UuidCreator.getSequentialWithFingerprint();
```

Examples of sequential UUID:

```text
1e98eff0-eddc-0470-a649-ad1cde652e10
1e98eff0-eddc-0471-a649-ad1cde652e10
1e98eff0-eddc-0472-a649-ad1cde652e10
1e98eff0-eddc-0473-a649-ad1cde652e10
1e98eff0-eddc-0474-a649-ad1cde652e10
1e98eff0-eddc-0475-a649-ad1cde652e10
1e98eff0-eddc-0476-a649-ad1cde652e10
1e98eff0-eddc-0477-a649-ad1cde652e10
1e98eff0-eddc-0478-a649-ad1cde652e10
1e98eff0-eddc-0479-a649-ad1cde652e10
1e98eff0-eddc-047a-a649-ad1cde652e10
1e98eff0-eddc-047b-a649-ad1cde652e10
1e98eff0-eddc-047c-a649-ad1cde652e10
1e98eff0-eddc-047d-a649-ad1cde652e10
1e98eff0-eddc-047e-a649-ad1cde652e10
1e98eff0-eddc-047f-a649-ad1cde652e10
                 ^ look

|-----------------|----|-----------|
     timestamp    clkseq  node id
```

### Time-based (version 1)

The Time-based UUID has a timestamp and may have a hardware address. 

The versions 0 and 1 use the same basic algorithm. So both are based on date and time. The only difference is that the version 1 changes the timestamp byte order in a special layout required by the RFC-4122.

```java
// Time-based
UUID uuid = UuidCreator.getTimeBased();
```

```java
// Time-based with hardware address
UUID uuid = UuidCreator.getTimeBasedWithMac();
```

```java
// Time-based with host fingerprint (hash of system properties)
UUID uuid = UuidCreator.getTimeBasedWithFingerprint();
```

Examples of time-based UUID:

```text
0edd7640-8eff-11e9-8649-972f32b091a1
0edd7641-8eff-11e9-8649-972f32b091a1
0edd7642-8eff-11e9-8649-972f32b091a1
0edd7643-8eff-11e9-8649-972f32b091a1
0edd7644-8eff-11e9-8649-972f32b091a1
0edd7645-8eff-11e9-8649-972f32b091a1
0edd7646-8eff-11e9-8649-972f32b091a1
0edd7647-8eff-11e9-8649-972f32b091a1
0edd7648-8eff-11e9-8649-972f32b091a1
0edd7649-8eff-11e9-8649-972f32b091a1
0edd764a-8eff-11e9-8649-972f32b091a1
0edd764b-8eff-11e9-8649-972f32b091a1
0edd764c-8eff-11e9-8649-972f32b091a1
0edd764d-8eff-11e9-8649-972f32b091a1
0edd764e-8eff-11e9-8649-972f32b091a1
0edd764f-8eff-11e9-8649-972f32b091a1
       ^ look

|-----------------|----|-----------|
     timestamp    clkseq  node id
```

### DCE security (version 2)

The DCE Security is a time-based UUID that also has a local domain and a local identifier.

```java
// DCE Security
byte localDomain = DceSecurityUuidCreator.LOCAL_DOMAIN_GROUP;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
```

```java
// DCE Security with hardware address
byte localDomain = DceSecurityUuidCreator.LOCAL_DOMAIN_GROUP;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurityWithMac(localDomain, localIdentifier);
```

```java
// DCE Security with fingerprint (hash of system properties)
byte localDomain = DceSecurityUuidCreator.LOCAL_DOMAIN_GROUP;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurityWithFingerprint(localDomain, localIdentifier);
```

### Name-based using MD5 (version 3)

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

```java
// Name-based using MD5
UUID namespace = UuidNamespace.NAMESPACE_URL.getValue();
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(namespace, name);
```

```java
// Name-based using MD5 without name space
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(name);
```

### Random (version 4)

The Random UUID is a simple random array of 16 bytes. The default random generator is `SecureRandom`, but any one can be used.

```java
// Random using the default SecureRandom generator
UUID uuid = UuidCreator.getRandom();
```

```java
// Random using the fast Xorshift128Plus generator
UUID uuid = UuidCreator.getFastRandom();
```

Examples of random-based UUID:

```text
38b37e02-d978-42cc-b39f-699d41ad6b13
4241488c-c17a-48c9-bf82-aa0afe675c2f
03e26434-323c-411c-bedf-34f8b99889e8
625b9fa3-21d1-4ddc-a5d7-97d277fbe268
b60a97a3-c1f9-48e9-8afe-d8505fd3fe58
071105f2-6c78-4fbb-a5c1-c8f48afd1a76
5bf70214-67e4-4f31-b3fb-cb8a8366d158
1dd86663-2263-443a-a49c-29d6d877b3f4
1172b75d-d55d-436c-9832-63b4c64e1813
279a12eb-d411-45b9-85cc-98ff692fb0e2
3c18b7e9-75a3-4f58-b608-9bdadff6ecd9
c136ac3c-b25e-414d-9d9e-0821b90bfe14
3f020533-fec9-4a94-9287-b8be5b2dda77
9d887b20-3394-4350-aba4-621ef6ea837a
9e71dd3d-c839-46ee-a249-d375f3460583
246bd1c9-cc6d-48b7-bd88-a5035d597842

|----------------------------------|
            randomness
```

### Name-based using SHA-1 (version 5)

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name.

```java
// Name-based using SHA-1
UUID namespace = UuidNamespace.NAMESPACE_URL.getValue();
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(namespace, name);
```

```java
// Name-based using SHA-1 without name space
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(name);
```

### COMB GUID (non-standard)

The COMB GUID is a modified random UUID that replaces the last 6 bytes with Unix epoch milliseconds.


```java
// COMB GUID
UUID uuid = UuidCreator.getCombGuid();
```

Examples of COMB GUID:

```text
19a298c8-3f03-0d00-57fe-0170df0cfcdd
01a8dee9-44dc-d928-419d-0170df0cfcdd
b7ba49dd-946c-b4a1-04dd-0170df0cfcdd
faa4a270-7223-e8d3-5ed4-0170df0cfcdd
cb019dcd-bc8e-1f84-f008-0170df0cfcdd
610cbbc9-d157-5882-752c-0170df0cfcdd
b77d6a98-3cf9-12fc-4a07-0170df0cfcdd
c26bf75c-f95d-bea1-7c4b-0170df0cfcdd
5655bf86-a00e-1b72-0081-0170df0cfcde < millisecond changed
459f1115-1ab1-3647-92f6-0170df0cfcde
ec2bad25-8e46-5b27-80d8-0170df0cfcde
95c1a524-574e-865e-f626-0170df0cfcde
1ec88587-8552-5eaa-72d5-0170df0cfcde
c72801dc-c3c5-d0b9-4335-0170df0cfcde
d790cb8d-1308-0672-b767-0170df0cfcde
6d885798-609b-9113-e879-0170df0cfcde
                                   ^ look

|----------------------|-----------|
       randomness        millisecs
```

System properties and environment variables
------------------------------------------------------

### Node identifier

The `nodeid` property is used by the `DefaultNodeIdentifierStrategy`. If this property or variable exists, its value is returned. Otherwise, a random value is returned. In the default strategy the `nodeid` property is a *preferred value* for the node identifier, that is, it overrides the random value generated by the node identifier strategy. It may be useful if you want to identify each single machine by yourself, instead of allowing the algorithm do it for you. It accepts 12 hexadecimal chars.

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

In this implementation, the timestamp has milliseconds accuracy, that is, it uses `System.currentTimeMillis()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#currentTimeMillis()) to get the current milliseconds. An internal _counter_ is used to _simulate_ the standard timestamp resolution of 10 million intervals per second.

You can create any strategy that implements the `TimestampStrategy` in the case that none of the strategies provided suffices.

##### Counter

The counter range is from 0 to 9,999. Every time a request is made at the same millisecond, the counter is increased by 1. Each counter value corresponds to a 100 nanosecond interval. The timestamp is calculated with this formula: MILLISECONDS * 10,000 + COUNTER. 

The timestamp counter is not instantiated with ZERO, but with a random number between 0 and 255. The 8 least significant bits of the counter are random. Every time a new counter value is required, this number is incremented by 1. When the limit of 9,999 is reached within the same interval, the counter is restarted to ZERO and an exception is raised. The first counter value for the next interval is the least 8 significant bits of the previous counter value.

The random trailing 8 bits are to avoid two generators to create UUIDs with the same timestamp bits. The clock sequence helps a lot avoiding duplicates, but these random bits reduce a little more the probability of duplicates.

For example, instead of having a timestamp like 137793212122770000, the UUIDs will have a something like 137793212122770042. Note that the trailing 4 digits 0000 are initiated with 0042. These 4 digits are filled with the counter. The next timestamp will not be 137793212122770001, but 137793212122770043. And it continues until the timestamp reaches 137793212122779999. The next time, the timestamp will be back to 137793212122770000 and an exception will be thrown to the programmer decide what to do with it. 

##### Overrun exception

The overrun exception is thrown when too many requests are made within the same millisecond interval. If the timestamp counter reaches the maximum of 10,000 (0 to 9,999), it is restarted to ZERO and an exception is thrown to the developer decide how to handle it.

The exception is raised to comply the RFC-4122, that requires:

```text
   If a system overruns the generator by requesting too many UUIDs
   within a single system time interval, the UUID service MUST either
   return an error, or stall the UUID generator until the system clock
   catches up.
```

The approach that stalls the generator until the system clock catches up seems to be a bottleneck. So the error approach was chosen. And an error in the Java world is an `Exception`.

The `UuidCreator` class already deals with the overrun exception in the methods that return UUID values. The exception is just _ignored_. For example, if the application calls `UuidCreator.getSequential()` more than 9,999 times within the same interval of 1 millisecond, the exception won't be raised. Instead the method will return the next UUID value as if nothing had happened. This choice was made because the clock sequence helps a lot to avoid duplicates. In my opinion it's more pragmatic to trust in the clock sequence, unless the application requests more than 163 million UUIDs per millisecond. The theoretical limit of UUIDs created per millisecond is 163,840,000, since the clock sequence range is 16,384 and the counter range is 10,000.

If you prefer to use the factory classes directly, for example, getting the factory `SequentialUuidCreator` by calling the method `UuidCreator.getSequentialCreator()`, you can choose the best way to treat the overrun exception. This project was conceived with _freedom of choice_ in mind.

#### Clock sequence

The clock sequence helps to avoid duplicates. It comes in when the system clock is backwards or when the node identifier changes. It also expands the amount of UUIDs that can be created at the same millisecond.

The first bits of the clock sequence are multiplexed with the variant number of the RFC-4122. Because of that, it has a range from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards. In other words, it acts like a counter that is incremented whenever the timestamp repeats or may be repeated.

In the `DefaultClockSequenceStrategy` each generator on the same JVM receives a unique clock sequence value. This local uniqueness is managed by the class `ClockSequenceController`. 

You can create any strategy that implements the `ClockSequenceStrategy` in the case that none of the strategies are good for you.

##### State file

The state file is a simple file that keeps three key-value pairs: previous timestamp, previous node identifier and previous clock sequence. This file is read by the `DefaultClockSequenceStrategy` to decide what is the next clock sequence to use. 

The directory in which the file `uuidcreator.state` lies is configured by the system property `uuidcreator.state.directory` or environment variable `UUIDCREATOR_STATE_DIRECTORY`. This is one example of path for the state file in Linux systems: "/var/tmp/uuidcreator.state".

Since the state file is disabled by default, it can be enabled by the system property `uuidcreator.state.enabled` or the environment variable `UUIDCREATOR_STATE_ENABLED`.

The key-value pairs of the state file has effect in these factories: `TimeBasedUuidCreator`, `SequentialUuidCreator` and `DceSecurityUuidCreator`. The the other factories ignore the state file.

Don't enable the state file if you want to use multiple instances of UUID generators in parallel. If you do it, all instances of UUID generators will use the same clock sequence. Just let the algorithm generate different clock sequences for each generator.


#### Node identifier

The node identifier is an IEEE 802 MAC address, usually the host machine address. But if no address is available or if the usage of MAC address is not desired, the standard allows the usage of a random generated value. In this library the default behavior is to use a random node identifier generated by a secure random generator.

It's also possible to use a _preferred node identifier_ by setting a system property or environment variable. The system property is `uuidcreator.nodeid` and the environment variable is `UUIDCREATOR_NODEID`. All UUIDs created will use the value present in the property or variable.

Other strategies are provided by this library if the default strategy is not desired: machine address strategy, host fingerprint strategy and random generator strategy. If you really need to identify the UUIDs with MAC address, just use the `HardwareAddressNodeIdentifierStrategy`. If you want to let this library to calculate a node identifier based on the host configuration, that is, a host fingerprint, use the `FingerprintNodeIdentifierStrategy`. Or if you feel comfortable with random node identifiers that are generated at every method invocation, use the `RandomNodeIdentifierStrategy`.

You can also create your own strategy that implements the `NodeIdentifierStrategy`.

##### Hardware address

The hardware address node identifier is the MAC address associated with the host name. If that MAC address can't be found, it uses the first MAC address that is up and running. If no MAC is found, a random number is used.

##### Fingerprint

The host fingerprint is a number that is calculated based on the host configurations. A big list of system properties is collected and then processed using the SHA-256 hash algorithm. The host fingerprint is extracted from the last six bytes of that system data hash. The system properties used to calculate the host fingerprint are: operating system (name, version, arch), java virtual machine (vendor, version, runtime, VM), network settings (IP, MAC, host name, domain name), system resources (CPU cores, memory), locale (language, charset) and timezone.

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

#### Time-based vs Sequential

RFC-4122 splits the timestamp bits into three parts: high, middle, and low. Then it reverses the order of these three parts to create a UUID. The sequential does not do this. The timestamp bits are kept in the original layout. See this example:


Instant:

```
 2019-09-22T17:52:55.033Z
```

Instant's timestamp in hexadecimal:

```
 01e9dd61ce117e90
```

Timestamp split into high, middle and low bits:

```
 _1e9 dd61 ce117e90
 _hhh mmmm llllllll

h: high bits
m: middle bits
l: low bit
_: ignored bits
```

Timestamp in time-based layout (RFC-4122):

```
 ce117e90-dd61-11e9-8080-39d701ba2e7c
 llllllll-mmmm-vhhh-ssss-nnnnnnnnnnnn

l: low bits
m: middle bits
h: high bits
v: version
s: clock sequence bits
n: node id bits
```

Timestamp in sequential layout (this library):
```
 1e9dd61c-e117-0e90-8080-39d701ba2e7c
 hhhmmmml-llll-vlll-ssss-nnnnnnnnnnnn

l: low bits
m: middle bits
h: high bits
v: version
s: clock sequence bits
n: node id bits
```


###  DCE Security

The DCE Security UUID inherits the same characteristics of the time-based UUID. The standard doesn't describe the algorithm for generating this kind of UUID. These instructions are in the document "DCE 1.1: Authentication and Security Services", available in the Internet. <sup>[6]</sup>

The difference is that it also contains information of local domain and local identifier. A half of the timestamp is replaced by a local identifier number. And half of the clock sequence is replaced by a local domain number.

### Name-based

There are two types of name-based UUIDs: MD5 and SHA-1. The MD5 is registered as version 3. And the SHA-1 is registered as version 5.

Two parameters are needed to generate a name-based UUID: a name space and a name.

The name space is a UUID object. But a string may be passed as argument. The factory converts it to UUID. The name space is optional.

The name in the standard is an array of bytes. But a string may also be passed as argument.

### Random

The random-based factory uses `java.security.SecureRandom`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/security/SecureRandom.html) to get 'cryptographic quality random' numbers as the standard requires.

This library also provides a factory that uses a fast random number generator. The default fast RNG used is  `Xorshift128Plus`[<sup>&#x2197;</sup>](https://en.wikipedia.org/wiki/Xorshift), that is used by the main web browsers. Other generators of the `Xorshift` family are also provided.

If the `SecureRandom` and the `Xorshift128Plus` are not desired, any other RNG can be passed as parameter to the factory, since it extends the class `java.util.Random`.

Fluent interface
------------------------------------------------------

The UUID factories are configurable via [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface) methods. This section lists a lot of examples for each UUID version.


#### Time-based

All the examples in this subsection are also valid for [Sequential](#sequential-version-0-non-standard) and [DCE Security](#dce-security-version-2) UUIDs.

##### Timestamp

```java

// with fixed instant (now)
Instant instant = Instant.now();
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withInstant(instant)
    .create();

// with fixed timestamp (now as UUID timestamp)
Instant instant = Instant.now();
long timestamp = TimestampUtil.toTimestamp(instant);
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestamp(timestamp)
    .create();

// with fixed Unix epoch millisecond (now as Unix milliseconds)
long milliseconds = System.currentTimeMillis();
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withUnixMilliseconds(milliseconds);
    .create();

```

##### Timestamp strategy

```java

// with default timestamp strategy (System.currentTimeMillis() + counter)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(new DefaultTimestampStrategy())
    .create();

// with nanoseconds timestamp strategy (Instant.getNano())
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(new NanosecondTimestampStrategy())
    .create();

```

##### Node identifier

```java

// with fixed node identifier (0x111111111111L)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifier(0x111111111111L)
    .create();

// with hardware address (first MAC found)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withHardwareAddressNodeIdentifier()
    .create();

// with host fingerprint (SHA-256 hash of OS + JVM + Network + Resources + locale + timezone)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withFingerprintNodeIdentifier()
    .create();

// with random node identifier for each UUID
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withRandomNodeIdentifier()
    .create();

// with random node identifier for each UUID using a custom random generator
Random random = new Xoroshiro128PlusRandom();
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withRandomNodeIdentifier(random)
    .create();

```

##### Node identifier strategy

```java

// with default node identifier strategy (secure random)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new DefaultNodeIdentifierStrategy())
    .create();

// with random node identifier strategy (random number generated once)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new RandomNodeIdentifierStrategy())
    .create();

// with hardware address node identifier strategy (first MAC found)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(new HardwareAddressNodeIdentifierStrategy())
    .create();

```

##### Clock sequence

```java

// with fixed clock sequence (0x8888)
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withClockSequence(0x8888)
    .create();

```

##### Clock sequence strategy

```java

// with random clock sequence for each UUID
UUID uuid = UuidCreator.getTimeBasedCreator()
    .withClockSequenceStrategy(new RandomClockSequenceStrategy())
    .create();

```

#### Name-based

All the examples in this subsection are also valid for SHA-1 UUIDs.

```java

// with fixed and custom namespace as string (USERS)
String namespace = "USERS";
String name = "Paul Smith";
UUID uuid = UuidCreator.getNameBasedMd5Creator()
    .withNamespace(namespace)
    .create(name);

// with fixed and standard namespace as UUID (the standard URL namespace)
UUID namespace = UuidNamespace.NAMESPACE_URL.getValue();
String name = "www.github.com";
UUID uuid = UuidCreator.getNameBasedMd5Creator()
    .withNamespace(namespace)
    .create(name);

```

#### Random

```java

// with java random generator (java.util.Random)
UUID uuid = UuidCreator.getRandomCreator()
    .withRandomGenerator(new Random())
    .create();

// with fast random generator (Xorshift128Plus)
UUID uuid = UuidCreator.getRandomCreator()
    .withFastRandomGenerator()
    .create();

// with fast random generator (Xorshift128Plus with salt)
int salt = (int) FingerprintUtil.getFingerprint();
Random random = new Xorshift128PlusRandom(salt);
UUID uuid = UuidCreator.getRandomCreator()
    .withRandomGenerator(random)
    .create();

```

#### COMB GUID

```java

// with java random generator (java.util.Random)
UUID uuid = UuidCreator.getCombCreator()
    .withRandomGenerator(new Random())
    .create();

// with fast random generator (Xorshift128Plus)
UUID uuid = UuidCreator.getCombCreator()
    .withFastRandomGenerator()
    .create();

// with fast random generator (Xorshift128Plus with salt)
int salt = (int) FingerprintUtil.getFingerprint();
Random random = new Xorshift128PlusRandom(salt);
UUID uuid = UuidCreator.getCombCreator()
    .withRandomGenerator(random)
    .create();

```

#### DCE Security

```java

// with fixed local domain (standard POSIX User ID)
UUID uuid = UuidCreator.getDceSecurityCreator()
    .withLocalDomain(DceSecurityUuidCreator.LOCAL_DOMAIN_PERSON)
    .create(1701);

```

Benchmark
------------------------------------------------------

This section shows benchmarks using JMH v1.23.

```text
-----------------------------------------------------------------------------------
THROUGHPUT (operations/millis)
-----------------------------------------------------------------------------------
Benchmark                                  Mode  Cnt      Score      Error   Units
-----------------------------------------------------------------------------------
Throughput.EAIO_TimeBasedWithMac          thrpt    5  19578,248 ±  504,220  ops/ms
Throughput.Java_Random                    thrpt    5   2055,962 ±   76,224  ops/ms
Throughput.UuidCreator_CombGuid           thrpt    5  18925,621 ±  484,156  ops/ms
Throughput.UuidCreator_FastRandom         thrpt    5  74038,367 ± 6004,510  ops/ms
Throughput.UuidCreator_NameBasedMd5       thrpt    5   3741,944 ±   70,026  ops/ms
Throughput.UuidCreator_NameBasedSha1      thrpt    5   2735,199 ±   13,666  ops/ms
Throughput.UuidCreator_Random             thrpt    5   2030,067 ±   39,618  ops/ms
Throughput.UuidCreator_Sequential         thrpt    5  20062,382 ±  297,605  ops/ms
Throughput.UuidCreator_SequentialWithMac  thrpt    5  18200,088 ±  637,603  ops/ms
Throughput.UuidCreator_TimeBased          thrpt    5  18152,895 ±  414,916  ops/ms
Throughput.UuidCreator_TimeBasedWithMac   thrpt    5  17286,595 ± 1369,154  ops/ms
-----------------------------------------------------------------------------------
Total time: 00:12:04
-----------------------------------------------------------------------------------
```

```text
-----------------------------------------------------------------------------------
AVERAGE TIME (nanos/operation)
-----------------------------------------------------------------------------------
Benchmark                                  Mode  Cnt    Score   Error  Units
-----------------------------------------------------------------------------------
AverageTime.EAIO_TimeBasedWithMac          avgt    5   51,294 ± 1,341  ns/op
AverageTime.Java_Random                    avgt    5  493,111 ± 8,411  ns/op
AverageTime.UuidCreator_CombGuid           avgt    5   52,823 ± 1,889  ns/op
AverageTime.UuidCreator_FastRandom         avgt    5   13,397 ± 0,985  ns/op
AverageTime.UuidCreator_NameBasedMd5       avgt    5  271,170 ± 3,716  ns/op
AverageTime.UuidCreator_NameBasedSha1      avgt    5  364,245 ± 5,948  ns/op
AverageTime.UuidCreator_Random             avgt    5  496,775 ± 4,361  ns/op
AverageTime.UuidCreator_Sequential         avgt    5   54,790 ± 3,298  ns/op
AverageTime.UuidCreator_SequentialWithMac  avgt    5   54,584 ± 1,097  ns/op
AverageTime.UuidCreator_TimeBased          avgt    5   56,329 ± 3,442  ns/op
AverageTime.UuidCreator_TimeBasedWithMac   avgt    5   57,638 ± 3,312  ns/op
-----------------------------------------------------------------------------------
Total time: 00:12:03
-----------------------------------------------------------------------------------
```
These external generators are used for comparison:

- com.eaio.uuid.UUID (for time-based UUID);
- java.util.UUID (for random UUID).

Benchmarks executed in a PC with Ubuntu 19.04, JVM 1.8.0_161, CPU Intel i5-3330 and 8GB RAM.

You can find the benchmark source code at [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark).

Related projects
------------------------------------------------------

* [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark)

* [uuid-creator-database](https://github.com/fabiolimace/uuid-creator-database)

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

[9]. ULID Specification - Universally Unique Lexicographically Sortable Identifier

[10]. Device fingerprint

[1]: https://en.wikipedia.org/wiki/Universally_unique_identifier
[2]: https://tools.ietf.org/html/rfc4122
[3]: https://www.percona.com/blog/2007/03/13/to-uuid-or-not-to-uuid
[4]: https://www.percona.com/blog/2014/12/19/store-uuid-optimized-way
[5]: https://bradleypeabody.github.io/uuidv6
[6]: http://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01
[7]: http://www.informit.com/articles/printerfriendly/25862
[8]: https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net
[9]: https://github.com/ulid/spec
[10]: https://en.wikipedia.org/wiki/Device_fingerprint

External links
------------------------------------------------------

* [A brief history of the UUID](https://segment.com/blog/a-brief-history-of-the-uuid)

* [Syntax and semantics of the DCE variant of Universal Unique Identifiers (The OpenGroup)](https://pubs.opengroup.org/onlinepubs/9629399/apdxa.htm)

* [How is a Time-based UUID / GUID made](https://www.famkruithof.net/guid-uuid-timebased.html)

* [Sequential UUID Generators](https://blog.2ndquadrant.com/sequential-uuid-generators/)

* [Sequential UUID Generators on SSD ](https://www.2ndquadrant.com/en/blog/sequential-uuid-generators-ssd/)

* [Be Careful with UUID or GUID as Primary Keys](https://news.ycombinator.com/item?id=14523523)

* [Ordered-uuid - npmjs package](https://www.npmjs.com/package/ordered-uuid)

* [To UUID or not to UUID](http://stereobooster.github.io/to-uuid-or-not-to-uuid)

* [Primary Keys: IDs versus GUIDs](https://blog.codinghorror.com/primary-keys-ids-versus-guids/)

* [GUIDs are globally unique, but substrings of GUIDs aren’t](https://blogs.msdn.microsoft.com/oldnewthing/20080627-00/?p=21823)

* [MySQL Performance When Using UUID For Primary Key](https://blog.programster.org/mysql-performance-when-using-uuid-for-primary-key)

* [What are the performance improvement of Sequential Guid over standard Guid?](https://stackoverflow.com/questions/170346/what-are-the-performance-improvement-of-sequential-guid-over-standard-guid)

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

* [How good is Java's UUID.randomUUID?](https://stackoverflow.com/questions/2513573/how-good-is-javas-uuid-randomuuid)

* [ITU-T - ASN.1 PROJECT - Universally Unique Identifiers (UUIDs)](https://www.itu.int/en/ITU-T/asn1/Pages/UUID/uuids.aspx)

* [GUID/UUID Performance - MariaDB](https://mariadb.com/kb/en/library/guiduuid-performance/)

* [Difference between CLOCK_REALTIME and CLOCK_MONOTONIC? (StackOverflow)](https://stackoverflow.com/questions/3523442/difference-between-clock-realtime-and-clock-monotonic)

* [Is System.currentTimeMillis() monotonically increasing? (StackOverflow)](https://stackoverflow.com/questions/2978598/will-system-currenttimemillis-always-return-a-value-previous-calls)

* [GUID structure (Microsoft)](https://docs.microsoft.com/en-us/windows/win32/api/guiddef/ns-guiddef-guid)

* [UUID structure (Microsoft)](https://docs.microsoft.com/en-us/windows/win32/rpc/rpcdce/ns-rpcdce-uuid)

* [UuidCreate function (Microsoft)](https://docs.microsoft.com/pt-br/windows/win32/api/rpcdce/nf-rpcdce-uuidcreate)

* [UuidCreateSequential function (Microsoft)](https://docs.microsoft.com/pt-br/windows/win32/api/rpcdce/nf-rpcdce-uuidcreatesequential)

* [GUID/UUID Performance Breakthrough](http://mysql.rjweb.org/doc.php/uuid)

* [Storing UUID Values in MySQL Tables](http://mysqlserverteam.com/storing-uuid-values-in-mysql-tables/)

* [Going deep on UUIDs and ULIDs](https://www.honeybadger.io/blog/uuids-and-ulids/)

