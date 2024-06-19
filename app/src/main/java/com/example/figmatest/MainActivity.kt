package com.example.figmatest

import android.content.ContentValues.TAG
import android.content.Context
import android.net.MacAddress
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.figmatest.ui.theme.FigmaTestTheme
import com.example.figmatest.ui.theme.UnhiddenSoftApConfigurationBuilder
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private var mReservation: LocalOnlyHotspotReservation? = null

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
        val builder = UnhiddenSoftApConfigurationBuilder();
        val macAddress = MacAddress.fromString("02:00:00:00:00:00");
        val config = builder.setBand(UnhiddenSoftApConfigurationBuilder.BAND_2GHZ)
            .setAutoshutdownEnabled(true)
            .setPassphrase("12345678", UnhiddenSoftApConfigurationBuilder.SECURITY_TYPE_WPA2_PSK)
            .setMacRandomizationSetting(UnhiddenSoftApConfigurationBuilder.RANDOMIZATION_PERSISTENT)
            .setSsid("Andorid_test")
            .build()

        val executor = Executors.newSingleThreadExecutor();

        // Implement the LocalOnlyHotspotCallback
        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                Log.d(TAG, "Wifi Hotspot is on now")
                if (reservation != null) {
                    Log.d(TAG, "SSID is " + reservation.softApConfiguration.wifiSsid.toString())
                };
                if (reservation != null) {
                    Log.d(TAG, "PWD is " + reservation.softApConfiguration.passphrase.toString())
                };
                mReservation = reservation
            }

            override fun onStopped() {
                println("LocalOnlyHotspot stopped.")
            }

            override fun onFailed(reason: Int) {
                println("LocalOnlyHotspot failed with reason: $reason")
            }
        };
        val wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager;
        WifiManager::class.java.getMethod(
            "startLocalOnlyHotspot", SoftApConfiguration::class.java, Executor::class.java,
            LocalOnlyHotspotCallback::class.java,
        ).invoke(wifiManager, config, executor, callback);

//        val wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager;
//        wifiManager.startLocalOnlyHotspot(
//            @RequiresApi(Build.VERSION_CODES.O)
//            object : LocalOnlyHotspotCallback() {
//                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//                override fun onStarted(reservation: LocalOnlyHotspotReservation) {
//                    super.onStarted(reservation)
//                    Log.d(TAG, "Wifi Hotspot is on now")
//                    Log.d(TAG, "SSID is " + reservation.softApConfiguration.wifiSsid.toString());
//                    Log.d(TAG, "PWD is " + reservation.softApConfiguration.passphrase.toString());
//                    mReservation = reservation
//                }
//
//                override fun onStopped() {
//                    super.onStopped()
//                    Log.d(TAG, "onStopped: ")
//                }
//
//                override fun onFailed(reason: Int) {
//                    super.onFailed(reason)
//                    Log.d(TAG, "onFailed: ")
//                }
//            },
//            Handler()
//        );

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
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