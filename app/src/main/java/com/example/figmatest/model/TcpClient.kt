package com.example.figmatest.model

import com.example.figmatest.DataListenerIfc
import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc
import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc
import kotlinx.coroutines.CoroutineScope
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

class TcpClient(val upperLevelReceiver: DataReceiverIfc): DataSenderIfc {

    private var socket: Socket? = null
    private var output: OutputStream? = null
    private var input: InputStream? = null
    private var receiveBuffer: ByteBuffer? = null

    init {
        receiveBuffer = ByteBuffer.allocate(4096)
    }

    // Initialize the socket and streams
    private suspend fun init(ipAddress: String, port: Int) {
        try {
            withContext(Dispatchers.IO) {
                socket = Socket(ipAddress, port)
                output = socket!!.getOutputStream()
                input = socket!!.getInputStream()
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
        } catch (e: SocketException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Connect to the server using IP address
    suspend fun connect(ipAddress: String, port: Int) {
        init(ipAddress, port)
    }

    // Send data to the TCP server
    suspend fun send(data: ByteBuffer) {
        withContext(Dispatchers.IO) {
            output?.write(data.array(), 0, data.position())
        }
    }

    // Receive data from the TCP server (non-blocking)
     fun receive() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                while(socket != null && socket!!.isConnected) {
                    var bytesToRead = 0
                    bytesToRead = input?.available()!!
                    if (bytesToRead > 0) {
                        if (bytesToRead > receiveBuffer?.capacity()!!) {
                            bytesToRead = receiveBuffer!!.capacity()
                        }
                        if (input!!.read(receiveBuffer!!.array(), 0, bytesToRead) > 0) {
                            receiveBuffer!!.limit(bytesToRead)
                            upperLevelReceiver.onDataReceived(receiveBuffer)
                        }
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Close the connection
    fun close() {
        try {
            output?.close()
            input?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun sendData(sendBuffer: ByteBuffer?): Boolean {
        CoroutineScope(Dispatchers.Main).launch {
            if (sendBuffer != null) {
                send(sendBuffer)
            }
        }
        return true
    }

    fun isConnected(): Boolean {
        return socket?.let { it.isConnected && !it.isClosed } ?: false
    }
}