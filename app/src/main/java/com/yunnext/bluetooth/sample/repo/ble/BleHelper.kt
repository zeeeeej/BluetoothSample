package com.yunnext.bluetooth.sample.repo.ble

import android.app.Application
import android.bluetooth.BluetoothGatt
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleMtuChangedCallback
import com.clj.fastble.callback.BleNotifyCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object BleHelper {

    const val PREFIX = "light_"

    fun initApp(app: Application) {
        BleManager.getInstance().splitWriteNum = 240
        BleManager.getInstance().init(app)
        BleManager.getInstance().setReConnectCount(0)
        BleManager.getInstance().setOperateTimeout(30000)
    }


    suspend fun startScan(code: String): com.yunnext.bluetooth.sample.domain.HDResult<BleDevice> = suspendCancellableCoroutine { con ->
        d("startScan")
        if (!BleManager.getInstance().isBlueEnable) {
            con.resumeWithException(RuntimeException("蓝牙已关闭"))
        } else {
            BleManager.getInstance().initScanRule(
                BleScanRuleConfig.Builder().setScanTimeOut(15000).build()
            )
            val bleScanCallback = object : BleScanCallback() {

                override fun onScanStarted(success: Boolean) {

                }

                override fun onScanning(bleDevice: com.clj.fastble.data.BleDevice?) {
                    val d = bleDevice ?: return
                    val deviceName = d.name ?: ""
                    if (deviceName.startsWith(
                            BleHelper.PREFIX,
                            true
                        ) && deviceName.endsWith(code.takeLast(6))
                    ) {
                        if (!con.isCompleted) {
                            BleManager.getInstance().cancelScan()
                            con.resume(com.yunnext.bluetooth.sample.domain.HDResult.Success(d))
                        }
                    }
                }

                override fun onScanFinished(scanResultList: MutableList<com.clj.fastble.data.BleDevice>?) {
//                if (!con.isCompleted) {
//                    con.resume(HDResult.Fail(RuntimeException("找不到设备")))
//                }
                    if (!con.isCompleted) {
                        con.resumeWithException(RuntimeException("找不到设备"))
                    }
                }
            }
            BleManager.getInstance().scan(bleScanCallback)
        }


        con.invokeOnCancellation {
            d("startScan::invokeOnCancellation")
            BleManager.getInstance().cancelScan()
        }
    }


    private suspend fun updateMtu(device: BleDevice): Boolean {
        return suspendCancellableCoroutine<Boolean> { con ->
            BleManager.getInstance()
                .setMtu(device, Protocol.MTU, object : BleMtuChangedCallback() {
                    override fun onSetMTUFailure(exception: BleException?) {
                        con.resume(false)
                    }

                    override fun onMtuChanged(mtu: Int) {
                        con.resume(true)
                    }
                })
            con.invokeOnCancellation {
            }
        }
    }

//    fun write(device: BleDevice,data:ByteArray){
//
//        fun msg(msg: String, extra: (String) -> Unit) {
//            extra(msg)
//            trySend(BleEvent.Msg(msg = msg))
//        }
//
//        BleManager.getInstance().write(
//            device,
//            Protocol.UUID_SERVICE,
//            Protocol.UUID_CH_WRITE,
//            data,
//            object : BleWriteCallback() {
//                override fun onWriteSuccess(
//                    current: Int,
//                    total: Int,
//                    justWrite: ByteArray?
//                ) {
//                    msg("写入${data.display}成功") {
//                        d(it)
//                    }
//                }
//
//                override fun onWriteFailure(exception: BleException?) {
//                    trySend(BleEvent.Error("onWriteFailure"))
//                    msg("写入${data.display}失败${exception}") {
//                        w(it)
//                    }
//                }
//
//            })
//    }

    private val writeChannel: Channel<ByteArray> = Channel()

    private fun write(data: ByteArray) {
        writeChannel.trySend(data)
    }

    fun power(on: Boolean) {
        val value = if (on) byteArrayOf(1) else byteArrayOf(0)
        val no = byteArrayOf(0x02)
        val length = byteArrayOf((value + no).size.toByte())
        val payload = length + no + value
        val power = Protocol.rtcData(
            ProtocolCmd.DeviceInfoUpdateWrite, payload
        )
        write(power.toByteArray())
    }

    fun wash(on: Boolean) {
        val value = if (on) byteArrayOf(1) else byteArrayOf(0)
        val no = byteArrayOf(0x03)
        val length = byteArrayOf((value + no).size.toByte())
        val payload = length + no + value
        val power = Protocol.rtcData(
            ProtocolCmd.DeviceInfoUpdateWrite, payload
        )
        write(power.toByteArray())
    }

    fun production() {
        val payload = byteArrayOf()
        val power = Protocol.rtcData(
            ProtocolCmd.ProductionWriteNotify, payload
        )
        write(power.toByteArray())
    }

    fun connect(device: BleDevice): Flow<BleEvent> {
        return channelFlow {


            fun (() -> Unit).opt() {
                launch {
                    delay(200)
                    this@opt()
                }
            }

            fun msg(msg: String, extra: (String) -> Unit) {
                extra(msg)
                trySend(BleEvent.Msg(msg = msg))
            }


            val writeBlock: (ByteArray) -> Unit = { data: ByteArray ->
                msg("写入${data.display}") {
                    i(it)
                }

                BleManager.getInstance().write(
                    device,
                    Protocol.UUID_SERVICE,
                    Protocol.UUID_CH_WRITE,
                    data,
                    object : BleWriteCallback() {
                        override fun onWriteSuccess(
                            current: Int,
                            total: Int,
                            justWrite: ByteArray?
                        ) {
                            msg("写入${data.display}成功") {
                                d(it)
                            }
                        }

                        override fun onWriteFailure(exception: BleException?) {
                            trySend(BleEvent.Error("onWriteFailure"))
                            msg("写入${data.display}失败${exception}") {
                                w(it)
                            }
                        }

                    })
            }

            val authBlock: () -> Unit = {

                try {
                    val dest = Protocol.parseFromBroadcast(device.name ?: "")
                    check(!dest.isNullOrEmpty()) {
                        "dest为空"
                    }
                    val authenticationWrite = Protocol.authenticationWrite(Protocol.ACCESSKEY, dest)
                    val authWrite = authenticationWrite.toByteArray()
                    msg("开始鉴权[${ProtocolCmd.AuthWrite}] 目标：${dest}") {
                        i(it)
                    }
                    writeBlock(authWrite)
                } catch (e: Exception) {
                    e.printStackTrace()
                    msg("鉴权失败$e") { i(it) }
                    trySend(BleEvent.Error("auth ${e.localizedMessage}"))
                }
            }

            val syncDeviceInfo: () -> Unit = {
                try {

                    val syncDeviceInfo = Protocol.rtcData(
                        ProtocolCmd.DeviceInfoSelectWrite,
                        byteArrayOf()
                    )
                    val payload = syncDeviceInfo.toByteArray()
                    msg("开始同步设备信息[${ProtocolCmd.DeviceInfoSelectWrite}]") {
                        i(it)
                    }
                    writeBlock(payload)
                } catch (e: Exception) {
                    e.printStackTrace()
                    msg("同步设备信息$e") { i(it) }
                    trySend(BleEvent.Error("auth ${e.localizedMessage}"))
                }
            }

            val parseData = { data: ByteArray ->
                require(data.size >= 6) {
                    "数据错误$data"
                }
                require(byteArrayOf(data[0], data[1]).contentEquals(Protocol.HEAD)) {
                    "数据头错误$data"
                }
                val length = byteArrayOf(data[3], data[4])

                @OptIn(ExperimentalStdlibApi::class)
                val lengthInt = byteArrayOf(data[3], data[4]).toHexString().toInt(16)

                if (lengthInt == 0) {
                    require(data.size == 6) {
                        "数据长度错误2$data"
                    }
                } else {
                    require(data.size - 6 == lengthInt) {
                        "数据长度错误1$data"
                    }
                }

                val crc = (data.dropLast(1).sum() % 256).toByte()
                require(crc == data[data.size - 1]) {
                    "crc错误$data"
                }
                val cmd = data[2]
                val payload =
                    if (lengthInt == 0) byteArrayOf() else data.copyOfRange(5, 5 + lengthInt)
                ProtocolData(
                    head = Protocol.HEAD, cmd = cmd, length = length, payload = payload, crc = crc
                )
            }


            val notifyBlock = {
                val service = Protocol.UUID_SERVICE
                val ch = Protocol.UUID_CH_NOTIFY
                msg("打开Notify $service/$ch") { i(it) }
                BleManager.getInstance().notify(
                    device,
                    service,
                    ch,
                    object : BleNotifyCallback() {
                        override fun onNotifySuccess() {
                            msg("打开Notify成功") { d(it) }
                            authBlock.opt()
                        }

                        override fun onNotifyFailure(exception: BleException?) {
                            msg("打开Notify失败${exception}") { w(it) }
                            trySend(BleEvent.Error("打开Notify失败${exception}"))
                        }

                        override fun onCharacteristicChanged(data: ByteArray?) {
                            data ?: return
                            msg("收到${data.display}") { d(it) }
                            // 解析
                            try {
                                val protocolData = parseData(data)
                                when (protocolData.cmd) {
                                    ProtocolCmd.AuthNotify.cmd -> {
                                        val payload = protocolData.payload
                                        val success = payload.contentEquals(byteArrayOf(0x00))
                                        msg("收到鉴权结果：$success") { i(it) }
                                        trySend(BleEvent.Authed(success))

                                        if (success) {
                                            syncDeviceInfo.opt()
                                        }
                                    }

                                    ProtocolCmd.VersionNotify.cmd -> {
                                        val payload = protocolData.payload
                                        val version = payload.decodeToString()
                                        msg("收到版本信息：[$version]") { i(it) }
                                        trySend(BleEvent.Version(version))
                                    }

                                    ProtocolCmd.DeviceInfoSelectNotify.cmd -> {
                                        // 5aa5a2008a19013131373338373632303937363535373932383131000000000502006400640503006400000504000000000505000000000306000005070000000002080105090000000105100000000005130000000005140000000005150000001905160000000408175357312e302e35051800003bd205190000000005200000453105210000095f052200000000cc
                                        val payload = protocolData.payload
                                        msg("收到设备信息：") { i(it) }
                                        val map = parseDeviceInfo(payload)
                                        msg(map.display) {
                                            d(it)
                                        }
                                        trySend(BleEvent.DeviceInfo(map))
                                    }

                                    ProtocolCmd.ProductionWriteNotify.cmd -> {
                                        val payload = protocolData.payload
                                        msg("收到产测信息：") { i(it) }

                                        try {
                                            val r = parseProduction(payload)
                                            msg(r.toString()) {
                                                d(it)
                                            }
                                            trySend(r)
                                        } catch (e: Exception) {
                                            msg("收到产测信息：解析失败") { w(it) }
                                        }

                                    }

                                    ProtocolCmd.DeviceInfoUpdateNotify.cmd -> {
                                        val payload = protocolData.payload
                                        msg("收到返回设置设备信息：") { i(it) }

                                        try {
                                            val r = parseSetDeviceInfo(payload)
                                            msg(r.display2) {
                                                d(it)
                                            }
                                            trySend(BleEvent.SetDeviceResult(r))
                                        } catch (e: Exception) {
                                            msg("收到返回设置设备信息解析失败:$e") { w(it) }
                                        }
                                    }

                                    else -> {
                                        trySend(BleEvent.Notify(data = parseData(data)))
                                    }
                                }
                            } catch (e: Exception) {
                                msg("数据异常：$e ->${data.display}") { w(it) }
                            }
                        }
                    })
            }

            val mtuBlock = {
                msg("设置MTU") { i(it) }
                BleManager.getInstance()
                    .setMtu(device, Protocol.MTU, object : BleMtuChangedCallback() {
                        override fun onSetMTUFailure(exception: BleException?) {
                            msg("设置MTU失败$exception") { w(it) }
                            trySend(BleEvent.Error("onSetMTUFailure"))
                        }

                        override fun onMtuChanged(mtu: Int) {
                            msg("设置MTU成功$mtu") { d(it) }
                            notifyBlock.opt()
                        }
                    })
            }

            val connectBlock = {
                msg("开始连接 ${device.mac}/${device.name}") { i(it) }
                BleManager.getInstance().connect(device.mac, object : BleGattCallback() {
                    override fun onStartConnect() {
                    }

                    override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) {
                        msg("连接失败$exception") { w(it) }
                        trySend(BleEvent.Error("连接失败$exception"))
                    }

                    override fun onConnectSuccess(
                        bleDevice: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) {
                        msg("连接成功") { d(it) }
                        mtuBlock.opt()
                    }

                    override fun onDisConnected(
                        isActiveDisConnected: Boolean,
                        device: BleDevice?,
                        gatt: BluetoothGatt?,
                        status: Int
                    ) {
                        msg("断开连接isActiveDisConnected:$isActiveDisConnected") { w(it) }
                        trySend(BleEvent.Disconnect(isActiveDisConnected))
                    }
                })
            }

            launch {
                writeChannel.receiveAsFlow().collect { data ->
                    writeBlock(data)
                }
            }
            connectBlock()

            awaitClose() {
                msg("connect::awaitClose") { i(it) }
                BleManager.getInstance().disconnect(device)
            }
        }
    }
}

