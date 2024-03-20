@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)

package com.example.qrcodescanner.qrcode

import android.Manifest
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.qrcodescanner.ui.theme.QRCodeScannerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalGetImage
@Composable
fun TesteBarcode(parametro: (String?) -> Unit) {

    var barcodeString: String? = null

    QRCodeScannerTheme {
        val cameraPermission = rememberPermissionState(
            Manifest.permission.CAMERA
        )
        LaunchedEffect(key1 = true) {
            if (!cameraPermission.status.isGranted) {
                cameraPermission.launchPermissionRequest()
            }
        }

        val camera = remember {
            BarcodeCam()
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .drawWithContent {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val width = canvasWidth * .9f
                            val height = width * 3 / 4f

                            drawContent()

                            drawRect(Color(0x99000000))

                            // Draws the rectangle in the middle
                            drawRoundRect(
                                topLeft = Offset(
                                    (canvasWidth - width) / 2,
                                    canvasHeight * .3f
                                ),
                                size = Size(width, height),
                                color = Color.Transparent,
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                blendMode = BlendMode.SrcIn
                            )

                            // Draws the rectangle outline
                            drawRoundRect(
                                topLeft = Offset(
                                    (canvasWidth - width) / 2,
                                    canvasHeight * .3f
                                ),
                                color = Color.White,
                                size = Size(width, height),
                                cornerRadius = CornerRadius(24.dp.toPx()),
                                style = Stroke(
                                    width = 2.dp.toPx()
                                ),
                                blendMode = BlendMode.Src
                            )
                        }
                ) {
                    if (cameraPermission.status.isGranted) {
                        camera.CameraPreview(
                            onBarcodeScanned = { barcode ->
                                barcode?.displayValue?.let {
                                    if (barcodeString.isNullOrEmpty()) {
                                        parametro.invoke(it)
                                    }
                                    barcodeString = it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

