# UUID Creator

A Java library for generating and handling RFC-4122 UUIDs.

RFC-4122 UUIDs:

*   __Version 1__: Time-based;
*   __Version 2__: DCE Security;
*   __Version 3__: Name-based with MD5;
*   __Version 4__: Random-based;
*   __Version 5__: Name-based with SHA1;
*   __Version 6__: Time-ordered (proposed).

Non-standard GUIDs:

*   __Prefix COMB__: combination of the creation millisecond (prefix) with random bytes;
*   __Suffix COMB__: combination of the creation millisecond (suffix) with random bytes;
*   __Short Prefix COMB__: combination the creation minute (prefix) with random bytes;
*   __Short Suffix COMB__: combination the creation minute (suffix) with random bytes.

Read the [Wiki pages](https://github.com/f4b6a3/uuid-creator/wiki).

## Maven dependency

Add these lines to your `pom.xml`:

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>uuid-creator</artifactId>
  <version>4.2.1</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/uuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.uuid`
*   OSGi symbolic name: `com.github.f4b6a3.uuid`

## How to Use

### Library Facade

Create a [Random-based](https://github.com/f4b6a3/uuid-creator/wiki/1.4.-Random-based) UUID:

```java
UUID uuid = UuidCreator.getRandomBased();
```

Create a [Time-based](https://github.com/f4b6a3/uuid-creator/wiki/1.1.-Time-based) UUID:

```java
// with a static random node ID
UUID uuid = UuidCreator.getTimeBased();
```
```java
// with a MAC address node ID
UUID uuid = UuidCreator.getTimeBasedWithMac();
```
```java
// with a hash of Hostname+MAC+IP node ID
UUID uuid = UuidCreator.getTimeBasedWithHash();
```
```java
// with a changing random node ID
UUID uuid = UuidCreator.getTimeBasedWithRandom();
```

Create a [Time-ordered](https://github.com/f4b6a3/uuid-creator/wiki/1.6.-Time-ordered) UUID:

```java
// with a static random node ID
UUID uuid = UuidCreator.getTimeOrdered();
```
```java
// with a MAC address node ID
UUID uuid = UuidCreator.getTimeOrderedWithMac();
```
```java
// with a hash of Hostname+MAC+IP node ID
UUID uuid = UuidCreator.getTimeOrderedWithHash();
```
```java
// with a changing random node ID
UUID uuid = UuidCreator.getTimeOrderedWithRandom();
```

Create a [Name-based with MD5](https://github.com/f4b6a3/uuid-creator/wiki/1.3.-Name-based-with-MD5) UUID:

```java
// without namespace
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(name);
```
```java
// with namespace
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedMd5(UuidNamespace.NAMESPACE_URL, name);
```

Create a [Name-based with SHA-1](https://github.com/f4b6a3/uuid-creator/wiki/1.5.-Name-based-with-SHA-1) UUID:

```java
// without namespace
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(name);
```
```java
// with namespace
String name = "https://github.com/";
UUID uuid = UuidCreator.getNameBasedSha1(UuidNamespace.NAMESPACE_URL, name);
```

Create a [DCE Security](https://github.com/f4b6a3/uuid-creator/wiki/1.2.-DCE-Security) UUID:

```java
// DCE Security
int localIdentifier = 1701;
UuidLocalDomain localDomain = UuidLocalDomain.LOCAL_DOMAIN_PERSON;
UUID uuid = UuidCreator.getDceSecurity(localDomain, localIdentifier);
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
UUID uuid = MachineId.getMachineUuid(); // 7bc3cfd7-844f-46ad-51a9-1aa22d3c427a
```

### Library Codecs

This library also provides many codecs for canonical string, byte array, base-n, slugs, etc.

#### Main codecs

Convert a UUID to and from [byte array](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#binarycodec):

```java
byte[] bytes = BinaryCodec.INSTANCE.encode(uuid);
UUID uuid = BinaryCodec.INSTANCE.decode(bytes);
```
Convert a UUID to and from [canonical string](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#stringcodec):

```java
// It is 7x faster than `UUID.toString()` in JDK 8.
String string = StringCodec.INSTANCE.encode(uuid);
// It is 7x faster than `UUID.fromString()` in JDK 8.
UUID uuid = StringCodec.INSTANCE.decode(string);
```

Convert a UUID to and from [URI](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#uricodec):

```java
// uuid: 01234567-89AB-4DEF-A123-456789ABCDEF
// uri:: urn:uuid:01234567-89ab-4def-a123-456789abcdef
URI uri = UriCodec.INSTANCE.encode(uuid);
UUID uuid = UriCodec.INSTANCE.decode(uri);
```

#### Base-N codecs

There are base-n codecs for base-16, base-32, base-36, base-58, base-62 and base-64. 

Custom codecs can be instantiated with `BaseNCodec.newInstance(int|String)`.

Convert a UUID to and from [base-n](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#base-n-codecs):

```java
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base16: 0123456789ab4defa123456789abcdef
// It is 22x faster than `UUID.toString().replaceAll("-", "")`.
String string = Base16Codec.INSTANCE.encode(uuid);
UUID uuid = Base16Codec.INSTANCE.decode(string);
```

```java
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base32: aerukz4jvng67ijdivtytk6n54
String string = Base32Codec.INSTANCE.encode(uuid);
UUID uuid = Base32Codec.INSTANCE.decode(string);
```

```java
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base58: 199dn6s7UNiX3LyNkQ1Cfx
String string = Base58BitcoinCodec.INSTANCE.encode(uuid);
UUID uuid = Base58BitcoinCodec.INSTANCE.decode(string);
```

```java
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base62: 0296tiiBY28FKCYq1PVSGd
String string = Base62Codec.INSTANCE.encode(uuid);
UUID uuid = Base62Codec.INSTANCE.decode(string);
```

```java
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base64: ASNFZ4mrTe-hI0VniavN7w
String string = Base64UrlCodec.INSTANCE.encode(uuid);
UUID uuid = Base64UrlCodec.INSTANCE.decode(string);
```

Convert a UUID to and from a *custom* base-n:

```java
// Returns a base-20 string using a CUSTOM radix (20)
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base20: 00b5740h195313554732654bjhj9e7
int radix = 20; // expanded to alphabet "0123456789abcdefghij"
UuidCodec<String> codec = BaseNCodec.newInstance(radix);
String string = codec.encode(uuid);
```

```java
// Returns a base-10 string using a CUSTOM alphabet ("0-9")
// uuid::: 01234567-89AB-4DEF-A123-456789ABCDEF
// base10: 001512366075203566477668990085887675887
String alphabet = "0-9"; // expanded to alphabet "0123456789"
UuidCodec<String> codec = BaseNCodec.newInstance(alphabet);
String string = codec.encode(uuid);
```

#### Other codecs

Convert a UUID to and from [Slug](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#slugcodec):

```java
// uuid: 01234567-89AB-4DEF-A123-456789ABCDEF
// slug: SgEjRWeJq97xI0VniavN7w
String slug = SlugCodec.INSTANCE.encode(uuid);
UUID uuid = SlugCodec.INSTANCE.decode(slug);
```

Convert a UUID to and from [NCName](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#ncnamecodec):

```java
// uuid: 01234567-89AB-4DEF-A123-456789ABCDEF
// name: EASNFZ4mr3vEjRWeJq83vK
String name = NcnameCodec.INSTANCE.encode(uuid);
UUID uuid = NcnameCodec.INSTANCE.encode(name);
```

Convert a UUID to and from [.Net Guid](https://github.com/f4b6a3/uuid-creator/wiki/4.0.-Library-codecs#dotnetguid1codec-and-dotnetguid4codec):

```java
// Convert time-based (version 1) to .Net Guid
UUID guid = DotNetGuid1Codec.INSTANCE.encode(timeUuid);
UUID guid = DotNetGuid1Codec.INSTANCE.encode(randomUuid);
```
```java
// Convert time-based (version 4) to .Net Guid
UUID guid = DotNetGuid4Codec.INSTANCE.encode(timeUuid);
UUID guid = DotNetGuid4Codec.INSTANCE.encode(randomUuid);
```

## Other identifier generators

Check out the other ID generators.

*   [ULID Creator](https://github.com/f4b6a3/ulid-creator)
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator)
*   [KSUID Creator](https://github.com/f4b6a3/ksuid-creator)
