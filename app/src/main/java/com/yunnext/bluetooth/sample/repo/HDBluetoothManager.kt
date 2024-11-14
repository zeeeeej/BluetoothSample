package com.yunnext.bluetooth.sample.repo

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.effectCompleted
import com.yunnext.bluetooth.sample.domain.effectDoIng
import com.yunnext.bluetooth.sample.domain.effectDoIngUnit
import com.yunnext.bluetooth.sample.domain.effectFail
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.domain.effectSuccess
import com.yunnext.bluetooth.sample.repo.ble.d
import com.yunnext.bluetooth.sample.repo.ble.e
import com.yunnext.bluetooth.sample.repo.ble.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class LocalDevice(val address: String, val name: String, val enable: Boolean)

class HDBluetoothManager(context: Context) {
    private val context: Context = context.applicationContext
    private val tag = "[connect]"
    private val bluetoothAdapter =
        // (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        context.getSystemService(BluetoothManager::class.java).adapter


    init {
        require(bluetoothAdapter != null) {
            "设备不支持蓝牙"
        }
        try {
            d("${tag}\n ${bluetoothAdapter.supportDisplay}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun localDeviceFlow(): Flow<LocalDevice> {
        return callbackFlow {
            val address = bluetoothAdapter.address
            val name = bluetoothAdapter.name
            val enabled = bluetoothAdapter.isEnabled

            send(LocalDevice(address = address ?: "", name = name ?: "", enable = enabled))
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        BluetoothAdapter.ACTION_STATE_CHANGED -> {
                            d("localDeviceFlow onReceive")
                            val state =
                                intent.getIntExtra(
                                    BluetoothAdapter.EXTRA_STATE,
                                    BluetoothAdapter.STATE_ON
                                )
                            val previousState = intent.getIntExtra(
                                BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                                BluetoothAdapter.STATE_ON
                            )

                            launch {
                                send(
                                    LocalDevice(
                                        address = address ?: "",
                                        name = name ?: "",
                                        enable = state == BluetoothAdapter.STATE_ON
                                    )
                                )
                            }
                        }
                    }
                }
            }
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            )
            awaitClose() {
                context.unregisterReceiver(broadcastReceiver)
            }
        }
    }

    fun enabled() = bluetoothAdapter.isEnabled

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun enable(activity: Activity, code: Int) {
//        if (value) bluetoothAdapter.enable()
//        else bluetoothAdapter.disable()
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, code)
        }
