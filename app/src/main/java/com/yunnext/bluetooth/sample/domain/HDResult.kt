package com.yunnext.bluetooth.sample.domain

import com.yunnext.bluetooth.sample.repo.http.ApiException

sealed class HDResult<out T> {
    data class Success<T>(val data: T) : com.yunnext.bluetooth.sample.domain.HDResult<T>()
    data class Fail(val error: Throwable) : com.yunnext.bluetooth.sample.domain.HDResult<Nothing>()
}

fun <T> httpSuccess(data: T): com.yunnext.bluetooth.sample.domain.HDResult<T> =
    com.yunnext.bluetooth.sample.domain.HDResult.Success(data)
fun <T> httpFail(e: Throwable, code: Int = -1): com.yunnext.bluetooth.sample.domain.HDResult<T> =
    com.yunnext.bluetooth.sample.domain.HDResult.Fail(
        ApiException(
            code,
            e.message ?: "",
            e
        )
    )

fun <T, R> com.yunnext.bluetooth.sample.domain.HDResult<T>.map(map: (T) -> R): com.yunnext.bluetooth.sample.domain.HDResult<R> {
    return when (this) {
        is com.yunnext.bluetooth.sample.domain.HDResult.Fail -> this
        is com.yunnext.bluetooth.sample.domain.HDResult.Success -> com.yunnext.bluetooth.sample.domain.httpSuccess(
            map(this.data)
        )
    }
}

fun <T> com.yunnext.bluetooth.sample.domain.HDResult<T>.display(display: (T) -> String) = when (this) {
    is com.yunnext.bluetooth.sample.domain.HDResult.Fail -> this.error.message
    is com.yunnext.bluetooth.sample.domain.HDResult.Success -> display(this.data)
}