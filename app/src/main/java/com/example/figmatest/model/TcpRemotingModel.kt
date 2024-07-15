package com.example.figmatest.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TcpRemotingModel : RemotingModel() {

    private var tcpClient: TcpClient? = null
    private var udpClient: UdpClient? = null
    private var ip: String = ""

    init {
        tcpClient = TcpClient(this)
        udpClient = UdpClient()
        setLowerLevelSender(tcpClient!!)
    }
    fun start() {
        CoroutineScope(Dispatchers.IO).launch {
            udpClient?.start()
            while (ip.isEmpty()) {
                var newIp =  udpClient?.receive().toString()
                if (newIp.isNotEmpty()) {
                    if (!newIp.equals(ip)) {
                        tcpClient?.connect(newIp, 8080)
                        ip = newIp
                    }
                }
            }
            if (tcpClient?.isConnected() == true) {
                tcpClient?.receive()
            }
        }
    }
}