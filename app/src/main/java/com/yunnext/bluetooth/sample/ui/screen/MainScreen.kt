package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import color
import com.yunext.kotlin.kmp.compose.clickablePure
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.consumeEffect
import kotlinx.coroutines.launch
import randomZhongGuoSe

@Preview("DeviceListPreview")
@Composable
fun MainScreenPreview(modifier: Modifier = Modifier) {
    MainScreen(modifier = Modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier,
) {
    val activity = LocalContext.current
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        //bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = true)
    )
    val vm: MainViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val deviceList = @Composable {
        Column(
            modifier = Modifier
//                .heightIn(min = 480.dp)
//                .clip(RoundedCornerShape(16.dp))
//                .background(Color.White)
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("蓝牙列表")


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (val effect = state.scanEffect) {
                        Effect.Completed -> "搜索完毕"
                        is Effect.Progress<*, *> -> "搜索中..."
                        is Effect.Fail -> "搜索失败：${effect.output.message}"
                        Effect.Idle -> "点击开启搜索"
                        is Effect.Success -> "搜索成功"
                    },
                    style = TextStyle.Default.copy(
                        color = randomZhongGuoSe().color,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )

                val stoppedBlock = @Composable {
                    Image(Icons.Default.Search, null)
                }
                val startedBlock = @Composable {
                    // 创建一个可观察的动画状态
                    val infiniteTransition = rememberInfiniteTransition(label = "1")
                    val animationValue by infiniteTransition.animateFloat(
                        // 使用 infiniteRepeatable 创建无限重复的动画
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000), // 1000毫秒的补间动画
                            repeatMode = RepeatMode.Restart // 动画重复模式
                        ), label = "2"
                    )
                    Image(
                        Icons.Default.Refresh, null, modifier = Modifier
                            .rotate(animationValue)
                    )
                }
                Box(Modifier.clickablePure {
                    when (val effect = state.scanEffect) {
                        Effect.Completed -> {
                            vm.startScan()
                        }

                        is Effect.Progress<*, *> -> {
                            vm.stopScan()
                        }

                        is Effect.Fail -> {
                            vm.stopScan()
                        }

                        Effect.Idle -> {
                            vm.startScan()
                        }

                        is Effect.Success -> {
                            vm.stopScan()
                        }
                    }
                }) {
                    when (val effect = state.scanEffect) {
                        Effect.Completed -> {
                            stoppedBlock()
                        }

                        is Effect.Progress<*, *> -> startedBlock()
                        is Effect.Fail -> stoppedBlock()
                        Effect.Idle -> stoppedBlock()
                        is Effect.Success -> stoppedBlock()
                    }

                    LaunchedEffect(state.scanEffect) {
                        state.scanEffect.consumeEffect {
                            onIdle {
                                println("xxxxx-onIdle")
                            }

                            onSuccess { i, o ->
                                println("xxxxx-onSuccess $i $o")
                            }

                            onFail { i, e ->
                                println("xxxxx-onFail $i $e")
                            }

                            onCompleted {
                                println("xxxxx-onCompleted")
                            }

                            onProgress<Int> { i, p ->
                                println("xxxxx-onProgress $i $p ${p.javaClass}")
                            }
                        }
                    }
                }


            }


            BluetoothDeviceList(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),

                bondedList = state.bondedDevices,
                discoveryList = state.discoveryDevices,
                bindEffect = state.bindEffect,
                onBind = {
                    vm.bond(state.discoveryDevices[it])
                },
                onConnect = {
                    vm.connect(state.bondedDevices[it])
                },
                onStopBond = {
                    vm.stopBond()
                },
                onCancelBond = {
                    vm.cancelBond(state.bondedDevices[it])
                }, onStartClient = {
                    vm.startClient(it)
                })
        }
    }

    val content = @Composable {
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        color = ZhongGuoSe.远山紫.color
                    )
                }) {
            Box(modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    LocalDeviceComponent(
                        modifier = Modifier.fillMaxWidth(),
                        localDevice = state.localDevice,

                        )

                    RecordComponent(modifier = Modifier.fillMaxWidth(),
                        playEffect = state.playEffect,
                        recordEffect = state.recordEffect,
                        onStopRecord = {
                            vm.stopRecord()
                        },
                        onStopPlay = { vm.stopPlay() },
                        onStartRecord = {
                            vm.startRecord()
                        },
                        onStartPlay = {
                            vm.startPlay()
                        }, connectedDevice = state.connectDevice,
                        onDisconnect = { vm.disconnect() })
                    DiscoverableComponent(modifier = Modifier.fillMaxWidth(),
                        start = state.startDiscoverable,
                        onStart = {
                            vm.questDiscoverable(activity)
                        },
                        onStop = {
                            vm.stopQuestDiscoverable()
                        })


                    AudioComponent(modifier = Modifier.fillMaxWidth(),
                        audioEffect = state.audioEffect,
                        onStop = {
                            vm.stopAudio()
                        },
                        onStart = { vm.startAudio() },
                        connectedDevice = state.connectDevice,
                        onDisconnect = { vm.disconnect() })

                    SPPComponent(
                        modifier = Modifier.fillMaxWidth(),
                        typeOwner = state.typeOwner,
                        onSelected = {
                            vm.selectType(it)
                        },
                        onSend = {
                            vm.writeSPPMsg(it)
                        }, onStartServer = {
                            vm.startServer()
                        }, onOpenBlueList = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                                vm.startScan()
                            }
                        })

                    BleComponent(modifier = Modifier.fillMaxWidth(),
                        start = state.startDiscoverable,
                        onStart = {
                            vm.startBleServer()
                        },
                        onStop = {
                        })


                }
            }
        }
    }



    BottomSheetScaffold(modifier = Modifier.fillMaxSize(),
        scaffoldState = bottomSheetScaffoldState,
//        sheetPeekHeight = 0.dp,
        sheetContent = {
            deviceList()
        }) { _ ->
        content()
    }

}

