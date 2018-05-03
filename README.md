
UUID Generator
======================================================

Summary
------------------------------------------------------

It can generate these types of UUIDs:

* _Random_: the pseudo-randomly generated version;
* _Timestamp:_ the time-based version;
* _Timestamp Private:_ the time-based version without hardware address;
* _Sequential:_ a modified time-based version;
* _Sequential Private:_ a modified time-based version without hardware address;

Differences between Timestamp UUID and Sequential UUID
------------------------------------------------------

UUID Sequential is a modified implementation of the Timestamp UUID. All octets dedicated to the timestamp in the Sequential UUID are in the natural order. In the default timestamp UUID the timestamp octets are arranged differently (RFC-4122, section 4.2).

The UUID has this following structure:

`
TTTTTTTT-TTTT-VTTT-RSS-MMMMMMMMMMMM
`

The dashes separate the UUID in parts called "fields". The first tree fields contain timestamp bytes. The forth field contains a clock sequence number generated to avoid duplications. And the last field contains bytes of the machine hardware, but it is generally desired to fill this field with random bytes instead of the actual MAC.

In Timestamp UUID the bytes are arranged in this way:

* `TTTTTTTT`: this field contains the lowest bytes of the timestamp;
* `TTTT`: this field contains the middle bytes of the timestamp;
* `VTTT`: in this field, "V" is the version (always 1) and the "TTT" are the highest bytes of timestamp;
* `RSS`: in this field, "R" is the variant and "SSS" are a clock sequence;
* `MMMMMMMMMMMM`: this field contains the hardware address or node (MAC)

In Sequential UUID the bytes are arranged in this way:

* `TTTTTTTT`: this field contains the **highest** bytes of the timestamp;
* `TTTT`: this field contains the middle bytes of the timestamp;
* `VTTT`: in this field, "V" is the version (always 4) and the "TTT" are the **lowest** bytes of timestamp;
* `RSS`: in this field, "R" is the variant and "SSS" are a clock sequence;
* `MMMMMMMMMMMM`: this field contains the hardware address or node (MAC)

To understand the difference between Timestamp UUID and Sequential UUID, look thise two practical examples of UUID generated at the same instant.

* Timestamp UUID:  79592ca7-4d7f-11e8-b946-3bababbf5f8b
* Sequential UUID: 1e84d7f7-9592-4ca7-b947-3bababbf5f8b

Note that the byte order of the first three fields are different in both examples. But both have the same bytes of a single instant, that is 2018-05-01T20:37:32.687274310Z.

Now see the three fields that contain timestamp information separated from the other fields. The lowest bytes of the timestamp are highlighted (the "V" is for version).

* Timestamp UUID:  **79592ca7**4d7fV1e8
* Sequential UUID: 1e84d7f**79592**V**ca7**

In short, that is the is the difference between both.

Timings compared to java.util.UUID
------------------------------------------------------

A simple time measurement was done to test how long each methods take to generate 100,000 (a hundred thousand) UUIDs. These are the average results:

* java.util.UUID.randomUUID(): 47 ms
* UUIDGenerator.getRandomUUID(): 34 ms
* UUIDGenerator.getTimestampPrivateUUID(): 15 ms
* UUIDGenerator.getSequentialPrivateUUID(): 15 ms

The method getTimestampPrivateUUID() uses SecureRandom (java.security.SecureRandom).

The machine used to do this test was an Intel i5-3330 with 16GB RAM.

