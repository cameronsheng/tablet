package com.example.figmatest.view

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.figmatest.R
import com.example.figmatest.controller.TcpDataController

class TcpDataView : ComponentActivity(), DataViewIfc {

    private lateinit var outputTextView: TextView
    private lateinit var tcpDataController: TcpDataController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tcp_data_view);
        outputTextView = findViewById(R.id.outputText)

        tcpDataController = TcpDataController(this, this)
        tcpDataController.start()

    }

    override fun displayData(data : String) {
        outputTextView.append(data)
    }

    fun onSendSettingsPressed(v: View?) {
        tcpDataController.sendSettings()
    }

    fun onSendCommandPressed(v: View?) {
        tcpDataController.sendCommand()
    }

}