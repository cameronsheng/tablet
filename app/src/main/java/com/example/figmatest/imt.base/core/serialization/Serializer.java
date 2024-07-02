package com.example.figmatest.imt.base.core.serialization;

import java.nio.charset.StandardCharsets;

/**
 * Serializes various data types into the given byte buffer.
 * If more bytes are written than defined, an IndexOutOfBoundsException will be thrown.
 * Created by mguntli on 28.09.2015.
 */
public class Serializer {
    private int bufferSize;
    private byte[] byteBuffer;
    private int bufferPos;

    /**
     * Create a new Serializer that writes directly to the given byte buffer.
     * @param byteBuffer byte buffer array to where the data should be written.
     * @param bufferSize size available to write, can be smaller than the actual array size
     */
    public Serializer(byte[] byteBuffer, int bufferSize) {
        initialize(byteBuffer, bufferSize);
    }

    /**
     * Create a new Serializer with an empty buffer.
     * Note: call initialize to assign a byte buffer to read from.
     */
    public Serializer() {
        initialize(null, 0);
    }

    /**
     * Initialize the Serializer with the given byte buffer and reset write position to the beginning.
     * @param byteBuffer byte buffer array to where the data should be written.
     * @param bufferSize size available to write, can be smaller than the actual array size
     */
    public void initialize(byte[] byteBuffer, int bufferSize) {
        if (bufferSize > 0) {
            if (byteBuffer == null) {
                throw new IllegalArgumentException("If defined buffer size is > 0, the byte array has to exist");
            } else if (bufferSize > byteBuffer.length) {
                throw new IllegalArgumentException("Buffer size cannot be larger than the actual size of the array");
            }
        }
        this.byteBuffer = byteBuffer;
        this.bufferSize = bufferSize;
        this.bufferPos = 0;
    }

    /**
     * Reset the write position to the beginning.
     */
    public void resetWritePosition() {
        bufferPos = 0;
    }

    /**
     * Returns the write position within the byte buffer.
     * @return write position
     */
    public int getBufferPos() {
        return bufferPos;
    }

    /**
     * Returns the size of the byte buffer in number of bytes.
     * @return size in number of bytes
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Returns the remaining bytes to write from the current position until the byte buffer is full.
     * @return remaining bytes to write until full
     */
    public int getRemainingBytesToWrite() {
        return getBufferSize() - getBufferPos();
    }

    /** Writes a boolean represented as a 1 byte (8bit) value into the Serializer. */
    public void writeBool(final boolean value) {
        writeRawByte(value ? SerializableIfc.BOOLEAN_VALUE_TRUE : SerializableIfc.BOOLEAN_VALUE_FALSE);
    }

    /** Writes a 1 byte (8bit) signed value into the Serializer. */
    public void writeInt8(final byte value) {
        writeRawByte(value);
    }

    /** Writes a byte (8bit) array into the Serializer. */
    public void writeInt8Array(final byte[] array, int arraySize) {
        int fittingArraySize = getFittingArraySize(arraySize);
        writeInt32(fittingArraySize);
        for (int i = 0; i < fittingArraySize; i++) {
            writeRawByte(array[i]);
        }
    }

    /** Determines the proper array size within the remaining space. */
    private int getFittingArraySize(int arraySize) {
        final int bufferSpace = getBufferSize() - getBufferPos();
        if (bufferSpace > (arraySize + 4)) {
            return arraySize;
        } else {
            return bufferSpace - 4;
        }
    }

    /**
     * Writes a 1 byte (8bit) unsigned value into the Serializer.
     * @param value value to write, will be truncated if range exceeds UINT8_MIN / UINT8_MAX.
     */
    public void writeUInt8(short value) {
        value = Unsigned.truncateToRangeUInt8(value);
        writeRawByte(value);
    }

    /** Writes a 2 byte (16bit) signed value into the Serializer.
     * @param value value to write, will be truncated if range exceeds INT16_MIN / INT16_MAX.
     */
    public void writeInt16(short value) {
        writeRawBigEndian16(value);
    }

