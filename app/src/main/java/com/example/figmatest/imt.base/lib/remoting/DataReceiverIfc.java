package com.example.figmatest.imt.base.lib.remoting;

import java.nio.ByteBuffer;

/**
 * Defines a common interface for receiving data over the remote service.
 * Created by mguntli on 09.10.2015.
 */
public interface DataReceiverIfc {

    /**
     * Callback when data is received.
     * @param receiveBuffer the buffer to read (limit pointing to the end of the data).
     */
    void onDataReceived(ByteBuffer receiveBuffer);
}
