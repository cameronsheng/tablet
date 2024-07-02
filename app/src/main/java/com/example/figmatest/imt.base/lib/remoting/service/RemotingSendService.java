package com.example.figmatest.imt.base.lib.remoting.service;

import com.example.figmatest.imt.base.core.serialization.Serializer;
import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc;
import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Sending part of the remoting service.
 * A four byte identifier will be added when the object is sent.
 * Created by mguntli on 16.10.2015.
 */
public class RemotingSendService extends RemotingServiceBase implements RemotingSendServiceIfc {

    // reuse objects to prevent heap trashing
    private ByteBuffer byteBuffer;
    private DataSenderIfc lowerLevelSender = null;
    private Serializer serializer;
    private boolean stop = false;

    /**
     * Constructor without setting the lower level sender.
     * Note: Lower level sender has to be set in order to forward data
     * @param maxTransferSizeBytes the maximum number of bytes a message can contain
     */
    public RemotingSendService(final int maxTransferSizeBytes) {
        initializeByteBuffer(maxTransferSizeBytes);
    }

    /**
     * Constructor with lower level sender.
     * @param maxTransferSizeBytes the maximum number of bytes a message can contain
     * @param lowerLevelSender layer sending the data (including the checksum)
     */
    public RemotingSendService(final int maxTransferSizeBytes, DataSenderIfc lowerLevelSender) {
        setLowerLevelSender(lowerLevelSender);
        initializeByteBuffer(maxTransferSizeBytes);
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public boolean send(int remoteObjectId) {
        RemoteObjectIfc remoteObject = objectDictionary.get(remoteObjectId);
        if (lowerLevelSender == null || remoteObject == null || stop) {
            return false;
        }

        try {
            serializer.initialize(byteBuffer.array(), byteBuffer.capacity());
            serializer.writeInt32(remoteObjectId);
            remoteObject.onSend(serializer);

            byteBuffer.position(serializer.getBufferPos());
            return lowerLevelSender.sendData(byteBuffer);
        } catch (BufferOverflowException ex) {
            // specified maxTransferSizeBytes was too small!
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Set the sender interface.
     * @param lowerLevelSender layer sending the data (including the checksum)
     */
    public void setLowerLevelSender(DataSenderIfc lowerLevelSender) {
        this.lowerLevelSender = lowerLevelSender;
    }

    private void initializeByteBuffer(final int maxTransferSizeBytes) {
        this.byteBuffer = ByteBuffer.allocate(maxTransferSizeBytes).order(ByteOrder.BIG_ENDIAN);
        this.serializer = new Serializer(byteBuffer.array(), maxTransferSizeBytes);
    }
}