    /** Writes a 2 byte (16bit) unsigned value into the Serializer.
     * @param value value to write, will be truncated if range exceeds UINT16_MIN / UINT16_MAX.
     */
    public void writeUInt16(int value) {
        value = Unsigned.truncateToRangeUInt16(value);
        writeRawBigEndian16(value);
    }

    /** Writes a 4 byte (32bit) signed value into the Serializer.
     * @param value value to write, will be truncated if range exceeds INT32_MIN / INT32_MAX.
     */
    public void writeInt32(int value) {
        writeRawBigEndian32(value);
    }

    /** Writes a 4 byte (32bit) unsigned value into the Serializer.
     * @param value value to write, will be truncated if range exceeds UINT32_MIN / UINT32_MAX.
     */
    public void writeUInt32(long value) {
        value = Unsigned.truncateToRangeUInt32(value);
        writeRawBigEndian32((int)value);
    }

    /** Writes a 4 byte (32bit) float value into the Serializer. */
    public void writeFloat32(float value) {
        writeRawBigEndian32(Float.floatToIntBits(value));
    }

    /** Writes a 8 byte (64bit) float value into the Serializer. */
    public void writeFloat64(double value) {
        writeRawBigEndian64(Double.doubleToLongBits(value));
    }

    /** Writes a string as null terminated ISO8859-1 (1 byte per character) into the Serializer.
     * @param string string
     * @param maxByteLength maximum length of resulting character sequence including null termination
     */
    public void writeString(String string, int maxByteLength) {
        if ((string == null) || (maxByteLength == 0) || (string.length() == 0)) {
            writeRawByte('\0');
            return;
        }

        int charactersToWrite;
        if (string.length() < maxByteLength) {
            charactersToWrite = string.length();
        } else {
            // last byte is null termination
            charactersToWrite = maxByteLength - 1;
        }

        byte[] stringBytes = string.getBytes(StandardCharsets.ISO_8859_1);
        for (int i = 0; i < charactersToWrite; i++) {
            writeRawByte(stringBytes[i]);
        }
        writeRawByte('\0');
    }

    /**
     * Puts data into the Serializer.
     * @param data to write into the byte buffer
     * @return this
     */
    public Serializer write(final SerializableIfc data) {
        if (data != null) {
            data.serialize(this);
        }
        return this;
    }

    /** Write a single byte into the byte buffer, represented by an integer value. */
    private void writeRawByte(final int value) {
        writeRawByte((byte) value);
    }

    /** Write a single byte into the byteBuffer. */
    private void writeRawByte(final byte value) {
        if (bufferPos >= bufferSize) {
            throw new IndexOutOfBoundsException("Writing further than the defined buffer size in a Serializer is not allowed!");
        }
        byteBuffer[bufferPos++] = value;
    }

    /** Write a big-endian 16-bit integer into the byteBuffer. */
    private void writeRawBigEndian16(final int value) {
        writeRawByte((value >>  8) & 0xFF);
        writeRawByte((value      ) & 0xFF);
    }

    /** Write a big-endian 32-bit integer into the byteBuffer. */
    private void writeRawBigEndian32(final int value) {
        writeRawByte((value >> 24) & 0xFF);
        writeRawByte((value >> 16) & 0xFF);
        writeRawByte((value >>  8) & 0xFF);
        writeRawByte((value      ) & 0xFF);
    }

    /** Write a big-endian 64-bit integer into the byteBuffer. */
    public void writeRawBigEndian64(final long value) {
        writeRawByte((int)(value >> 56) & 0xFF);
        writeRawByte((int)(value >> 48) & 0xFF);
        writeRawByte((int)(value >> 40) & 0xFF);
        writeRawByte((int)(value >> 32) & 0xFF);
        writeRawByte((int)(value >> 24) & 0xFF);
        writeRawByte((int)(value >> 16) & 0xFF);
        writeRawByte((int)(value >>  8) & 0xFF);
        writeRawByte((int)(value      ) & 0xFF);
    }
}
