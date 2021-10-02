# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

Nothing unreleased.

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

### Remoded
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

### Remoded

-   Removed UuidCreatorException // used by overrun exception
-   Removed IllegalUuidException // use IllegalArgumentException instead
-   Removed UuidSettings.getNodeIdentifierDeprecated // remove deprecated
-   Renamed UuidSettings to UuidCreatorSettings

[unreleased]: https://github.com/f4b6a3/uuid-creator/compare/uuid-creator-4.1.1...HEAD
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

