package com.yunnext.bluetooth.sample.ui.screen

import ZhongGuoSe
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import randomZhongGuoSe

enum class Direction {
    Up, Down, Left, Right;
}

@Composable
fun ControllerComponent(
    modifier: Modifier = Modifier,
    onCommand: (Command) -> Unit,
    start: Boolean,
    direction: Direction?
) {

    val icon = @Composable { d: Direction ->
        Image(
            Icons.AutoMirrored.Filled.ArrowBack,
            null,
            colorFilter = ColorFilter.tint(Color.Red),
            modifier = Modifier.rotate(
                when (d) {
                    Direction.Up -> 90f
                    Direction.Down -> -90f
                    Direction.Left -> 0f
                    Direction.Right -> 180f
                }
            )
        )
    }

    val button: @Composable ((Boolean, @Composable () -> Unit) -> Unit) =
        @Composable { sel, content ->
            Box(
                modifier = Modifier
                    .background(
                        if (!sel) {
                            ZhongGuoSe.云水蓝.color
                        } else {
                            ZhongGuoSe.宝石绿.color
                        }
                    )
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val w = constraints.maxWidth / 4
                        val h = constraints.maxHeight / 4
                        val s = Math.min(w, h)
                        layout(s, s) {
                            placeable.placeRelative(
                                (s - placeable.width) / 2,
                                (s - placeable.width) / 2
                            )
                        }
                    },
            ) {
                content()
            }
        }


    Box(modifier = modifier) {
        Box(modifier = Modifier
            .align(Alignment.TopCenter)
            .clickable(enabled = start) {
                onCommand(Command.Up)
            }) {
            button(direction == Direction.Up) {
                icon(Direction.Up)
            }
        }

        Box(modifier = Modifier
            .align(Alignment.CenterStart)
            .clickable(enabled = start) {
                onCommand(Command.Left)
            }) {
            button(direction == Direction.Left) {
                icon(Direction.Left)
            }
        }

        Box(modifier = Modifier
            .align(Alignment.CenterEnd)
            .clickable(enabled = start) {
                onCommand(Command.Right)
            }) {
            button(direction == Direction.Right) {
                icon(Direction.Right)
            }
        }

        Box(modifier = Modifier
            .align(Alignment.BottomCenter)
            .clickable(enabled = start) {
                onCommand(Command.Down)
            }) {
            button(direction == Direction.Down) {
                icon(Direction.Down)
            }
        }

        Box(modifier = Modifier
            .align(Alignment.Center)
            .clickable(enabled = true) {
                if (start) {
                    onCommand(Command.Stop)
                } else {
                    onCommand(Command.Start)
                }
            }) {
            button(start) {
                if (start) {
                    val infiniteTransition = rememberInfiniteTransition(label = "1")
                    val animationValue by infiniteTransition.animateFloat(
                        // 使用 infiniteRepeatable 创建无限重复的动画
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000,easing = LinearEasing), // 1000毫秒的补间动画
                            repeatMode = RepeatMode.Restart // 动画重复模式
                        ), label = "LoadingIcon"
                    )
                    Image(
                        Icons.Default.Refresh,
                        null,
                        colorFilter = ColorFilter.tint(Color.Red),
                        modifier = Modifier.rotate(animationValue)
                    )
                } else {
                    Image(
                        Icons.Default.PlayArrow,
                        null,
                        colorFilter = ColorFilter.tint(Color.Red),
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ControllerComponentPreview(modifier: Modifier = Modifier) {
    ControllerComponent(
        modifier = modifier.size(200.dp),
        onCommand = {},
        start = true,
        direction = Direction.Down
    )
}

@Preview
@Composable
fun ControllerComponentPreview2(modifier: Modifier = Modifier) {
    ControllerComponent(
        modifier = modifier.size(100.dp),
        onCommand = {},
        start = false,
        direction = Direction.Up
    )
}