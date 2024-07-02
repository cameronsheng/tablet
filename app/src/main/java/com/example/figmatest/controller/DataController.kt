package com.example.figmatest.controller

import android.content.Context
import com.example.figmatest.DataListenerIfc
import com.example.figmatest.model.SerialDevice
import com.example.figmatest.model.TcpRemotingModel
import com.example.figmatest.view.SerialDataViewIfc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataController(private val view : SerialDataViewIfc, private val context : Context) : DataListenerIfc {

    override fun onDataReceived(data: ByteArray) {
        // send data to activity
        view.displayData(data.toString())
    }

    fun start() {
        CoroutineScope(Dispatchers.Main).launch {
            TcpRemotingModel.start()
        }

    }

    fun sendData(data : String) {
        TcpRemotingModel.sendSettings()
        TcpRemotingModel.sendCommand()
    }

    fun stop() {

    }
}