//        bluetoothAdapter.listenUsingInsecureL2capChannel()
    }

    fun bondedDeviceFlow() = callbackFlow<List<BluetoothDevice>> {
        val bondedDevices = bluetoothAdapter.bondedDevices
        send(bondedDevices.toList())

        awaitClose {

        }
    }

    fun discoveryDeviceFlow() = callbackFlow<List<BluetoothDevice>> {
        d("discoveryDeviceFlow")
        val map: MutableMap<String, BluetoothDevice> = mutableMapOf()
        val add = { device: BluetoothDevice ->
            launch {
                d("discoveryDeviceFlow add ${device.display}")
                val deviceHardwareAddress = device.address ?: return@launch
                val deviceName = device.name
                if (deviceName.isNullOrEmpty()) return@launch
                map[deviceHardwareAddress] = device
                send(map.map { (_, v) -> v })
            }
        }
        d("discoveryDeviceFlow 初始化broadcastReceiver")
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        d("discoveryDeviceFlow onReceive")
                        val device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return

                        add(device)

                    }
                }
            }
        }
        d("discoveryDeviceFlow 注册broadcastReceiver")
        context.registerReceiver(broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        val startDiscovery: Boolean = bluetoothAdapter.startDiscovery()
        if (!startDiscovery) throw RuntimeException("打开搜索失败")
//        var startDiscovery: Boolean = bluetoothAdapter.startDiscovery()
//        do {
//            bluetoothAdapter.cancelDiscovery()
//            delay(1000)
//            startDiscovery = bluetoothAdapter.startDiscovery()
//            d("discoveryDeviceFlow startDiscovery")
//            delay(1000)
//        } while (isActive && !startDiscovery)

        awaitClose {
            d("discoveryDeviceFlow awaitClose")
            stopDiscoveryDevice()
            context.unregisterReceiver(broadcastReceiver)

        }
    }

    private fun stopDiscoveryDevice() {
        bluetoothAdapter.cancelDiscovery()
    }

    @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
    fun bindFlow(deviceAddress: String): Flow<Effect<String, Unit>> {
        return callbackFlow {
            send(effectIdle())
            val remoteDevice =
                bluetoothAdapter.getRemoteDevice(deviceAddress) ?: return@callbackFlow send(
                    effectFail(
                        deviceAddress, RuntimeException("找不到此设备")
                    )
                )
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.action) {
                        BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                            d("bind onReceive")
                            val device: BluetoothDevice =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) ?: return
                            val state: Int = intent.getIntExtra(
                                BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE
                            )
                            val previousState: Int = intent.getIntExtra(
                                BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE
                            )
                            d("bind state = $state ,previousState = $previousState")
                            if (device.address == deviceAddress && state == BluetoothDevice.BOND_BONDED) {
                                launch {
                                    send(effectSuccess(deviceAddress, Unit))
                                }
                            }
                        }
                    }
                }
            }
            context.registerReceiver(
                broadcastReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            )
            val createBond = remoteDevice.createBond()
            if (createBond) {
                send(effectDoIng(deviceAddress, 0))
            } else {
                send(effectFail(deviceAddress, RuntimeException("绑定失败")))
            }
            awaitClose {
                d("bind awaitClose")
                launch {
                    send(effectCompleted())
                }
                context.unregisterReceiver(broadcastReceiver)
            }
        }
    }

    suspend fun questDiscoverable(
        activity: ComponentActivity, second: Int = 300
    ): Flow<Effect<Unit, DiscoverableMode>> {
        return callbackFlow {
            send(effectIdle())
            try {
                require(second > 0) {
                    "second must > 0"
                }
                val discoverableIntent: Intent =
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, second)
                    }


                val launcher = activity.activityResultRegistry.register(
                    KEY, ActivityResultContracts.StartActivityForResult()
                ) { result: ActivityResult ->
                    val rCode = result.resultCode
                    d(
                        "questDiscoverable result :$rCode ${
                            ActivityResult.resultCodeToString(
                                resultCode = rCode
                            )
                        } "
                    )
                    when (rCode) {
                        Activity.RESULT_OK -> {

                        }

                        Activity.RESULT_CANCELED -> {
                            launch {
                                send(effectFail(RuntimeException("请求失败")))
                            }
                        }

                        else -> {

                        }

                    }
                }
                activity.lifecycle.addObserver(object : LifecycleEventObserver {
                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                        if (event == Lifecycle.Event.ON_DESTROY) {
                            launcher.unregister()
                            activity.lifecycle.removeObserver(this)
                        }
                    }
                })
                val broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        when (intent?.action) {
                            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                                val mode = intent.getIntExtra(
                                    BluetoothAdapter.EXTRA_SCAN_MODE,
                                    BluetoothAdapter.SCAN_MODE_NONE
                                )
                                val previousMode = intent.getIntExtra(
                                    BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE,
                                    BluetoothAdapter.SCAN_MODE_NONE
                                )
                                d("questDiscoverable onReceive $mode $previousMode")
                                val discoverableMode = discoverableMode(mode)
                                launch {
                                    send(effectSuccess(discoverableMode))
                                }
                            }
                        }
                    }
                }
                send(effectDoIng())
                context.registerReceiver(
                    broadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
                )
                launcher.launch(discoverableIntent)
            } catch (e: Throwable) {
                effectFail(e)
            }

            awaitClose() {

            }

        }
    }

    suspend fun cancelBond(deviceAddress: String) {
        suspendCancellableCoroutine<Unit> {

            try {
                val remoteDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
                    ?: return@suspendCancellableCoroutine it.resumeWithException(
                        RuntimeException("找不到此设备")
                    )
                val result =
                    remoteDevice.javaClass.getMethod("removeBond")?.invoke(remoteDevice) as Boolean
                if (result) {
                    it.resume(Unit)
                } else {
                    it.resumeWithException(
                        RuntimeException("removeBond fail")
                    )
                }

            } catch (e: Exception) {
                it.resumeWithException(e)
            }

            it.invokeOnCancellation {

            }
        }

    }

    fun connectA2dpFlow(deviceAddress: String): Flow<Effect<String, BluetoothDevice>> {
        val a2dpDisconnect = { bluetoothA2dp: BluetoothA2dp, device: BluetoothDevice ->
            d("${tag}connectFlow a2dpDisconnect")
            try {
                val a2dp = bluetoothA2dp
                val clazz = BluetoothA2dp::class.java
                clazz.getMethod("disconnect", BluetoothDevice::class.java).invoke(a2dp, device)
            } catch (e: Exception) {
                e("${tag}connectFlow a2dpDisconnect error $e")
                e.printStackTrace()
            }
        }
        return callbackFlow {
            val remoteDevice =
                bluetoothAdapter.getRemoteDevice(deviceAddress) ?: return@callbackFlow send(
                    effectFail(
                        deviceAddress, RuntimeException("找不到此设备")
                    )
                )
            send(effectIdle())
            var bluetoothA2dp: BluetoothA2dp? = null
            val bluetoothServiceListener = object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                    i("connectFlow onServiceConnected profile=$profile,proxy=$proxy")
                    if (profile == BluetoothProfile.A2DP) {
                        bluetoothA2dp = proxy as BluetoothA2dp

                        val audioManager =
                            (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
                                mode = AudioManager.MODE_IN_COMMUNICATION
                                // Bluetooth SCO (Synchronous Connection-Oriented) 是蓝牙协议栈中的一个协议，它用于支持实
                                // 时音频传输，特别是在需要低延迟的通信场合，如蓝牙耳机和车载免提系统。
                                isBluetoothScoOn = true
                            }
                        audioManager.startBluetoothSco()

                        launch {
                            send(effectSuccess(deviceAddress, remoteDevice))
                        }
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    e("connectFlow onServiceDisconnected profile=$profile")
                    if (profile == BluetoothProfile.A2DP) {
                        bluetoothA2dp = null
                        launch {
                            send(effectFail(deviceAddress, RuntimeException("a2dp断开连接")))
                        }
                    }
                }
            }

            send(effectDoIngUnit(deviceAddress))
            bluetoothAdapter.getProfileProxy(
                context, bluetoothServiceListener, BluetoothProfile.A2DP
            )

            awaitClose() {
                e("${tag}connectFlow awaitClose")
                val a2dp = bluetoothA2dp
                bluetoothA2dp = null
                if (a2dp != null) {
                    //a2dpDisconnect(a2dp, remoteDevice) //
                    bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, a2dp)
                }
            }
        }
    }


    private val bluetoothServiceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            i("onServiceConnected profile=$profile,proxy=$proxy")
        }

        override fun onServiceDisconnected(profile: Int) {
            e("onServiceDisconnected profile=$profile")
        }

    }

    fun start() {
        val audioManager = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
            mode = AudioManager.MODE_IN_COMMUNICATION
            // Bluetooth SCO (Synchronous Connection-Oriented) 是蓝牙协议栈中的一个协议，它用于支持实
            // 时音频传输，特别是在需要低延迟的通信场合，如蓝牙耳机和车载免提系统。
            isBluetoothScoOn = true
        }
        audioManager.startBluetoothSco()
    }

    fun unRegisterStateChanged(broadcastReceiver: BroadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver)
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Flow<HDBluetoothData> {

        return channelFlow<HDBluetoothData> {
            // audioManager

            val audioManager =
                (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager).apply {
                    mode = AudioManager.MODE_IN_COMMUNICATION
                    // Bluetooth SCO (Synchronous Connection-Oriented) 是蓝牙协议栈中的一个协议，它用于支持实
                    // 时音频传输，特别是在需要低延迟的通信场合，如蓝牙耳机和车载免提系统。
                    // isBluetoothScoOn = true
                }
            d("${tag}初始化AudioManager")
            val a2dpConnect = { a2dp: BluetoothA2dp ->
                d("${tag}a2dpConnect")
                try {
                    val clazz = BluetoothA2dp::class.java
                    clazz.getMethod("connect", BluetoothDevice::class.java).invoke(a2dp, device)
                } catch (e: Exception) {
                    e("${tag}a2dpConnect error $e")
                    e.printStackTrace()
                }
            }
            //
            var bluetoothA2dp: BluetoothA2dp? = null
            val bluetoothServiceListener = object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                    i("${tag}onServiceConnected profile=$profile,proxy=$proxy")
                    val a2dp = proxy as BluetoothA2dp
                    bluetoothA2dp = a2dp
                    a2dpConnect(a2dp)
                    audioManager.startBluetoothSco()
//                    audioManager.setCommunicationDevice()
                }

                override fun onServiceDisconnected(profile: Int) {
                    e("${tag}onServiceDisconnected profile=$profile")
//                    bluetoothA2dp?.finalize()
                }
            }
            d("${tag}初始化BluetoothA2dp BluetoothProfile.ServiceListener")


            val createBond = {
                d("${tag}createBond")
                device.createBond()
            }

            /**
             * 能解绑蓝牙，效果同[cancelBond]
             */
            val a2dpDisconnect = {
                d("${tag}a2dpDisconnect")
                try {
                    val a2dp = bluetoothA2dp
                    val clazz = BluetoothA2dp::class.java
                    clazz.getMethod("disconnect", BluetoothDevice::class.java).invoke(a2dp, device)
                } catch (e: Exception) {
                    e("${tag}a2dpDisconnect error $e")
                    e.printStackTrace()
                }
            }

            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val action = intent?.action
                    when (action) {
                        BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED -> {
                            val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)
                            val previousState =
                                intent.getIntExtra(BluetoothA2dp.EXTRA_PREVIOUS_STATE, -1)
                            d("${tag}BroadcastReceiver BluetoothA2dp : state=$state,previousState=$previousState")
                            when (state) {
                                BluetoothProfile.STATE_CONNECTED -> {
                                    d("BluetoothProfile.STATE_CONNECTED")
                                    audioManager.startBluetoothSco()
                                }

                                BluetoothProfile.STATE_DISCONNECTED -> {
                                    d("BluetoothProfile.STATE_DISCONNECTED")
                                }

                                else -> {
                                    d("BluetoothProfile : $state")
                                }
                            }
                        }

                        BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                            val state = intent.getIntExtra(
                                BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE
                            )
                            val previousState = intent.getIntExtra(
                                BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.BOND_NONE
                            )
                            d("${tag}BroadcastReceiver BluetoothDevice : state=$state,previousState=$previousState")
                            when (state) {
                                BluetoothDevice.BOND_NONE -> {
//                                    createBond()
                                }

                                BluetoothDevice.BOND_BONDED -> {
//                                    a2dpConnect()
                                }
                            }
                        }

                        AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED -> {
                            val state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                            val previousState =
                                intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, -1);
                            d("${tag}BroadcastReceiver BluetoothA2dp : state=$state,previousState=$previousState")
                            when (state) {
                                AudioManager.SCO_AUDIO_STATE_CONNECTED -> {
                                    audioManager.isBluetoothA2dpOn = true
                                }
                            }
                        }
                    }
                }
            }
            val filter = IntentFilter().apply {
                addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)
                addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
            d("${tag}registerReceiver")
            context.registerReceiver(broadcastReceiver, filter)
            d("${tag}getProfileProxy")
            val result = bluetoothAdapter.getProfileProxy(
                context, bluetoothServiceListener, BluetoothProfile.A2DP
            )
            awaitClose() {
                e("${tag}awaitClose")
            }
        }
    }

    fun unRegisterPlayStateChanged(broadcastReceiver: BroadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver)
    }

    fun registerPlayStateChanged() {
        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                when (action) {
                    BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED -> {
                        val state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)
                        val previousState =
                            intent.getIntExtra(BluetoothA2dp.EXTRA_PREVIOUS_STATE, -1)
                        d("BluetoothA2dp : state=$state,previousState=$previousState")
                        when (state) {
                            BluetoothA2dp.STATE_PLAYING -> {
                                d("BluetoothA2dp.STATE_PLAYING")
                            }

                            BluetoothA2dp.STATE_NOT_PLAYING -> {
                                d(" BluetoothA2dp.STATE_PLAYING")
                            }

                            else -> {
                                d("BluetoothA2dp : $state")
                            }
                        }
                    }
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)
        }
        context.registerReceiver(broadcastReceiver, filter)
    }


    // MediaRecorder recorder = new MediaRecorder();
    //recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置音频源为麦克风
    //recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 设置输出格式
    //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 设置音频编码
    //recorder.setOutputFile("/path/to/file"); // 设置输出文件路径
    //recorder.prepare(); // 准备录制
    //recorder.start(); // 开始录制
    //// ...
    //recorder.stop(); // 停止录制
    //recorder.release(); // 释放资源

    fun startRecordAudioFlow(name: String): Flow<Effect<String, Unit>> {
        return callbackFlow {
            val mediaRecorder = MediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(context.cacheDir.absolutePath + "/$name")
                setOnInfoListener { mr, what, extra ->

                }
                setOnErrorListener { mr, what, extra ->
                    launch {
                        send(effectFail(name, IllegalStateException("录制出错：$what $extra")))
                    }
                }
//                addOnRoutingChangedListener({ TODO("Not yet implemented") }, Handler())
            }
            mediaRecorder.prepare()
            launch {
                send(effectDoIngUnit(name))
            }
            mediaRecorder.start()

            awaitClose {
                mediaRecorder.stop()
            }
        }
    }

    private var _mediaRecorder: MediaRecorder? = null
    fun startRecordAudio() {
        _mediaRecorder?.stop()
        _mediaRecorder = null
        val mediaRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(context.cacheDir.absolutePath + "/$DEFAULT_NAME")
        }
        _mediaRecorder = mediaRecorder
        mediaRecorder.prepare()
        mediaRecorder.start()
    }

    fun stopRecordAudio() {
        _mediaRecorder?.stop()
        _mediaRecorder = null
    }

