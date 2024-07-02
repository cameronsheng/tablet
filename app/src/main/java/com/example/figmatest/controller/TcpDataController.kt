package com.example.figmatest.controller

import android.content.Context
import com.example.figmatest.DataListenerIfc
import com.example.figmatest.model.TcpRemotingModel
import com.example.figmatest.protocol.VitalSignsDataProtocol
import com.example.figmatest.view.DataViewIfc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TcpDataController(private val view : DataViewIfc, private val context : Context) : DataListenerIfc {

    override fun onDataReceived(data: Any) {
        // send data to activity
        when (data) {
            is VitalSignsDataProtocol -> {
                view.displayData("Received VitalSignsData with tidal volume: " + data.tidalVolume);
            }
        }
    }

    fun start() {
        TcpRemotingModel.addDataListener(this)
        TcpRemotingModel.start()
    }

    fun sendData(data : String) {
    }

    fun sendSettings() {
        TcpRemotingModel.sendSettings()
    }

    fun sendCommand() {
        TcpRemotingModel.sendCommand()
    }

    fun stop() {

    }
}