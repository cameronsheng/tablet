package com.example.figmatest

import android.app.Application
import android.content.Context
import com.example.figmatest.model.TcpRemotingModel

class TamboApplication  : Application() {

    companion object {
        private lateinit var instance: TamboApplication

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Start TCP remoting model
        TcpRemotingModel.start()
    }
}