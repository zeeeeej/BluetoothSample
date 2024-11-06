package com.yunnext.bluetooth.sample.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import com.yunnext.bluetooth.sample.domain.ToastUtil
import com.yunnext.bluetooth.sample.ui.screen.MainScreen
import com.yunnext.bluetooth.sample.ui.screen.compontent.LoadingComponent
import com.yunnext.bluetooth.sample.ui.theme.BluetoothSampleTheme

class MainActivity : ComponentActivity() {

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkPermission()
    }

    private fun checkPermission() {
        val permissions = loadPermissionNoGant()
        if (permissions.isNotEmpty()) {
            ToastUtil.toast("请检查APP权限，以便正常使用。")
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LaunchedEffect(Unit) {
                enableEdgeToEdge()
            }
            BluetoothSampleTheme {
                val loading: Boolean by remember {
                    derivedStateOf {
                        false
                    }
                }


                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .padding(0.dp)
                    ) {
                        MainScreen(modifier = Modifier.padding(innerPadding))
                        if (loading) {
                            BasicAlertDialog(
                                onDismissRequest = {

                                },
                                properties = DialogProperties(
                                    dismissOnBackPress = false,
                                    dismissOnClickOutside = false
                                )
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    LoadingComponent(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }

                }
            }
        }
        requestPermission()
    }

    private fun loadPermissionNoGant() = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.BLUETOOTH_CONNECT
        } else {
            null
        },
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.BLUETOOTH_SCAN
        } else {
            null
        },
        run {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.ACCESS_FINE_LOCATION
        },
        run {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.RECORD_AUDIO
        },
    )


    private fun requestPermission() {
        val permissions = loadPermissionNoGant()
        if (permissions.isNotEmpty()) {
            launcher.launch(permissions.toTypedArray())
        }

    }


    companion object {
        fun skip(activity: Activity, animal: Boolean = true) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
            if (!animal) {
                activity.overridePendingTransition(0, 0)
            }
        }
    }
}
