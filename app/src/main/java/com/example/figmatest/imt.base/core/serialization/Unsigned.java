package com.example.figmatest.imt.base.core.serialization;

/**
 * Utility class to map, check and truncate to unsigned values used in DFF.
 * Since java does not provide unsigned primitive types, each unsigned
 * value is promoted up to the next bigger primitive data type.
 * Created by mguntli on 29.09.2015.
 */
public class Unsigned {
    public static final short UINT8_MIN = 0;
    public static final short UINT8_MAX = 255;
    public static final int UINT16_MIN = 0;
    public static final int UINT16_MAX = 65535;
    public static final int UINT32_MIN = 0;
    public static final long UINT32_MAX = 4294967295L;

    /**
     * Private constructor because it contains only static functions.
     */
    private Unsigned() {
    }

    /**
     * Check if the given value is out of range for an unsigned 8bit value.
     * @param uint8Value unsigned 8bit value represented as short
     * @return true if uint8Value is out of range, false otherwise
     */
    public static boolean isOutOfRangeRangeUInt8(short uint8Value) {
        return ((uint8Value < UINT8_MIN) || (uint8Value > UINT8_MAX));
    }

    /**
     * Check if the given value is out of range for an unsigned 16bit value.
     * @param uint16Value unsigned 16bit value represented as int
     * @return true if uint16Value is out of range, false otherwise
     */
    public static boolean isOutOfRangeRangeUInt16(int uint16Value) {
        return ((uint16Value < UINT16_MIN) || (uint16Value > UINT16_MAX));
    }

    /**
     * Check if the given value is out of range for an unsigned 32bit value.
     * @param uint32Value unsigned 32bit value represented as long
     * @return true if uint32Value is out of range, false otherwise
     */
    public static boolean isOutOfRangeRangeUInt32(long uint32Value) {
        return ((uint32Value < UINT32_MIN) || (uint32Value > UINT32_MAX));
    }

    /**
     * Truncate the given value to the range of an unsigned 8bit value.
     * @param value value which should be truncated to fit within an unsigned 8bit value
     * @return unsigned 8bit value represented as short
     */
    public static short truncateToRangeUInt8(short value) {
        if (value < UINT8_MIN) {
            value = UINT8_MIN;
        } else if (value > UINT8_MAX) {
            value = UINT8_MAX;
        }
        return value;
    }

    /**
     * Truncate the given value to the range of an unsigned 16bit value.
     * @param value value which should be truncated to fit within an unsigned 16bit value
     * @return unsigned 16bit value represented as int
     */
    public static int truncateToRangeUInt16(int value) {
        if (value < UINT16_MIN) {
            value = UINT16_MIN;
        } else if (value > UINT16_MAX) {
            value = UINT16_MAX;
        }
        return value;
    }

    /**
     * Truncate the given value to the range of an unsigned 32bit value.
     * @param value value value which should be truncated to fit within an unsigned 32bit value
     * @return unsigned 32bit value represented as long
     */
    public static long truncateToRangeUInt32(long value) {
        if (value < UINT32_MIN) {
            value = UINT32_MIN;
        } else if (value > UINT32_MAX) {
            value = UINT32_MAX;
        }
        return value;
    }
}
