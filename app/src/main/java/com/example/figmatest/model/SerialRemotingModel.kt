package com.example.figmatest.model

import android.content.Context

object SerialRemotingModel : RemotingModel() {

    private var serialDevice: SerialDevice? = null

    init {
        serialDevice = SerialDevice(this)
        setLowerLevelSender(serialDevice!!)
    }
    fun start(context: Context) {
        serialDevice?.start(context)
    }
}