package com.example.figmatest.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.runBlocking
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException

class UdpClient {
    private var socket: DatagramSocket? = null

    companion object {
        private const val PORT = 9999
        private const val TIMEOUT = 2000 // 2 seconds
        private const val BUFFER_SIZE = 1024
        private const val MATCH_STRING = "Tambo"
    }

    // Initialize the socket and bind it to the specified port
    fun start() {
        try {
            socket = DatagramSocket(PORT)
            socket?.broadcast = true
            socket?.soTimeout = TIMEOUT
            println("Socket initialized and bound to port $PORT with timeout set to ${TIMEOUT / 1000} seconds")
        } catch (e: SocketException) {
            e.printStackTrace()
        }
    }

    // Receive UDP broadcast data, check against "tambo", and return the sender's IP address
    suspend fun receive(): String {
        return withContext(Dispatchers.IO) {
            try {
                val buffer = ByteArray(BUFFER_SIZE)
                val packet = DatagramPacket(buffer, buffer.size)
                socket?.receive(packet)  // This is a blocking call
                val receivedData = String(packet.data, 0, packet.length)
                val senderIp = packet.address.hostAddress

                if (receivedData == MATCH_STRING) {
                    println("Received matching message: $receivedData from IP: $senderIp")
                    senderIp
                } else {
                    println("Received non-matching message: $receivedData from IP: $senderIp")
                    ""
                }
            } catch (e: SocketTimeoutException) {
                println("Socket timed out waiting for a packet")
                ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    // Close the socket when done
    fun close() {
        socket?.close()
        println("Socket closed")
    }
}