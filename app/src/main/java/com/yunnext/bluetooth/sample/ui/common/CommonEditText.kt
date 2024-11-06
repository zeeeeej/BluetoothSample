package com.yunnext.bluetooth.sample.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp

@Composable
fun CommonEditText(
    modifier: Modifier = Modifier,
    value: String,
    hint: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = hint,
            maxLines = 1,
            style = TextStyle.Default.copy(fontSize = 14.sp, color = Color.Black.copy(0.2f))
        )
    },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit,

    ) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier,
        textStyle = TextStyle.Default.copy(fontSize = 14.sp, color = Color.Black),
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        singleLine = true,
        maxLines = 1,
        visualTransformation = visualTransformation,
//        shape = RoundedCornerShape(12.dp),
//        colors = TextFieldDefaults.textFieldColors(
//            cursorColor = China.r_luo_xia_hong,
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent
//        )
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
//                cursorColor = China.r_luo_xia_hong,
        )
    )
}
