package com.yunnext.bluetooth.sample.repo.resp

import kotlinx.serialization.Serializable

@Serializable
data class UserResp(
    val token:String?,
    val tokenUser:UserInnerResp?
)

@Serializable
data class UserInnerResp(
    val id:String?,
    val name:String?,
    val account:String?,
    val roleId:String?,
    val roleName:String?,
    val root:String?,
    val company:String?,
    val userType:String?,
)