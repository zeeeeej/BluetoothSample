package com.yunnext.bluetooth.sample.domain

import com.yunnext.bluetooth.sample.repo.resp.UserResp
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val account: String,
    val root: String,
    val company: String,
    val userType: String,
    val token: String
)

fun UserResp.convert(): User {
    return User(
        username = this.tokenUser?.name ?: "",
        account = this.tokenUser?.account ?: "",
        root = this.tokenUser?.root ?: "",
        company = this.tokenUser?.company ?: "",
        userType = this.tokenUser?.userType ?: "",
        token = this.token ?: ""
    )
}