package com.example.figmatest

import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.figmatest.controller.DataController
import com.example.figmatest.model.SerialDevice
import com.example.figmatest.ui.theme.FigmaTestTheme
import com.example.figmatest.view.SerialDataViewIfc


class MainActivity : ComponentActivity(), SerialDataViewIfc {

    private var mReservation: LocalOnlyHotspotReservation? = null

    private var dataController : DataController? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FigmaTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }
            }
        }
        Log.d("Main", "start serial controller")
        SerialDevice.start(this)
//        dataController = DataController(this, this)
//        dataController?.start()
//        val builder = UnhiddenSoftApConfigurationBuilder();
//        val macAddress = MacAddress.fromString("02:00:00:00:00:00");
//        val config = builder.setBand(UnhiddenSoftApConfigurationBuilder.BAND_2GHZ)
//            .setAutoshutdownEnabled(true)
//            .setPassphrase("12345678", UnhiddenSoftApConfigurationBuilder.SECURITY_TYPE_WPA2_PSK)
//            .setMacRandomizationSetting(UnhiddenSoftApConfigurationBuilder.RANDOMIZATION_PERSISTENT)
//            .setSsid("Andorid_test")
//            .build()
//
//        val executor = Executors.newSingleThreadExecutor();
//
//        // Implement the LocalOnlyHotspotCallback
//        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
//            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
//                Log.d(TAG, "Wifi Hotspot is on now")
//                if (reservation != null) {
//                    Log.d(TAG, "SSID is " + reservation.softApConfiguration.wifiSsid.toString())
//                };
//                if (reservation != null) {
//                    Log.d(TAG, "PWD is " + reservation.softApConfiguration.passphrase.toString())
//                };
//                mReservation = reservation
//            }
//
//            override fun onStopped() {
//                println("LocalOnlyHotspot stopped.")
//            }
//
//            override fun onFailed(reason: Int) {
//                println("LocalOnlyHotspot failed with reason: $reason")
//            }
//        };
//        val wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager;
//        WifiManager::class.java.getMethod(
//            "startLocalOnlyHotspot", SoftApConfiguration::class.java, Executor::class.java,
//            LocalOnlyHotspotCallback::class.java,
//        ).invoke(wifiManager, config, executor, callback);

//        val intent = Intent(this, SerialDataView::class.java)
//        startActivity(intent)
    }

    override fun displayData(data: String) {
        setContent {
            FigmaTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(data)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "$name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FigmaTestTheme {
        Greeting("Android")
    }
}