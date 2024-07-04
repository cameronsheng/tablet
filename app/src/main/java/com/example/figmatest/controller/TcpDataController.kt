package com.example.figmatest.controller

import android.content.Context
import com.example.figmatest.DataListenerIfc
import com.example.figmatest.model.TcpRemotingModel
import com.example.figmatest.protocol.VitalSignsDataProtocol
import com.example.figmatest.view.DataViewIfc

class TcpDataController(private val view : DataViewIfc, private val context : Context) : DataListenerIfc {

    override fun onDataReceived(data: Any) {
        // send data to activity
        when (data) {
            is VitalSignsDataProtocol -> {
                view.displayData(data.tidalVolume);
            }
        }
    }

    fun start() {
        TcpRemotingModel.addDataListener(this)
        TcpRemotingModel.start()
    }

    fun sendData(data : String) {
    }

    fun onSendSettingsPressed() {
        TcpRemotingModel.sendSettings()
    }

    fun onSendCommandPressed() {
        TcpRemotingModel.sendCommand()
    }

    fun stop() {

    }
}