# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

Nothing unreleased.

## [6.1.1] - 2025-04-13

- Fixed incorrect timestamp in UUIDv7 when using user-specified instant. #107

## [6.1.0] - 2025-03-29

- Added microsecond precision to UUIDv7;
- Added methods to `GUID`;
- Refactored many classes;
- Updated some tests.

## [6.0.0] - 2024-07-07

This version has breaking changes.

- Added `UuidBuilder` as a helper for generating custom UUIDs (UUIDv8);
- Renamed `StringCodec` to `StandardStringCodec`;
- Renamed `BinaryCodec` to `StandardBinaryCodec`;
- Removed `GUID.v8()` (deprecated);
- Removed `UuidCreator.getNameBasedMD5()` with `UUID` as name parameter (deprecated);
- Removed `UuidCreator.getNameBasedSHA1()` with `UUID` as name parameter (deprecated);
- Removed several code-smells in order improve code-quality and adhere to SOLID principles (by Jay Patel);
- Updated references to the IETF standard RFC 9562;
- Many refactorings and optimizations.

## [5.3.7] - 2023-12-22

Regular maintenance.

## [5.3.6] - 2023-12-21

Refactor UUID version 7 factory. #98

## [5.3.5] - 2023-09-24

Deprecates name-based methods with a UUID name parameter.

## [5.3.4] - 2023-09-23

`GUID.v8()` has been **deprecated** due to recent sudden changes in the UUIDv8 discussions. It will be removed when the new RFC is finally published.

See the latest discussions about UUIDv8:

* https://github.com/ietf-wg-uuidrev/rfc4122bis/issues/143
* https://github.com/ietf-wg-uuidrev/rfc4122bis/issues/144
* https://github.com/ietf-wg-uuidrev/rfc4122bis/issues/147

## [5.3.3] - 2023-09-03

Add a getTimeOrderedEpoch with instant parameter (by Nicola) #82

## [5.3.2] - 2023-05-01

Add a faster parser for GUID #81

## [5.3.1] - 2023-05-01

- Remove the GUID.get() method, synonymous with GUID.toUUID() #80
- Hide MSB and LSB methods used only for tests #79

## [5.3.0] - 2023-04-30

- Add an alternative to the JDK's built-in UUID #78
- Add a MIN and MAX methods #77

## [5.2.1] - 2023-04-29

Fix the initial state of `TimeOrderedEpochFactory`. #76

## [5.2.0] - 2022-10-23

- **Fix MAX UUID** (it was wrong since v5.0.0). #75
- Add a fast method to generate random-based identifiers. #74

## [5.1.2] - 2022-09-11

Rewrite docs. #73

## [5.1.1] - 2022-08-21

Optimized `UuidComparator`. #71

## [5.1.0] - 2022-07-09

Add support for `RandomGenerator` in Java 17. #70

## [5.0.0] - 2022-07-02

Please go to v5.2.0 due to incorrect MAX UUID.

This version has breaking changes.

- Added Max UUID (**wrong!**, fixed in v5.2.0). #67
- Added implementations for UUID v7. #67
- Added minimum support for UUID v8. #67
- Added `UriCodec.isUuidUri()`. #66
- Added `UrnCodec` with `UrnCodec.isUuidUrn()`. #66
- Changed `UuidCodec` implementations to explicitly and only throw `InvalidUuidException`. #65
- Fixed out-of-order when trying to generate a huge batch of UUID v6 in a loop. #69

## [4.6.1] - 2022-03-19

Added time interval parameter for `ShortPrefixCombFactory` and `ShortSuffixCombFactory` constructors. #64

## [4.6.0] - 2022-03-12

Added `UuidComparator` for comparing and sorting UUIDs. #62

Added `CustomDivisor` to be used with `BaseNCodec`. #63

## [4.5.0] - 2022-02-14

This version contains has a breaking change.

