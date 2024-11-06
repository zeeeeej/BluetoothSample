package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunnext.bluetooth.sample.repo.LocalDevice

@Composable
fun LocalDeviceComponent(modifier: Modifier = Modifier, localDevice: LocalDevice) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("本地设备")
        Column(Modifier.padding(horizontal = 16.dp)) {
            Row {
                Text(localDevice.address)
                Text(localDevice.name)
            }
            Text(if (localDevice.enable) "蓝牙开启" else "蓝牙关闭")
        }

    }
}