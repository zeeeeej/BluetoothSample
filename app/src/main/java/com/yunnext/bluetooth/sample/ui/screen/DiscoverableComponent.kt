package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.repo.DiscoverableMode
import com.yunnext.bluetooth.sample.repo.display
import com.yunnext.bluetooth.sample.ui.common.CommitButton
import kotlinx.coroutines.launch

@Preview
@Composable
fun DiscoverableComponentPreview() {
    DiscoverableComponent(
        modifier = Modifier.fillMaxWidth(),
        start = effectIdle(),
        onStop = {},
        onStart = {})
}

@Composable
fun DiscoverableComponent(
    modifier: Modifier = Modifier,
    start: Effect<Unit, DiscoverableMode>,
    onStop: () -> Unit,
    onStart: () -> Unit,
) {
    val rememberCoroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("对外广播设置")
        CommitButton(
            modifier = Modifier.fillMaxWidth(),
            text = when (start) {
                Effect.Completed -> "请求完成"
                is Effect.Progress<*, *> -> "请求中..."
                is Effect.Fail -> "请求失败"
                Effect.Idle -> "开启向外广播"
                is Effect.Success -> "当前向外广播状态：[${start.output.display}]"
            },
            enable = true
        ) {
            rememberCoroutineScope.launch {
                when (start) {
                    Effect.Completed -> onStart()
                    is Effect.Progress<*, *> -> onStop()
                    is Effect.Fail -> onStart()
                    Effect.Idle -> onStart()
                    is Effect.Success -> onStop()
                }
            }
        }
    }
}