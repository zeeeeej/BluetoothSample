package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.effectCompleted
import com.yunnext.bluetooth.sample.domain.effectFail
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.domain.effectProgress
import com.yunnext.bluetooth.sample.domain.effectSuccess

@Preview("BondStateComponentPreview4")
@Composable
fun BondStateComponentPreview3(modifier: Modifier = Modifier) {
    BondStateComponent(Modifier.fillMaxWidth(), effect = effectProgress("11", 2), onCancel = {})
}

@Preview("BondStateComponentPreview3")
@Composable
fun BondStateComponentPreview4(modifier: Modifier = Modifier) {
    BondStateComponent(Modifier.fillMaxWidth(), effect = effectIdle(), onCancel = {})
}

@Preview("BondStateComponentPreview5")
@Composable
fun BondStateComponentPreview5(modifier: Modifier = Modifier) {
    BondStateComponent(Modifier.fillMaxWidth(), effect = effectCompleted(), onCancel = {})
}

@Preview("BondStateComponentPreview1")
@Composable
fun BondStateComponentPreview1(modifier: Modifier = Modifier) {
    BondStateComponent(Modifier.fillMaxWidth(), effect = effectSuccess("11", Unit), onCancel = {})
}

@Preview("BondStateComponentPreview2")
@Composable
fun BondStateComponentPreview2(modifier: Modifier = Modifier) {
    BondStateComponent(
        Modifier.fillMaxWidth(),
        effect = effectFail("11", Throwable("错误")),
        onCancel = {})
}

@Composable
internal fun BondStateComponent(
    modifier: Modifier = Modifier,
    effect: Effect<String, Unit>,
    onCancel: () -> Unit
) {
    Box(modifier = modifier
        .padding(12.dp)
        .clickable {
            when (val ef = effect) {
                Effect.Completed -> {

                }

                is Effect.Progress<*, *> -> {
                    onCancel()
                }

                is Effect.Fail -> {

                }

                Effect.Idle -> {}
                is Effect.Success -> {

                }
            }
        }) {
        Text(
            text = when (effect) {
                Effect.Completed -> "绑定完成"
                is Effect.Progress<*, *> -> "<${effect.input}>绑定中..."
                is Effect.Fail -> "<${effect.input}>绑定失败${effect.output.message}"
                Effect.Idle -> "请选择一个可用的设备绑定"
                is Effect.Success -> "<${effect.input}>绑定成功"
            },
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = ZhongGuoSe.月季红.color//randomZhongGuoSe().color
            )
        )
    }
    // todo 哈哈

}
