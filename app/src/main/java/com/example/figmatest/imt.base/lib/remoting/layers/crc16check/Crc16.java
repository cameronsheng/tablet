package com.example.figmatest.imt.base.lib.remoting.layers.crc16check;

/**
 * Cyclic redundancy check with two bytes (Crc16)
 * Created by mguntli on 14.10.2015.
 */
class Crc16 {

    private final short[] lookupTable;

    /**
     * Constructor which creates a lookup table based on the given generator polynomial.
     * @param generatorPolynomial This polynomial becomes the divisor in a polynomial long division
     */
    Crc16(short generatorPolynomial) {
        lookupTable = calculateCrc16LookupTable(generatorPolynomial);
    }

    /**
     * Calculates a two byte Crc16 checksum.
     * @param buffer buffer to calculate the crc of
     * @param size number of bytes to calculate from data
     * @param checksumInit seed value for the crc calculation
     * @return the calculated crc checksum
     */
    short crc16(byte[] buffer, int size, short checksumInit) {
        short crc = checksumInit;
        for (int i = 0; i < size; i++) {
            // mask with 0xFF because java does not support unsigned types
            crc = (short)(lookupTable[((crc >> 8) & 0x00FF)] ^ (crc << 8) ^ (buffer[i] & 0xFF));
        }
        return crc;
    }

    /**
     * Calculates a two byte Crc16 checksum. The "Rocksoft^tm Model CRC Algorithm" (see http://www.geocities.com/SiliconValley/Pines/8659/crc.htm)
     * @param buffer buffer to calculate the crc of
     * @param dataSize number of bytes to calculate from data
     * @param checksumInit seed value for the crc calculation
     * @return the calculated crc checksum
     */
    short crc16(byte[] buffer, int dataSize, short checksumInit, short usXorOut, boolean isReflected) {
        short remainder = checksumInit;
        int data;

        if (isReflected) {
            // calculate crc
            for (int i = 0; i < dataSize; i++) {
                data = (byte)(reflectByte(buffer[i]) ^ (remainder >> 8));
                remainder = (short)(lookupTable[data] ^ (remainder << 8));
            }
            return (short)(reflectShort(remainder) ^ usXorOut);
        } else {
            // calculate crc
            for (int i = 0; i < dataSize; i++) {
                data = ((buffer[i] & 0x00FF) ^ ((remainder >> 8) & 0x00FF));
                remainder = (short)(lookupTable[data] ^ (remainder << 8));
            }
            return (short)(remainder ^ usXorOut);
        }
    }

    private byte reflectByte(byte data) {
        byte reflection = 0x00;
        for (byte bit = 0; bit < 8; bit++) {
            if ((data & 0x01) != 0 ) {
                //lint -e{701} Shift left of signed quantity (int)
                //lint -e{734} Loss of precision (assignment) (31 bits to 16 bits)
                reflection |= (1 << (7 - bit));
            }
            data = (byte)(data >> 1);
        }
        return reflection;
    }

    private short reflectShort(short data) {
        short reflection = 0x0000;
        for (byte bit = 0; bit < 16; bit++) {
            if ((data & 0x0001) != 0) {
                //lint -e{701} Shift left of signed quantity (int)
                //lint -e{734} Loss of precision (assignment) (31 bits to 16 bits)
                reflection |= (1 << (15 - bit));
            }
            data = (short)(data >> 1);
        }
        return reflection;
    }

    /**
     * Calculate the Crc16 lookup table based on the generator polynomial.
     * @param generatorPolynomial This polynomial becomes the divisor in a polynomial long division
     * @return filled lookup table
     */
    private static short[] calculateCrc16LookupTable(short generatorPolynomial) {
        short[] crcTable = new short[256];
        short remainder;
        int dividend;
        byte bit;

        // Compute the remainder of each possible dividend.
        for (dividend = 0; dividend < 256; ++dividend) {
            // Start with the dividend followed by zeros.
            remainder = (short)(dividend << 8);
            // Perform modulo-2 division, a bit at a time.
            for (bit = 8; bit > 0; --bit) {
                // Try to divide the current data bit.
                if ((remainder & 0x8000) != 0) {
                    remainder = (short)((remainder << 1) ^ generatorPolynomial);
                } else {
                    remainder = (short)(remainder << 1);
                }
            }
            // Store the result into the table.
            crcTable[dividend] = remainder;
        }
        return crcTable;
    }
}
