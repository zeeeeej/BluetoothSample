package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.doing
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.ui.common.CommitButton
import com.yunnext.bluetooth.sample.ui.common.CommonArrowTitle

@Composable
fun AudioComponent(
    modifier: Modifier = Modifier,
    defaultShow: Boolean = false,
    audioEffect: Effect<Unit, Unit>,
    onStart: () -> Unit,
    onStop: () -> Unit,
    connectedDevice: DeviceVo?,
    onDisconnect: () -> Unit
) {
    var show by remember { mutableStateOf(defaultShow) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CommonArrowTitle(modifier = Modifier.clickable {
            show = !show
        }, title = "通话", opened = show)
        AnimatedVisibility(show) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectedDeviceComponent(
                    modifier = Modifier.fillMaxWidth(),
                    device = connectedDevice,
                    onDisconnect = onDisconnect
                )
                CommitButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (audioEffect.doing) "doing" else "stopped",
                    enable = true
                ) {
                    if (audioEffect.doing) {
                        onStop()
                    } else {
                        onStart()
                    }
                }
            }

        }
    }
}

@Preview("RecordComponentPreview")
@Composable
fun AudioComponentPreview(modifier: Modifier = Modifier) {
    AudioComponent(
        modifier.fillMaxSize(),
        defaultShow = true,
        audioEffect = effectIdle(),
        onStart = {},
        onStop = {},
        connectedDevice = DeviceVo("", "", ""),
        onDisconnect = {}
    )
}