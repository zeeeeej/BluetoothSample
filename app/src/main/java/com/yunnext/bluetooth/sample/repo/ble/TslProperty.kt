package com.yunnext.bluetooth.sample.repo.ble

class TslProperty {

}

enum class TslPropertyKey {
    BarcodeId, FilterLife1, FilterLife2, FilterLife3, FilterLife4, FilterLife5, FilterLife6, FilterLife7, FilterLife8,
    IsOpen, ActionState, WashState, AdminLock, IsDeal, AiFlushMode, InTds, OutTds, InTemperature, OutTemperature,
    PureWater, TotalWater, TotalUsedPureWater, TotalUsedWater, PeriodicRhythm, ResetTimes, FirmwareVersion,
    PowerOnTime, OutageNum, CurrDeviceTime, LastOutageTime, WarnState,
    ;
}

val TslPropertyKey.unit: String
    get() = when (this) {
        TslPropertyKey.BarcodeId -> ""
        TslPropertyKey.FilterLife1 -> "百分比"
        TslPropertyKey.FilterLife2 -> "百分比"
        TslPropertyKey.FilterLife3 -> "百分比"
        TslPropertyKey.FilterLife4 -> "百分比"
        TslPropertyKey.FilterLife5 -> "百分比"
        TslPropertyKey.FilterLife6 -> "百分比"
        TslPropertyKey.FilterLife7 -> "百分比"
        TslPropertyKey.FilterLife8 -> "百分比"
        TslPropertyKey.IsOpen -> ""
        TslPropertyKey.ActionState -> ""
        TslPropertyKey.WashState -> ""
        TslPropertyKey.AdminLock -> ""
        TslPropertyKey.IsDeal -> ""
        TslPropertyKey.AiFlushMode -> ""
        TslPropertyKey.InTds -> "ppm"
        TslPropertyKey.OutTds -> "ppm"
        TslPropertyKey.InTemperature -> "摄氏度"
        TslPropertyKey.OutTemperature -> "摄氏度"
        TslPropertyKey.PureWater -> "毫升"
        TslPropertyKey.TotalWater -> "毫升"
        TslPropertyKey.TotalUsedPureWater -> "毫升"
        TslPropertyKey.TotalUsedWater -> "毫升"
        TslPropertyKey.PeriodicRhythm -> "毫升"
        TslPropertyKey.ResetTimes -> "次"
        TslPropertyKey.FirmwareVersion -> ""
        TslPropertyKey.PowerOnTime -> "秒"
        TslPropertyKey.OutageNum -> "秒"
        TslPropertyKey.CurrDeviceTime -> "秒"
        TslPropertyKey.LastOutageTime -> "秒"
        TslPropertyKey.WarnState -> ""
    }


