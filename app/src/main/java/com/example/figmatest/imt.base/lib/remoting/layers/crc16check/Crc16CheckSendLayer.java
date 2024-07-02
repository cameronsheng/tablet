package com.example.figmatest.imt.base.lib.remoting.layers.crc16check;

import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc;

import java.nio.ByteBuffer;

/**
 * This layer is responsible for adding the two byte Crc16 checksum into the output byte stream.
 * Created by mguntli on 14.10.2015.
 */
public class Crc16CheckSendLayer implements DataSenderIfc {

    private DataSenderIfc lowerLevelSender;
    private short initialCrcValue = 0;
    private static final Crc16 crc = new Crc16(Crc16CheckLayerConstants.GENERATOR_POLYNOM);

    /**
     * Default Constructor.
     * Note: Lower level sender has to be set in order to forward data
     */
    public Crc16CheckSendLayer() {
    }

    /**
     * Constructor with lower level sender.
     * @param lowerLevelSender layer sending the data (including the checksum)
     */
    public Crc16CheckSendLayer(DataSenderIfc lowerLevelSender) {
        setLowerLevelSender(lowerLevelSender);
    }

    @Override
    public boolean sendData(ByteBuffer sendBuffer) {
        if (lowerLevelSender == null || sendBuffer == null) {
            return false;
        }

        appendCrc16Checksum(sendBuffer);
        return lowerLevelSender.sendData(sendBuffer);
    }

    /**
     * Set the sender interface.
     * @param lowerLevelSender layer sending the data (including the checksum)
     */
    public void setLowerLevelSender(DataSenderIfc lowerLevelSender) {
        this.lowerLevelSender = lowerLevelSender;
    }

    /**
     * Set the initial CRC value to increase robustness of CRC-16 checksum.
     * @param initialCrcValue Older implementations used a default value of 0, which is the least robust.
     *                        Recommendation: use 0xFFFF as initial value
     */
    public void setInitialCrcValue(short initialCrcValue) {
        this.initialCrcValue = initialCrcValue;
    }

    private void appendCrc16Checksum(ByteBuffer sendBuffer) {
        final short calculatedChecksum = crc.crc16(sendBuffer.array(), sendBuffer.position(), initialCrcValue, (short)0, false);
        sendBuffer.putShort(calculatedChecksum);
    }
}
