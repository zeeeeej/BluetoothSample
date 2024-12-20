package com.yunnext.bluetooth.sample.repo.http

import com.yunnext.bluetooth.sample.domain.HDResult
import com.yunnext.bluetooth.sample.domain.hdResultFail

class ApiException(val code: Int = -1, msg: String = "", cause: Throwable? = null) :
    RuntimeException(msg, cause)

fun hdResultFailForHttp(e: Throwable, code: Int = -1): HDResult<*> =
    hdResultFail(
        ApiException(
            code,
            e.message ?: "",
            e
        )
    )