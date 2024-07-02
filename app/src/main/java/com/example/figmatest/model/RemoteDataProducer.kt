package com.example.figmatest.model

import com.example.figmatest.DataListenerIfc

abstract class RemoteDataProducer : DataProducerIfc {

    protected val dataListeners = ArrayList<DataListenerIfc>()

    override fun addDataListener(listener: DataListenerIfc) {
        dataListeners.add(listener)
    }

    override fun removeDataListener(listener: DataListenerIfc) {
        dataListeners.remove(listener)
    }

    fun processData(data : Any) {
        for (listener in dataListeners) {
            listener.onDataReceived(data)
        }
    }
}