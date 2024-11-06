package com.yunnext.bluetooth.sample.domain

sealed interface LoadingState {
    data object NaN : LoadingState
    data class Loading(val msg: String = ""):LoadingState
}