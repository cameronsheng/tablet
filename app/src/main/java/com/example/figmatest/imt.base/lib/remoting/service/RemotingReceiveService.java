package com.example.figmatest.imt.base.lib.remoting.service;


import com.example.figmatest.imt.base.core.serialization.Deserializer;
import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc;
import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;

import java.nio.ByteBuffer;

/**
 * Receiver part of the remoting service.
 * A four byte identifier will be used to determinate the data receiver.
 * Created by mguntli on 16.10.2015.
 */
public class RemotingReceiveService extends RemotingServiceBase implements DataReceiverIfc {

    private final Deserializer deserializer;
    private final RemotingReceiveErrorCallbackIfc errorCallback;

    /**
     * Default Constructor.
     */
    public RemotingReceiveService() {
        this.deserializer = new Deserializer();
        this.errorCallback = null;
    }

    /**
     * Constructor with error callback.
     * @param callback when an error occurs
     */
    public RemotingReceiveService(RemotingReceiveErrorCallbackIfc callback) {
        this.deserializer = new Deserializer();
        this.errorCallback = callback;
    }

    @Override
    public void onDataReceived(ByteBuffer receiveBuffer) {
        if (receiveBuffer == null || receiveBuffer.limit() == 0) {
            return;
        }

        deserializer.initialize(receiveBuffer.array(), receiveBuffer.limit());
        final int remoteObjectId = deserializer.readInt32();
        RemoteObjectIfc remoteObject = objectDictionary.get(remoteObjectId);
        if (remoteObject != null) {
            remoteObject.onReceive(deserializer);
        } else if (errorCallback != null) {
            errorCallback.onRemoteObjectNotRegistered(remoteObjectId);
        }
    }
}
