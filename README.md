

UUID Creator
======================================================

This is a Java library for generating [Universally Unique Identifiers](https://en.wikipedia.org/wiki/Universally_unique_identifier).

List of implemented UUID types:

*   __UUID Version 1__: the time-based version with gregorian epoch specified in RFC-4122;
*   __UUID Version 2__: the DCE Security version with embedded POSIX UIDs specified in DCE 1.1;
*   __UUID Version 3__: the name-based version that uses MD5 hashing specified in RFC-4122;
*   __UUID Version 4__: the randomly or pseudo-randomly generated version specified in RFC-4122;
*   __UUID Version 5__: the name-based version that uses SHA-1 hashing specified in RFC-4122;
*   __UUID Version 6__: the time-ordered version with gregorian epoch proposed as [new UUID format](https://datatracker.ietf.org/doc/draft-ietf-uuidrev-rfc4122bis/);
*   __UUID Version 7__: the time-ordered version with Unix epoch proposed as [new UUID format](https://datatracker.ietf.org/doc/draft-ietf-uuidrev-rfc4122bis/).

This project contains a [micro benchmark](https://github.com/f4b6a3/uuid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/uuid-creator/tree/master/src/test/java/com/github/f4b6a3/uuid).

The jar file can be downloaded directly from [maven.org](https://repo1.maven.org/maven2/com/github/f4b6a3/uuid-creator/).

Read the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki) and the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator).

Dependency
------------------------------------------------------

Maven:

```xml
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>5.3.3</version>
</dependency>
```

Gradle:

```
implementation 'com.github.f4b6a3:uuid-creator:5.3.3'
```

See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.uuid`
*   OSGi symbolic name: `com.github.f4b6a3.uuid`

Usage
------------------------------------------------------

All UUID types can be created from the facade [`UuidCreator`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/UuidCreator.html).

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

Alternative
------------------------------------------------------

The [`GUID`](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator/latest/com.github.f4b6a3.uuid/com/github/f4b6a3/uuid/alt/GUID.html) class is an alternative to the JDK's built-in `UUID`.

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

Other identifier generators
------------------------------------------------------

Check out the other ID generators from the same family:

*   [ULID Creator](https://github.com/f4b6a3/ulid-creator): Universally Unique Lexicographically Sortable Identifiers
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator): Time Sortable Identifiers
*   [KSUID Creator](https://github.com/f4b6a3/ksuid-creator): K-Sortable Unique Identifiers

License
------------------------------------------------------

This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).
