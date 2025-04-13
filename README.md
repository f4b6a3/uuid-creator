UUID Creator
======================================================

This is a Java library for generating [Universally Unique Identifiers](https://en.wikipedia.org/wiki/Universally_unique_identifier).

This library is fully compliant with [RFC 9562](https://www.rfc-editor.org/rfc/rfc9562), the Internet standard which obsoletes [RFC 4122](https://www.rfc-editor.org/rfc/rfc9562).

List of implemented UUID subtypes:

*   __UUID Version 1__: the Gregorian time-based UUID specified in RFC 9562;
*   __UUID Version 2__: the DCE Security version, with embedded POSIX UIDs, specified in DCE 1.1;
*   __UUID Version 3__: the name-based version that uses MD5 hashing specified in RFC 9562;
*   __UUID Version 4__: The randomly or pseudorandomly generated version specified in RFC 9562;
*   __UUID Version 5__: the name-based version that uses SHA-1 hashing specified in RFC 9562;
*   __UUID Version 6__: the reordered Gregorian time-based UUID specified in RFC 9562;
*   __UUID Version 7__: the Unix Epoch time-based UUID specified in RFC 9562.

This library solves some of the JDK's UUID issues:

| Problem | Solution |
|---------|----------|
| `UUID` can't generate Gregorian time-based UUIDs (UUIDv1). | Use [`UuidCreator.getTimeBased()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getTimeBased()). |
| `UUID` can't generate SHA-1 UUIDs (UUIDv5). | Use [`UuidCreator.getNameBasedSha1()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getNameBasedSha1(java.util.UUID,java.lang.String)) |
| `UUID.nameUUIDFromBytes()`, which generates MD5 UUIDs (UUIDv3), does not have a namespace parameter as required by the standard. | Use [`UuidCreator.getNameBasedMd5()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getNameBasedMd5(java.util.UUID,java.lang.String)) |
| `UUID` has no validation method, which makes developers use `UUID.fromString()` or regular expression for validation. | Use [`UuidValidator.isValid()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidValidator.html#isValid(java.lang.String)). |
| Some methods such as `UUID.timestamp()` are strongly related to UUIDv1, even though it's impossible to generate UUIDv1. | Use [`UuidUtil`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidUtil.html). |
| `UUID.randomUUID()` can be slow due to [lack of entropy](https://medium.com/@RamLakshmanan/java-uuid-generation-performance-impact-cec888b7d9b8) in the operating system. | Use [`UuidCreator.getRandomBasedFast()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getRandomBasedFast()).<br><em>However, keep in mind that it is not cryptographically secure.</em> |
| `UUID.compareTo()` [behaves unexpectedly](https://yoshiori.hatenablog.com/entry/2024/02/22/173322) due to signed `long` comparisons, causing non-alphabetical sorting. | Use [`UuidComparator`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidComparator.html). |
| `UUID.fromString()` allows non-standard strings like `0-0-0-0-0` as valid UUID strings. | Use [`UuidCreator.fromString()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#fromString(java.lang.String)). |

This project contains a [micro benchmark](https://github.com/f4b6a3/uuid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/uuid-creator/tree/master/src/test/java/com/github/f4b6a3/uuid), with more than 90% coverage.

For more information, read the the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator) and the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki).

> **NOTE**:
> This software is not supported or maintained by any organization. This information may be useful when having an organization behind a project is a criterion for deciding whether software can be adopted or not.

Dependency
------------------------------------------------------

Maven:

```xml
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>6.1.1</version>
</dependency>
```

Gradle:

```
implementation 'com.github.f4b6a3:uuid-creator:6.1.0'
```

See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

> **HINT:**
> The `jar` file can be downloaded directly from [maven.org](https://repo1.maven.org/maven2/com/github/f4b6a3/uuid-creator/).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.uuid`
*   OSGi symbolic name: `com.github.f4b6a3.uuid`

Usage
------------------------------------------------------

All UUID subtypes can be created from the facade [`UuidCreator`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html).

The goal of the facade is to make most of the library's functionality available in a single place so that you don't have to worry about the internals of the library. All you need is to decide which UUID subtype you need for your application and call the respective generation method. If in doubt, read the documentation and check out the source code.

Create a [UUIDv1](https://github.com/f4b6a3/uuid-creator/wiki/1.1.-UUIDv1):

```java
UUID uuid = UuidCreator.getTimeBased();
```

Create a [UUIDv2](https://github.com/f4b6a3/uuid-creator/wiki/1.2.-UUIDv2):

```java
UUID uuid = UuidCreator.getDceSecurity(UuidLocalDomain.LOCAL_DOMAIN_PERSON, 1234);
```

Create a [UUIDv3](https://github.com/f4b6a3/uuid-creator/wiki/1.3.-UUIDv3):

```java
UUID uuid = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL, "https://github.com/");
```

Create a [UUIDv4](https://github.com/f4b6a3/uuid-creator/wiki/1.4.-UUIDv4):

```java
UUID uuid = UuidCreator.getRandomBased();
```

Create a [UUIDv5](https://github.com/f4b6a3/uuid-creator/wiki/1.5.-UUIDv5):

```java
UUID uuid = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL, "https://github.com/");
```

Create a [UUIDv6](https://github.com/f4b6a3/uuid-creator/wiki/1.6.-UUIDv6):

```java
UUID uuid = UuidCreator.getTimeOrdered();
```

Create a [UUIDv7](https://github.com/f4b6a3/uuid-creator/wiki/1.7.-UUIDv7):

```java
UUID uuid = UuidCreator.getTimeOrderedEpoch();
```

> **NOTE:**
> A UUID version is a [**UUID subtype**](https://www.rfc-editor.org/rfc/rfc9562#name-iana-uuid-subtype-registry-). The word "version" is not used in the sense that a higher version number makes the previous one obsolete. There are 8 subtypes of UUID and each of them is assigned a number; for example, a UUIDv7 is a UUID of subtype 7. Likewise, a UUID variant is a [**UUID type**](https://www.rfc-editor.org/rfc/rfc9562#section-4.1). There are 4 types of UUID: (1) the prehistoric one, (2) the one described in RFC 9562, (3) the one belonging to Microsoft and (4) the one reserved for the future. RFC 9562 retains the terms “version” and “variant” for compatibility with previous specifications and existing implementations.

Alternative API
------------------------------------------------------

[`GUID`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html) is an alternative implementation to the classic JDK's UUID. It also serves as a standalone generator, independent from the rest of the library. This may result in fewer classes being loaded.

This new API was also designed to be an alternative to [`UuidCreator`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html) with three goals in mind: clean interface, simple implementation, and high performance. It was inspired by popular libraries for [Javascript](https://www.npmjs.com/package/uuid) and [Python](https://docs.python.org/3/library/uuid.html).

```java
GUID guid = GUID.v1();
```
```java
GUID guid = GUID.v2(GUID.LOCAL_DOMAIN_PERSON, 1234);
```
```java
GUID guid = GUID.v3(GUID.NAMESPACE_DNS, "www.example.com");
```
```java
GUID guid = GUID.v4();
```
```java
GUID guid = GUID.v5(GUID.NAMESPACE_DNS, "www.example.com");
```
```java
GUID guid = GUID.v6();
```
```java
GUID guid = GUID.v7();
```

You can generate random-based GUIDs by passing an instance of `SecureRandom` as a parameter:

```java
GUID guid = GUID.v4(new SecureRandom());
```

You can also generate JDK's UUIDs using GUID's API. For example, you can generate a JDK's UUID version 7 with this simple statement:

```java
UUID uuid = GUID.v7().toUUID();
```

> **NOTE:**
> `GUID` API uses `ThreadLocalRandom` by default, which is a **non-cryptographic** PRNG, so it doesn't block when generating UUIDs. If your project needs a “cryptographic quality” random generator, you can pass a `SecureRandom` object to the methods that expect a `Random` parameter.


Other identifier generators
------------------------------------------------------

Check out the other ID generators from the same family:

*   [ULID Creator](https://github.com/f4b6a3/ulid-creator): Universally Unique Lexicographically Sortable Identifiers
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator): Time Sortable Identifiers
*   [KSUID Creator](https://github.com/f4b6a3/ksuid-creator): K-Sortable Unique Identifiers

License
------------------------------------------------------

This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).

------------------------------------------------------

_Personal Notes:_
1. _The library can do much more than the examples shown in this document (much more than I should have done). I hope most people find this project useful. In other words, your like is my payment._
2. _The name of this software is UUID Creator or uuid-creator. Use "com.github.f4b6a3" or "f4b6a3" only when necessary to avoid doubt, as this is just a unique package name to follow Java convention (now I know it wasn't a good idea)._
