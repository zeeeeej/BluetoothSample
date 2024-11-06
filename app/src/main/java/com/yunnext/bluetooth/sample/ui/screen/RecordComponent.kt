package com.yunnext.bluetooth.sample.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yunnext.bluetooth.sample.domain.Effect
import com.yunnext.bluetooth.sample.domain.doing
import com.yunnext.bluetooth.sample.domain.effectIdle
import com.yunnext.bluetooth.sample.ui.common.CommitButton

@Preview("RecordComponentPreview")
@Composable
fun RecordComponentPreview(modifier: Modifier = Modifier) {
    RecordComponent(
        modifier.fillMaxSize(),
        playEffect = effectIdle(),
        recordEffect = effectIdle(),
        onStartPlay = {},
        onStartRecord = {},
        onStopPlay = {},
        onStopRecord = {})
}

@Composable
fun RecordComponent(
    modifier: Modifier = Modifier,
    playEffect: Effect<String, Unit>,
    recordEffect: Effect<String, Unit>,
    onStartPlay: () -> Unit,
    onStopPlay: () -> Unit,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("音频录制&播放")
        CommitButton(
            modifier = Modifier.fillMaxWidth(),
            text = when (recordEffect) {
                Effect.Completed -> "录制完毕"
                is Effect.Progress<*, *> -> "录制中..."
                is Effect.Fail -> "录制失败"
                Effect.Idle -> "点击录制"
                is Effect.Success -> "录制成功"
            },
            enable = !playEffect.doing
        ) {
            when (recordEffect) {
                Effect.Completed -> onStartRecord()
                is Effect.Progress<*, *> -> onStopRecord()
                is Effect.Fail -> onStartRecord()
                Effect.Idle -> onStartRecord()
                is Effect.Success -> onStartRecord()
            }
        }

        Spacer(Modifier.height(16.dp))

        CommitButton(
            modifier = Modifier.fillMaxWidth(),
            text = when (playEffect) {
                Effect.Completed -> "播放完毕"
                is Effect.Progress<*, *> -> "${playEffect.input}播放中..."
                is Effect.Fail -> "${playEffect.input}播放失败:${playEffect.output.message}"
                Effect.Idle -> "点击播放"
                is Effect.Success -> "${playEffect.input}播放成功"
            }//if (startPlay) "停止播放" else "播放",
            , enable = !recordEffect.doing
        ) {

            when (playEffect) {
                Effect.Completed -> onStartPlay()
                is Effect.Progress<*, *> -> onStopPlay()
                is Effect.Fail -> onStartPlay()
                Effect.Idle -> onStartPlay()
                is Effect.Success -> onStartPlay()
            }
        }
    }
}