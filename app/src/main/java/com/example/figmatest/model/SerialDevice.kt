package com.example.figmatest.model

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import com.example.figmatest.imt.base.lib.remoting.DataReceiverIfc
import com.example.figmatest.imt.base.lib.remoting.DataSenderIfc
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.util.SerialInputOutputManager
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.Executors


class SerialDevice(val upperLevelReceiver: DataReceiverIfc) : DataSenderIfc {

    private var serialPort: UsbSerialPort? = null
    private var ioManager: SerialInputOutputManager? = null
    private var usbDevice: UsbDevice? = null
    private var usbManager: UsbManager? = null
    private var driver: UsbSerialDriver? = null

    fun start(context: Context): Boolean {
        //val context: Context = TamboApplication.getAppContext()
        usbManager = context.getSystemService(USB_SERVICE) as UsbManager
        //usbDevice = usbManager?.deviceList?.values?.firstOrNull()
        var drivers =  UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        driver = drivers.get(0)
        if (driver == null) {
            Log.d("Figma", "No driver possible")
            return false
        }
        val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent("com.example.USB_PERMISSION"),
            PendingIntent.FLAG_UPDATE_CURRENT)

        val permissionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    "com.example.USB_PERMISSION" -> {
                        synchronized(this) {
                            if (intent.getBooleanExtra(
                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                    false
                                )
                            ) {
                                Log.d("USB", "Permission accepted for device")
                                    // Call method to set up device communication
                                    connect()
                            } else {
                                Log.d("USB", "Permission denied for device")
                            }
                        }
                    }
                }
            }
        }

        val filter: IntentFilter = IntentFilter("com.example.USB_PERMISSION")
        context.registerReceiver(permissionReceiver, filter)
        usbManager!!.requestPermission(driver!!.device, permissionIntent)

        return false
    }

    private fun connect(): Boolean {
        if (driver != null) {
            serialPort = driver?.ports?.get(0)

            val connection = usbManager?.openDevice(driver!!.device)

            if (connection == null) {
                return false
            }
            serialPort?.apply {
                open(connection)
                setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            }

            ioManager =
                SerialInputOutputManager(serialPort, object : SerialInputOutputManager.Listener {
                    override fun onRunError(e: Exception) {
                        // Handle error
                    }

                    override fun onNewData(data: ByteArray) {
                        upperLevelReceiver.onDataReceived(ByteBuffer.wrap(data))
                    }
                })
            Executors.newSingleThreadExecutor().submit(ioManager)
            return true
        } else {
            return false
        }
    }

    fun disconnect() {
        ioManager?.stop()
        try {
            serialPort?.close()
        } catch (e: IOException) {
            // Handle error
        }
    }

    fun sendData(data: ByteArray) {
        serialPort?.write(data, 1000)
    }

    override fun sendData(sendBuffer: ByteBuffer?): Boolean {
        var data = sendBuffer?.let { ByteArray(it.position()) }
        if (sendBuffer != null) {
            sendBuffer.rewind()
            sendBuffer.get(data)
            if (data != null) {
                sendData(data)
                return true
            }
        }
        return false
    }
}