package com.yunnext.bluetooth.sample.repo.ble

class ProtocolData internal constructor(
    /* 帧头 2BYTE */
    val head: ByteArray,
    /* 命令 1BYTE */
    val cmd: Byte,
    /* payload长度 2BYTE */
    val length: ByteArray,
    val payload: ByteArray,
    /* CRC 1BYTE 各BYTE之和%256 */
    val crc: Byte,
) {
    init {
        require(head.size == 2) {
            "错误的帧头：${head}"
        }
    }
}

fun ProtocolData.toByteArray() =
    (this.head + this.cmd  + this.length).let { cur ->
        if (this.payload.isEmpty()) {
            cur + crc
        } else {
            cur + this.payload + crc
        }
    }





