package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.ui.theme.Color333
import com.yunnext.bluetooth.sample.ui.theme.Color666
import randomZhongGuoSe

typealias DeviceWrapper = DeviceVo

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BluetoothDeviceList(
    modifier: Modifier = Modifier,
    bondedList: List<DeviceWrapper>,
    discoveryList: List<DeviceWrapper>,
    bindEffect: Effect<String, Unit>,
    onBind: (Int) -> Unit,
    onConnect: (Int) -> Unit,
    onStopBond: () -> Unit,
    onCancelBond: (Int) -> Unit,
) {
    Box(modifier = modifier) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            if (bondedList.isNotEmpty()) {
                stickyHeader() {
                    Text("已配对的设备")
                }
            }

            itemsIndexed(
                bondedList,
                { _, d -> d.address },
                contentType = { _, _ -> "2" }) { index, d ->
                DeviceItem(device = d, modifier = Modifier.clickable {
                    onConnect.invoke(index)
                }, deleteVisible = false, onDelete = { onCancelBond.invoke(index) })
            }
            if (discoveryList.isNotEmpty()) {
                stickyHeader() {

                    Column {
                        Text("可用设备")
                        BondStateComponent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            effect = bindEffect, onCancel = onStopBond
                        )
                    }
                }
            }
            itemsIndexed(
                discoveryList,
                { _, d -> d.address },
                contentType = { _, _ -> "2" }) { index, d ->
                DeviceItem(device = d, modifier = Modifier.clickable {
                    onBind.invoke(index)
                }, deleteVisible = true, onDelete = {

                })
            }
        }
    }
}

@Preview("DeviceItemPreview")
@Composable
private fun DeviceItemPreview(modifier: Modifier = Modifier) {
    DeviceItem(
        Modifier.fillMaxWidth(),
        device = DeviceWrapper("111", "222", "3333"),
        deleteVisible = true,
        onDelete = {})
}

@Composable
internal fun DeviceItem(
    modifier: Modifier = Modifier, device: DeviceWrapper,
    deleteVisible: Boolean = true,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ZhongGuoSe.月白.color)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color = randomZhongGuoSe().color)
        ) { }

        Spacer(Modifier.width(8.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    device.name,
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black//randomZhongGuoSe().color
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "(${device.type})",
                    style = TextStyle.Default.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Color333//randomZhongGuoSe().color
                    )
                )
            }
            Text(
                "(${device.address})",
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color666//randomZhongGuoSe().color
                )
            )
        }
        if (!deleteVisible) {
            Image(Icons.Default.Delete, null, modifier = Modifier.clickable {
                onDelete()
            })
        }

    }
}