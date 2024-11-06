package com.yunnext.bluetooth.sample.domain

import androidx.compose.runtime.Stable

@Stable
interface BaseState {
    val loading: LoadingState
        get() = LoadingState.NaN
    val toast: ToastState
        get() = ToastState.NaN
}