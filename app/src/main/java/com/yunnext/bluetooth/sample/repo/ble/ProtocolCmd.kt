package com.yunnext.bluetooth.sample.repo.ble

import java.beans.PropertyChangeEvent

enum class ProtocolCmd {
    AuthWrite, AuthNotify, DeviceInfoSelectWrite, DeviceInfoSelectNotify, DeviceInfoUpdateWrite, DeviceInfoUpdateNotify, TimestampWrite, TimestampNotify, VersionNotify, ProductionWriteNotify, HistorySelectWrite, HistoryStartNotify, HistoryNotify, HistoryCompleteNotify, HistorySetNotify, HistoryClearNotify, ;
}


val ProtocolCmd.cmd: Byte
    get() = when (this) {
        ProtocolCmd.AuthWrite -> 0xB1.toByte()
        ProtocolCmd.AuthNotify -> 0xB2.toByte()
        ProtocolCmd.DeviceInfoSelectWrite -> 0xA1.toByte()
        ProtocolCmd.DeviceInfoSelectNotify -> 0xA2.toByte()
        ProtocolCmd.DeviceInfoUpdateWrite -> 0xA3.toByte()
        ProtocolCmd.DeviceInfoUpdateNotify -> 0xA4.toByte()
        ProtocolCmd.TimestampWrite -> 0xD1.toByte()
        ProtocolCmd.TimestampNotify -> 0xD2.toByte()
        ProtocolCmd.VersionNotify -> 0xF1.toByte()
        ProtocolCmd.ProductionWriteNotify -> 0xFE.toByte()
        ProtocolCmd.HistorySelectWrite -> 0xC1.toByte()
        ProtocolCmd.HistoryStartNotify -> 0xC2.toByte()
        ProtocolCmd.HistoryNotify -> 0xC3.toByte()
        ProtocolCmd.HistoryCompleteNotify -> 0xC4.toByte()
        ProtocolCmd.HistorySetNotify -> 0xC5.toByte()
        ProtocolCmd.HistoryClearNotify -> 0xC6.toByte()
    }


//<editor-fold desc="设备信息回复0xA2">
enum class DeviceInfoSelectNotify {
    D01, D02, D03, D04, D05, D06, D07, D08, D09, D10, D11, D12, D13, D14, D15, D16, D17, D18, D19, D20, D21, D22;
}

val DeviceInfoSelectNotify.no: Byte
    get() = when (this) {
        DeviceInfoSelectNotify.D01 -> 0x01
        DeviceInfoSelectNotify.D02 -> 0x02
        DeviceInfoSelectNotify.D03 -> 0x03
        DeviceInfoSelectNotify.D04 -> 0x04
        DeviceInfoSelectNotify.D05 -> 0x05
        DeviceInfoSelectNotify.D06 -> 0x06
        DeviceInfoSelectNotify.D07 -> 0x07
        DeviceInfoSelectNotify.D08 -> 0x08
        DeviceInfoSelectNotify.D09 -> 0x09
        DeviceInfoSelectNotify.D10 -> 0x10
        DeviceInfoSelectNotify.D11 -> 0x11
        DeviceInfoSelectNotify.D12 -> 0x12
        DeviceInfoSelectNotify.D13 -> 0x13
        DeviceInfoSelectNotify.D14 -> 0x14
        DeviceInfoSelectNotify.D15 -> 0x15
        DeviceInfoSelectNotify.D16 -> 0x16
        DeviceInfoSelectNotify.D17 -> 0x17
        DeviceInfoSelectNotify.D18 -> 0x18
        DeviceInfoSelectNotify.D19 -> 0x19
        DeviceInfoSelectNotify.D20 -> 0x20
        DeviceInfoSelectNotify.D21 -> 0x21
        DeviceInfoSelectNotify.D22 -> 0x22
    }

typealias DeviceInfoSelectNotifyMap = Map<TslPropertyKey, Any?>