Remove the `Base32CrfCodec` created 16 days ago to wait for the new base-32 format proposed in an [IETF draft](https://github.com/uuid6/uuid6-ietf-draft). #61

## [4.4.1] - 2022-02-05

Add `Clock` parameter for COMB factories and time functions for tests. #60

## [4.4.0] - 2022-01-28

This version contains breaking changes.

Add credits file for contributors. #58

Remove excessive base-n codecs (breaking changes). #59

## [4.3.2] - 2022-01-26

Fixed an NPE that occurred since v4.1.4 when trying to get the hardware address on machines with OpenConnect virtual interfaces (by Andrey). #56

## [4.3.1] - 2021-12-12

Regular maintenance.

## [4.3.0] - 2021-12-09

Optimize default random function.

## [4.2.1] - 2021-11-28

Optimize windows time function.

## [4.2.0] - 2021-11-27

Time function for windows.

Optimize network util.

## [4.1.4] - 2021-11-27

Optimize default time function.

Optimize mac node id function.

## [4.1.3] - 2021-11-06

Change the counter's reset range from 256 to 10000.

## [4.1.2] - 2021-10-03

Regular maintenance.

## [4.1.1] - 2021-09-13

More optimization for all base-n codecs. #47

Added codecs for base-16, base-32 and base-36 in upper case.

Added more test cases for base-n codecs.

## [4.1.0] - 2021-09-04

Finish adding OSGi entries to Manifest.MF. #46

Module and bundle names are the same as the root package name.

The OSGi symbolic name is the same as the JPMS module name: `com.github.f4b6a3.uuid`.

## [4.0.2] - 2021-08-29

Add another OSGi entry to Manifest.MF to require Java 8 #46

## [4.0.1] - 2021-08-29

Add OSGi entries to Manifest.MF #46

## [4.0.0] - 2021-08-14

The code was restructured. This version contains has breaking changes.

Most people will not be affected by the changes because they only use the static factories of the `UuidCreator` facade.

List of changes:

-   The strategy interfaces was replaced by JDK functional interfaces.
-   The subclasses of `AbstractUuidCreator` was replaced by subclasses of `UuidFactory`.
-   The new time-based factories can be instantiated using the Builder pattern.
-   The abstract time-based factory is ready to implement [new proposed versions](https://github.com/uuid6/uuid6-ietf-draft) of UUIDs.
-   The `README.md` was simplified. Most of it's contents was moved to [Github Wiki](https://github.com/f4b6a3/uuid-creator/wiki).

### Added

-   Added `ClockSeqFunction`
-   Added `NodeIdFunction`
-   Added `RandomFunction`
-   Added `TimeFunction`
-   Added `DefaultClockSeqFunction`
-   Added `DefaultTimeFunction`
-   Added `DefaultRandomFunction`
-   Added `DefaultNodeIdFunction`
-   Added `MacNodeIdFunction`
-   Added `HashNodeIdFunction`
-   Added `RandomNodeIdFunction`
-   Added `CHANGELOG.md`
-   Added benchmark code

### Changed

-   Modified `UuidCreator`
-   Modified `UuidTime`
-   Modified `UuidUtil`
-   Modified `README.md`

### Renamed

-   Renamed `AbstractUuidCreator` to `UuidFactory`
-   Renamed `NoArgumentsUuidCreator` to `NoArgsFactory`
-   Renamed `AbstractNameBasedUuidCreator` to `AbstNameBasedFactory`
-   Renamed `AbstractRandomBasedUuidCreator` to `AbstRandomBasedFactory`
-   Renamed `AbstractTimeBasedUuidCreator` to `AbstTimeBasedFactory`
-   Renamed `NameBasedMd5UuidCreator` to `NameBasedMd5Factory`
-   Renamed `NameBasedSha1UuidCreator` to `NameBasedSha1Factory`
-   Renamed `RandomBasedUuidCreator` to `RandomBasedFactory`
-   Renamed `TimeBasedUuidCreator` to `TimeBasedFactory`
-   Renamed `TimeOrderedUuidCreator` to `TimeOrderedFactory`
-   Renamed `DceSecurityUuidCreator` to `DceSecurityFactory`
-   Renamed `PrefixCombCreator` to `PrefixCombFactory`
-   Renamed `ShortPrefixCombCreator` to `ShortPrefixCombFactory`
-   Renamed `SuffixCombCreator` to `SuffixCombFactory`
-   Renamed `ShortSuffixCombCreator` to `ShortSuffixCombFactory`

### Removed

-   Removed `ClockSequenceStrategy` // replaced by `ClockSeqFunction`
-   Removed `DefaultClockSequenceStrategy` // replaced by `DefaultClockSecFunction`
-   Removed `TimestampStrategy` // replaced by `TimeFunction`
-   Removed `DefaultTimestampStrategy` // replaced by `DefaultTimeFunction`
-   Removed `RandomStrategy` // replaced by `RandomFunction`
-   Removed `DefaultRandomStrategy` // replaced by `DefaultRandomFunction`
-   Removed `NodeIdentifierStrategy` // replaced by `NodeIdFunction`
-   Removed `DefaultNodeIdentifierStrategy` // replaced by `DefaultNodeIdFunction`
-   Removed `MacNodeIdentifierStrategy` // replaced by `MacNodeIdFunction`
-   Removed `HashNodeIdentifierStrategy` // replaced by `HashNodeIdFunction`
-   Removed `RandomNodeIdentifierStrategy` // replaced by `RandomNodeIdFunction`
-   Removed `FixedTimestampStretegy`
-   Removed `FixedClockSequenceStrategy`
-   Removed `FixedNodeIdentifierStrategy`
-   Removed `ClockSequenceController`
-   Removed `OtherRandomStrategy`
-   Removed `UuidConverter`

## [3.7.3] - 2021-07-04

Added a module name: `com.github.f4b6a3.uuid` for Java 9+. #43

## [3.7.2] - 2021-06-05 

The decoding speed was increased for base-36, base-58, and base-62. Added more tests for base-n codecs. #42

## [3.7.1] - 2021-05-30

More optimizations for all base-n codecs. #41

## [3.7.0] - 2021-05-20

Added new base-n codecs for base-36, base-58 and base-62 #39

Many optimizations for all UUID codecs. #38

## [3.6.0] - 2021-04-08

Added shared instances for UUID codecs. #36

Now the user can choose the algorithm used by SecureRandom by defining a system property or environment variable.  #37

## [3.5.0] - 2021-01-18

Added method `extractInstant()` to extract time from COMB #35

### Added

-   Added `CombUtil.extractPrefix()` // unix time
-   Added `CombUtil.extractSuffix()` // unix time
-   Added `CombUtil.extractPrefixInstant()` // Instant
-   Added `CombUtil.extractSuffixInstant()` // Instant

## [3.4.2] - 2020-12-31

Regular maintenance.

## [3.4.1] - 2020-12-21

Finished codecs for base-n, slug, ncname, uri, etc (complement) #30


## [3.4.0] - 2020-12-20

Added codecs for base-16, base-32 and base-64. #30

Added codecs for UUID Slugs. #30

Added codecs for UUID Names. #31 

Added codecs for UUID URIs #32

List of codecs:

-   `BinaryCodec`:      for byte arrays
-   `StringCodec`:      for canonical strings
-   `SlugCodec`:        for slugs based on UUID
-   `NcnameCodec`:      for NCNames based on UUID
-   `UriCodec`:         for URNs wrapped with java.net.URI
-   `DotNetGuid1Codec`: for dotNet Guids
-   `DotNetGuid4Codec`: for dotNet Guids
-   `TimeOrderedCodec`: for time-ordered UUIDs
-   `BaseNCodec`:       for base-16, base-32 and base-64
    -   `UuidBase16Codec`: base-16
    -   `UuidBase32Codec`: base-32
    -   `UuidBase32CrockfordCodec`: base-32 crockford
    -   `UuidBase64Codec`: base-64
    -   `UuidBase64UrlCodec`: base-64-url

## [3.3.0] - 2020-12-05

Annotate strategy interfaces as functional interfaces #29

## [3.2.3] - 2020-12-04

Added the option "random" to the system property "uuidcreator.node" and
environment variable "UUIDCREATOR_NODE". 

## [3.2.2] - 2020-12-02

Rename the timestamp counter to accumulator #28

## [3.2.1] - 2020-12-02

Rename the timestamp counter to accumulator #28

## [3.2.0] - 2020-11-29

Change the `DefaultTimestampStrategy` #28

## [3.1.3] - 2020-11-16

Regular maintenance.

## [3.1.2] - 2020-11-07

Optimize UUID conversion to and from String.

Now the method UuidConverter#fromString(String) is 5x faster than `UUID#fromString(String)` in JDK 8 and 11.

Now the method UuidConverter#toString(String) is 4x faster than `UUID#toString()` in JDK 8.

## [3.1.1] - 2020-10-31

Change name-based generators to prevent thread contention #26

## [3.1.0] - 2020-10-24

### Added

-   Added `MachineId` // hash-based machine-id (hash of hostname, MAC and IP)
-   Added `SharedRandom` // for classes that need a secure random generator

### Updated
-   Change `DefaultRandomStrategy` // don't use TlsSecureRandom (removed)
-   Change `MacNodeIdentifierStrategy` // don't use NetworkData (removed)
-   Change `HashNodeIdentifierStrategy` // dont't use Fingerprint (removed)
-   Update test case
-   Update README.md
-   Update javadoc
-   Coverage: 85.8%

### Removed
Remove `TlsSecureRandom` // replaced by SharedRandom
Remove `NetworkData` // too much properties
Remove `Fingerprint` // too complex


## [3.0.0] - 2020-11-18

### Added

-   Add test case to `NameBasedMd5UuidCreator` // threads in parallel
-   Add test case to `NameBasedSha1UuidCreator` // threads in parallel

### Updated

-   Change `AbstractNameBasedUuidCreator` // unecessary MessageDigest.reset()
-   Changed `DefaultTimestampStrategy` // wait next millisec if overrun occurs
-   Updated test cases
-   Updated README.md
-   Updated javadoc
-   Test coverage: 87.4%

### Removed

-   Removed UuidCreatorException // used by overrun exception
-   Removed IllegalUuidException // use IllegalArgumentException instead
-   Removed UuidSettings.getNodeIdentifierDeprecated // remove deprecated
-   Renamed UuidSettings to UuidCreatorSettings

[unreleased]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-6.1.1...HEAD
[6.1.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-6.1.0...uuid-creator-6.1.1
[6.1.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-6.0.0...uuid-creator-6.1.0
[6.0.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.7...uuid-creator-6.0.0
[5.3.7]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.6...uuid-creator-5.3.7
[5.3.6]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.5...uuid-creator-5.3.6
[5.3.5]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.4...uuid-creator-5.3.5
[5.3.4]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.3...uuid-creator-5.3.4
[5.3.3]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.2...uuid-creator-5.3.3
[5.3.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.1...uuid-creator-5.3.2
[5.3.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.3.0...uuid-creator-5.3.1
[5.3.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.2.1...uuid-creator-5.3.0
[5.2.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.2.0...uuid-creator-5.2.1
[5.2.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.1.2...uuid-creator-5.2.0
[5.1.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.1.1...uuid-creator-5.1.2
[5.1.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.1.0...uuid-creator-5.1.1
[5.1.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-5.0.0...uuid-creator-5.1.0
[5.0.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.6.1...uuid-creator-5.0.0
[4.6.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.6.0...uuid-creator-4.6.1
[4.6.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.5.0...uuid-creator-4.6.0
[4.5.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.4.1...uuid-creator-4.5.0
[4.4.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.4.0...uuid-creator-4.4.1
[4.4.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.3.2...uuid-creator-4.4.0
[4.3.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.3.1...uuid-creator-4.3.2
[4.3.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.3.0...uuid-creator-4.3.1
[4.3.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.2.1...uuid-creator-4.3.0
[4.2.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.2.0...uuid-creator-4.2.1
[4.2.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.4...uuid-creator-4.2.0
[4.1.4]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.3...uuid-creator-4.1.4
[4.1.3]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.2...uuid-creator-4.1.3
[4.1.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.1...uuid-creator-4.1.2
[4.1.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.0...uuid-creator-4.1.1
[4.1.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.0.2...uuid-creator-4.1.0
[4.0.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.0.1...uuid-creator-4.0.2
[4.0.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.0.0...uuid-creator-4.0.1
[4.0.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.7.3...uuid-creator-4.0.0
[3.7.3]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.7.2...uuid-creator-3.7.3
[3.7.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.7.1...uuid-creator-3.7.2
[3.7.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.7.0...uuid-creator-3.7.1
[3.7.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.6.0...uuid-creator-3.7.0
[3.6.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.5.0...uuid-creator-3.6.0
[3.5.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.4.2...uuid-creator-3.5.0
[3.4.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.4.1...uuid-creator-3.4.2
[3.4.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.4.0...uuid-creator-3.4.1
[3.4.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.3.0...uuid-creator-3.4.0
[3.3.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.2.3...uuid-creator-3.3.0
[3.2.3]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.2.2...uuid-creator-3.2.3
[3.2.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.2.1...uuid-creator-3.2.2
[3.2.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.2.0...uuid-creator-3.2.1
[3.2.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.1.3...uuid-creator-3.2.0
[3.1.3]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.1.2...uuid-creator-3.1.3
[3.1.2]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.1.1...uuid-creator-3.1.2
[3.1.1]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.1.0...uuid-creator-3.1.1
[3.1.0]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-3.0.0...uuid-creator-3.1.0
[3.0.0]: https://github.com/f4b6a3/uuid-creator/releases/tag/uuid-creator-3.0.0

