package com.voidloop.formulae.view

import android.app.Application
import com.voidloop.formulae.utils.NetworkStateHolder.registerConnectivityMonitor

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerConnectivityMonitor()
    }
}