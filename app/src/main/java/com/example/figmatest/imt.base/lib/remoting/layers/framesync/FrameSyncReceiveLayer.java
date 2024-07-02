package com.example.figmatest.imt.base.lib.remoting.layers.framesync;

import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc;

import java.nio.ByteBuffer;

/**
 * This layer is responsible for extracting valid DataFrames out of the incoming byte stream with frame start, frame end and escaping control.
 * Created by mguntli on 13.10.2015.
 */
public class FrameSyncReceiveLayer implements DataReceiverIfc {

    private final ByteBuffer receiveFrame;
    private DataReceiverIfc upperLevelReceiver = null;
    private boolean isEscaping = false;
    private boolean hasDetectedInvalidFrame = false;

    /**
     * Default Constructor.
     * Note: Upper level receiver has to be set in order to forward data
     */
    public FrameSyncReceiveLayer(final int maxTransferSizeBytes) {
        this.receiveFrame = ByteBuffer.allocate(maxTransferSizeBytes);
    }

    /**
     * Constructor with upper level receiver.
     * @param upperLevelReceiver layer receiving the escaped and framed data
     */
    public FrameSyncReceiveLayer(final int maxTransferSizeBytes, DataReceiverIfc upperLevelReceiver) {
        this.receiveFrame = ByteBuffer.allocate(maxTransferSizeBytes);
        setUpperLevelReceiver(upperLevelReceiver);
    }

    /**
     * Set the receiver interface.
     * @param upperLevelReceiver layer receiving the verified data (without the checksum)
     */
    public void setUpperLevelReceiver(DataReceiverIfc upperLevelReceiver) {
        this.upperLevelReceiver = upperLevelReceiver;
    }

    @Override
    public void onDataReceived(ByteBuffer receiveBuffer) {
        if (upperLevelReceiver == null || receiveBuffer == null || receiveBuffer.limit() <= 0) {
            return;
        }

        final int bytesToRead = receiveBuffer.limit();
        for (int i = 0; i < bytesToRead; i++) {
            byte character = receiveBuffer.get(i);
            if (!hasDetectedInvalidFrame) {
                handleFrameInput(character);
            } else {
                handleInvalidFrameInput(character);
            }
        }
    }

    private void handleFrameInput(byte dataElement) {
        if (isEscaping) {
            if ((dataElement != FrameSyncLayerConstants.ESCAPE_CHARACTER) && (dataElement != FrameSyncLayerConstants.FRAME_END_CHARACTER)) {
                onFrameInvalidDetected();
            } else {
                appendDataToReceiverBuffer(dataElement);
            }
            isEscaping = false;
        } else {
            if (dataElement == FrameSyncLayerConstants.ESCAPE_CHARACTER) {
                isEscaping = true;
            } else if (dataElement == FrameSyncLayerConstants.FRAME_END_CHARACTER) {
                onFrameEndDetected();
            } else {
                appendDataToReceiverBuffer(dataElement);
            }
        }
    }

    private void handleInvalidFrameInput(byte dataElement) {
        if (!isEscaping && (dataElement == FrameSyncLayerConstants.ESCAPE_CHARACTER)) {
            isEscaping = true;
        } else if (!isEscaping && dataElement == FrameSyncLayerConstants.FRAME_END_CHARACTER) {
            hasDetectedInvalidFrame = false;
        } else {
            isEscaping = false;
        }
    }

    private void appendDataToReceiverBuffer(byte dataElement) {
        if (receiveFrame.hasRemaining()) {
            receiveFrame.put(dataElement);
        }
    }

    private void onFrameEndDetected() {
        if (receiveFrame.position() > 0) {
            // set the new limit so that the next layers knows how far it can read
            receiveFrame.limit(receiveFrame.position());
            upperLevelReceiver.onDataReceived(receiveFrame);
        }
        resetReceiveFrame();
    }

    private void onFrameInvalidDetected() {
        resetReceiveFrame();
        hasDetectedInvalidFrame = true;
    }

    private void resetReceiveFrame() {
        receiveFrame.rewind();
        receiveFrame.limit(receiveFrame.capacity());
    }
}
