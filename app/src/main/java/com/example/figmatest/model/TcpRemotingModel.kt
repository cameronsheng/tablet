package com.example.figmatest.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TcpRemotingModel : RemotingModel() {

    private var tcpClient: TcpClient? = null
    private var udpClient: UdpClient? = null

    init {
        tcpClient = TcpClient(this)
        udpClient = UdpClient()
        setLowerLevelSender(tcpClient!!)
    }
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            udpClient?.start()
            var ip = "";
            while (ip.isEmpty()) {
                ip = udpClient?.receive().toString()
            }
            tcpClient?.connect(ip, 8080)
            tcpClient?.receive()
        }
    }
}