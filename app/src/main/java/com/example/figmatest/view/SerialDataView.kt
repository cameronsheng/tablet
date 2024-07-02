package com.example.figmatest.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.figmatest.R
import com.example.figmatest.controller.SerialDataController
import com.example.figmatest.controller.TcpDataController

class SerialDataView : ComponentActivity(), DataViewIfc {

    private lateinit var outputTextView: TextView
    private lateinit var serialDataController: SerialDataController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.serial_data_view);
        outputTextView = findViewById(R.id.outputText)

        serialDataController = SerialDataController(this, this)
        serialDataController.start()

    }

    override fun displayData(data : String) {
        outputTextView.append(data)
    }

    fun onSendSettingsPressed(v: View?) {
        serialDataController.sendSettings()
    }

    fun onSendCommandPressed(v: View?) {
        serialDataController.sendCommand()
    }

}