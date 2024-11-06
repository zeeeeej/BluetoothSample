package com.yunnext.bluetooth.sample.repo.http

import kotlinx.serialization.Serializable

@Serializable
class HttpResp< T>(
    val code: Int,
    val msg: String,
    val data: T,
    val success: Boolean
)