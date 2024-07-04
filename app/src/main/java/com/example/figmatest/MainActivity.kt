package com.example.figmatest

import android.content.Intent
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.figmatest.controller.TcpDataController
import com.example.figmatest.view.DataViewIfc
import com.example.figmatest.view.SerialDataView
import com.example.figmatest.view.TcpDataView
import com.scichart.charting.visuals.SciChartSurface


class MainActivity : ComponentActivity(), DataViewIfc {

    private var mReservation: LocalOnlyHotspotReservation? = null

    private var tcpDataController : TcpDataController? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        setSciChartLicense()
    }

    override fun displayData(data: Float) {
    }

    fun onSerialDataPressed(v: View?) {
        val intent = Intent(this, SerialDataView::class.java)
        startActivity(intent)
    }

    fun onTcpDataPressed(v: View?) {
        val intent = Intent(this, TcpDataView::class.java)
        startActivity(intent)
    }

    fun setSciChartLicense() {
        try {
            SciChartSurface.setRuntimeLicenseKey(
                """
            <LicenseContract>
            <Customer> IMT Information Management Technology AG</Customer>
            <OrderId>ABTSOFT-1338</OrderId>
            <LicenseCount>1</LicenseCount>
            <IsTrialLicense>false</IsTrialLicense>
            <SupportExpires>12/02/2017 00:00:00</SupportExpires>
            <ProductCode>SC-ANDROID-2D-ENTERPRISE-SITE-SRC</ProductCode>
            <KeyCode>403af21ec9923266a24020324dc5e01b928533c945c5bf80d74d0650d266fa6cfa385b909e4b6ae0a4e6310442a452cb22b39dcc4f573f0e49913aaef1b6d83f690d4ee502388854ee10802bd45b6a8deaf1d8b72775c5fb7d3096914f63d5b13f461c3a024400d49ea43751fb4d32c24eeb9b055cc40098204698b92447511d19d4d368980c03e513643323187c5bcd899b79777e2fc35e7db6c6d13d07c4e34a6041a9b5f85fc9f0cd6f47d6df684a3e8deab4e7424dd84b661721c7ca3a7f3b93bc7da9e59df11caad7dba3f8</KeyCode>
            </LicenseContract>
            """.trimIndent()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
