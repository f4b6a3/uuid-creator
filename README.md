
### Small UUID Generator

It can generate these kind of UUIDs:

* Version 1:
  - Timestamp;
  - Timestamp with no MAC adress (private);
  
* Version 4:
  - Random;
  - Sequential;
  - Sequential with no MAC adress (private);
 
The *Sequential UUID* is a modified implementation of Timestamp UUID. All the octets deticated to timestamp in the Sequential UUID are in the _natural_ order. In the standard Timestamp UUID the octets of timetamp are arranged in an inversed order (RFC-4122).

