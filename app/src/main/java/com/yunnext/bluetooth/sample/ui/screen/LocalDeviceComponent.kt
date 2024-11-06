package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunnext.bluetooth.sample.repo.LocalDevice
import randomZhongGuoSe

@Composable
fun LocalDeviceComponent(modifier: Modifier = Modifier, localDevice: LocalDevice,connectedDevice:DeviceVo?,onDisconnect:()->Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("本地设备")
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BluetoothDeviceComponent(
                modifier = Modifier,
                left = localDevice.name,
                right = localDevice.address
            )

            Text(
                if (localDevice.enable) "蓝牙开启" else "蓝牙关闭",
                style = TextStyle.Default.copy(
                    color = if (localDevice.enable) ZhongGuoSe.宝石绿.color else ZhongGuoSe.月季红.color,
                    fontWeight = FontWeight.Bold
                )
            )

            val current = @Composable { d: DeviceVo? ->

            }
            ConnectedDeviceComponent(modifier = Modifier.fillMaxWidth(), device = connectedDevice, onDisconnect = onDisconnect)
        }
    }
}