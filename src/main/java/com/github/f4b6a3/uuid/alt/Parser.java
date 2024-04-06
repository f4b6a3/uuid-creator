package com.github.f4b6a3.uuid.alt;

import java.util.Arrays;

import static com.github.f4b6a3.uuid.alt.GUID.GUID_CHARS;

public final class Parser {

    private static final byte[] VALUES = new byte[256];
    static {
        Arrays.fill(VALUES, (byte) -1);
        VALUES['0'] = 0;
        VALUES['1'] = 1;
        VALUES['2'] = 2;
        VALUES['3'] = 3;
        VALUES['4'] = 4;
        VALUES['5'] = 5;
        VALUES['6'] = 6;
        VALUES['7'] = 7;
        VALUES['8'] = 8;
        VALUES['9'] = 9;
        VALUES['A'] = 10;
        VALUES['B'] = 11;
        VALUES['C'] = 12;
        VALUES['D'] = 13;
        VALUES['E'] = 14;
        VALUES['F'] = 15;
        VALUES['a'] = 10;
        VALUES['b'] = 11;
        VALUES['c'] = 12;
        VALUES['d'] = 13;
        VALUES['e'] = 14;
        VALUES['f'] = 15;
    }

    public static GUID parse(final String string) {

        if (!valid(string)) {
            throw new IllegalArgumentException("Invalid GUID string: " + string);
        }

        long msb = 0;
        long lsb = 0;

        // UUID string WITH hyphen
        msb |= (long) VALUES[string.charAt(0x00)] << 60;
        msb |= (long) VALUES[string.charAt(0x01)] << 56;
        msb |= (long) VALUES[string.charAt(0x02)] << 52;
        msb |= (long) VALUES[string.charAt(0x03)] << 48;
        msb |= (long) VALUES[string.charAt(0x04)] << 44;
        msb |= (long) VALUES[string.charAt(0x05)] << 40;
        msb |= (long) VALUES[string.charAt(0x06)] << 36;
        msb |= (long) VALUES[string.charAt(0x07)] << 32;
        // input[8] = '-'
        msb |= (long) VALUES[string.charAt(0x09)] << 28;
        msb |= (long) VALUES[string.charAt(0x0a)] << 24;
        msb |= (long) VALUES[string.charAt(0x0b)] << 20;
        msb |= (long) VALUES[string.charAt(0x0c)] << 16;
        // input[13] = '-'
        msb |= (long) VALUES[string.charAt(0x0e)] << 12;
        msb |= (long) VALUES[string.charAt(0x0f)] << 8;
        msb |= (long) VALUES[string.charAt(0x10)] << 4;
        msb |= (long) VALUES[string.charAt(0x11)];
        // input[18] = '-'
        lsb |= (long) VALUES[string.charAt(0x13)] << 60;
        lsb |= (long) VALUES[string.charAt(0x14)] << 56;
        lsb |= (long) VALUES[string.charAt(0x15)] << 52;
        lsb |= (long) VALUES[string.charAt(0x16)] << 48;
        // input[23] = '-'
        lsb |= (long) VALUES[string.charAt(0x18)] << 44;
        lsb |= (long) VALUES[string.charAt(0x19)] << 40;
        lsb |= (long) VALUES[string.charAt(0x1a)] << 36;
        lsb |= (long) VALUES[string.charAt(0x1b)] << 32;
        lsb |= (long) VALUES[string.charAt(0x1c)] << 28;
        lsb |= (long) VALUES[string.charAt(0x1d)] << 24;
        lsb |= (long) VALUES[string.charAt(0x1e)] << 20;
        lsb |= (long) VALUES[string.charAt(0x1f)] << 16;
        lsb |= (long) VALUES[string.charAt(0x20)] << 12;
        lsb |= (long) VALUES[string.charAt(0x21)] << 8;
        lsb |= (long) VALUES[string.charAt(0x22)] << 4;
        lsb |= (long) VALUES[string.charAt(0x23)];

        return new GUID(msb, lsb);
    }

    private static final int[] DASH_POSITIONS = {8, 13, 18, 23};
    private static final int DASH_REQUIRED_COUNT = 4;

    private static boolean isCharValid(char chr) {
        return chr >= 0 && chr < VALUES.length && VALUES[chr] >= 0;
    }
    private static boolean isDashPosition(int index) {
        for (int dashPosition : DASH_POSITIONS) {
            if (index == dashPosition) {
                return true;
            }
        }
        return false;
    }

    public static boolean valid(final String guid) {
        if (guid == null || guid.length() != GUID_CHARS) {
            return false; // null or wrong length
        }

        int dashesCount = 0;
        for (int i = 0; i < GUID_CHARS; i++) {
            char chr = guid.charAt(i);
            if (isCharValid(chr)) {
                continue; // character is valid
            }
            if (isDashPosition(i) && chr == '-') {
                dashesCount++;
                continue;
            }
            return false; // invalid character
        }

        return dashesCount == DASH_REQUIRED_COUNT;
    }
}