package com.example.figmatest.model

object TcpRemotingModel : RemotingModel() {

    private lateinit var tcpClient: TcpClient
    suspend fun start() {
        tcpClient.connect("192.168.5.200", 8080)
        tcpClient.addDataListener(this)
    }
}