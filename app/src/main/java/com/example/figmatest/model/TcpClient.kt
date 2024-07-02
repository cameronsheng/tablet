package com.example.figmatest.model

import com.example.figmatest.DataListenerIfc
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
import java.net.UnknownHostException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TcpClient : DataProducer() {

    private var socket: Socket? = null
    private var output: PrintWriter? = null
    private var input: BufferedReader? = null

    // Initialize the socket and streams
    private suspend fun init(ipAddress: String, port: Int) {
        try {
            withContext(Dispatchers.IO) {
                socket = Socket(ipAddress, port)
                output = PrintWriter(socket!!.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                receive()
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
    suspend fun send(data: String) {
        withContext(Dispatchers.IO) {
            output?.println(data)
        }
    }

    // Receive data from the TCP server (non-blocking)
    suspend fun receive() {
        return withContext(Dispatchers.IO) {
            try {
                while(true) {
                    val data = input?.readLine() ?: break
                    processData(data.encodeToByteArray())
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
}