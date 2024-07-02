package com.example.figmatest.model

object TcpRemotingModel : RemotingModel() {

    private var tcpClient: TcpClient? = null

    init {
        tcpClient = TcpClient()
        setLowerLevelSender(tcpClient!!)
    }
    suspend fun start() {
        tcpClient?.connect("192.168.5.166", 8080)
        sendSettings()
        sendCommand()
        tcpClient?.addDataListener(this)
        tcpClient?.receive()
    }
}