package com.yunnext.bluetooth.sample.ui.screen

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunnext.bluetooth.sample.domain.BaseState
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.LoadingState
import com.yunnext.bluetooth.sample.domain.ToastState
import com.yunnext.bluetooth.sample.domain.effectCompleted
import com.yunnext.bluetooth.sample.domain.effectDoIng
import com.yunnext.bluetooth.sample.domain.effectFail
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.domain.effectSuccess
import com.yunnext.bluetooth.sample.repo.DiscoverableMode
import com.yunnext.bluetooth.sample.repo.HDBluetoothManager
import com.yunnext.bluetooth.sample.repo.LocalDevice
import com.yunnext.bluetooth.sample.repo.ble.d
import com.yunnext.bluetooth.sample.repo.deviceClazz
import com.yunnext.bluetooth.sample.ui.MyApp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class DeviceVo(val name: String, val address: String, val type: String)

private fun BluetoothDevice.asDeviceVo() =
    DeviceVo(name = this.name ?: "", address = this.address, type = this.deviceClazz)

data class MainState(
    override val loading: LoadingState = LoadingState.NaN,
    override val toast: ToastState = ToastState.NaN,
    val discoveryDevices: List<DeviceVo> = emptyList(),
    val connectDevice: DeviceVo? = null,
    val scanEffect: Effect<Unit, Unit> = Effect.Idle,
    val bindEffect: Effect<String, Unit> = Effect.Idle,
    val playEffect: Effect<String, Unit> = Effect.Idle,
    val recordEffect: Effect<String, Unit> = Effect.Idle,
    val cancelBindEffect: Effect<String, Unit> = Effect.Idle,
    val bondedDevices: List<DeviceVo> = emptyList(),
    val startDiscoverable: Effect<Unit, DiscoverableMode> = Effect.Idle,
    val localDevice: LocalDevice = LocalDevice("", "", false)
) : BaseState