@OptIn(ExperimentalStdlibApi::class)
fun parseDeviceInfo(payload: ByteArray): DeviceInfoSelectNotifyMap {
    val map: MutableMap<TslPropertyKey, Any?> = mutableMapOf()
    var cur = 0
    while (cur < payload.size - 1) {
        // 数据长度
        val len = payload[cur]
        // 数据
        val data = payload.sliceArray(cur + 1..cur + len)
        // 添加到结果
        d("[parseDeviceInfo]data:${data.display}")
        if (data.isNotEmpty()) {
            val no = data[0]
            val value = data.sliceArray(1..<data.size)
            d("[parseDeviceInfo]       no:${no.toHexString()},value:${value.toHexString()}")
            when (no) {
                DeviceInfoSelectNotify.D01.no -> {
                    map[TslPropertyKey.BarcodeId] = value.dropLastWhile { // 去除结尾的0
                        it == 0.toByte()
                    }.toByteArray().decodeToString()
                }

                DeviceInfoSelectNotify.D02.no -> {
                    try {
                        val filter01 = byteArrayOf(value[2], value[3]).toInt()
                        val filter02 = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.FilterLife1] = filter01
                        map[TslPropertyKey.FilterLife2] = filter02
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D03.no -> {
                    try {
                        val filter03 = byteArrayOf(value[2], value[3]).toInt()
                        val filter04 = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.FilterLife3] = filter03
                        map[TslPropertyKey.FilterLife4] = filter04
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D04.no -> {
                    try {
                        val filter05 = byteArrayOf(value[2], value[3]).toInt()
                        val filter06 = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.FilterLife5] = filter05
                        map[TslPropertyKey.FilterLife6] = filter06
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D05.no -> {
                    try {
                        val filter07 = byteArrayOf(value[2], value[3]).toInt()
                        val filter08 = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.FilterLife7] = filter07
                        map[TslPropertyKey.FilterLife8] = filter08
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D06.no -> {
                    try {
                        d("[parseDeviceInfo]       0x06设备状态解析")
                        val isOpen = value[1].bitCheck0(0)
                        val actionState = value[1].bitCheck1(1)
                        val washState = value[1].bitCheck1(2)
                        val adminLock = value[1].bitCheck1(3)
                        // ...
                        map[TslPropertyKey.IsOpen] = isOpen
                        map[TslPropertyKey.ActionState] = actionState
                        map[TslPropertyKey.WashState] = washState
                        map[TslPropertyKey.AdminLock] = adminLock
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D07.no -> {
                    try {
                        val list = listOfNotNull(
                            if (value[3].bitCheck1(0)) Error.E01 else null,
                            if (value[3].bitCheck1(1)) Error.E02 else null,
                            if (value[3].bitCheck1(2)) Error.E03 else null,
                            if (value[3].bitCheck1(3)) Error.E04 else null,
                            if (value[3].bitCheck1(4)) Error.E05 else null,
                            if (value[3].bitCheck1(5)) Error.E06 else null,
                            if (value[3].bitCheck1(6)) Error.E07 else null,
                            if (value[3].bitCheck1(7)) Error.E08 else null,
                            if (value[2].bitCheck1(0)) Error.E09 else null,
                            if (value[2].bitCheck1(1)) Error.E10 else null,
                            if (value[2].bitCheck1(2)) Error.E11 else null,
                            if (value[2].bitCheck1(3)) Error.E12 else null,
                            if (value[2].bitCheck1(4)) Error.E13 else null,
                            if (value[2].bitCheck1(5)) Error.E14 else null,
                            if (value[2].bitCheck1(6)) Error.E15 else null,
                            if (value[2].bitCheck1(7)) Error.E16 else null,
                            if (value[1].bitCheck1(0)) Error.E17 else null,
                            if (value[1].bitCheck1(1)) Error.E18 else null,
                            if (value[1].bitCheck1(2)) Error.E19 else null,
                            if (value[1].bitCheck1(3)) Error.E20 else null,
                            if (value[1].bitCheck1(4)) Error.E21 else null,
                            if (value[1].bitCheck1(5)) Error.E22 else null,
                            if (value[1].bitCheck1(6)) Error.E23 else null,
                            if (value[1].bitCheck1(7)) Error.E24 else null,
                            if (value[0].bitCheck1(0)) Error.E25 else null,
                            if (value[0].bitCheck1(1)) Error.E26 else null,
                            if (value[0].bitCheck1(2)) Error.E27 else null,
                            if (value[0].bitCheck1(3)) Error.E28 else null,
                            if (value[0].bitCheck1(4)) Error.E29 else null,
                            if (value[0].bitCheck1(5)) Error.E30 else null,
                            if (value[0].bitCheck1(6)) Error.E31 else null,
                            //...
                        )
                        map[TslPropertyKey.IsDeal] = list

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D08.no -> {
                    try {
                        map[TslPropertyKey.AiFlushMode] = value.toInt() == 1
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D09.no -> {
                    try {
                        val high = byteArrayOf(value[2], value[3]).toInt()
                        val lower = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.InTds] = high
                        map[TslPropertyKey.OutTds] = lower
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D10.no -> {
                    try {
                        val high = byteArrayOf(value[2], value[3]).toInt()
                        val lower = byteArrayOf(value[0], value[1]).toInt()
                        map[TslPropertyKey.InTemperature] = high
                        map[TslPropertyKey.OutTemperature] = lower
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D13.no -> {
                    try {
                        map[TslPropertyKey.TotalUsedPureWater] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D14.no -> {
                    try {
                        map[TslPropertyKey.TotalUsedWater] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D15.no -> {
                    try {
                        map[TslPropertyKey.PeriodicRhythm] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D16.no -> {
                    try {
                        map[TslPropertyKey.ResetTimes] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D17.no -> {
                    try {
                        map[TslPropertyKey.FirmwareVersion] = value.decodeToString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D18.no -> {
                    try {
                        map[TslPropertyKey.PowerOnTime] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D19.no -> {
                    try {
                        map[TslPropertyKey.OutageNum] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D20.no -> {
                    try {
                        map[TslPropertyKey.CurrDeviceTime] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D21.no -> {
                    try {
                        map[TslPropertyKey.LastOutageTime] = value.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                DeviceInfoSelectNotify.D22.no -> {
                    try {
                        val list = listOfNotNull(
                            if (value[3].bitCheck1(0)) Protection.S01 else null,
                            if (value[3].bitCheck1(1)) Protection.S02 else null,
                            if (value[3].bitCheck1(2)) Protection.S03 else null,
                            if (value[3].bitCheck1(3)) Protection.S04 else null,
                            if (value[3].bitCheck1(4)) Protection.S05 else null,
                            if (value[3].bitCheck1(5)) Protection.S06 else null,
                            if (value[3].bitCheck1(6)) Protection.S07 else null,
                            if (value[3].bitCheck1(7)) Protection.S08 else null,
                            if (value[2].bitCheck1(0)) Protection.S09 else null,
                            if (value[2].bitCheck1(1)) Protection.S10 else null,
                            if (value[2].bitCheck1(2)) Protection.S11 else null,
                            if (value[2].bitCheck1(3)) Protection.S12 else null,
                            if (value[2].bitCheck1(4)) Protection.S13 else null,
                            if (value[2].bitCheck1(5)) Protection.S14 else null,
                            if (value[2].bitCheck1(6)) Protection.S15 else null,
                            if (value[2].bitCheck1(7)) Protection.S16 else null,
                            if (value[1].bitCheck1(0)) Protection.S17 else null,
                            if (value[1].bitCheck1(1)) Protection.S18 else null,
                            if (value[1].bitCheck1(2)) Protection.S19 else null,
                            if (value[1].bitCheck1(3)) Protection.S20 else null,
                            if (value[1].bitCheck1(4)) Protection.S21 else null,
                            if (value[1].bitCheck1(5)) Protection.S22 else null,
                            //...
                        )
                        map[TslPropertyKey.WarnState] = list
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        // 更新位置
        cur += 1 + data.size
    }
    return map
}

val DeviceInfoSelectNotifyMap.display: String
    get() = "------\n<<设备信息>>\n${
        this.map { (k, v) ->
            "${k.name}  =  ${v}"
        }.joinToString("\n") { it }
    }\n------"
//</editor-fold>

//<editor-fold desc="产测回复0xFE">
fun parseProduction(payload: ByteArray): BleEvent.Production {
    val timestamp = payload.sliceArray(0..3).toInt()
    val code = payload.sliceArray(4..<payload.size).decodeToString()
    return BleEvent.Production(timestamp = timestamp.toLong(), code = code)


}
//</editor-fold>

//<editor-fold desc="返回设置设备信息0xA4"> //
enum class SetDeviceInfoKey {
    Set1,
    Set2,
    Set3,
    Set4,
    Set5,
    Set6,
    Set7,
    Set8,
    Set9,
    Set10,
    Set11,
    Set12,
    Set13,
    Set14,
    Set15,
    Set16,
    Set17,
}

val SetDeviceInfoKey.key: Byte
    get() = when (this) {
        SetDeviceInfoKey.Set1 -> 0x01.toByte()
        SetDeviceInfoKey.Set2 -> 0x02.toByte()
        SetDeviceInfoKey.Set3 -> 0x03.toByte()
        SetDeviceInfoKey.Set4 -> 0x04.toByte()
        SetDeviceInfoKey.Set5 -> 0x05.toByte()
        SetDeviceInfoKey.Set6 -> 0x06.toByte()
        SetDeviceInfoKey.Set7 -> 0x07.toByte()
        SetDeviceInfoKey.Set8 -> 0x08.toByte()
        SetDeviceInfoKey.Set9 -> 0x09.toByte()
        SetDeviceInfoKey.Set10 -> 0x10.toByte()
        SetDeviceInfoKey.Set11 -> 0x11.toByte()
        SetDeviceInfoKey.Set12 -> 0x12.toByte()
        SetDeviceInfoKey.Set13 -> 0x13.toByte()
        SetDeviceInfoKey.Set14 -> 0x14.toByte()
        SetDeviceInfoKey.Set15 -> 0x15.toByte()
        SetDeviceInfoKey.Set16 -> 0x16.toByte()
        SetDeviceInfoKey.Set17 -> 0x17.toByte()
    }

val SetDeviceInfoKey.text: String
    get() = when (this) {
        SetDeviceInfoKey.Set1 -> "配件编号"
        SetDeviceInfoKey.Set2 -> "开关机"
        SetDeviceInfoKey.Set3 -> "冲洗"
        SetDeviceInfoKey.Set4 -> "AI冲洗"
        SetDeviceInfoKey.Set5 -> "锁机"
        SetDeviceInfoKey.Set6 -> "滤芯1/2寿命"
        SetDeviceInfoKey.Set7 -> "滤芯3/4寿命"
        SetDeviceInfoKey.Set8 -> "滤芯5/6寿命"
        SetDeviceInfoKey.Set9 -> "滤芯7/8寿命"
        SetDeviceInfoKey.Set10 -> "滤芯1/2寿命标准总值"
        SetDeviceInfoKey.Set11 -> "滤芯3/4寿命标准总值"
        SetDeviceInfoKey.Set12 -> "滤芯5/6寿命标准总值"
        SetDeviceInfoKey.Set13 -> "滤芯7/8寿命标准总值"
        SetDeviceInfoKey.Set14 -> "锁定设备"
        SetDeviceInfoKey.Set15 -> "激活设备"
        SetDeviceInfoKey.Set16 -> "滤芯复位"
        SetDeviceInfoKey.Set17 -> "更换主板"
    }
typealias SetDeviceInfoNotifyMap = Map<SetDeviceInfoKey, Boolean>

val SetDeviceInfoNotifyMap.display2: String
    get() = "------\n<<设置设备信息结果>>\n${
        this.map { (k, v) ->
            "${k.text}  =  ${if (v) "成功" else "失败"}"
        }.joinToString("\n") { it }
    }\n------"

@OptIn(ExperimentalStdlibApi::class)
fun parseSetDeviceInfo(payload: ByteArray): SetDeviceInfoNotifyMap {
    // 5aa5a4 0002 0200 a7
    val map: MutableMap<SetDeviceInfoKey, Boolean> = mutableMapOf()
    var cur = 0
    while (cur < payload.size - 1) {
        // 数据长度
        val len = 2
        // 数据
        val data = payload.sliceArray(cur..<cur + len)
        // 添加到结果
        d("[parseSetDeviceInfo]data:${data.display}")
        if (data.isNotEmpty()) {
            val no = data[0]
            d("[parseSetDeviceInfo]       no:${no.toHexString()},value:${data[1].toHexString()}")
            when (no) {
                SetDeviceInfoKey.Set1.key -> {
                    val value = data[1].toInt() == 0
                    map[SetDeviceInfoKey.Set1] = value
                }

                SetDeviceInfoKey.Set2.key -> {
                    val value = data[1].toInt() == 0
                    map[SetDeviceInfoKey.Set2] = value
                }

                SetDeviceInfoKey.Set3.key -> {
                    val value = data[1].toInt() == 0
                    map[SetDeviceInfoKey.Set3] = value
                }

                SetDeviceInfoKey.Set4.key -> {
                    val value = data[1].toInt() == 0
                    map[SetDeviceInfoKey.Set4] = value
                }

                SetDeviceInfoKey.Set5.key -> {
                    val value = data[1].toInt() == 0
                    map[SetDeviceInfoKey.Set5] = value
                }

                else -> throw RuntimeException("todo")
            }
        }
        // 更新位置
        cur += data.size
    }
    return map


}
//</editor-fold>