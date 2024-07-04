package com.example.figmatest.controller

import android.content.Context
import com.example.figmatest.DataListenerIfc
import com.example.figmatest.enums.EngineCommand
import com.example.figmatest.model.SerialRemotingModel
import com.example.figmatest.model.TcpRemotingModel
import com.example.figmatest.protocol.VitalSignsDataProtocol
import com.example.figmatest.view.DataViewIfc

class SerialDataController(private val view : DataViewIfc, private val context : Context) :
    DataListenerIfc {

    override fun onDataReceived(data: Any) {
        // send data to activity
        when (data) {
            is VitalSignsDataProtocol -> {
                view.displayData(data.tidalVolume);
            }
        }
    }

    fun start() {
        SerialRemotingModel.addDataListener(this)
        SerialRemotingModel.start(context)
    }

    fun sendData(data : String) {
    }

    fun sendSettings() {
        SerialRemotingModel.sendSettings()
    }

    fun sendCommand() {
        SerialRemotingModel.sendCommand(EngineCommand.START)
    }

    fun stop() {

    }
}