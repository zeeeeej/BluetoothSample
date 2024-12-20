package com.yunnext.bluetooth.sample.domain

import com.yunnext.bluetooth.sample.repo.ble.datetimeFormat

fun main() {
    println( "tttttt->"+datetimeFormat {
        (0xffffff *24L*3600*1000L).toStr()
    })
}