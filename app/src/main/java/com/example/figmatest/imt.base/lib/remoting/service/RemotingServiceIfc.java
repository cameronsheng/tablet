package com.example.figmatest.imt.base.lib.remoting.service;

import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;

/**
 * Defines the interface for a remoting service.
 * A remoting service is responsible for data transmission from / to a remote device.
 * Note: Routing of received data is based on the identifier whereas the remote object registered itself.
 * Created by mguntli on 13.10.2015.
 */
public interface RemotingServiceIfc {

    /**
     * Adds a remote object to the list of remote objects managed by this remote service.
     * If another object is already registered under the given id, it will be overwritten.
     * @param remoteObject Remote object that should be added.
     * @param remoteObjectId Identifier under which the remote object should be registered.
     */
    void add(RemoteObjectIfc remoteObject, final int remoteObjectId);

    /**
     * Removes a remote object from the list of remote objects managed by this remoting service.
     * @param remoteObjectId Identifier under which the remote object is registered.
     */
    void remove(final int remoteObjectId);
}
