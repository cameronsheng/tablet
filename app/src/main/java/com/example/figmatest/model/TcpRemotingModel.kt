package com.example.figmatest.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TcpRemotingModel : RemotingModel() {

    private var tcpClient: TcpClient? = null

    init {
        tcpClient = TcpClient(this)
        setLowerLevelSender(tcpClient!!)
    }
    fun start() {
        CoroutineScope(Dispatchers.Main).launch {
            tcpClient?.connect("192.168.5.200", 8080)
            tcpClient?.receive()
        }
    }
}