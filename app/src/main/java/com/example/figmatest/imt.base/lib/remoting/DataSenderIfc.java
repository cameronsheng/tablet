package com.example.figmatest.imt.base.lib.remoting;

import java.nio.ByteBuffer;

/**
 * Defines a common interface for sending data over the remote service.
 * Created by mguntli on 09.10.2015.
 */
public interface DataSenderIfc {

    /**
     * Send the content of the given byte buffer.
     * @param sendBuffer the buffer to send (position pointing to the end of the data, limit is the maximum frame capacity).
     * @return true on success, false otherwise
     */
    boolean sendData(ByteBuffer sendBuffer);
}
