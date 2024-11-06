package com.yunnext.bluetooth.sample.domain

import kotlinx.serialization.Serializable

@Serializable
data class Product(val code:String,val name:String)