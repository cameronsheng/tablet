package com.example.figmatest.imt.base.lib.remoting;


import com.example.figmatest.imt.base.core.serialization.Deserializer;
import com.example.figmatest.imt.base.core.serialization.Serializer;

/**
 * Defines a common interface for remote objects.
 * Created by mguntli on 13.10.2015.
 */
public interface RemoteObjectIfc {

    /**
     * Callback when data has been received.
     * @param deserializer to read remote data from
     */
    void onReceive(Deserializer deserializer);

    /**
     * Callback when data has to be prepared for sending.
     * @param serializer to write remote data into
     */
    void onSend(Serializer serializer);
}
