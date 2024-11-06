package com.yunnext.bluetooth.sample.repo.http

class ApiException(val code: Int = -1, msg: String = "", cause: Throwable? = null) :
    RuntimeException(msg, cause)