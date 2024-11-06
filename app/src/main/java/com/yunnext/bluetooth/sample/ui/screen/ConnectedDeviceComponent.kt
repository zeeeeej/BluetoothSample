package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import randomZhongGuoSe

@Composable
fun ConnectedDeviceComponent(
    modifier: Modifier = Modifier,
    device: DeviceVo?,
    onDisconnect: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(randomZhongGuoSe().color.copy(alpha = .25f))
            .padding(horizontal = 0.dp, vertical = 12.dp)
            .clickable {
                if (device != null) {
                    onDisconnect()
                }
            }) {
        Text("当前连接设备：")
        if (device == null) {
            Text("无", style = TextStyle.Default.copy(fontSize = 11.sp))
        } else {
            Text(
                "${device.address}(${device.name})",
                style = TextStyle.Default.copy(fontSize = 11.sp)
            )
        }
    }
}