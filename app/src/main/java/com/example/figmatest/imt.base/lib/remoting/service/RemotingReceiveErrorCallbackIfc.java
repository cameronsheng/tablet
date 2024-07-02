package com.example.figmatest.imt.base.lib.remoting.service;

/**
 * Defines the interface to handle errors in the receiver part of the remoting service.
 * Created by mguntli on 16.10.2015.
 */
public interface RemotingReceiveErrorCallbackIfc {

    /**
     * Callback function when a remote object id has been received which is not registered in the service.
     * @param remoteObjectId remote object id received from the input stream
     */
    void onRemoteObjectNotRegistered(final int remoteObjectId);
}
