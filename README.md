
UUID Generator
======================================================

Summary
------------------------------------------------------

UUID Generator can generate UUIDs (Universally Unique Identifiers), also known as GUIDs (Globally Unique Identifiers). It provides methods for RFC-4122 versions 1, 3, 4, 5. It also provides methods for a sequential version 0.

These types of UUIDs can be generated:

* __Random__: the pseudo-randomly generated version;
* __Time-based:__ the time-based version;
* __Time-based MAC:__ the time-based version with hardware address;
* __Sequential:__ a modified time-based version;
* __Sequential MAC:__ a modified time-based version with hardware address;
* __Name-based MD5:__ a base-named version that uses MD5;
* __Name-based SHA1:__ a base-named version that uses SHA-1.

The sequential UUID is a different implementation of the standard time-based UUIDs.

How to Use
------------------------------------------------------

### Version 0: Sequential UUID

The Sequential UUID is a modified time-based UUID. The bytes of timestamp part are arrenged in the 'natural' order. The version number 0 (zero) was chosen to identify Sequential UUIDs. It may be considered as a non-standard 'extension' of the RFC-4122.

**Example:**

```java
UUID uuid = UUIDGenerator.getSequentialUUID();
System.out.println(uuid.toString());
// Output: 1e879099-a413-0e81-9888-737f8d128eed
```

### Version 1: Time-based UUID

The Time-based UUID embeds a timestamp and may embed a hardware address.

**Example:**

```java
UUID uuid = UUIDGenerator.getTimeBasedUUID();
System.out.println(uuid.toString());
// Output: 8caea7c7-7909-11e8-a919-4b43423cffc9
```
### Version 2: DCE Security

The DCE Security version 2 is out of scope.

### Version 3: Name-based UUID hashed with MD5

The Name-based UUID version 3 is a MD5 hash of a name space and a name.

**Example:**

```java
UUID namespace = UUIDGenerator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedMD5UUID(namespace, name);
System.out.println(uuid.toString());
// Output: 295df05a-2c43-337c-b6b8-4b84826e4a94
```

### Version 4: Random UUID

The Random UUID is a simple random array of 16 bytes.

**Example:**

```java
UUID uuid = UUIDGenerator.getRandomUUID();
System.out.println(uuid.toString());
// Output: f390cc68-6004-417b-81df-4514e4a7323b
```

### Version 5: Name-based UUID hashed with SHA-1

The Name-based UUID version 5 is a SHA-1 hash of a name space and a name. 

**Example:**

```java
UUID namespace = UUIDGenerator.NAMESPACE_URL;
String name = "https://github.com/";
UUID uuid = UUIDGenerator.getNameBasedSHA1UUID(namespace, name);
System.out.println(uuid.toString());
// Output: 39983165-606c-5d83-abfa-b97af8b1ae8d
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

* Time-based UUID: **79592ca7**4d7fV1e8
* Sequential UUID: 1e84d7f**79592**V**ca7**

In short, that is the is the difference between both.

Benchmark using JMH
------------------------------------------------------

Here is a table showing the results of a simple benchmark using JMH:

| Benchmark | Mode | Cnt | Score | Error | Units |
| :---      | :---: | :---: | ---: | ---: | :---: |
| java.util.UUID.randomUUID() | ss | 100 | 56,200 | ±5,334 | ms/op |
| UUIDGenerator.getRandomUUID() | ss | 100 | 48,534 | ±3,726 | ms/op |
| UUIDGenerator.getTimeBasedUUID() | ss | 100 | 9,250 | ±4,651 | ms/op |
| UUIDGenerator.getSequentialUUID() | ss | 100 | 9,359 | ±4,512 | ms/op |
| UUIDGenerator.getNameBasedMD5UUID(name) | ss | 100 | 47,153 | ±5,419 | ms/op |
| UUIDGenerator.getNameBasedSHA1UUID(name) | ss | 100 | 56,507 | ±5,385 | ms/op |
| UUIDGenerator.getTimeBasedWithHardwareAddressUUID() | ss | 100 | 9,342 | ±4,585 | ms/op |
| UUIDGenerator.getSequentialWithHardwareAddressUUID() | ss | 100 | 9,350 | ±4,585 | ms/op | 
|Total time: 00:00:49|

These are the configurations used to run this benchmark:

```java
@State(Scope.Thread)
@Warmup(iterations = 1, batchSize = 1000)
@Measurement(iterations = 10, batchSize = 100000 )
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
```

The method getRandomUUID() uses SecureRandom (java.security.SecureRandom).

The machine used to do this test is an Intel i5-3330 with 16GB RAM.

