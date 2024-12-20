package com.yunnext.bluetooth.sample.ui.screen.compontent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntSize
import com.yunext.kotlin.kmp.compose.clickablePure

@Preview("AlertComponentPreview")
@Composable
fun AlertComponentPreview() {
    // 请打开手机蓝牙
    AlertComponent(Modifier, msg = "蓝牙未开启，") {

    }
}

@Composable
fun AlertComponent(
    modifier: Modifier = Modifier,
    msg: String,
    left: String = "取消",
    right: String = "开启蓝牙",
    onClick: (Boolean) -> Unit
) {

    Column(
        modifier = modifier
            .width(270.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)

    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                msg,
                style = TextStyle.Default.copy(
                    color = Color(0xff030303),
                    textAlign = TextAlign.Center
                ),
                fontWeight = FontWeight.Bold, fontSize = 17.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(.5.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black.copy(alpha = .1f),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                })

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(
                text = left,
                style = TextStyle.Default.copy(
                    color = Color(0xff030303),
                    textAlign = TextAlign.Center
                ),
                fontSize = 17.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
                    .clickablePure {
                        onClick(false)
                    }
            )

            Spacer(
                Modifier
                    .fillMaxHeight()
                    .width(.5.dp)
                    .drawBehind {
                        drawLine(
                            color = Color.Black.copy(alpha = .1f),
                            start = Offset.Zero,
                            end = Offset(size.width, size.height)
                        )
                    }
            )

            Text(
                text = right,
                style = TextStyle.Default.copy(
                    color = Color(0xff030303),
                    textAlign = TextAlign.Center
                ),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
                    .clickablePure {
                        onClick(true)
                    }

            )
        }

    }

}