package com.yunnext.bluetooth.sample.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.yunnext.bluetooth.sample.BuildConfig

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this.applicationContext
    }


    companion object {

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: Context

        val version: String
            get() = BuildConfig.VERSION_NAME
    }
}