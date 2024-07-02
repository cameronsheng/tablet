package com.example.figmatest.imt.base.lib.remoting.service;

import android.util.SparseArray;

import com.example.figmatest.imt.base.lib.remoting.RemoteObjectIfc;


/**
 * Provides the base class for RemotingSendService and RemotingReceiveService, which implements the RemotingServiceIfc
 * Created by mguntli on 16.10.2015.
 */
abstract class RemotingServiceBase implements RemotingServiceIfc {

    protected final SparseArray<RemoteObjectIfc> objectDictionary = new SparseArray<>();

    @Override
    public void add(RemoteObjectIfc remoteObject, final int remoteObjectId) {
        objectDictionary.put(remoteObjectId, remoteObject);
    }

    @Override
    public void remove(final int remoteObjectId) {
        objectDictionary.remove(remoteObjectId);
    }
}
