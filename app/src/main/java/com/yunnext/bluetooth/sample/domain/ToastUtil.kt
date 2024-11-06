package com.yunnext.bluetooth.sample.domain

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.yunnext.bluetooth.sample.ui.MyApp

object ToastUtil {
    private var toast: Toast? = null

    fun toast(msg: String?, context: Context? = null) {
        if (msg.isNullOrEmpty()) return
        try {
            toast?.cancel()
            toast = null
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        val temp: Toast = when (context) {
            is Activity -> Toast.makeText(context, msg, Toast.LENGTH_SHORT)
            else -> Toast.makeText(MyApp.CONTEXT, msg, Toast.LENGTH_SHORT)
        }
        toast = temp
        temp.setText(msg)
        temp.show()
    }

}