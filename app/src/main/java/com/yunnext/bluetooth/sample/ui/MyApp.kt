package com.yunnext.bluetooth.sample.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.yunnext.bluetooth.sample.BuildConfig

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        CONTEXT = this.applicationContext
    }

    override fun getResources(): Resources {
        val resources = super.getResources()
        val configuration = resources.configuration
        if (configuration.fontScale != 1.0f) {
            println("MyApp::getResources"+configuration.fontScale)
            configuration.fontScale = 1.0f
            // 但是这个函数被标记为过期了
            //resources.updateConfiguration(configuration, resources.displayMetrics)
            // 那么我们直接这么来
            return createConfigurationContext(configuration).resources
        }
        return resources
    }



    companion object {

        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        lateinit var CONTEXT: Context

        val version: String
            get() = BuildConfig.VERSION_NAME
    }
}