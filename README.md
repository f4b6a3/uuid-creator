
UUID Creator
======================================================

This is a Java library for [RFC-4122 Universally Unique Identifiers](https://en.wikipedia.org/wiki/Universally_unique_identifier).

RFC-4122 UUIDs:

*   __Version 1__: Time-based;
*   __Version 2__: DCE Security;
*   __Version 3__: Name-based with MD5;
*   __Version 4__: Random-based;
*   __Version 5__: Name-based with SHA1;
*   __Version 6__: Time-ordered ([IETF draft](https://github.com/uuid6/uuid6-ietf-draft));
*   __Version 7__: Time-ordered with Unix Epoch ([IETF draft](https://github.com/uuid6/uuid6-ietf-draft)).

Non-standard GUIDs:

*   __Prefix COMB__: combination of the creation millisecond (prefix) with random bytes;
*   __Suffix COMB__: combination of the creation millisecond (suffix) with random bytes;
*   __Short Prefix COMB__: combination the creation minute (prefix) with random bytes;
*   __Short Suffix COMB__: combination the creation minute (suffix) with random bytes.

This library contains a good amount of [unit tests](https://github.com/f4b6a3/uuid-creator/tree/master/src/test/java/com/github/f4b6a3/uuid). It also has a [micro benchmark](https://github.com/f4b6a3/uuid-creator/tree/master/benchmark) for you to check if the performance is good enough.

Read the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki).

Maven dependency
------------------------------------------------------

Add these lines to your `pom.xml`:

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>4.6.1</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.uuid`
*   OSGi symbolic name: `com.github.f4b6a3.uuid`

How to Use
------------------------------------------------------

### Library Facade

Create a [Random-based](https://github.com/f4b6a3/uuid-creator/wiki/1.4.-Random-based) UUID:

```java
UUID uuid = UuidCreator.getRandomBased();
```

Create a [Time-based](https://github.com/f4b6a3/uuid-creator/wiki/1.1.-Time-based) UUID:

```java
UUID uuid = UuidCreator.getTimeBased();
```

Create a [Time-ordered](https://github.com/f4b6a3/uuid-creator/wiki/1.6.-Time-ordered) UUID:

```java
UUID uuid = UuidCreator.getTimeOrdered();
```

Create a [Time-ordered with Unix Epoch](https://github.com/f4b6a3/uuid-creator/wiki/1.7.-Time-ordered-epoch) UUID:

```java
UUID uuid = UuidCreator.getTimeOrderedEpoch();
```

Create a [Name-based with MD5](https://github.com/f4b6a3/uuid-creator/wiki/1.3.-Name-based-with-MD5) UUID:

```java
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL, name);
```

Create a [Name-based with SHA-1](https://github.com/f4b6a3/uuid-creator/wiki/1.5.-Name-based-with-SHA-1) UUID:

```java
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL, name);
```

Create a [DCE Security](https://github.com/f4b6a3/uuid-creator/wiki/1.2.-DCE-Security) UUID:

```java
int localIdentifier = 1701;
UUID uuid = UuidCreator.getDceSecurity(UuidLocalDomain.LOCAL_DOMAIN_PERSON, localIdentifier);
```

Create a [Prefix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.1.-Prefix-COMB) GUID:

```java
UUID uuid = UuidCreator.getPrefixComb();
```

Create a [Suffix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.2.-Suffix-COMB) GUID:

```java
UUID uuid = UuidCreator.getSuffixComb();
```

Create a [Short Prefix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.3.-Short-Prefix-COMB) GUID:

```java
UUID uuid = UuidCreator.getShortPrefixComb();
```

Create a [Short Suffix COMB](https://github.com/f4b6a3/uuid-creator/wiki/2.4.-Short-Suffix-COMB) GUID:

```java
UUID uuid = UuidCreator.getShortSuffixComb();
```

### Library Utilities

This library provides many utilities for validation, version checking, information extraction, etc.

Validate a UUID string:

```java
UuidValidator.isValid(uuid);
UuidValidator.validate(uuid); // Throws an exception if INVALID
```

Check  the version of UUID:

```java
UuidUtil.isTimeBased(uuid);
UuidUtil.isTimeOrdered(uuid);
UuidUtil.isRandomBased(uuid);
```
Extract information from a UUID:

```java
Instant instant = UuidUtil.getInstant(uuid);
int clocksq = UuidUtil.getClockSequence(uuid);
long nodeid = UuidUtil.getNodeIdentifier(uuid);
UuidVersion version = UuidUtil.getVersion(uuid);
UuidVariant variant = UuidUtil.getVariant(uuid);
```

Extract information from a COMB GUID:

```java
long prefix = UuidUtil.getPrefix(comb); // Unix milliseconds
long suffix = UuidUtil.getSuffix(comb); // Unix milliseconds
Instant instant = UuidUtil.getPrefixInstant(comb);
Instant instant = UuidUtil.getSuffixInstant(comb);
```

Get the machine ID:

```java
long id = MachineId.getMachineId(); // 0x7bc3cfd7844f46ad (8918200211668420269)
UUID uuid = MachineId.getMachineUuid(); // 7bc3cfd7-844f-46ad-81a9-1aa22d3c427a
```

### Library Codecs

This library also provides many codecs for canonical string, byte array, base-n, slugs, etc.

#### Main codecs

Convert a UUID to and from [byte array](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#binarycodec):

```java
UUID uuid = BinaryCodec.INSTANCE.decode(/* 16 bytes */);
byte[] bytes = BinaryCodec.INSTANCE.encode(uuid);
```
Convert a UUID to and from [canonical string](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#stringcodec):

```java
// 7x faster than `UUID.fromString()` and `UUID.toString()`
UUID uuid = StringCodec.INSTANCE.decode("01234567-89ab-4def-a123-456789abcdef");
String string = StringCodec.INSTANCE.encode(uuid);
```

Convert a UUID to and from [URI](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#uricodec):

```java
UUID uuid = UriCodec.INSTANCE.decode("urn:uuid:01234567-89ab-4def-a123-456789abcdef");
URI uri = UriCodec.INSTANCE.encode(uuid);
```

#### Base-N codecs

There are base-n codecs for base-16, base-32, base-58, base-62 and base-64.

Custom codecs can be instantiated with `BaseNCodec.newInstance(int|String)`.

Convert a UUID to and from [base-n](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#base-n-codecs):

```java
// 22x faster than `UUID.toString().replaceAll("-", "")`
UUID uuid = Base16Codec.INSTANCE.decode("0123456789ab4defa123456789abcdef");
String string = Base16Codec.INSTANCE.encode(uuid);
```

```java
UUID uuid = Base32Codec.INSTANCE.decode("aerukz4jvng67ijdivtytk6n54");
String string = Base32Codec.INSTANCE.encode(uuid);
```

```java
UUID uuid = Base58BtcCodec.INSTANCE.decode("199dn6s7UNiX3LyNkQ1Cfx");
String string = Base58BtcCodec.INSTANCE.encode(uuid);
```

```java
UUID uuid = Base62Codec.INSTANCE.decode("0296tiiBY28FKCYq1PVSGd");
String string = Base62Codec.INSTANCE.encode(uuid);
```

```java
UUID uuid = Base64UrlCodec.INSTANCE.decode("ASNFZ4mrTe-hI0VniavN7w");
String string = Base64UrlCodec.INSTANCE.encode(uuid);
```

Convert a UUID to and from a *custom* base-n:

```java
// a base-20 string using a CUSTOM radix (20)
int radix = 20; // expanded to "0123456789abcdefghij"
BaseNCodec codec = BaseNCodec.newInstance(radix);

UUID uuid = codec.decode("00b5740h195313554732654bjhj9e7");
String string = codec.encode(uuid);
```

```java
// a base-10 string using a CUSTOM alphabet ("0-9")
String alphabet = "0-9"; // expanded to "0123456789"
BaseNCodec codec = BaseNCodec.newInstance(alphabet);

UUID uuid = codec.decode("001512366075203566477668990085887675887");
String string = codec.encode(uuid);
```

Using a fast division library to boost encoding speed:

```java
// a base-62 codec using a fast division library, i.e., `libdivide`
CustomDivider divider = x -> {
    /* my division code here */
    return new long[] { quotient, remainder };
};
BaseNCodec codec = BaseNCodec.newInstance(62, divider);
```

#### Other codecs

Convert a UUID to and from [Slug](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#slugcodec):

```java
UUID uuid = SlugCodec.INSTANCE.decode("SgEjRWeJq97xI0VniavN7w");
String string = SlugCodec.INSTANCE.encode(uuid);
```

Convert a UUID to and from [NCName](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#ncnamecodec):

```java
UUID uuid = NcnameCodec.INSTANCE.decode("EASNFZ4mr3vEjRWeJq83vK");
String string = NcnameCodec.INSTANCE.encode(uuid);
```

Convert a UUID to and from [.Net Guid](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#dotnetguid1codec-and-dotnetguid4codec):

```java
// Convert time-based (version 1) to .Net Guid
UUID guid = DotNetGuid1Codec.INSTANCE.encode(uuid);
UUID uuid = DotNetGuid1Codec.INSTANCE.decode(guid);
```
```java
// Convert random-based (version 4) to .Net Guid
UUID guid = DotNetGuid4Codec.INSTANCE.encode(uuid);
UUID uuid = DotNetGuid4Codec.INSTANCE.encode(guid);
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