sealed interface BleEvent {
    data class Disconnect(val isActiveDisConnected: Boolean) : BleEvent {
        override fun toString(): String {
            return "已断开：$isActiveDisConnected"
        }
    }

    data object Connected : BleEvent {
        override fun toString(): String {
            return "已连接"
        }
    }

    data class Error(val msg: String) : BleEvent {
        override fun toString(): String {
            return "错误：$msg"
        }
    }

    data class Authed(val success: Boolean) : BleEvent {
        override fun toString(): String {
            return "鉴权：$success"
        }
    }

    data class Version(val version: String) : BleEvent {
        override fun toString(): String {
            return "版本:$version"
        }
    }

    data class DeviceInfo(val properties: DeviceInfoSelectNotifyMap) : BleEvent {
        override fun toString(): String {
            return "设备信息:$properties"
        }
    }

    data class SetDeviceResult(val properties: SetDeviceInfoNotifyMap) : BleEvent {
        override fun toString(): String {
            return "设置设备信息:$properties"
        }
    }

    data class Notify(val data: ProtocolData) : BleEvent {
        override fun toString(): String {
            return "收到" + data.toByteArray().display
        }
    }

    data class Production(val timestamp: Long, val code: String) : BleEvent {
        override fun toString(): String {
            return "收到" + timestamp + "秒，" + "code=$code"
        }
    }

    data class Msg(val msg: String) : BleEvent
}

val BleEvent.Production.display: String
    get() = "时间戳：${datetimeFormat { (this@display.timestamp * 1000L).toStr() }}\n配件码：$code"

