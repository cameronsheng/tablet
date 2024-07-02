package com.example.figmatest.imt.base.lib.remoting.service;

/**
 * Defines the interface for a remoting send service.
 * Created by mguntli on 16.10.2015.
 */
public interface RemotingSendServiceIfc {

    /**
     * Sends the remote object to the remoting peer.
     * You have to register your remote object first, or it won't be sent.
     * @param remoteObjectId the identifier of the remote object that should be sent to the remoting peer.
     * @return true on success, false otherwise
     */
    boolean send(int remoteObjectId);
}
