package com.example.figmatest.imt.base.lib.remoting.service;

import com.example.figmatest.imt.base.core.serialization.Deserializer;
import com.example.figmatest.imt.base.core.serialization.Serializer;
import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;

/**
 * Test helper
 * Created by mguntli on 16.10.2015.
 */
class DummyRemoteObject implements RemoteObjectIfc {

    int dataToSend;
    int dataReceived;
    int onReceiveCounter;
    int onSendCounter;

    DummyRemoteObject() {
    }

    DummyRemoteObject(int dataToSend) {
        this.dataToSend = dataToSend;
    }

    @Override
    public void onReceive(Deserializer deserializer) {
        onReceiveCounter++;
        dataReceived = deserializer.readInt32();
    }

    @Override
    public void onSend(Serializer serializer) {
        onSendCounter++;
        serializer.writeInt32(dataToSend);
    }

    public int getDataReceived() {
        return dataReceived;
    }

    public int getOnReceiveCounter() {
        return onReceiveCounter;
    }

    public int getOnSendCounter() {
        return onSendCounter;
    }
}
