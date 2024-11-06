package com.yunnext.bluetooth.sample.domain

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun clipBroad(context: Context, label: String, text: String) {
    return suspendCoroutine {
        val ctx = context.applicationContext
        val clipboard: ClipboardManager =
            ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        it.resume(Unit)
    }
}