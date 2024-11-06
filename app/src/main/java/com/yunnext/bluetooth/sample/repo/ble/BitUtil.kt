package com.yunnext.bluetooth.sample.repo.ble

import java.nio.ByteBuffer
import java.nio.ByteOrder

// https://blog.csdn.net/pl0020/article/details/104813884/

fun Byte.bitApply0(position: Int): Byte {
    return ((this.toInt() and 0xff) and (1 shl position).inv()).toByte()
}

fun Byte.bitApply1(position: Int): Byte {
    return ((this.toInt() and 0xff) or (1 shl position)).toByte()
}

fun Byte.bitCheck0(position: Int): Boolean {
    return ((this.toInt() and 0xff) and (1 shl position)) == 0
}

fun Byte.bitCheck1(position: Int): Boolean {
    return ((this.toInt() and 0xff) and (1 shl position)) != 0
}

//fun Long.toByteArray(): ByteArray {
//    return ByteBuffer.allocate(4)
//        .putInt(this.toInt())
//        .order(ByteOrder.LITTLE_ENDIAN)
//        .array()
//}
//
//fun ByteArray.toInt():Int = ByteBuffer.wrap(this)
//    .order(ByteOrder.BIG_ENDIAN)
//    .int


fun Int.toByteArray(): ByteArray {
    return byteArrayOf(
        (this and 0xFF).toByte(),
        ((this and 0xFF00) shr 8).toByte(),
        ((this and 0xFF0000) shr 16).toByte(),
        ((this and 0xFF000000.toInt()) shr 24).toByte(),
    )
}

fun ByteArray.toIntByByteBuffer(): Int = ByteBuffer.wrap(this)
    .order(ByteOrder.LITTLE_ENDIAN)
    .int


fun Int.applyU() = if (this < 0) this + 256 else this