//    private var _mediaPlayer: MediaPlayer? = null

//    fun getBondDevice():BluetoothDevice{
//        bluetoothAdapter.bondedDevices
//    }


    fun playFlow(name: String): Flow<Effect<String, Unit>> {
        return callbackFlow {
            d("playFlow")
            val path = context.cacheDir.absolutePath + "/$name"
            val mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(path)
                setOnCompletionListener {
                    d("playFlow setOnCompletionListener")
                    launch {
                        send(effectSuccess(name, Unit))
                    }

                }

                setOnErrorListener { mp, what, extra ->
                    d("playFlow setOnErrorListener $what $extra")
                    launch {
                        send(effectFail(name, RuntimeException("error:$extra")))
                    }
                    false
                }

                setOnPreparedListener {
                    d("playFlow setOnPreparedListener")
                    launch {
                        send(effectDoIngUnit(name))
                    }
                }
            }
            mediaPlayer.prepare()
            mediaPlayer.start()

            awaitClose {
                e("playFlow awaitClose")
                mediaPlayer.stop()
            }
        }
    }


    //<editor-fold desc=" // 服务发现协议(SDP)">
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalStdlibApi::class)
    fun bluetoothServerFlow(
        name: String = "hadlinks-bluetooth-test12",
        uuid: UUID = Server_UUID,
        writeChannel: Channel<ByteArray>
    ): Flow<BluetoothServerEvent> {
        d("bluetoothServerFlow ------------------------------------")
        d("bluetoothServerFlow mmServerSocket start $name $uuid...")
        return callbackFlow<BluetoothServerEvent> {
            var state: BluetoothServerState = BluetoothServerState.Closed
            val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
//            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(name, uuid)
                bluetoothAdapter?.listenUsingRfcommWithServiceRecord(name, uuid) // 安全的
            }
            d("bluetoothServerFlow mmServerSocket 初始化完毕 :$mmServerSocket")
            var myIn: InputStream? = null
            var myOut: OutputStream? = null

            val updateStateBlock: suspend BluetoothServerState.() -> Unit = {
                d("bluetoothServerFlow mmServerSocket 状态变化：$this")
                state = this
                send(BluetoothServerEvent.State(state))
            }

            BluetoothServerState.Listen.updateStateBlock()

            launch {
                writeChannel.receiveAsFlow()

                    .collect() {
                        try {
                            check(state is BluetoothServerState.Connected) {
                                "未连接，无法发送数据 ${it.toHexString()}"
                            }
                            launch(Dispatchers.IO) {
                                d("bluetoothServerFlow write:[${it.toHexString()}]")
                                myOut?.write(it)
                                myOut?.flush()
                            }
                        } catch (e: Exception) {
                            e("bluetoothServerFlow write:[${it.toHexString()}] error :$e")
                            e.printStackTrace()
                        }
                    }
            }

            launch(Dispatchers.IO) {
                while (state !is BluetoothServerState.Closed) {
                    d("bluetoothServerFlow accept ...... 等待客户端连接")
                    val socket: BluetoothSocket? = try {
                        mmServerSocket?.accept()
                    } catch (e: IOException) {
                        d("bluetoothServerFlow Socket's accept() method failed : $e")

                        null
                    }
                    // 阻塞中
                    // 当有客户端连接时
                    val buffer = ByteArray(1024)
                    var bytes: Int
                    socket?.also { ss ->

                        when (state) {
                            BluetoothServerState.Closed -> {
                                d("bluetoothServerFlow 已关闭")
                            }

                            BluetoothServerState.Listen -> {
                                d("bluetoothServerFlow 正在监听数据...")
                                try {
                                    val tmpIn = ss.inputStream
                                    val tmpOut = ss.outputStream
                                    myIn = tmpIn
                                    myOut = tmpOut
                                    BluetoothServerState.Connected(ss.remoteDevice?.address ?: "")
                                        .updateStateBlock()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    BluetoothServerState.Closed.updateStateBlock()
                                }
                                val tmpIn = myIn
                                if (tmpIn != null) {
                                    while (state is BluetoothServerState.Connected) {
                                        try {
                                            bytes = tmpIn.read(buffer)
                                            println("parseCommand bytes=${bytes.toInt()}")
                                            val v = buffer.sliceArray(0..<bytes)
                                            println("parseCommand v=${v.size} ${v.decodeToString()} ${v.toHexString()}")
                                            trySend(BluetoothServerEvent.Msg(buffer.sliceArray(0..<bytes)))
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }

                            BluetoothServerState.Connecting -> {
                                d("bluetoothServerFlow 正在连接")
                            }

                            is BluetoothServerState.Connected -> {
                                d("bluetoothServerFlow 已连接")

                            }
                        }
                    } ?: run {
                        BluetoothServerState.Closed.updateStateBlock()
                    }
                }
            }

            awaitClose {
                d("bluetoothServerFlow awaitClose")
                try {
                    myIn = null
                    myOut = null
                    mmServerSocket?.close()
                } catch (e: IOException) {
                    e("bluetoothServerFlow Could not close the connect socket")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalStdlibApi::class)
    fun bluetoothClientFlow(
        deviceAddress: String,
        uuid: UUID = Server_UUID,
        writeChannel: Channel<ByteArray>
    ): Flow<BluetoothClientEvent> {

        d("bluetoothClientFlow ------------------------------------")
        d("bluetoothClientFlow mmSocket start... ${deviceAddress} $uuid")
        return callbackFlow<BluetoothClientEvent> {
            val remoteDevice =
                bluetoothAdapter.getRemoteDevice(deviceAddress) ?: return@callbackFlow
            val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
                remoteDevice.createRfcommSocketToServiceRecord(uuid)
            }
            d("bluetoothClientFlow mmSocket 初始化完毕 $mmSocket")
            var state: BluetoothClientState = BluetoothClientState.Closed
            var myIn: InputStream? = null
            var myOut: OutputStream? = null
            val updateStateBlock: suspend BluetoothClientState.() -> Unit = {
                d("bluetoothClientFlow  更新state :$state")
                state = this
                send(BluetoothClientEvent.State(state))
            }
            launch() {
                writeChannel.receiveAsFlow().collect() {
                    try {
                        check(state is BluetoothClientState.Connected) {
                            "未连接，无法发送数据 ${it.toHexString()}"
                        }
                        launch(Dispatchers.IO) {
                            d("bluetoothClientFlow  write :${it.toHexString()}")
                            myOut?.write(it)
                            myOut?.flush()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                BluetoothClientState.Listen.updateStateBlock()
            }

            launch(Dispatchers.IO) {

                // Cancel discovery because it otherwise slows down the connection.
                bluetoothAdapter?.cancelDiscovery()
                while (state != BluetoothClientState.Closed) {
                    mmSocket?.let { socket ->
                        // Connect to the remote device through the socket. This call blocks
                        // until it succeeds or throws an exception.
                        try {
                            val buffer = ByteArray(1024)
                            var bytes: Int
                            d("bluetoothClientFlow  connect")
                            socket.connect()
                            d("bluetoothClientFlow  connect end")
                            when (state) {
                                BluetoothClientState.Closed -> {
                                    d("bluetoothClientFlow  Closed")
                                }

                                BluetoothClientState.Listen -> {
                                    d("bluetoothClientFlow  Listen")
                                    try {
                                        val tmpIn = socket.inputStream
                                        val tmpOut = socket.outputStream
                                        myIn = tmpIn
                                        myOut = tmpOut
                                        BluetoothClientState.Connected(deviceAddress)
                                            .updateStateBlock()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        BluetoothClientState.Closed.updateStateBlock()
                                    }

                                    val tmpIn = myIn
                                    if (tmpIn != null) {
                                        while (state is BluetoothClientState.Connected) {
                                            bytes = tmpIn.read(buffer)
                                            println("parseCommand bytes=${bytes.toInt()}")
                                            val v = buffer.sliceArray(0..<bytes)
                                            println("parseCommand v=${v.size} ${v.decodeToString()} ${v.toHexString()}")
                                            trySend(BluetoothClientEvent.Msg(buffer.sliceArray(0..<bytes)))
                                        }
                                    }
                                }

                                BluetoothClientState.Connecting -> {
                                    d("bluetoothClientFlow  Connecting")
                                }

                                is BluetoothClientState.Connected -> {
                                    d("bluetoothClientFlow  Connected")

                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            BluetoothClientState.Closed.updateStateBlock()
                        }
                    }
                }

            }

            awaitClose {
                try {
                    myIn = null
                    myOut = null
                    mmSocket?.close()
                } catch (e: IOException) {
                    e("bluetoothClientFlow Could not close the connect socket")
                }
            }
        }
    }

    //</editor-fold>


//    fun play(path: String = context.cacheDir.absolutePath + "/$DEFAULT_NAME") {
//        val mediaPlayer = MediaPlayer().apply {
//            setAudioStreamType(AudioManager.STREAM_MUSIC)
//            setDataSource(path)
//            setOnCompletionListener {
//                d("play setOnCompletionListener")
//            }
//
//            setOnErrorListener { mp, what, extra ->
//                d("play setOnCompletionListener $what $extra")
//                false
//            }
//
//            setOnPreparedListener {
//                d("play setOnPreparedListener")
//            }
//        }
//        _mediaPlayer = mediaPlayer
//        mediaPlayer.prepare()
//        mediaPlayer.start()
//    }
//
//    fun stopPlay() {
//        _mediaPlayer?.stop()
//        _mediaPlayer = null
//    }


    companion object {
        private const val KEY = "com.yunnext.bluetooth.sample.repo.HDBluetoothManager"
        internal const val DEFAULT_NAME = "hadlinks.3gp"
        internal val Server_UUID by lazy {
            UUID.fromString("3c858ee0-f714-4683-9a9a-bd5d8b5ec3a0")
        }
    }

}


sealed interface HDBluetoothData {
    data object Idle : HDBluetoothData
}

sealed interface DiscoverableMode {
    val value: Int

    data object ScanModeConnectableDiscoverable : DiscoverableMode {
        override val value: Int
            get() = BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE
    }

    data object ScanModeConnectable : DiscoverableMode {
        override val value: Int
            get() = BluetoothAdapter.SCAN_MODE_CONNECTABLE
    }

    data object ScanModeNone : DiscoverableMode {
        override val value: Int
            get() = BluetoothAdapter.SCAN_MODE_NONE
    }
}

fun discoverableMode(value: Int): DiscoverableMode {
    return when (value) {
        DiscoverableMode.ScanModeNone.value -> DiscoverableMode.ScanModeNone
        DiscoverableMode.ScanModeConnectable.value -> DiscoverableMode.ScanModeConnectable
        DiscoverableMode.ScanModeConnectableDiscoverable.value -> DiscoverableMode.ScanModeConnectableDiscoverable
        else -> throw IllegalStateException("错误的可检测模式:$value")
    }
}

val DiscoverableMode.display: String
    get() = when (this) {
        DiscoverableMode.ScanModeConnectable -> "设备未处于可检测到模式，但仍然可以接收连接。"
        DiscoverableMode.ScanModeConnectableDiscoverable -> "设备处于可检测到模式"
        DiscoverableMode.ScanModeNone -> "未处于可检测到模式，无法接收连接"
    }

sealed interface BluetoothServerState {
    data object Listen : BluetoothServerState
    data object Closed : BluetoothServerState
    data class Connected(val deviceAddress: String) : BluetoothServerState
    data object Connecting : BluetoothServerState
}

sealed interface BluetoothServerEvent {
    data class State(val state: BluetoothServerState) : BluetoothServerEvent

    data class Msg(val data: ByteArray) : BluetoothServerEvent
}

sealed interface BluetoothClientState {
    data object Listen : BluetoothClientState
    data object Closed : BluetoothClientState
    data class Connected(val deviceAddress: String) : BluetoothClientState
    data object Connecting : BluetoothClientState
}

sealed interface BluetoothClientEvent {
    data class State(val state: BluetoothClientState) : BluetoothClientEvent

    data class Msg(val data: ByteArray) : BluetoothClientEvent

}
