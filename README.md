
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

* Timestamp UUID:  6a012540-4d0c-11e8-b90b-6f38ff517141
* Sequential UUID: 1e84d0c6-a012-4540-b90c-6f38ff517141

Note that the byte order of the first three fields are different in both examples. But both have the same bytes of a single instant, that is 2018-05-01T06:53:54.820640011Z.

Now see the three fields that contain timestamp information separated from the other fields. The lowest bytes of the timestamp are highlighted (the "V" is for version).

* Timestamp UUID:  **6a012540**-4d0c-V1e8
* Sequential UUID: 1e84d0c6-**a012**-V**540**

In short, that is the is the difference between both.