class MainViewModel() : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state = _state.asStateFlow()
    private val hdBluetoothManager by lazy {
        HDBluetoothManager(MyApp.CONTEXT)
    }

    private var startRecordJob: Job? = null
    private var startPlayJob: Job? = null
    private var startScanJob: Job? = null
    private var bondJob: Job? = null
    private var cancelBondJob: Job? = null
    private var questDiscoverableJob: Job? = null
    private var connectJob: Job? = null

    init {
        viewModelScope.launch {
            hdBluetoothManager.localDeviceFlow().collect() {
                _state.value = state.value.copy(localDevice = it)
            }
        }
    }

    fun startScan(timeout: Long = 12000L) {
        startScanJob?.cancel()
        startScanJob = viewModelScope.launch {
            try {


                withTimeout(timeout) {
                    _state.value = state.value.copy(
                        scanEffect = effectIdle(),
                        discoveryDevices = emptyList(),
                        bondedDevices = emptyList()
                    )
                    _state.value = state.value.copy(scanEffect = effectDoIng())
                    val j1 = launch {
                        hdBluetoothManager.discoveryDeviceFlow()
                            .collect() { source ->
                                _state.value =
                                    state.value.copy(discoveryDevices = source.map(BluetoothDevice::asDeviceVo))
                            }
                    }

                    val j2 = launch {
                        hdBluetoothManager.bondedDeviceFlow().collect() { source ->
                            _state.value =
                                state.value.copy(bondedDevices = source.map(BluetoothDevice::asDeviceVo))
                        }
                    }
                    j1.join()
                    j2.join()
                    _state.value = state.value.copy(scanEffect = effectSuccess())
                }
            } catch (e: Throwable) {

                if (e is TimeoutCancellationException) {
                    _state.value = state.value.copy(scanEffect = effectFail(e))
                } else {
                    _state.value = state.value.copy(scanEffect = effectFail(e))
                }
            }
        }.also {
            it.invokeOnCompletion {
                _state.value = state.value.copy(scanEffect = effectCompleted())
            }
        }
    }

    fun stopScan() {
        startScanJob?.cancel()
    }

    fun startRecord() {
        startRecordJob?.cancel()
        startRecordJob = viewModelScope.launch {
            val name = HDBluetoothManager.DEFAULT_NAME
            try {
                hdBluetoothManager.startRecordAudioFlow(name).collect() {
                    _state.value = state.value.copy(recordEffect = it)
                }
            } catch (e: Throwable) {
                _state.value = state.value.copy(recordEffect = effectFail(name, e))
            }
        }.also {
            it.invokeOnCompletion {
                _state.value = state.value.copy(recordEffect = effectCompleted())
            }
        }


    }

    fun stopRecord() {
        startRecordJob?.cancel()
    }

    fun startPlay() {
        startPlayJob?.cancel()
        startPlayJob = viewModelScope.launch {
            val name = HDBluetoothManager.DEFAULT_NAME
            try {
                hdBluetoothManager.playFlow(name = name).collect {
                    when (it) {
                        Effect.Completed -> {}
                        is Effect.Progress<*, *> -> {}
                        is Effect.Fail -> {}
                        Effect.Idle -> {}
                        is Effect.Success -> {}
                    }
                    _state.value = state.value.copy(playEffect = it)
                }
            } catch (e: Throwable) {
                _state.value = state.value.copy(playEffect = effectFail(input = name, output = e))
            }
        }.also {
            it.invokeOnCompletion {
                _state.value = state.value.copy(playEffect = effectCompleted())
            }
        }

    }

    fun stopPlay() {
        startPlayJob?.cancel()
        startPlayJob = null
    }

    fun bond(device: DeviceVo) {
        bondJob?.cancel()
        d("testBind bind $device")
        bondJob = viewModelScope.launch {
            try {
                withTimeout(10000) {
                    hdBluetoothManager.bindFlow(deviceAddress = device.address).collect {
                        when (it) {
                            Effect.Completed -> {
                                _state.value = state.value.copy(bindEffect = it)
                            }

                            is Effect.Progress<*, *> -> {
                                _state.value = state.value.copy(bindEffect = it)
                            }

                            is Effect.Fail -> {
                                _state.value = state.value.copy(bindEffect = it)
                            }

                            Effect.Idle -> {
                                _state.value = state.value.copy(bindEffect = it)
                            }

                            is Effect.Success -> {
                                _state.value = state.value.copy(
                                    discoveryDevices = emptyList(),
                                    bondedDevices = emptyList()
                                )
                                _state.value = state.value.copy(bindEffect = it)
                                delay(2000)
                                startScan()
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                d("testBind e: $e")
                if (e is TimeoutCancellationException) {
                    _state.value = state.value.copy(bindEffect = effectFail(device.address, e))
                } else {
                    _state.value = state.value.copy(bindEffect = effectFail(device.address, e))
                }
//                _state.value = state.value.copy(bindEffect = effectFail(device.address,Throwable("绑定超时！")))
            }
        }.also {
            it.invokeOnCompletion {
                d("testBind invokeOnCompletion: $it")
                _state.value = state.value.copy(bindEffect = effectCompleted())
                _state.value = state.value.copy(bindEffect = effectIdle())
            }
        }
    }

    fun stopBond() {
        bondJob?.cancel()
    }

    fun cancelBond(deviceAddress: String) {
        cancelBondJob?.cancel()
        cancelBondJob = viewModelScope.launch {
            try {
                _state.value = state.value.copy(cancelBindEffect = effectIdle())
                hdBluetoothManager.cancelBond(deviceAddress = deviceAddress)
                _state.value =
                    state.value.copy(cancelBindEffect = effectSuccess(deviceAddress, Unit))
                delay(2000)
                if (state.value.connectDevice?.address == deviceAddress) {
                    disconnect()
                }
                startScan()
            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = state.value.copy(cancelBindEffect = effectFail(deviceAddress, e))
            } finally {
                _state.value = state.value.copy(cancelBindEffect = effectCompleted())
            }
        }
    }
    fun cancelBond(device: DeviceVo) {
        cancelBond(deviceAddress = device.address)

    }

    fun disconnect() {
        connectJob?.cancel()
    }

    fun connect(device: DeviceVo) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            hdBluetoothManager.connectA2dpFlow(deviceAddress = device.address).collect {
                when (it) {
                    Effect.Completed -> {}
                    is Effect.Progress<*, *> -> {}
                    is Effect.Fail -> {}
                    Effect.Idle -> {}
                    is Effect.Success -> {
                        _state.value = state.value.copy(connectDevice = it.output.asDeviceVo())
                        delay(1000)
                        startPlay()
                    }
                }
            }
        }.also {
            it.invokeOnCompletion {
                _state.value = state.value.copy(connectDevice = null)
                stopPlay()
            }
        }
    }

    fun questDiscoverable(activity: Context) {
        questDiscoverableJob?.cancel()
        questDiscoverableJob = viewModelScope.launch {
            try {
                if (activity is ComponentActivity) {
                    hdBluetoothManager.questDiscoverable(activity).collect() {
                        when (it) {
                            Effect.Completed -> {}
                            is Effect.Progress<*, *> -> {}
                            is Effect.Fail -> {}
                            Effect.Idle -> {}
                            is Effect.Success -> {}
                        }
                        _state.value = state.value.copy(startDiscoverable = it)
                    }
                }
            } catch (e: Throwable) {
                if (e !is CancellationException) {
                    _state.value = state.value.copy(startDiscoverable = effectFail(e))
                }
            }
        }.also {
            it.invokeOnCompletion {
                _state.value = state.value.copy(startDiscoverable = effectCompleted())
            }
        }
    }

    fun stopQuestDiscoverable() {
        questDiscoverableJob?.cancel()
        questDiscoverableJob = null
    }
}