package com.example.figmatest.model

import com.example.figmatest.DataListenerIfc

interface DataProducerIfc {

    fun addDataListener(listener: DataListenerIfc)

    fun removeDataListener(listener: DataListenerIfc)
}