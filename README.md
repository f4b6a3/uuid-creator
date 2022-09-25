

UUID Creator
======================================================

This is a Java library for generating [Universally Unique Identifiers](https://en.wikipedia.org/wiki/Universally_unique_identifier).

UUIDs:

*   __UUID Version 1__: the time-based version with gregorian epoch specified in RFC-4122;
*   __UUID Version 2__: the DCE Security version with embedded POSIX UIDs specified in DCE 1.1;
*   __UUID Version 3__: the name-based version that uses MD5 hashing specified in RFC-4122;
*   __UUID Version 4__: the randomly or pseudo-randomly generated version specified in RFC-4122;
*   __UUID Version 5__: the name-based version that uses SHA-1 hashing specified in RFC-4122;
*   __UUID Version 6__: the time-ordered version with gregorian epoch proposed as [new UUID format](https://github.com/uuid6/uuid6-ietf-draft);
*   __UUID Version 7__: the time-ordered version with Unix epoch proposed as [new UUID format](https://github.com/uuid6/uuid6-ietf-draft).

COMB GUIDs:

*   __Prefix COMB GUID__: a combination of random bytes with a prefix (millisecond);
*   __Suffix COMB GUID__: a combination of random bytes with a suffix (millisecond);
*   __Short Prefix COMB GUID__: a combination of random bytes with a small prefix (minute);
*   __Short Suffix COMB GUID__: a combination of random bytes with a small suffix (minute).

This project contains a [micro benchmark](https://github.com/f4b6a3/uuid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/uuid-creator/tree/master/src/test/java/com/github/f4b6a3/uuid).

The jar file can be downloaded directly from [maven.org](https://repo1.maven.org/maven2/com/github/f4b6a3/uuid-creator/).

Read the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki) and the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/uuid-creator).

Maven dependency
------------------------------------------------------

Add these lines to your `pom.xml`:

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>5.1.2</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.uuid`
*   OSGi symbolic name: `com.github.f4b6a3.uuid`

How to Use
------------------------------------------------------

All UUID types can be created from the facade [`UuidCreator`](https://github.com/f4b6a3/uuid-creator/blob/master/src/main/java/com/github/f4b6a3/uuid/UuidCreator.java).

Create a [Random-based](https://github.com/f4b6a3/uuid-creator/wiki/1.4.-Random-based) unique identifier (UUIDv4):

```java
UUID uuid = UuidCreator.getRandomBased();
```

Create a [Time-based](https://github.com/f4b6a3/uuid-creator/wiki/1.1.-Time-based) unique identifier (UUIDv1):

```java
UUID uuid = UuidCreator.getTimeBased();
```

Create a [Time-ordered](https://github.com/f4b6a3/uuid-creator/wiki/1.6.-Time-ordered) unique identifier (UUIDv6):

```java
UUID uuid = UuidCreator.getTimeOrdered();
```

Create a [Time-ordered with Unix Epoch](https://github.com/f4b6a3/uuid-creator/wiki/1.7.-Time-ordered-epoch) unique identifier (UUIDv7):

```java
UUID uuid = UuidCreator.getTimeOrderedEpoch();
```

Create a [Name-based with MD5](https://github.com/f4b6a3/uuid-creator/wiki/1.3.-Name-based-with-MD5) unique identifier (UUIDv3):

```java
UUID uuid = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL, "https://github.com/");
```

Create a [Name-based with SHA-1](https://github.com/f4b6a3/uuid-creator/wiki/1.5.-Name-based-with-SHA-1) unique identifier (UUIDv5):

```java
UUID uuid = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL, "https://github.com/");
```

Create a [DCE Security](https://github.com/f4b6a3/uuid-creator/wiki/1.2.-DCE-Security) unique identifier (UUIDv2):

```java
UUID uuid = UuidCreator.getDceSecurity(UuidLocalDomain.LOCAL_DOMAIN_PERSON, 1234);
```

Create a [Prefix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.0.-COMB-GUID#prefix-comb):

```java
UUID uuid = UuidCreator.getPrefixComb();
```

Create a [Suffix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.0.-COMB-GUID#suffix-comb):

```java
UUID uuid = UuidCreator.getSuffixComb();
```

Create a [Short Prefix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.0.-COMB-GUID#short-prefix-comb):

```java
UUID uuid = UuidCreator.getShortPrefixComb();
```

Create a [Short Suffix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.0.-COMB-GUID#short-suffix-comb):

```java
UUID uuid = UuidCreator.getShortSuffixComb();
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

