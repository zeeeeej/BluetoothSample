package com.yunnext.bluetooth.sample.domain

sealed class HDResult<out T> {
    data class Success<T>(val data: T) : HDResult<T>()
    data class Fail(val error: Throwable) : HDResult<Nothing>()
}

fun <T> hdResultSuccess(data: T): HDResult<T> =
    HDResult.Success(data)

fun hdResultFail(e: Throwable): HDResult<*> =
    HDResult.Fail(error = e)


fun <T, R> HDResult<T>.map(map: (T) -> R): HDResult<R> {
    return when (this) {
        is HDResult.Fail -> this
        is HDResult.Success -> hdResultSuccess(
            map(this.data)
        )
    }
}

fun <T> HDResult<T>.display(display: (T) -> String) = when (this) {
    is HDResult.Fail -> this.error.message
    is HDResult.Success -> display(this.data)
}