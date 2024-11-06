package com.yunnext.bluetooth.sample.repo.ble

import android.util.Log


val ByteArray.display: String
    @OptIn(ExperimentalStdlibApi::class)
    get() = "[${this.toHexString()}]"

private const val TAG = "_ble"

fun d(msg: String) {
    Log.d(TAG, msg)
}

fun e(msg: String) {
    Log.e(TAG, msg)
}

fun w(msg: String) {
    Log.w(TAG, msg)
}

fun i(msg: String) {
    Log.i(TAG, msg)
}


fun ByteArray.toInt(): Int {
    if (this.size > 4) {
        throw IllegalArgumentException("ByteArray size must not be greater than 4")
    }

    var result = 0
    for (i in this.indices) {
        result = result shl 8 // result 左移 8 位
        result = result or (this[i].toInt() and 0xFF) // 将字节添加到结果中
    }
    return result
}