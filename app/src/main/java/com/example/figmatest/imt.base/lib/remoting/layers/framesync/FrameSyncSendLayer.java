package com.example.figmatest.imt.base.lib.remoting.layers.framesync;

import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc;

import java.nio.ByteBuffer;

/**
 * This layer is responsible for packing valid DataFrames into the output byte stream with frame end and escaping control.
 * If a reserved byte is contained in the data stream, a special character (ESCAPE_CHARACTER) will be placed in front of that byte.
 * At the end of the frame, the FRAME_END_CHARACTER will be placed.
 * Created by mguntli on 13.10.2015.
 */
public class FrameSyncSendLayer implements DataSenderIfc {

    private ByteBuffer byteBuffer;
    private DataSenderIfc lowerLevelSender;

    /**
     * Constructor.
     * @param maxTransferSizeBytes the maximum number of bytes a message can contain
     */
    public FrameSyncSendLayer(final int maxTransferSizeBytes) {
        initializeByteBuffer(maxTransferSizeBytes);
    }

    /**
     * Constructor with lower level sender.
     * @param maxTransferSizeBytes the maximum number of bytes a message can contain
     * @param lowerLevelSender layer sending the data (including the escaping and frame end characters)
     */
    public FrameSyncSendLayer(final int maxTransferSizeBytes, DataSenderIfc lowerLevelSender) {
        initializeByteBuffer(maxTransferSizeBytes);
        setLowerLevelSender(lowerLevelSender);
    }

    /**
     * Set the sender interface.
     * @param lowerLevelSender layer sending the data (including the escaping and frame end characters)
     */
    public void setLowerLevelSender(DataSenderIfc lowerLevelSender) {
        this.lowerLevelSender = lowerLevelSender;
    }

    @Override
    public boolean sendData(ByteBuffer sendBuffer) {
        if (lowerLevelSender == null || sendBuffer == null) {
            return false;
        }

        byteBuffer.rewind();
        final int dataLength = sendBuffer.position();
        for (int i = 0; i < dataLength; i++) {
            byte character = sendBuffer.get(i);
            if (isEscapingRequired(character)) {
                byteBuffer.put(FrameSyncLayerConstants.ESCAPE_CHARACTER);
            }
            byteBuffer.put(character);
        }
        byteBuffer.put(FrameSyncLayerConstants.FRAME_END_CHARACTER);
        return lowerLevelSender.sendData(byteBuffer);
    }

    private boolean isEscapingRequired(byte ch) {
        return ch == FrameSyncLayerConstants.ESCAPE_CHARACTER || ch == FrameSyncLayerConstants.FRAME_END_CHARACTER;
    }

    private void initializeByteBuffer(final int maxTransferSizeBytes) {
        byteBuffer = ByteBuffer.allocate(maxTransferSizeBytes);
    }
}
