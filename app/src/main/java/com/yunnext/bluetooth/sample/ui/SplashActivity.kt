package com.yunnext.bluetooth.sample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunnext.bluetooth.sample.R
import com.yunnext.bluetooth.sample.ui.theme.BluetoothSampleTheme
import com.yunnext.bluetooth.sample.ui.theme.Color333
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    //    private val vm: LoginViewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluetoothSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .padding(0.dp)
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Image(
                                painterResource(R.mipmap.login_bg),
                                null,
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(
                                Modifier.padding(innerPadding),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(300.dp))

                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "蓝牙示例",
                                    style = TextStyle.Default.copy(
                                        color = Color333,
                                        fontSize = 56.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    ), modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                delay(1000)
                MainActivity.skip(this@SplashActivity, false)
                finish()
            }
        }
    }
}
