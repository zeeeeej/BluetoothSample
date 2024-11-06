package com.yunnext.bluetooth.sample.ui.screen.compontent

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yunnext.bluetooth.sample.R


@Preview("LoadingComponentPreview")
@Composable
fun LoadingComponentPreview(modifier: Modifier = Modifier) {
    LoadingComponent()
}

@Composable
fun LoadingComponent(
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(
                RoundedCornerShape(
                    16.dp
                )
            )
            .shadow(
                4.dp, RoundedCornerShape(
                    16.dp
                )
            )
            .background(Color.White)
           , contentAlignment = Alignment.Center
    ) {

        if (content == null) {
            // 创建一个可观察的动画状态
            val infiniteTransition = rememberInfiniteTransition(label = "1")
            val animationValue by infiniteTransition.animateFloat(
                // 使用 infiniteRepeatable 创建无限重复的动画
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000), // 1000毫秒的补间动画
                    repeatMode = RepeatMode.Restart // 动画重复模式
                ), label = "2"
            )
            Image(
                painter = painterResource(R.mipmap.loading_b),
                null,
                modifier = Modifier.rotate(animationValue)
            )
        } else {
            content()
        }
    }
}

@Composable
fun LoadingIcon(modifier: Modifier = Modifier) {
    // 创建一个可观察的动画状态
    val infiniteTransition = rememberInfiniteTransition(label = "1")
    val animationValue by infiniteTransition.animateFloat(
        // 使用 infiniteRepeatable 创建无限重复的动画
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000), // 1000毫秒的补间动画
            repeatMode = RepeatMode.Restart // 动画重复模式
        ), label = "LoadingIcon"
    )
    Image(
        painter = painterResource(R.mipmap.loading_s),
        null,
        modifier = Modifier.rotate(animationValue)
    )
}