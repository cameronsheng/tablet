package com.example.figmatest.imt.base.lib.remoting.service;

import com.example.figmatest.imt.base.core.serialization.SerializableIfc;

public interface GenericReceiverIfc {

    void onDataReceived(SerializableIfc data);
}
