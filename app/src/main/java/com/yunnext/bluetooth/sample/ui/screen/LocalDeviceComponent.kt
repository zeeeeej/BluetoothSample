package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunnext.bluetooth.sample.repo.LocalDevice
import com.yunnext.bluetooth.sample.ui.common.CommonArrow
import com.yunnext.bluetooth.sample.ui.common.CommonArrowTitle
import randomZhongGuoSe

@Composable
fun LocalDeviceComponent(
    modifier: Modifier = Modifier,
    localDevice: LocalDevice,
) {
    var show by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        CommonArrowTitle(modifier = Modifier .clickable {
            show = !show
        },title = "本地设备信息", opened = show)

        AnimatedVisibility(show) {
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


            }
        }

    }
}