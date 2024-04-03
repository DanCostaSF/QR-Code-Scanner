package com.example.qrcodescanner.qrcode

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalGetImage
class QRCodeActivity : ComponentActivity() {

    val viewModel: QRCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeScreen()
        }
    }

    @Composable
    private fun QRCodeScreen() {
        val cameraPermission = rememberPermissionState(
            Manifest.permission.CAMERA
        )

        var openQRCodeReader by remember {
            mutableStateOf(false)
        }

        var qrCodeResult by remember {
            mutableStateOf("")
        }

        val context = LocalContext.current


        LaunchedEffect(key1 = true) {
            when {
                cameraPermission.status.shouldShowRationale -> {
                    Toast.makeText(context, "Usuario negou", Toast.LENGTH_SHORT).show()
                }

                !cameraPermission.status.isGranted -> {
                    cameraPermission.launchPermissionRequest()
                }

                cameraPermission.status.isGranted -> {
                    openQRCodeReader = true
                    qrCodeResult = ""
                }
            }
        }

        if (openQRCodeReader) {
            QRCodeReader(hasCameraPermission = cameraPermission.status.isGranted) {
                if (it.isNotEmpty() && it != qrCodeResult) {
                    qrCodeResult = it
                    val intent = Intent(this@QRCodeActivity, FinishActivity::class.java).run {
                        this.putExtra("barcode", it)
                    }
                    Log.i("QrCodeTeste", it)
                    startActivity(intent)
                }
            }
        }
    }
}