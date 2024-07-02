package com.example.figmatest.imt.base.core.serialization;

/**
 * Serialization is the process of translating data structures into a binary representation.
 * This process of serializing an object is also called marshalling an object.
 * The opposite operation, extracting a data structure from a series of bytes, is deserialization (which is also called unmarshalling).
 * Created by mguntli on 28.09.2015.
 */
public interface SerializableIfc {
    byte BOOLEAN_VALUE_TRUE = 1;
    byte BOOLEAN_VALUE_FALSE = 0;

    /**
     * Computes the maximum number of bytes required to encode this message.
     */
    int getMaxSerializedSize();

    /**
     * Deserialize this object from the given deserializer.
     * @param deserializer to read the data from
     */
    void deserialize(Deserializer deserializer);

    /**
     * Serialize this object into the given serializer.
     * @param serializer to write the data to
     */
    void serialize(Serializer serializer);

    SerializableIfc deepClone();
}
