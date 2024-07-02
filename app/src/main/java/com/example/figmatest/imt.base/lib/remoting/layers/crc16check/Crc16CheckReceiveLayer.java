package com.example.figmatest.imt.base.lib.remoting.layers.crc16check;

import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc;

import java.nio.ByteBuffer;

/**
 * This layer is responsible to check and remove the two byte Crc16 checksum from the input byte stream.
 * Created by mguntli on 14.10.2015.
 */
public class Crc16CheckReceiveLayer implements DataReceiverIfc {

    private DataReceiverIfc upperLevelReceiver;
    private short initialCrcValue = 0;
    private Crc16CheckFailedCallbackIfc crc16CheckFailedCallback;
    private static final Crc16 crc = new Crc16(Crc16CheckLayerConstants.GENERATOR_POLYNOM);

    /**
     * Default Constructor.
     * Note: Upper level receiver has to be set in order to forward data
     */
    public Crc16CheckReceiveLayer() {
    }

    /**
     * Constructor with upper level receiver.
     * @param upperLevelReceiver layer receiving the verified data (without the checksum)
     */
    public Crc16CheckReceiveLayer(DataReceiverIfc upperLevelReceiver) {
        setUpperLevelReceiver(upperLevelReceiver);
    }

    /**
     * Constructor with upper level receiver and callback when a crc16 checksum error is detected.
     * @param upperLevelReceiver layer receiving the verified data (without the checksum)
     * @param crc16CheckFailedCallback callback when a crc16 checksum error is detected
     */
    public Crc16CheckReceiveLayer(DataReceiverIfc upperLevelReceiver, Crc16CheckFailedCallbackIfc crc16CheckFailedCallback) {
        setUpperLevelReceiver(upperLevelReceiver);
        setCrc16CheckFailedCallback(crc16CheckFailedCallback);
    }

    @Override
    public void onDataReceived(ByteBuffer receiveBuffer) {
        if (upperLevelReceiver == null || receiveBuffer == null || receiveBuffer.limit() <= 0) {
            return;
        }

        final int dataLengthWithoutCheckSum = receiveBuffer.limit() - Crc16CheckLayerConstants.NUMBER_OF_CRC_BYTES;
        final short transferredChecksum = readCheckSumFromBuffer(receiveBuffer, dataLengthWithoutCheckSum);
        final short calculatedChecksum = crc.crc16(receiveBuffer.array(), dataLengthWithoutCheckSum, initialCrcValue, (short)0, false);

        if (transferredChecksum == calculatedChecksum) {
            // set the new limit so that the next layers knows how far it can read
            receiveBuffer.limit(dataLengthWithoutCheckSum);
            upperLevelReceiver.onDataReceived(receiveBuffer);
        } else if (crc16CheckFailedCallback != null) {
            crc16CheckFailedCallback.onCrc16CheckFailed(receiveBuffer);
        }
        // else silently ignore the error
    }

    /**
     * Set the receiver interface.
     * @param upperLevelReceiver layer receiving the verified data (without the checksum)
     */
    public void setUpperLevelReceiver(DataReceiverIfc upperLevelReceiver) {
        this.upperLevelReceiver = upperLevelReceiver;
    }

    /**
     * Set the callback when a Crc16 check failed.
     * @param crc16CheckFailedCallback callback when a crc16 checksum error is detected
     */
    public void setCrc16CheckFailedCallback(Crc16CheckFailedCallbackIfc crc16CheckFailedCallback) {
        this.crc16CheckFailedCallback = crc16CheckFailedCallback;
    }

    /**
     * Set the initial CRC value to increase robustness of CRC-16 checksum.
     * @param initialCrcValue Older implementations used a default value of 0, which is the least robust.
     *                        Recommendation: use 0xFFFF as initial value
     */
    public void setInitialCrcValue(short initialCrcValue) {
        this.initialCrcValue = initialCrcValue;
    }

    private short readCheckSumFromBuffer(ByteBuffer receiveBuffer, final int dataLengthWithoutCheckSum) {
        receiveBuffer.position(dataLengthWithoutCheckSum);
        final short transferredChecksum = receiveBuffer.getShort();
        receiveBuffer.rewind();
        return transferredChecksum;
    }
}
