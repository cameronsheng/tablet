package com.example.figmatest.view

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.figmatest.R
import com.example.figmatest.controller.DataController

class SerialDataView : ComponentActivity(), SerialDataViewIfc, View.OnClickListener {

    private lateinit var outputTextView: TextView
    private lateinit var inputEditTextView: EditText
    private lateinit var button: Button
    private lateinit var dataController: DataController

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.serial_data_view);

        outputTextView = findViewById(R.id.outputTextView)
        inputEditTextView = findViewById(R.id.inputEditText)
        button = findViewById(R.id.actionButton)

        button.setOnClickListener(this)

        dataController = DataController(this, this)
        dataController.start()
    }

    override fun displayData(data : String) {
        outputTextView.setText(data)
    }

    override fun onClick(v: View?) {
        dataController.sendData(outputTextView.text.toString())
    }

}