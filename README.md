
UUID Creator
======================================================

A Java library for generating and handling RFC-4122 UUIDs.

#### RFC-4122 UUIDs:

* __Version 1__: Time-based;
* __Version 2__: DCE Security;
* __Version 3__: Name-based (MD5);
* __Version 4__: Random-based;
* __Version 5__: Name-based (SHA1);
* __Version 6__: Time-ordered (proposed).

#### Non-standard GUIDs:

* __Prefix COMB__: combination of the creation millisecond (prefix) with random bytes;
* __Suffix COMB__: combination of the creation millisecond (suffix) with random bytes;
* __Short Prefix COMB__: combination the creation minute (prefix) with random bytes;
* __Short Suffix COMB__: combination the creation minute (suffix) with random bytes.

How to Use
------------------------------------------------------

### No time to read

This library was designed to be _easy to use_. The only things you have to know are what a UUID is and what type you need.

If you just want a _random_ UUID, which is the most common case, use this single line of code:

```java
UUID uuid = UuidCreator.getRandomBased();
```

Or if you want a UUID that is _based on_ creation time, use this line of code:

```java
UUID uuid = UuidCreator.getTimeBased();
```

Or if you want a UUID that is _ordered by_ creation time, use this line:

```java
UUID uuid = UuidCreator.getTimeOrdered();
```

