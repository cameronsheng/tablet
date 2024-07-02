package com.example.figmatest.imt.base.lib.remoting.service;

import com.example.figmatest.imt.base.core.serialization.Deserializer;
import com.example.figmatest.imt.base.core.serialization.SerializableIfc;
import com.example.figmatest.imt.base.core.serialization.Serializer;
import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;

public class GenericRemoteObject implements RemoteObjectIfc {

    private GenericReceiverIfc receiver;
    private SerializableIfc dataToSend;
    private SerializableIfc receivedData;

    public GenericRemoteObject(GenericReceiverIfc receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onReceive(Deserializer deserializer) {
        receivedData.deserialize(deserializer);
        receiver.onDataReceived(receivedData);
    }

    @Override
    public void onSend(Serializer serializer) {
        serializer.write(dataToSend);
    }

    public void setDataToSend(SerializableIfc dataToSend) {
        this.dataToSend = dataToSend;
    }
}
