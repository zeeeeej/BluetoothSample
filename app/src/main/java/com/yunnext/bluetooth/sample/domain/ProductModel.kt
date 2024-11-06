package com.yunnext.bluetooth.sample.domain

import kotlinx.serialization.Serializable

@Serializable
data class ProductModel(
    val id: String,
    val name: String,
    val identifier: String,
    val img: String,
)