All UUID types can be generated from the [facade](https://en.wikipedia.org/wiki/Facade_pattern) `UuidCreator`. If you have a special requirement that is not covered by the facade, you can read the rest of this document or check the source code.

The generators are [thread-safe](https://en.wikipedia.org/wiki/Thread_safety).

### Maven dependency

Add these lines to your `pom.xml`:

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>3.4.1</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Version 1: Time-based

The Time-based UUID has a timestamp and a node identifier. These two information represent WHEN and WHERE the UUID was created.

The timestamp is the count of 100 nanosecond intervals since 1582-10-15, the beginning of Gregorian calendar.

The timestamp bytes in this version are __rearranged__ in a special layout, unlike the time-ordered UUID (version 6). This is the only difference between version 1 and version 6.

The node identifier can be a MAC address, a hash of system information, a specific number or a random number (default).

See the section [Node identifier](#node-identifier) to know how to use the environment variable `UUIDCREATOR_NODE` and the system property `uuidcreator.node`.

```java
// Time-based with a random node identifier (default)
UUID uuid = UuidCreator.getTimeBased();
```

```java
// Time-based with hardware address as node identifier
UUID uuid = UuidCreator.getTimeBasedWithMac();
```

```java
// Time-based with system data hash as node identifier
UUID uuid = UuidCreator.getTimeBasedWithHash();
```

```java
// Time-based with random node identifier that always changes
UUID uuid = UuidCreator.getTimeBasedWithRandom();
```

Sequence of time-based UUIDs:

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

#### Other usage examples

```java
// Time-based with a date and time chosen by you
Instant myInstant = Instant.parse("1987-01-23T01:23:45.123456789Z");
UUID uuid = UuidCreator.getTimeBased(myInstant, null, null);
```

```java
// Time-based with a clock sequence chosen by you
Integer myClockSeq = 0xAAAA; // Override the random clock sequence
UUID uuid = UuidCreator.getTimeBased(null, myClockSeq, null);
```

```java
// Time-based with a node identifier chosen by you
Long myNode = 0xAAAAAAAAAAAAL; // Override random node identifier
UUID uuid = UuidCreator.getTimeBased(null, null, myNode);
```

### Version 2: DCE Security

The DCE Security<sup>[6]</sup> is a time-based UUID that also has a local domain and a local identifier.

```java
// DCE Security
UuidLocalDomain localDomain = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
```

List of predefined local domains:

* `UuidLocalDomain.LOCAL_DOMAIN_PERSON`: Local identifier is member of a [user domain](https://en.wikipedia.org/wiki/User_identifier);
* `UuidLocalDomain.LOCAL_DOMAIN_GROUP`: Local identifier is member of a [group domain](https://en.wikipedia.org/wiki/Group_identifier);
* `UuidLocalDomain.LOCAL_DOMAIN_ORG`: Local identifier is member of an organization domain.

### Version 3: Name-based (MD5)

The version 3 UUID is generated by the MD5 hash algorithm.

The parameter `namespace` is optional.

The parameter `name` can be any string or any byte array.

According to the RFC-4122, "the concept of name and name space should be broadly construed, and not limited to textual names".

The SHA-1 hash algorithm is preferred over MD5.

```java
// Name-based MD5
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(name);
```

```java
// Name-based MD5 using a name space
UuidNamespace namespace = UuidNamespace.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(namespace, name);
```

List of predefined [name spaces](https://en.wikipedia.org/wiki/Namespace):

* `UuidNamespace.NAMESPACE_DNS`: Name string is a [fully-qualified domain name](https://en.wikipedia.org/wiki/Domain_Name_System);
* `UuidNamespace.NAMESPACE_URL`: Name string is a [URL](https://en.wikipedia.org/wiki/URL);
* `UuidNamespace.NAMESPACE_ISO_OID`: Name string is an [ISO Object ID](https://en.wikipedia.org/wiki/Object_identifier);
* `UuidNamespace.NAMESPACE_X500_DN`: Name string is an [X.500 Distinguished Name](https://en.wikipedia.org/wiki/X.500) (in DER or a text output format).

### Version 4: Random-based

The random-based UUID is a random array of 16 bytes.

```java
// Random-based using SecureRandom generator
UUID uuid = UuidCreator.getRandomBased();
```

Sequence of random-based UUIDs:

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

### Version 5: Name-based (SHA-1)

The version 5 UUID is generated by the SHA-1 hash algorithm.

The parameter `namespace` is optional.

The parameter `name` can be any string or any byte array.

According to the RFC-4122, "the concept of name and name space should be broadly construed, and not limited to textual names".

The SHA-1 hash algorithm is preferred over MD5.

```java
// Name-based SHA-1
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(name);
```

```java
// Name-based SHA-1 using a name space
UuidNamespace namespace = UuidNamespace.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(namespace, name);
```

List of predefined [name spaces](https://en.wikipedia.org/wiki/Namespace):

* `UuidNamespace.NAMESPACE_DNS`: Name string is a [fully-qualified domain name](https://en.wikipedia.org/wiki/Domain_Name_System);
* `UuidNamespace.NAMESPACE_URL`: Name string is a [URL](https://en.wikipedia.org/wiki/URL);
* `UuidNamespace.NAMESPACE_ISO_OID`: Name string is an [ISO Object ID](https://en.wikipedia.org/wiki/Object_identifier);
* `UuidNamespace.NAMESPACE_X500_DN`: Name string is an [X.500 Distinguished Name](https://en.wikipedia.org/wiki/X.500) (in DER or a text output format).

### Version 6: Time-ordered (proposed)

The Time-ordered<sup>[4]</sup> <sup>[5]</sup> UUID has a timestamp and a node identifier. These two information represent WHEN and WHERE the UUID was created.

The timestamp is the count of 100 nanosecond intervals since 1582-10-15, the beginning of Gregorian calendar.

The timestamp bytes are __kept__ in the original order, unlike the time-based UUID (version 1). This is the only difference between version 1 and version 6.

The node identifier can be a MAC address, a hash of system information, a specific number or a random number (default).

See the section [Node identifier](#node-identifier) to know how to use the environment variable `UUIDCREATOR_NODE` and the system property `uuidcreator.node`.

```java
// Time-ordered with a random node identifier (default)
UUID uuid = UuidCreator.getTimeOrdered();
```

```java
// Time-ordered with hardware address as node identifier
UUID uuid = UuidCreator.getTimeOrderedWithMac();
```

```java
// Time-ordered with system data hash as node identifier
UUID uuid = UuidCreator.getTimeOrderedWithHash();
```

```java
// Time-ordered with random node identifier that always changes
UUID uuid = UuidCreator.getTimeOrderedWithRandom();
```

Sequence of time-ordered UUIDs:

```text
1e98eff0-eddc-6470-a649-ad1cde652e10
1e98eff0-eddc-6471-a649-ad1cde652e10
1e98eff0-eddc-6472-a649-ad1cde652e10
1e98eff0-eddc-6473-a649-ad1cde652e10
1e98eff0-eddc-6474-a649-ad1cde652e10
1e98eff0-eddc-6475-a649-ad1cde652e10
1e98eff0-eddc-6476-a649-ad1cde652e10
1e98eff0-eddc-6477-a649-ad1cde652e10
1e98eff0-eddc-6478-a649-ad1cde652e10
1e98eff0-eddc-6479-a649-ad1cde652e10
1e98eff0-eddc-647a-a649-ad1cde652e10
1e98eff0-eddc-647b-a649-ad1cde652e10
1e98eff0-eddc-647c-a649-ad1cde652e10
1e98eff0-eddc-647d-a649-ad1cde652e10
1e98eff0-eddc-647e-a649-ad1cde652e10
1e98eff0-eddc-647f-a649-ad1cde652e10
                 ^ look

|-----------------|----|-----------|
     timestamp    clkseq  node id
```

#### Other usage examples

```java
// Time-ordered with a date and time chosen by you
Instant myInstant = Instant.parse("1987-01-23T01:23:45.123456789Z");
UUID uuid = UuidCreator.getTimeOrdered(myInstant, null, null);
```

```java
// Time-ordered with a clock sequence chosen by you
Integer myClockSeq = 0xAAAA; // Override the random clock sequence
UUID uuid = UuidCreator.getTimeOrdered(null, myClockSeq, null);
```

```java
// Time-ordered with a node identifier chosen by you
Long myNode = 0xAAAAAAAAAAAAL; // Override the random node identifier
UUID uuid = UuidCreator.getTimeOrdered(null, null, myNode);
```

### Prefix COMB (non-standard)

The Prefix COMB<sup>[7]</sup> is a modified random-based UUID that replaces the FIRST 6 bytes of the UUID with a prefix.

The PREFIX is the creation millisecond (Unix epoch).

```java
// Prefix COMB using SecureRandom generator
UUID uuid = UuidCreator.getPrefixComb();
```

Sequence of Prefix COMBs:

```text
01720b5c-bf10-423f-9b20-1f88a7e2763a
01720b5c-bf10-43bf-a639-9b511243507f
01720b5c-bf10-4b47-9925-f96f0e197ba5
01720b5c-bf10-49ba-84c0-7ace9ae546bb
01720b5c-bf10-4327-b62a-2a242dcc197f
01720b5c-bf10-47cc-b631-4f262f500172
01720b5c-bf10-42ca-b063-28d5329b8d90
01720b5c-bf10-4442-876b-2710215d41e1
01720b5c-bf11-4a08-a63f-4495659603b4 < millisecond changed
01720b5c-bf11-49a8-8339-8e3b429fc6fb
01720b5c-bf11-4879-81f9-97194a331e3f
01720b5c-bf11-497f-a599-c52a8c692107
01720b5c-bf11-403c-a1e1-4f112539f97a
01720b5c-bf11-4e97-a9e3-59215d3d5fb1
01720b5c-bf11-4135-bb7f-422ec3c33cca
01720b5c-bf11-4460-9a62-bcf16e35b418
01720b5c-bf11...
            ^ look

|------------|---------------------|
    prefix         randomness
```

### Suffix COMB (non-standard)

The Suffix COMB<sup>[7]</sup> is a modified random-based UUID that replaces the LAST 6 bytes of the UUID with a suffix.

The SUFFIX is the creation millisecond (Unix epoch).

This type of COMB is only useful for MS SQL Server because of its [peculiar sorting algorithm](https://docs.microsoft.com/pt-br/archive/blogs/sqlprogrammability/how-are-guids-compared-in-sql-server-2005).

```java
// Suffix COMB using SecureRandom generator
UUID uuid = UuidCreator.getSuffixComb();
```

Sequence of Suffix COMBs:

```text
0c7fb3f5-cf99-4dc4-942a-01720b5cbf0c
4fa8ccdc-75c5-43ae-bf2f-01720b5cbf0c
3fdc05c0-8787-4072-b710-01720b5cbf0c
e1eea5da-5afa-4273-80c3-01720b5cbf0c
d7df7ec2-3a1a-4c9b-9fbc-01720b5cbf0c
119a1f87-59d1-4f08-b7e9-01720b5cbf0c
d0bb8776-37fa-47d4-978f-01720b5cbf0c
867a1ec3-f8de-46ec-b3a4-01720b5cbf0c
1739a555-5a45-44fd-9331-01720b5cbf0d < millisecond changed
43d9c807-2286-4bd7-8263-01720b5cbf0d
643653e4-8ae2-43bf-be16-01720b5cbf0d
e1f40089-7cf0-42f1-a27a-01720b5cbf0d
f68b0dfd-fe74-42d3-a4c4-01720b5cbf0d
adb7f7eb-760f-427d-ba6e-01720b5cbf0d
4b69b874-f2b9-4b1a-ad31-01720b5cbf0d
612cfbc3-70c0-4301-ae95-01720b5cbf0d
                     ...01720b5cbf0d
                                   ^ look

|----------------------|-----------|
       randomness         suffix
```

### Short Prefix COMB (non-standard)

The Short Prefix COMB<sup>[10]</sup> is a modified random-based UUID that replaces the FIRST 2 bytes of the UUID with a prefix.

The PREFIX is the creation minute (Unix epoch). It wraps around every 45 days (2^16/60/24 = \~45).

```java
// Short Prefix COMB using SecureRandom generator
UUID uuid = UuidCreator.getShortPrefixComb();
```

Sequence of Short Prefix COMBs:

```text
2fe8ef26-5684-4192-aab6-fc6a0aaa191c
2fe808c2-bd77-4338-8954-e2152ae8a8df
2fe8c7c2-2c1e-4f7d-88fa-e3d115d7e4c9
2fe84e2f-b8ed-4a1f-99be-742e781067f7
2fe85dad-4e7f-4447-b035-23882d69027d
2fe810cb-fa4d-4d2c-9e4f-b122d0d19391
2fe8a8c1-b039-477b-8b63-483eb986434e
2fe839c9-b1b7-43c7-88c5-09fda1ef30e6
2fe8a971-e3ac-4f3b-858a-1aad577e8c36
2fe87c36-9e81-40c8-bff2-1bf9956c0d32
2fe86aca-f113-4ef4-8b69-1b5de35d0832
2fe8ec69-7acc-4cff-91c9-f658b331ee67
2fe88b94-993f-4176-9991-1f9e778a79a0
2fe8b041-d0de-4552-b6b5-449a8ee32134
2fe8da35-ce9d-4d4a-90e5-c2a4c89f18c7
2fe8...

|--|-------------------------------|
prefix         randomness
```

### Short Suffix COMB (non-standard)

The Short Suffix COMB GUID<sup>[10]</sup> is a modified random-based UUID that replaces 2 bytes near to the end of the UUID with a suffix.

The SUFFIX is the creation minute (Unix epoch). It wraps around every 45 days (2^16/60/24 = \~45).

This type of COMB is only useful for MS SQL Server because of its [peculiar sorting algorithm](https://docs.microsoft.com/pt-br/archive/blogs/sqlprogrammability/how-are-guids-compared-in-sql-server-2005).

```java
// Short Suffix COMB using SecureRandom generator
UUID uuid = UuidCreator.getShortSuffixComb();
```

Sequence of Short Suffix COMBs:

```text
0c06fac1-fe6a-4efa-ad45-2fe861065351
2d505d8f-10e6-42b3-9be3-2fe8064a65e9
d2eed78b-3277-42e4-ae58-2fe8e6f09523
5e471a1c-6cd8-4112-a9cb-2fe81873ba5f
5777ecca-471d-4926-8c71-2fe898083dc9
2c9602c6-d3e7-4cce-b687-2fe8681f205e
7cc02c02-c867-4bbb-a130-2fe87bd427cc
033d4881-f059-4171-bc83-2fe89e5a2bed
727f4de4-0c62-4132-b3ce-2fe824ff3d5b
e2df0d73-cc2b-455f-a9e4-2fe8bf4bf0f0
ad0a704a-0d03-48b5-a7eb-2fe8e01165d7
94d785b2-eb62-4eaa-88e7-2fe8fc5c3478
4f2a5300-938d-44d2-9b2a-2fe83a86787e
384df3b4-36b7-4154-961a-2fe87bbbe5f4
53ab5fd3-31ee-4cb9-8779-2fe8a887b3be
                     ...2fe8...

|-----------------------|--|-------|
       randomness      suffix
```

Implementation
------------------------------------------------------

### Canonical format

The canonical format is a hexadecimal string that contains 5 groups separated by dashes.

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

### Java implementation

The `java.util.UUID`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html) class implements a UUID with two `long` fields, called most significant bits (MSB) and least significant bits (LSB).

```
Implementation in java.util.UUID

 00000000-0000-v000-m000-000000000000
|msb---------------|lsb--------------|

msb: Most Significant Bits
lsb: Least Significant Bits

v: version number
m: variant number (sharing bits with clock-sequence)
```

###  Time-based structure

The Time-based UUID has three parts: timestamp, clock-sequence and node identifier.

```
Time-based UUID structure

 00000000-0000-v000-m000-000000000000
|1-----------------|2---|3-----------|

1: timestamp
2: clock-sequence
3: node identifier
```

#### Timestamp

The timestamp is a value that represents date and time. It has 3 subparts: low timestamp, middle timestamp, high timestamp.

```
Standard timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---|3---|

1: timestamp low      *
2: timestamp mid     ***
3: timestamp high   *****
```

In the version 1 UUID the timestamp bytes are rearranged so that the highest bits are put in the end of the array of bits and the lowest ones in the beginning. The standard _timestamp resolution_ is 1 second divided by 10,000,000.

The timestamp is the amount of 100 nanoseconds intervals since 1582-10-15. Since the timestamp has 60 bits (unsigned), the greatest date and time that can be represented is 5236-03-31T21:21:00.684Z (2^60/10_000_000/60s/60m/24h/365.25y + 1582 A.D. = \~5235).

In this implementation, the timestamp has milliseconds accuracy, that is, it uses `System.currentTimeMillis()`[<sup>&#x2197;</sup>](https://docs.oracle.com/javase/7/docs/api/java/lang/System.html#currentTimeMillis()) to get the current milliseconds. An internal _accumulator_ is used to _simulate_ the standard timestamp resolution of 10 million intervals per second.

You can create a strategy that implements the `TimestampStrategy` if you don't like the default strategy.

##### Accumulator

The accumulator range is from 0 to 9,999. It is instantiated with a random number between 0 and 255.

Every time a request is made within the same millisecond, the elapsed 100-nanosecond intervals between two calls is added to the accumulator.

The timestamp is calculated with this formula: MILLISECONDS * 10,000 + ACCUMULATOR.

##### Overrun

The RFC-4122 requires that:

```text
   If a system overruns the generator by requesting too many UUIDs
   within a single system time interval, the UUID service MUST either
   return an error, or stall the UUID generator until the system clock
   catches up.
```

If the accumulator reaches the maximum of 10,000 within a single millisecond, the generator waits for next millisecond.

You probably don't have to worry if your application doesn't reach the theoretical limit of **10 million UUIDs per second per node (10k/ms/node)**.

#### Clock sequence

The clock sequence helps to avoid duplicates. It comes in when the system clock is backwards or when the node identifier changes.

The first bits of the clock sequence are multiplexed with the variant number of the RFC-4122. Because of that, it has a range from 0 to 16383 (0x0000 to 0x3FFF). This value is increased by 1 if more than one request is made by the system at the same timestamp or if the timestamp is backwards. In other words, it is a counter that is incremented whenever the timestamp repeats or may be repeated.

The `DefaultClockSequenceStrategy` has a controller to ensure that each instance of this strategy receives a unique clock sequence value. This prevents more than one instance in the same JVM from sharing the same clock sequence at any time. This local uniqueness is managed by the class `ClockSequenceController`.

You can also implement a custom strategy that implements the `ClockSequenceStrategy` if you want to control it yourself.

#### Node identifier

In this library the node identifier is generated by a secure random generator by default. Alternatively you can use a IEEE 802 MAC address or a system data hash as node identifier.

The effective way to avoid collisions is to ensure that each generator has its own node identifier. As a suggestion, a device ID managed by your the App can be used, if you don't want a node identifier based on a random number, a MAC address or a system data hash.

You can also create your own strategy that implements the `NodeIdentifierStrategy`.

##### Hardware address

The hardware address node identifier is the MAC address associated with the hostname. If that MAC address can't be found, it is the first MAC address that is up and running. If no MAC is found, it is a random number.

##### System data hash

The system data hash is calculated from a list of system properties: hostname, MAC and IP. These information are collected and passed to a SHA-256 message digest. The node identifier is the first 6 bytes of the resulting hash.

##### System property and environment variable

It's possible to manage the node identifier for each machine by defining the system property `uuidcreator.node` or the environment variable `UUIDCREATOR_NODE`. The system property has priority over the environment variable. If no property or variable is defined, the node identifier is randomly chosen.

These options are accepted:

- The string "mac" to use the MAC address;
- The string "hash" to use the hash of hostname, MAC and IP;
- The string "random" to use a random number that always changes;
- The string representation of a specific number between 0 and 2^48-1.

The number formats are: decimal, hexadecimal, and octal.

* Defining a system property:

```bash
# Append one of these examples to VM arguments

# Use the MAC address as node identifier
-Duuidcreator.node="mac"

# Use the hash of system data as node identifier
-Duuidcreator.node="hash"

# Use a random value as node identifier that always changes
-Duuidcreator.node="random"

# Use a specific number as node identifier
-Duuidcreator.node="0xC0DA0615BB23"
```

* Defining an environment variable:

```bash
# Append one of these examples to /etc/environment or ~/.profile

# Use the MAC address as node identifier
export UUIDCREATOR_NODE="mac"

# Use the hash of system data as node identifier
export UUIDCREATOR_NODE="hash"

# Use a random value as node identifier that always changes
export UUIDCREATOR_NODE="random"

# Use a specific number as node identifier
export UUIDCREATOR_NODE="0xC0DA0615BB23"
```

These are more examples of how to define the environment variable based on hostname:

```bash
# Append one of these examples to /etc/environment or ~/.profile

# Use the first 12 chars of the hostname hash
export UUIDCREATOR_NODE=0x`hostname | sha256sum | cut -c-12`

# Use the first 12 chars of the hostname hash (with multicast bit required by RFC-4122)
export UUIDCREATOR_NODE=$(printf "0x%x" $(( 0x`hostname | sha256sum | cut -c-12` | 0x010000000000 )))
```

### Time-ordered

The Time-ordered UUID inherits the same characteristics of the time-based UUID. The only difference is that the timestamp bits are not rearranged as the Time-based UUID does. <sup>[4]</sup> <sup>[5]</sup>

```
Time-ordered timestamp arrangement

 00000000-0000-v000-m000-000000000000
|1-------|2---|3---|

1: timestamp high   *****
2: timestamp mid     ***
3: timestamp low      *
```

#### Time-based vs Time-ordered

The RFC-4122 splits the timestamp bits into three parts: high, middle, and low. Then it reverses the order of these three parts to create a time-based UUID. The reason for this layout is not documented in the RFC-4122.

In the time-ordered, the timestamp bits are kept in the original layout. See the comparison below.

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
 _aaa bbbb cccccccc

a: time high bits
b: time middle bits
c: time low bit
_: version
```

Timestamp in time-based layout (version 1):

```
 ce117e90-dd61-11e9-8080-39d701ba2e7c
 cccccccc-bbbb-_aaa-dddd-eeeeeeeeeeee

c: time low bits
b: time middle bits
a: time high bits
_: version
d: clock sequence bits
e: node id bits
```

Timestamp in time-ordered layout (version 6):

```
 1e9dd61c-e117-6e90-8080-39d701ba2e7c
 aaabbbbc-cccc-_ccc-dddd-eeeeeeeeeeee

a: time high bits
b: time middle bits
c: time low bits
_: version
d: clock sequence bits
e: node id bits
```

### Random-based

The random-based UUID is a random array of 16 bytes.

The random-based factory uses a thread local `java.security.SecureRandom` to get 'cryptographic quality random' bytes as the standard requires.

If the default `SecureRandom` is not desired, any instance of `java.util.Random` can be used.

You can also implement a custom `RandomStrategy` that uses any random generator you like, not only instances of `java.util.Random`.

All COMB creators inherit the same characteristics of the random-based creator.

### Name-based

There are two types of name-based UUIDs: version 3 (MD5) and version 5 (SHA-1).

Two arguments are needed to generate a name-based UUID: a name space and a name.

The name space is an optional `UUID` object.

The name argument may be a `String` or a `byte` array.

### DCE Security

The DCE Security<sup>[6]</sup> UUID inherits the same characteristics of the time-based UUID.

The difference is that it also contains information of local domain and local identifier. A half of the timestamp is replaced by a local identifier number. And half of the clock sequence is replaced by a local domain number.

Other usage examples
------------------------------------------------------

This section contains a lot of examples on how to setup the internal UUID creators for specific needs.

Most cases you don't have to configure the internal creators directly. All the UUID types can be generated using a single line of code, for example, `UuidCreator.getTimeBased()`.

But if you want, for example, a time-based UUID that has a more accurate timestamp, you can implement another `TimestampStrategy` and pass it to the `TimeBasedUuidCreator`.

All UUID creators are configurable via [method chaining](https://en.wikipedia.org/wiki/Method_chaining).

### Time-based

All the examples in this subsection are also valid for Time-ordered and DCE Security UUIDs.

##### Timestamp strategy

```java
// with timestamp provided by a custom strategy
TimestampStrategy customStrategy = new CustomTimestampStrategy();
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(customStrategy);
UUID uuid = timebased.create();
```

```java
// with timestamp provided by a method reference or lambda expression
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withTimestampStrategy(System::currentTimeMillis);
UUID uuid = timebased.create();
```

##### Node identifier

```java
// with hardware address (first MAC found)
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withMacNodeIdentifier();
UUID uuid = timebased.create();
```

```java
// with system data hash (SHA-256 hash of hostname, MAC and IP)
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withHashNodeIdentifier();
UUID uuid = timebased.create();
```

```java
// with fixed number (48 bits), for example, a device ID managed by your App
long number = 0x0000aabbccddeeffL;
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifier(number);
UUID uuid = timebased.create();
```

```java
// with fixed byte array (6 bytes), for example, a device ID managed by your App
byte[] bytes = {(byte) 0xaa, (byte) 0xbb, (byte) 0xcc, (byte) 0xdd, (byte) 0xee, (byte) 0xff};
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifier(bytes);
UUID uuid = timebased.create();
```

```java
// with host IP address
try {
	byte[] ip = InetAddress.getLocalHost().getAddress();
	TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
		.withNodeIdentifier(ip);
} catch (UnknownHostException | SocketException e) {
	// treat exception
}

UUID uuid = timebased.create();

```

##### Node identifier strategy

```java
// with node identifier provided by a custom strategy
// the custom strategy could read the device ID from a file and return it as node identifier
NodeIdentifierStrategy customStrategy = new CustomNodeIdentifierStrategy();
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(customStrategy);
UUID uuid = timebased.create();
```

```java
// with node identifier provided by a method reference or lambda expression
Random random = new Random();
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withNodeIdentifierStrategy(random::nextLong);
UUID uuid = timebased.create();
```

##### Clock sequence

```java
// with fixed number (0 to 16383)
int number = 2039;
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withClockSequence(number);
UUID uuid = timebased.create();
```

```java
// with fixed byte array (2 bytes)
byte[] bytes = {(byte) 0xaa, (byte) 0xbb};
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withClockSequence(bytes);
UUID uuid = timebased.create();
```

##### Clock sequence strategy

```java
// with clock sequence provided by a custom strategy
ClockSequenceStrategy customStrategy = new CustomClockSequenceStrategy();
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withClockSequenceStrategy(customStrategy);
UUID uuid = timebased.create();
```

```java
// with clock sequence provided by a lambda expression
Random random = new Random();
TimeBasedUuidCreator timebased = UuidCreator.getTimeBasedCreator()
    .withClockSequenceStrategy((timestamp) -> random.nextInt()); // ignoring the input
UUID uuid = timebased.create();
```

### Random-based

All the examples in this subsection are also valid for COMBs.

##### Random generator

```java
// with another random generator that is an instance of `java.util.Random`
Random random = new Random();
RandomBasedUuidCreator randombased = UuidCreator.getRandomBasedCreator()
    .withRandomGenerator(random);
UUID uuid = randombased.create();
```

##### Random generator strategy

```java
// with another random strategy that uses an instance of `java.util.Random`
// this example is equivalent to the previous one, but more verbose
RandomStrategy strategy = new OtherRandomStrategy(new Random());
RandomBasedUuidCreator randombased = UuidCreator.getRandomBasedCreator()
    .withRandomStragety(strategy);
UUID uuid = randombased.create();
```

```java
// with a CUSTOM random strategy that uses any random generator
RandomStrategy customStrategy = new CustomRandomStrategy();
RandomBasedUuidCreator randombased = UuidCreator.getRandomBasedCreator()
    .withRandomStrategy(customStrategy);
UUID uuid = randombased.create();
```

```java
// with random values provided by a method reference or lambda expression
import com.github.niceguy.random.AwesomeRandom;
AwesomeRandom awesomeRandom = new AwesomeRandom();
RandomBasedUuidCreator randombased = UuidCreator.getRandomBasedCreator()
		.withRandomStrategy(awesomeRandom::nextBytes);
UUID uuid = randombased.create();
```

### Name-based

All the examples in this subsection are also valid for SHA-1 UUIDs.

```java
// without name space
String name = "Paul Smith";
NameBasedMd5UuidCreator namebased = UuidCreator.getNameBasedMd5Creator();
UUID uuid = namebased.create(name);
```

```java
// with a predefined name space
UuidNamespace namespace = UuidNamespace.NAMESPACE_URL;
String name = "www.github.com";
NameBasedMd5UuidCreator namebased = UuidCreator.getNameBasedMd5Creator()
    .withNamespace(namespace);
UUID uuid = namebased.create(name);
```

```java
// with a CUSTOM name space
// In this example, the category "products/books" is transformed into a custom namespace
UUID customNamespace = UuidCreator.getNameBasedMd5("products/books");
String name = "War and Peace - Leo Tolstoy";
NameBasedMd5UuidCreator namebased = UuidCreator.getNameBasedMd5Creator()
    .withNamespace(customNamespace);
UUID uuid = namebased.create(name);
```

### DCE Security

```java
// with predefined local domain (POSIX User ID)
UuidLocalDomain localDomain = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
int localIdentifier = 1701;
DceSecurityUuidCreator dcesecurity = UuidCreator.getDceSecurityCreator()
    .withLocalDomain(localDomain);
UUID uuid = dcesecurity.create(localIdentifier);
```

```java
// with fixed CUSTOM local domain
byte customLocalDomain = (byte) 54;
int localIdentifier = 1492;
DceSecurityUuidCreator dcesecurity = UuidCreator.getDceSecurityCreator()
    .withLocalDomain(customLocalDomain);
UUID uuid = dcesecurity.create(localIdentifier);
```

### Library utilities

This library provides many utilities for UUID validation, version checking, information extraction, etc.

##### UuidValidador

The `UuidValidator` is used to check if the UUID string representation is correct.

Using `UuidValidator` is much faster than using regular expression.

The methods below have variants that check the UUID version.

```java
// Returns true if is valid (these examples are valid)
UuidValidator.isValid("94d785b2eb624eaa88e72fe8fc5c3478");
UuidValidator.isValid("94D785B2EB624EAA88E72FE8FC5C3478");
UuidValidator.isValid("94d785b2-eb62-4eaa-88e7-2fe8fc5c3478");
UuidValidator.isValid("94D785B2-EB62-4EAA-88E7-2FE8FC5C3478");
```

```java
// Throws an exception if invalid (these examples are valid)
UuidValidator.validate("033d4881f0594171bc832fe89e5a2bed");
UuidValidator.validate("033D4881F0594171BC832FE89E5A2BED");
UuidValidator.validate("033d4881-f059-4171-bc83-2fe89e5a2bed");
UuidValidator.validate("033D4881-F059-4171-BC83-2FE89E5A2BED");
```

##### UuidUtil

This utility class provides methods for checking UUID version, extracting information from UUIDs, etc.

```java
// Returns true if UUID is NIL
UUID uuid = UuidCreator.fromString("00000000-0000-0000-0000-000000000000");
UuidUtil.isNil(uuid);
```

```java
// Returns true if UUID is random based (version 4)
UUID uuid = UuidCreator.fromString("e2df0d73-cc2b-455f-a9e4-2fe8bf4bf0f0");
UuidUtil.isRandomBased(uuid);
```

```java
// Returns true if UUID is time-based (version 1)
UUID uuid = UuidCreator.fromString("0edd764a-8eff-11e9-8649-972f32b091a1");
UuidUtil.isTimeBased(uuid);
```

```java
// Returns true if UUID is time-ordered (version 6)
UUID uuid = UuidCreator.fromString("1e98eff0-eddc-647f-a649-ad1cde652e10");
UuidUtil.isTimeOrdered(uuid);
```

```java
// Extract information from time-based or time-ordered UUIDs
UUID uuid = UuidCreator.fromString("0edd764a-8eff-11e9-8649-972f32b091a1");
Instant instant = UuidUtil.extractInstant(uuid);
long millis = UuidUtil.extractUnixMilliseconds(uuid);
int clocksq = UuidUtil.extractClockSequence(uuid);
long nodeid = UuidUtil.extractNodeIdentifier(uuid);
UuidVersion version = UuidUtil.getVersion(uuid);
UuidVariant variant = UuidUtil.getVariant(uuid);
```

```java
// Apply a given version number to a UUID
uuid = UuidUtil.applyVersion(uuid, 4); // 00000000-0000-4000-0000-000000000000
```

##### MachineId

The `MachineId` collects the hostname, the MAC address and the IP address and then generates a SHA-256 hash of these data. The resulting hash is truncated to generate a machine ID or UUID.

```java
// Get the machine ID
long id = MachineId.getMachineId();
```

```java
// Get the machine UUID
UUID uuid = MachineId.getMachineUuid();
```

### Library codecs

This library also provides many codecs for canonical string, byte array, base-n, slugs etc.

##### BinaryCodec

A UUID is an array of 16 bytes. The `BinaryCodec` converts a UUID to and from an array of bytes.

```java
// Convert a UUID into an array of bytes
UuidCodec<byte[]> codec = new BinaryCodec();
byte[] bytes = codec.encode(uuid);
```

```java
// Convert an array of bytes into a UUID
UuidCodec<byte[]> codec = new BinaryCodec();
UUID uuid = codec.decode(bytes);
```

##### StringCodec

The RFC-4122 defines a standard string representation, also referred as [canonical textual representation](https://en.wikipedia.org/wiki/Universally_unique_identifier#Format). The `StringCodec` converts a UUID to and from a string representation.

The string representation is a string of 32 hexadecimal (base-16) digits, displayed in five groups separated by hyphens, in the form 8-4-4-4-12 for a total of 36 characters (32 hexadecimal characters and 4 hyphens).

This codec decodes (parses) strings in these formats, with/without hyphens:

- 00000000-0000-V000-0000-000000000000 (canonical string);
- {00000000-0000-V000-0000-000000000000} (MS GUID string).
- urn:uuid:00000000-0000-V000-0000-000000000000 (URN UUID string);

The encoding and decoding processes may be much faster (5 to 7x) than `UUID#toString()` and `UUID#fromString(String)` in JDK 8.

If you prefer a string representation without hyphens, use the `Base16Codec` instead of `StringCodec`. This other codec may be much faster (10x) than doing `uuid.toString().replaceAll("-", "")`.

```java
// Convert UUID into a string
// It is much faster than `UUID.toString(UUID)` in JDK 8.
UuidCodec<String> codec = new StringCodec();
String string = codec.encode(uuid);
```

```java
// Convert a string into a UUID (these examples are valid)
// It is much faster than `UUID.fromString(String)` in JDK 8.
UuidCodec<String> codec = new StringCodec();
UUID uuid = codec.decode("53ab5fd331ee4cb987792fe8a887b3be");
UUID uuid = codec.decode("53AB5FD331EE4CB987792FE8A887B3BE");
UUID uuid = codec.decode("53ab5fd3-31ee-4cb9-8779-2fe8a887b3be");
UUID uuid = codec.decode("53AB5FD3-31EE-4CB9-8779-2FE8A887B3BE");
UUID uuid = codec.decode("{53ab5fd3-31ee-4cb9-8779-2fe8a887b3be}");
UUID uuid = codec.decode("{53AB5FD3-31EE-4CB9-8779-2FE8A887B3BE}");
UUID uuid = codec.decode("urn:uuid:53ab5fd3-31ee-4cb9-8779-2fe8a887b3be");
UUID uuid = codec.decode("urn:uuid:53AB5FD3-31EE-4CB9-8779-2FE8A887B3BE");
```

##### SlugCodec

A UUID Slug is a short string representation that can be safely included in URLs and file names.

The `SlugCodec` turns a UUID into a string that does not start with digits (0-9). Due to the default base-64-url alphabet, it is *case sensitive* and may contain '-' and '_'.

The `Base32Codec` can be passed to the `SlugCodec` constructor to generate base-32 slugs. Due to the base-32 alphabet, it is *case insensitive* and it contains only letters (a-zA-Z) and digits (2-7). This encoding substitution can be done to avoid the characters '-' and '_' of the base-64-url encoding, but it makes the slug case insensitive.

To turn a UUID into a slug, the version and variant [nibbles](https://en.wikipedia.org/wiki/Nibble) are are moved to the first position of the UUID byte array. The slugs generated of the same UUID version show a constant letter in the first position of the base-64-url string. 

This is how the UUID bits are rearranged:

```
  00000000-0000-V000-X000-000000000000
                |    |            ^
  ,-------------'    |   encode   |
  |,-----------------'      |   decode
  ||                        v
  VX000000-0000-0000-0000-000000000000
                             shift -->
  V: version nibble or character
  X: variant nibble or character
```

This table shows the slug prefixes for each UUID version:

```
VERSON  PREFIX   EXAMPLE
   1       G     GxA1e7vco3Ib6_mjtptP3w
   2       K     KryezRARVgTHLQ3zJpAXIw
   3       O     O9JfSS1IqIabkEWC-uXWNA
   4       S     S5iPSZYDt7q2w0qiIFZVwQ
   5       W     WY-Uv6WAY5os7Gfv4ILnvQ
   6       a     aMKkEoaymw0FSQNJRDL7Gw
```

If you don't like the change in the UUID bytes layout before the encoding to base-64-url, use the `Base64Codec` instead of `SlugCodec` to generate slugs.

`SlugCodec` and `NcnameCodec` are very similar. The difference between the two is the bit shift they do with the original UUID to transform it into a string.

In the case someone is interested in implementing this type of slug in another language, the change in the bytes layout don't have to be done with bit shifting. Since a base-16 character corresponds to a nibble, the layout change could be easily done by moving characters instead of by shifting bits. See `SlugCodecTest#moveCharacters()`.

```java
// Returns a UUID Slug
UuidCodec<String> codec = new SlugCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // SgEjRWeJq97xI0VniavN7w
```

```java
// Returns a UUID Slug encoded to base 32
UuidCodec<String> codec = new SlugCodec(new Base32Codec());
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // jiasgrlhrgv554jdivtytk6n54
```

##### NcnameCodec

A UUID NCName is a shorter string representation that conforms to the constraints of various other identifiers such as NCName in XML documents.

The `NcnameCodec` turns a UUID into a string that does not start with digits (0-9). But due to the default base-64-url encoding, it may contain '-' and '_'.

The `Base32Codec` can be passed to the `NcnameCodec` constructor to generate base-32 NCNames. Due to the base-32 alphabet, it is *case insensitive* and it contains only letters (a-zA-Z) and digits (2-7). This encoding substitution can be done to avoid the characters '-' and '_' of the base-64-url encoding, but it makes the NCName case insensitive.

The transformation scheme is outlined in this RFC: https://tools.ietf.org/html/draft-taylor-uuid-ncname-00

`SlugCodec` and `NcnameCodec` are very similar. The difference between the two is the bit shift they do with the original UUID to make it a string.

```java
// Returns a UUID NCName
UuidCodec<String> codec = new NcnameCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // EASNFZ4mr3vEjRWeJq83vK
```

```java
// Returns a UUID NCName encoded to base 32
UuidCodec<String> codec = new NcnameCodec(new Base32Codec());
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // eaerukz4jvpppci2fm6e2xtppk
```

##### UriCodec

The URN representation adds the prefix 'urn:uuid:' to a UUID canonical representation. The codec `UriCodec` encodes UUID to and from an opaque `java.net.URI`.

```java
// Returns a `java.net.URI`
UuidCodec<URI> codec = new UriCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
URI uri = codec.encode(uuid); // urn:uuid:01234567-89ab-4def-a123-456789abcdef
```

##### BaseNCodec

This library provides codecs for base-16, base-32 and base-64 encodings. All the encodings defined in the [RFC-4648](https://tools.ietf.org/html/rfc4648) are available. The [Crockford's base-32](https://www.crockford.com/base32.html) is also available.

These are the base-n encodings available:

- Base 16;
- Base 32;
- Base 32 Hex;
- Base 32 Crockford;
- Base 64;
- Base 64 URL.

The following examples encode the UUID `01234567-89AB-4DEF-A123-456789ABCDEF` to all the provided encodings.

```java
// Returns a base-16 string
// It is much faster than doing `uuid.toString().replaceAll("-", "")`.
UuidCodec<String> codec = new Base16Codec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // 0123456789ab4defa123456789abcdef
```

```java
// Returns a base-32 string
UuidCodec<String> codec = new Base32Codec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // aerukz4jvng67ijdivtytk6n54
```

```java
// Returns a base-32-hex string
UuidCodec<String> codec = new Base32HexCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // 04hkaps9ld6uv8938ljojaudts
```

```java
// Returns a Crockford's base-32 string
UuidCodec<String> codec = new Base32CrockfordCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // 04hmasw9nd6yz8938nkrkaydxw
```

```java
// Returns a base-64 string
UuidCodec<String> codec = new Base64Codec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // ASNFZ4mrTe+hI0VniavN7w
```

```java
// Returns a base-64-url string
UuidCodec<String> codec = new Base64UrlCodec();
UUID uuid = UUID.fromString("01234567-89AB-4DEF-A123-456789ABCDEF");
String string = codec.encode(uuid); // ASNFZ4mrTe-hI0VniavN7w
```

##### TimeOrderedCodec

Time-based UUIDs and time-ordered UUIDs have different byte layouts for the timestamp. The `TimeOrderedCodec` converts time-based UUIDs (v1) to and from time-ordered UUIDs (v6).

```java
// Convert time-based (version 1) to time-ordered (version 6)
UuidCodec<UUID> codec = new TimeOrderedCodec();
UUID orderUuid = codec.encode(timeUuid);
```

```java
// Convert time-ordered (version 6) to time-based (version 1)
UuidCodec<UUID> codec = new TimeOrderedCodec();
UUID timeUuid = codec.decode(orderUuid);
```

##### DotNetGuid1Codec and DotNetGuid4Codec

The [.Net Guid](https://docs.microsoft.com/en-us/dotnet/api/system.guid?view=net-5.0) stores the timestamp bytes in a different order from the RFC-4122 UUID. The `DotNetGuid1Codec` and `DotNetGuid4Codec` codecs convert RFC-4122 UUIDs to and from .Net GUIDs.

The .Net GUID stores the most significant bytes as little-endian, while the least significant bytes are stored as big-endian (network order).

```java
// Convert time-ordered (version 1) to .Net Guid
UuidCodec<UUID> codec = new DotNetGuid1Codec();
UUID guid = codec.encode(timeUuid);
```

```java
// Convert random-ordered (version 4) to .Net Guid
UuidCodec<UUID> codec = new DotNetGuid4Codec();
UUID guid = codec.encode(randomUuid);
```

Benchmark
------------------------------------------------------

This section shows benchmarks using JMH v1.23.

```text
================================================================================
THROUGHPUT (operations/msec)             Mode  Cnt      Score     Error   Units
================================================================================
Throughput.JDK_toString                 thrpt    5   2915,524 ±  86,400  ops/ms
Throughput.JDK_fromString               thrpt    5   1938,784 ±  88,818  ops/ms
Throughput.JDK_RandomBased              thrpt    5   2050,995 ±  21,636  ops/ms
Throughput.JDK_NameBasedMd5             thrpt    5   2809,598 ±  73,894  ops/ms
--------------------------------------------------------------------------------
Throughput.UuidCreator_toString   (7x)  thrpt    5  20940,350 ± 244,373  ops/ms (*)
Throughput.UuidCreator_fromString (5x)  thrpt    5  10229,685 ± 286,160  ops/ms
Throughput.UuidCreator_RandomBased      thrpt    5   2017,299 ±  23,892  ops/ms
Throughput.UuidCreator_PrefixComb       thrpt    5   2665,831 ±  49,381  ops/ms
Throughput.UuidCreator_ShortPrefixComb  thrpt    5   2082,030 ±  19,635  ops/ms
Throughput.UuidCreator_NameBasedMd5     thrpt    5   2847,436 ±  56,548  ops/ms
Throughput.UuidCreator_NameBasedSha1    thrpt    5   2155,267 ±  48,075  ops/ms
Throughput.UuidCreator_TimeBased        thrpt    5   9748,507 ±  77,969  ops/ms
Throughput.UuidCreator_TimeOrdered      thrpt    5   9784,130 ±  74,655  ops/ms
================================================================================
Total time: 00:17:20
================================================================================
```

(*) The methods `UuidCreator.toString()` and `UuidCreator.fromString()` are much faster only in JDK 8.

Benchmarks executed in a PC with JDK 8, Ubuntu 20.04, CPU Intel i5-3330 and 8GB RAM.

You can find the benchmark source code at [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark).

Related projects
------------------------------------------------------

* [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark): Benchmarks for uuid-creator

* [uuid-creator-database](https://github.com/fabiolimace/uuid-creator-database): Tests for uuid-creator in databases

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

[10]. Sequential UUID Generators

[1]: https://en.wikipedia.org/wiki/Universally_unique_identifier
[2]: https://tools.ietf.org/html/rfc4122
[3]: https://www.percona.com/blog/2007/03/13/to-uuid-or-not-to-uuid
[4]: https://www.percona.com/blog/2014/12/19/store-uuid-optimized-way
[5]: https://bradleypeabody.github.io/uuidv6
[6]: http://pubs.opengroup.org/onlinepubs/9696989899/chap5.htm#tagcjh_08_02_01_01
[7]: http://www.informit.com/articles/printerfriendly/25862
[8]: https://blogs.msdn.microsoft.com/dbrowne/2012/07/03/how-to-generate-sequential-guids-for-sql-server-in-net
[9]: https://github.com/ulid/spec
[10]: https://blog.2ndquadrant.com/sequential-uuid-generators/

Links about UUID 
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

* [The mysterious “Ordered UUID”](https://itnext.io/laravel-the-mysterious-ordered-uuid-29e7500b4f8)

* [UUID proposal for ECMAScript](https://github.com/tc39/proposal-uuid/)

* [NPM UUID](https://www.npmjs.com/package/uuid)

* [How does the Docker assign MAC addresses to containers?](https://stackoverflow.com/questions/42946453/how-does-the-docker-assign-mac-addresses-to-containers/42947044#42947044)

* [EcmaScript UUID Proposal](https://github.com/tc39/proposal-uuid/issues/15#issuecomment-522415349)

* [User identifier (POSIX UID)](https://en.wikipedia.org/wiki/User_identifier)

* [Group identifier (POSIX GID)](https://en.wikipedia.org/wiki/Group_identifier)

* [Name spaces](https://en.wikipedia.org/wiki/Namespace)

* [Domain Name System](https://en.wikipedia.org/wiki/Domain_Name_System)

* [URL](https://en.wikipedia.org/wiki/URL);

* [ISO Object identifier](https://en.wikipedia.org/wiki/Object_identifier);

* [X.500](https://en.wikipedia.org/wiki/X.500);

* [How many ways are there to sort GUIDs? How much time do you have?](https://devblogs.microsoft.com/oldnewthing/20190426-00/?p=102450)

* [How are GUIDs compared in SQL Server 2005?](https://docs.microsoft.com/pt-br/archive/blogs/sqlprogrammability/how-are-guids-compared-in-sql-server-2005)

* [When are you truly forced to use UUID as part of the design?](https://stackoverflow.com/questions/703035/when-are-you-truly-forced-to-use-uuid-as-part-of-the-design/)

* [Python UUID Module to Generate Universally Unique Identifiers](https://pynative.com/python-uuid-module-to-generate-universally-unique-identifiers/)
