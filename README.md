
UUID Generator
======================================================

Summary
------------------------------------------------------

It can generate these types of UUIDs:

- Random;
- _Timestamp:_ time-based identifier;
- _Timestamp Private:_ time-based identifier without hardware address;
- _Sequential:_ modified time-based identifier;
- _Sequential Private:_ modified time-based identifier without hardware address;

Differences between Timestamp UUID and Sequential UUID
------------------------------------------------------

UUID Sequential is a modified implementation of the Timestamp UUID. All octets dedicated to the timestamp in the Sequential UUID are in the natural order. In the default timestamp UUID the timestamp octets are arranged differently (RFC-4122, section 4.2).

The UUID has this following structure:

`
TTTTTTTT-TTTT-VTTT-RSS-MMMMMMMMMMMM
`

The dashes separate the UUID in parts called "fields". The first tree fields contain timestamp bytes. The forth field contains a clock sequence number generated to avoid duplications. And the last field contains bytes of the machine hardware, but it is generally desired to fill this field with random bytes instead of the actual MAC.

In Timestamp UUID the bytes are arranged in this way:

- TTTTTTTT: this field contains the lowest bytes of the timestamp;
- TTTT: this field contains the middle bytes of the timestamp;
- VTTT: in this field, "V" is the version (always 1) and the "TTT" are the highest bytes of timestamp;
- RSS: in this field, "R" is the variant and "SSS" are a clock sequence;
- MMMMMMMMMMMM: this field contains the hardware address or node (MAC)

In Sequential UUID the bytes are arranged in this way:

- TTTTTTTT: this field contains the **highest** bytes of the timestamp;
- TTTT: this field contains the middle bytes of the timestamp;
- VTTT: in this field, "V" is the version (always 4) and the "TTT" are the **lowest** bytes of timestamp;
- RSS: in this field, "R" is the variant and "SSS" are a clock sequence;
- MMMMMMMMMMMM: this field contains the hardware address or node (MAC)

To understand the difference between Timestamp UUID and Sequential UUID, look thise two practical examples of UUID generated at the same instant.

- Timestamp UUID:  62a9a1f1-4d09-11e8-b674-8416f91b1893
- Sequential UUID: 1e84d096-2a9a-41f1-b675-8416f91b1893

Note that the byte order of the first three fields are different in both examples. But both have the same bytes of a single instant, that is 2018-05-01T06:32:14.012875380Z.

Now see the three fields that contain timestamp information separated from the other fields. The lowest bytes of the timestamp are highlighted (the "V" is for version).

- Timestamp UUID:  **62a9a1f1**4d09V1e8
- Sequential UUID: 1e84d09**62a9aV1f1**

In short, that is the is the difference between both.

