package com.yunnext.bluetooth.sample.ui.common

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun CommonArrow(modifier: Modifier = Modifier, opened: Boolean) {
    Image(
        Icons.Default.ArrowDropDown,
        null,
        modifier = modifier.rotate(if (opened) 0f else -90f)
    )
}

@Composable
fun CommonArrowTitle(modifier: Modifier = Modifier, title: String, opened: Boolean) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(title)
        CommonArrow(opened = opened)
    }
}

