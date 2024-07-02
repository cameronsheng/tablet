package com.example.figmatest

import android.content.Intent
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.figmatest.controller.TcpDataController
import com.example.figmatest.view.TcpDataView
import com.example.figmatest.view.DataViewIfc


class MainActivity : ComponentActivity(), DataViewIfc {

    private var mReservation: LocalOnlyHotspotReservation? = null

    private var tcpDataController : TcpDataController? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

    }

    override fun displayData(data: String) {
    }

    fun onSerialDataPressed(v: View?) {
        val intent = Intent(this, TcpDataView::class.java)
        startActivity(intent)
    }

    fun onTcpDataPressed(v: View?) {
        val intent = Intent(this, TcpDataView::class.java)
        startActivity(intent)
    }
}
