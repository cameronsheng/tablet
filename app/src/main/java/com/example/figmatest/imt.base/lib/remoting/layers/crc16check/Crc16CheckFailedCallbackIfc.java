package com.example.figmatest.imt.base.lib.remoting.layers.crc16check;

import java.nio.ByteBuffer;

/**
 * Callback to handle a failed Crc16 check
 * Created by mguntli on 14.10.2015.
 */
public interface Crc16CheckFailedCallbackIfc {

    /**
     * Callback function when Crc16 check failed.
     * @param receiveBuffer the data stream including the original CRC checksum at the end
     */
    void onCrc16CheckFailed(ByteBuffer receiveBuffer);
}
