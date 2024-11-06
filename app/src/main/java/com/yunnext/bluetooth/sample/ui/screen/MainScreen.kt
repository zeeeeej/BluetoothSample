package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import color
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.ui.common.clickablePure
import randomZhongGuoSe

@Preview("DeviceListPreview")
@Composable
fun MainScreenPreview(modifier: Modifier = Modifier) {
    MainScreen(modifier = Modifier)
}

@Composable
fun MainScreen(
    modifier: Modifier,
) {
    val activity = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    color = ZhongGuoSe.远山紫.color
                )
            }) {

        val vm: MainViewModel = viewModel()
        val state by vm.state.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        Box(modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Column (verticalArrangement = Arrangement.spacedBy(12.dp)){

                LocalDeviceComponent(
                    modifier = Modifier
                        .fillMaxWidth(), localDevice = state.localDevice
                )

                RecordComponent(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    }
                )
                DiscoverableComponent(
                    modifier = Modifier.fillMaxWidth(),
                    start = state.startDiscoverable,
                    onStart = {
                        vm.questDiscoverable(activity)
                    },
                    onStop = {
                        vm.stopQuestDiscoverable()
                    })

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text("蓝牙列表")
                    val current = @Composable { d: DeviceVo? ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(randomZhongGuoSe().color.copy(alpha = .25f))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clickable {
                                    if (d != null) {
                                        vm.disconnect()
                                    }
                                }
                        ) {
                            Text("当前连接设备：")
                            if (d == null) {
                                Text("无", style = TextStyle.Default.copy(fontSize = 11.sp))
                            } else {
                                Text(
                                    "${d.address}(${d.name})",
                                    style = TextStyle.Default.copy(fontSize = 11.sp)
                                )
                            }
                        }
                    }
                    current(state.connectDevice)

                    Text(
                        when (val effect = state.scanEffect) {
                            Effect.Completed -> "搜索完毕"
                            is Effect.Progress<*, *> -> "搜索中..."
                            is Effect.Fail -> "搜索失败：${effect.output.message}"
                            Effect.Idle -> "点击开启搜索"
                            is Effect.Success -> "搜索成功"
                        },
                        style = TextStyle.Default.copy(color = randomZhongGuoSe().color),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickablePure {
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
                            }
                    )

                    BluetoothDeviceList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 500.dp),
                        bondedList = state.bondedDevices,
                        discoveryList = state.discoveryDevices,
                        bindEffect = state.bindEffect,
                        onBind = {
                            vm.bond(state.discoveryDevices[it])
                        }, onConnect = {
                            vm.connect(state.bondedDevices[it])
                        }, onStopBond = {
                            vm.stopBond()
                        }, onCancelBond = {
                            vm.cancelBond(state.bondedDevices[it])
                        })
                }


            }
        }
    }
}

