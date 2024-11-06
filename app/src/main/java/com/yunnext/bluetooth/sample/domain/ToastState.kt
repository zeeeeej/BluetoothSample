package com.yunnext.bluetooth.sample.domain

sealed interface ToastState {
    data object NaN : ToastState
    data class Toast(val msg: String, val time: Long = 0L,val cancelable:Boolean = false)
}