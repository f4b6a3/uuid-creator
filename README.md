

UUID Creator
======================================================

This is a Java library for generating [Universally Unique Identifiers](https://en.wikipedia.org/wiki/Universally_unique_identifier).

The Internet standard [RFC 9562](https://www.rfc-editor.org/rfc/rfc9562) was published in May 2024, making RFC 4122 obsolete. This library is fully compatible with the new RFC. It was developed closely following the evolution of the new internet standard until its publication.

List of implemented UUID types:

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
| `UUID` can't generate Gregorian time-based UUIDs (UUIDv1). | Use [`UuidCreator.getTimeBased()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getTimeBased()) or [`GUID.v1()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#v1()). |
| `UUID` can't generate SHA-1 UUIDs (UUIDv5). | Use [`UuidCreator.getNameBasedSha1()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getNameBasedSha1(java.util.UUID,java.lang.String)) or [`GUID.v5()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#v5(com.github.f4b6a3.uuid.alt.GUID,java.lang.String)). |
| `UUID` has no validation method, which makes developers use `UUID.fromString()` or regular expression for validation. | Use [`UuidValidator`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidValidator.html) or [`GUID.valid()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#valid(java.lang.String)). |
| `UUID.nameUUIDFromBytes()`, which generates MD5 UUIDs (UUIDv3), does not have a namespace parameter as required by the standard. | Use [`UuidCreator.getNameBasedMd5()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getNameBasedMd5(java.util.UUID,java.lang.String)) or [`GUID.v3()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#v3(com.github.f4b6a3.uuid.alt.GUID,java.lang.String)). |
| Some methods such as `UUID.timestamp()` are strongly related to UUIDv1, even though it's impossible to generate UUIDv1. | Use [`UuidUtil`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidUtil.html). |
| `UUID.randomUUID()` can be slow due to [lack of entropy](https://medium.com/@RamLakshmanan/java-uuid-generation-performance-impact-cec888b7d9b8) in the operating system. | Use [`UuidCreator.getRandomBasedFast()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#getRandomBasedFast()) or [`GUID.v4()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#v4()). However, keep in mind that both are not cryptographically secure. |
| `UUID.compareTo()` [behaves unexpectedly](https://yoshiori.hatenablog.com/entry/2024/02/22/173322) due to signed `long` comparisons, causing non-alphabetical sorting. | Use [`UuidComparator`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/util/UuidComparator.html). |
| `UUID.fromString()` allows non-canonical strings like `0-0-0-0-0` as valid UUID strings. | Use [`UuidCreator.fromString()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html#fromString(java.lang.String)) or [`new GUID()`](https://javadoc.io/static/com.github.f4b6a3/uuid-creator/5.3.7/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html#%3Cinit%3E(java.lang.String)). |

This project contains a [micro benchmark](https://github.com/f4b6a3/uuid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/uuid-creator/tree/master/src/test/java/com/github/f4b6a3/uuid).

Read the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki) and the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator).

Dependency
------------------------------------------------------

Maven:

```xml
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>5.3.7</version>
</dependency>
```

Gradle:

```
implementation 'com.github.f4b6a3:uuid-creator:5.3.3'
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

All UUID types can be created from the facade [`UuidCreator`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html).

The goal of this class is to make most of the library's functionality available in a single place so that you developers don't have to worry about the internals of the library. All you need is to decide which type of UUID you need for your application and call the respective generation method. If in doubt, read the documentation and check the source code.

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

> **PERSONAL NOTE:**
> The library can do a lot more than the examples above (much more than I should have done). So I sincerely hope that most people are satisfied with this. In other words, your like is my payment.

Alternative API
------------------------------------------------------

[`GUID`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html) is an alternative implementation to the classic JDK's UUID. It also serves as a standalone generator, independent from the rest of the library. This may result in fewer classes being loaded.

This new API was also designed to be an alternative to [`UuidCreator`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html) with three goals in mind: clean interface, simple implementation, and high performance. It was inspired by popular libraries for [Javascript](https://www.npmjs.com/package/uuid) and [Python](https://docs.python.org/3/library/uuid.html).

Additionaly, it does not block during GUID generation due to the **non-cryptographic** random number generator used by its factory methods. However, it is not recommended when the security of “cryptographic quality” generators is considered necessary.

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

### Generate JDK's UUID from GUID's API

You can generate JDK's UUIDs using GUID's API. For example, you can generate a JDK's UUID version 7 with this simple statement:

```java
UUID uuid = GUID.v7().toUUID();
```

When you call `toUUID()` the internal value of GUID is copied to the new JDK's UUID.

Deprecation
------------------------------------------------------

The methods which use a UUID as a "name" parameter such as [`UuidCreator.getNameBasedMd5(UUID name)`](https://github.com/f4b6a3/uuid-creator/blob/79e049eeeb43a7cd288f4c73f0d0daa6c339c7d6/src/main/java/com/github/f4b6a3/uuid/UuidCreator.java#L656) are **deprecated**. They will be removed soon after the new RFC is published. For more details, please read https://github.com/f4b6a3/uuid-creator/issues/91.

The [`v8()`](https://github.com/f4b6a3/uuid-creator/blob/master/src/main/java/com/github/f4b6a3/uuid/alt/GUID.java#L355) method of the alternative [`GUID`](https://github.com/f4b6a3/uuid-creator/blob/master/src/main/java/com/github/f4b6a3/uuid/alt/GUID.java) class is also **deprecated** and will be removed soon.

Other identifier generators
------------------------------------------------------

Check out the other ID generators from the same family:

*   [ULID Creator](https://github.com/f4b6a3/ulid-creator): Universally Unique Lexicographically Sortable Identifiers
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator): Time Sortable Identifiers
*   [KSUID Creator](https://github.com/f4b6a3/ksuid-creator): K-Sortable Unique Identifiers

License
------------------------------------------------------

This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).