fun TslPropertyKey.value(value: Any?): String? {
    value ?: return null
    return when (this) {
        TslPropertyKey.BarcodeId -> value as String
        TslPropertyKey.FilterLife1 -> (value as Int).toString()
        TslPropertyKey.FilterLife2 -> (value as Int).toString()
        TslPropertyKey.FilterLife3 -> (value as Int).toString()
        TslPropertyKey.FilterLife4 -> (value as Int).toString()
        TslPropertyKey.FilterLife5 -> (value as Int).toString()
        TslPropertyKey.FilterLife6 -> (value as Int).toString()
        TslPropertyKey.FilterLife7 -> (value as Int).toString()
        TslPropertyKey.FilterLife8 -> (value as Int).toString()
        TslPropertyKey.IsOpen -> if (value as Boolean) "开机" else "关机"
        TslPropertyKey.ActionState -> if (value as Boolean) "满水" else "制水"
        TslPropertyKey.WashState -> if (value as Boolean) "冲洗中" else "冲洗完成"
        TslPropertyKey.AdminLock -> if (value as Boolean) "状态aA" else "状态A"
        TslPropertyKey.IsDeal -> {
            val list = value as List<*>
            if (list.isEmpty()) "异常状态关闭" else "异常状态开启"
        }

        TslPropertyKey.AiFlushMode -> if (value as Boolean) "模式关" else "模式开"
        TslPropertyKey.InTds -> "${value as Int}"
        TslPropertyKey.OutTds -> "${value as Int}"
        TslPropertyKey.InTemperature -> "${value as Int}"
        TslPropertyKey.OutTemperature -> "${value as Int}"
        TslPropertyKey.PureWater -> "${value as Int}"
        TslPropertyKey.TotalWater -> "${value as Int}"
        TslPropertyKey.TotalUsedPureWater -> "${value as Int}"
        TslPropertyKey.TotalUsedWater -> "${value as Int}"
        TslPropertyKey.PeriodicRhythm -> "${value as Int}"
        TslPropertyKey.ResetTimes -> "${value as Int}"
        TslPropertyKey.FirmwareVersion -> value as String
        TslPropertyKey.PowerOnTime -> "${value as Int}"
        TslPropertyKey.OutageNum -> "${value as Int}"
        TslPropertyKey.CurrDeviceTime -> "${value as Int}"//datetimeFormat { ((value as Int)*1000L).toStr() }
        TslPropertyKey.LastOutageTime -> "${value as Int}"//datetimeFormat { ((value as Int)*1000L).toStr() }
        TslPropertyKey.WarnState -> {
            val list = value as List<*>
            if (list.isEmpty()) "保护状态关闭" else "保护状态开启"
        }
    }
}


val TslPropertyKey.text: String
    get() = when (this) {
        TslPropertyKey.BarcodeId -> "配件编号"
        TslPropertyKey.FilterLife1 -> "滤芯寿命1"
        TslPropertyKey.FilterLife2 -> "滤芯寿命2"
        TslPropertyKey.FilterLife3 -> "滤芯寿命3"
        TslPropertyKey.FilterLife4 -> "滤芯寿命4"
        TslPropertyKey.FilterLife5 -> "滤芯寿命5"
        TslPropertyKey.FilterLife6 -> "滤芯寿命6"
        TslPropertyKey.FilterLife7 -> "滤芯寿命7"
        TslPropertyKey.FilterLife8 -> "滤芯寿命8"
        TslPropertyKey.IsOpen -> "开关机状态"
        TslPropertyKey.ActionState -> "制水/满水（待机）"
        TslPropertyKey.WashState -> "冲洗"
        TslPropertyKey.AdminLock -> "锁机状态"
        TslPropertyKey.IsDeal -> "异常状态"
        TslPropertyKey.AiFlushMode -> "AI冲洗模式"
        TslPropertyKey.InTds -> "进水温度"
        TslPropertyKey.OutTds -> "出水TDS"
        TslPropertyKey.InTemperature -> "进水温度"
        TslPropertyKey.OutTemperature -> "出水温度"
        TslPropertyKey.PureWater -> "周期纯水量"
        TslPropertyKey.TotalWater -> "周期总水量"
        TslPropertyKey.TotalUsedPureWater -> "总历史纯水量"
        TslPropertyKey.TotalUsedWater -> "总历史用水量"
        TslPropertyKey.PeriodicRhythm -> "周期节奏"
        TslPropertyKey.ResetTimes -> "复位次数"
        TslPropertyKey.FirmwareVersion -> "固件版本号"
        TslPropertyKey.PowerOnTime -> "上电时间"
        TslPropertyKey.OutageNum -> "断电次数"
        TslPropertyKey.CurrDeviceTime -> "设备实际内置时间"
        TslPropertyKey.LastOutageTime -> "最后一次断电时间"
        TslPropertyKey.WarnState -> "保护状态"
    }

enum class Error {
    E01, E02, E03, E04, E05, E06, E07, E08, E09, E10, E11, E12, E13, E14, E15, E16, E17, E18, E19, E20, E21, E22, E23, E24, E25, E26, E27, E28, E29, E30, E31
    ;
}

enum class Protection {
    S01, S02, S03, S04, S05, S06, S07, S08, S09, S10, S11, S12, S13, S14, S15, S16, S17, S18, S19, S20, S21, S22,
    ;
}

