package com.yunnext.bluetooth.sample.repo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice


val BluetoothAdapter.supportDisplay: String
    get() = """
        |BluetoothAdapter支持：
        |isLe2MPhySupported                     ${this.isLe2MPhySupported}
        |isLeAudioSupported                     {this.isLeAudioSupported} // android13 33支持
        |isLeAudioBroadcastAssistantSupported   {this.isLeAudioBroadcastAssistantSupported} // android13 33支持
        |isLeAudioBroadcastSourceSupported      {this.isLeAudioBroadcastSourceSupported} // android13 33支持
        |isLeCodedPhySupported                  ${this.isLeCodedPhySupported}
        |isLePeriodicAdvertisingSupported       ${this.isLePeriodicAdvertisingSupported}
        |isLeExtendedAdvertisingSupported       ${this.isLeExtendedAdvertisingSupported}
        |isOffloadedFilteringSupported          ${this.isOffloadedFilteringSupported}
        |isMultipleAdvertisementSupported       ${this.isMultipleAdvertisementSupported}
        |isOffloadedScanBatchingSupported       ${this.isOffloadedScanBatchingSupported}
    """.trimMargin("|")


val BluetoothDevice.display: String
    get() = "[${this.address}]${this.name} (${this.deviceClazz})"
val BluetoothDevice.deviceClazz: String
    get() = when (val type = this.bluetoothClass.deviceClass and 0x1f00) {
        BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> {
            "耳机"
        }

        BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE -> {
            "麦克风"
        }

        BluetoothClass.Device.Major.COMPUTER -> {
            "电脑"
        }

        BluetoothClass.Device.Major.PHONE -> {
            "手机"
        }

        BluetoothClass.Device.Major.HEALTH -> {
            "健康类设备"
        }

        else -> {
            "其他"
        }

    }