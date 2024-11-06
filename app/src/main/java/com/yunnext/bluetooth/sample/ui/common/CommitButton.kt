package com.yunnext.bluetooth.sample.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Preview("enable false")
@Composable
fun CommitButtonPreview1() {
    CommitButton(text = "登录", enable = false) {

    }
}

@Preview("enable true")
@Composable
fun CommitButtonPreview2() {
    CommitButton(text = "登录", enable = true) {

    }
}

@Composable
fun CommitButton(
    modifier: Modifier = Modifier,
    text: String,
    enable: Boolean,
    onCommit: () -> Unit
) {

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .drawBehind {
                val color = if (enable) Color(0xFF3C6AF0) else Color(0xFFD8E1FC)
                drawRect(color = color)
            }
            .clickable(enabled = enable) { onCommit() }
            .padding(vertical = 12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text, style = TextStyle.Default.copy(
                color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
            )
        )
    }

}

@Preview("enable true")
@Composable
fun CommitButtonPreview3() {
    CommitButtonBlack(text = "登录", enable = true) {

    }
}

@Composable
fun CommitButtonBlack(
    modifier: Modifier = Modifier,
    text: String,
    enable: Boolean,
    onCommit: () -> Unit
) {

    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFFBBBBBB), shape = RoundedCornerShape(22.dp))
            .clickable(enabled = enable) { onCommit() }
            .padding(vertical = 12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text, style = TextStyle.Default.copy(
                color = Color.Black.copy(alpha = .9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }

}