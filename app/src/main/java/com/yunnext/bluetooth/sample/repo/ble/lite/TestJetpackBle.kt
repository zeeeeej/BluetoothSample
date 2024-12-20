package com.yunnext.bluetooth.sample.repo.ble.lite

import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.bluetooth.AdvertiseParams
import androidx.bluetooth.BluetoothLe
import androidx.bluetooth.BluetoothLe.Companion.ADVERTISE_STARTED
import androidx.bluetooth.GattCharacteristic
import androidx.bluetooth.GattService
import com.king.mlkit.vision.camera.util.LogUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import java.util.UUID

private const val BASE_UUID_HIS = "00000000-0000-1000-6864-79756e657874"

class TestJetpackBle(ctx: Context) {

    private val context = ctx.applicationContext
    private val coroutineScope =
        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob() + CoroutineName(TAG))
    private val bluetoothLe = BluetoothLe(context = context)

    private fun generateAdvertiseParams(): AdvertiseParams {
        return AdvertiseParams(
            isConnectable = true,
            shouldIncludeDeviceAddress = true,
            shouldIncludeDeviceName = true,
            serviceUuids = listOf(UUID.fromString(BASE_UUID_HIS))
        )
    }

    private fun generateGattService(): GattService {
        return GattService(
            uuid = UUID.randomUUID(),
            characteristics = listOf(
                GattCharacteristic(
                    uuid = UUID.randomUUID(),
                    GattCharacteristic.PROPERTY_READ or
                            GattCharacteristic.PROPERTY_WRITE or
                            GattCharacteristic.PROPERTY_NOTIFY
                )
            )
        )
    }

    fun startServer() {
        coroutineScope.launch {
            bluetoothLe.advertise(generateAdvertiseParams())
                .onCompletion {
                    i("startServer over!!!!")
                }
                .collect {
                    i("startServer:$it")

                    if (it == ADVERTISE_STARTED) {
                        bluetoothLe.openGattServer(listOf(generateGattService(),generateGattService()))
                            .onCompletion {
                                i("openGattServer over!!!!!")
                            }
                            .collect { request ->
                                i("==>openGattServer $request")
                                request.accept {
                                    val d = this@accept.device
                                    i("openGattServer accept device:${d.name} ${d.bondState}")

                                    launch {
                                        this@accept.subscribedCharacteristics.collect { chs ->
                                            i("openGattServer accept subscribedCharacteristics:${chs.size}")
                                            chs.forEach { c ->
                                                i("openGattServer accept subscribedCharacteristics:${c.uuid}-${c.properties}-${c.fwkCharacteristic.service.uuid}")
                                            }

                                        }
                                    }

                                    launch {
                                        this@accept.requests.collect {
                                            i("openGattServer accept requests:${it}")
                                        }
                                    }
                                }
                                i("openGattServer accept over.")
                            }
                    }
                }


        }.invokeOnCompletion {
            i("end!")
        }
    }

    private fun i(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "TestJetpackBle"
    }
}