package com.example.figmatest.imt.base.core.serialization;

import android.nfc.Tag;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Deserializes various data types from the given byte buffer.
 * If more bytes are read than defined, an IndexOutOfBoundsException will be thrown.
 * Created by mguntli on 28.09.2015.
 */
public class Deserializer {
    private int bufferSize;
    private byte[] byteBuffer;
    private int bufferPos;

    /**
     * Create a new Deserializer that reads directly from the given byte buffer.
     * @param byteBuffer buffer used to read data from
     * @param bufferSize size available to read, can be smaller than the actual array size
     */
    public Deserializer(final byte[] byteBuffer, int bufferSize) {
        initialize(byteBuffer, bufferSize);
    }

    /**
     * Create a new Deserializer with an empty buffer.
     * Note: call initialize to assign a byte buffer to read from.
     */
    public Deserializer() {
        initialize(null, 0);
    }

    /**
     * Initialize the Deserializer with the given byte buffer and reset read position to the beginning.
     * @param byteBuffer buffer used to read data from
     * @param bufferSize size available to read, can be smaller than the actual array size
     */
    public void initialize(final byte[] byteBuffer, int bufferSize) {
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
     * Reset the read position to the beginning.
     */
    public void resetReadPosition() {
        bufferPos = 0;
    }

    /**
     * Returns the read position within the byte buffer.
     * @return read position within the
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
     * Returns the remaining data to read from the current position until the byteBuffer is empty.
     * @return remaining data to read
     */
    public int getRemainingBytesToRead() {
        return getBufferSize() - getBufferPos();
    }

    /** Read a boolean represented as a 1 byte (8bit) value from the Deserializer. */
    public boolean readBool() {
        return readRawByte() != SerializableIfc.BOOLEAN_VALUE_FALSE;
    }

    /** Read a 1 byte (8bit) signed value from the Deserializer. */
    public byte readInt8() {
        return readRawByte();
    }

    /** Read a 1 byte (8bit) signed value from the Deserializer. */
    public byte[] readInt8Array() {
        byte[] array = null;
        int arraySize = readInt32();
        if (arraySize > 0) {
            array = new byte[arraySize];
            for (int i = 0; i < arraySize; i++) {
                array[i] = readInt8();
            }
        }
        return array;
    }

    /** Read a 1 byte (8bit) unsigned value from the Deserializer. */
    public short readUInt8() {
        return (short)(readRawByte() & 0xff);
    }

    /** Read a 2 byte (16bit) signed value from the Deserializer. */
    public short readInt16() {
        return readRawBigEndian16();
    }

    /** Read a 2 byte (16bit) unsigned value from the Deserializer. */
    public int readUInt16() {
        return readRawBigEndian16() & 0xffff;
    }

    /** Read a 4 byte (32bit) signed value from the Deserializer. */
    public int readInt32() {
        return readRawBigEndian32();
    }

    /** Read a 4 byte (32bit) unsigned value from the Deserializer. */
    public long readUInt32() {
        return (long)readRawBigEndian32() & 0xffffffffL;
    }

    /** Read a 4 byte (32bit) float value from the Deserializer. */
    public float readFloat32() {
        return Float.intBitsToFloat(readRawBigEndian32());
    }

    /** Read a 8 byte (64bit) float value from the Deserializer. */
    public double readFloat64() {
        return Double.longBitsToDouble(readRawBigEndian64());
    }

    /** Reads a string as null terminated ISO8859-1 (1 byte per character) from the Deserializer.
     * @return character sequence as string
     */
    public String readString() {
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        char character = (char)readRawByte();
        while (character != '\0') {
            array.write(character);
            character = (char)readRawByte();
        }
        try {
            return array.toString(StandardCharsets.ISO_8859_1.name());
        } catch (Exception e) {
            Log.d("Deserializer", e.toString());
            return "";
        }
    }

    /**  Returns data out of the Deserializer. */
    public Deserializer read(final SerializableIfc data) {
        data.deserialize(this);
        return this;
    }

    /** Read a single byte from the byteBuffer. */
    private byte readRawByte() {
        if (bufferPos >= bufferSize) {
            Log.e("Deserializer", "Reading further than the defined buffer size in a Deserializer is not allowed!");
            return 0;
        }
        return byteBuffer[bufferPos++];
    }

    private byte readRawByteIgnoreError() {
        if (bufferPos >= bufferSize) {
            return 0;
        }
        return byteBuffer[bufferPos++];
    }

    /** Read a 16-bit big-endian integer from the byteBuffer. */
    public short readRawBigEndian16() {
        final byte b2 = readRawByte();
        final byte b1 = readRawByte();
        return  (short)(((b1 & 0xff)) | ((b2 & 0xff) << 8));
    }

    /** Read a 32-bit big-endian integer from the byteBuffer. */
    public int readRawBigEndian32() {
        final byte b4 = readRawByte();
        final byte b3 = readRawByteIgnoreError();
        final byte b2 = readRawByteIgnoreError();
        final byte b1 = readRawByteIgnoreError();
        return   ((b1 & 0xff)      )
                | ((b2 & 0xff) <<  8)
                | ((b3 & 0xff) << 16)
                | ((b4 & 0xff) << 24);
    }

    /** Read a 64-bit big-endian integer from the stream. */
    public long readRawBigEndian64() {
        final byte b8 = readRawByte();
        final byte b7 = readRawByte();
        final byte b6 = readRawByte();
        final byte b5 = readRawByte();
        final byte b4 = readRawByte();
        final byte b3 = readRawByte();
        final byte b2 = readRawByte();
        final byte b1 = readRawByte();
        return   (((long)b1 & 0xff)      )
                | (((long)b2 & 0xff) <<  8)
                | (((long)b3 & 0xff) << 16)
                | (((long)b4 & 0xff) << 24)
                | (((long)b5 & 0xff) << 32)
                | (((long)b6 & 0xff) << 40)
                | (((long)b7 & 0xff) << 48)
                | (((long)b8 & 0xff) << 56);
    }
}
