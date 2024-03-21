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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.qrcodescanner.ui.theme.GreenQRCode
import com.example.qrcodescanner.ui.theme.QRCodeScannerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun TesteBarcode(parametro: (String?) -> Unit) {

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

                            val rectangleTopLeft = Offset((canvasWidth - width) / 2, canvasHeight * .3f)
                            val rectangleSize = Size(width, height)

                            drawRect(Color(0x99000000))

                            // Draws the rectangle in the middle
                            drawRoundRect(
                                topLeft = Offset(
                                    (canvasWidth - width) / 2,
                                    canvasHeight * .3f
                                ),
                                size = Size(width, height),
                                color = Color.Transparent,
                                blendMode = BlendMode.SrcIn
                            )

                            drawQrBorderCanvas(
                                curve = 0.dp,
                                strokeWidth = 3.dp,
                                capSize = 24.dp,
                                rectangleTopLeft = rectangleTopLeft,
                                rectangleSize = rectangleSize
                            )

                        }
                ) {
                    if (cameraPermission.status.isGranted) {
                        camera.CameraPreview(
                            onBarcodeScanned = { barcode ->
                                barcode?.displayValue?.let {
                                        parametro.invoke(it)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawQrBorderCanvas(
    borderColor: Color = GreenQRCode,
    curve: Dp,
    strokeWidth: Dp,
    capSize: Dp,
    lineCap: StrokeCap = StrokeCap.Round,
    rectangleTopLeft: Offset,
    rectangleSize: Size
) {
    val curvePx = curve.toPx()
    val mCapSize = capSize.toPx()

    // Linha inferior esquerda para centro
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + curvePx, rectangleTopLeft.y + rectangleSize.height),
        Offset(rectangleTopLeft.x + mCapSize, rectangleTopLeft.y + rectangleSize.height),
        strokeWidth.toPx(), lineCap
    )

    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + rectangleSize.width - mCapSize, rectangleTopLeft.y + rectangleSize.height),
        Offset(rectangleTopLeft.x + rectangleSize.width - curvePx, rectangleTopLeft.y + rectangleSize.height),
        strokeWidth.toPx(), lineCap
    )

    // Linha superior
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + rectangleSize.width, rectangleTopLeft.y + rectangleSize.height - mCapSize),
        Offset(rectangleTopLeft.x + rectangleSize.width, rectangleTopLeft.y + rectangleSize.height - curvePx),
        strokeWidth.toPx(), lineCap
    )

    // Linha inferior
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + mCapSize, rectangleTopLeft.y + rectangleSize.height),
        Offset(rectangleTopLeft.x + curvePx, rectangleTopLeft.y + rectangleSize.height),
        strokeWidth.toPx(), lineCap
    )

    // Linha esquerda
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x, rectangleTopLeft.y + curvePx),
        Offset(rectangleTopLeft.x, rectangleTopLeft.y + mCapSize),
        strokeWidth.toPx(), lineCap
    )

    // Linha superior esquerda para centro
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + curvePx, rectangleTopLeft.y),
        Offset(rectangleTopLeft.x + mCapSize, rectangleTopLeft.y),
        strokeWidth.toPx(), lineCap
    )

    // Linha superior direita para centro
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + rectangleSize.width - curvePx, rectangleTopLeft.y),
        Offset(rectangleTopLeft.x + rectangleSize.width - mCapSize, rectangleTopLeft.y),
        strokeWidth.toPx(), lineCap
    )

    // Linha inferior direita para centro
    drawLine(
        SolidColor(borderColor),
        Offset(rectangleTopLeft.x + rectangleSize.width - curvePx, rectangleTopLeft.y + rectangleSize.height),
        Offset(rectangleTopLeft.x + rectangleSize.width - mCapSize, rectangleTopLeft.y + rectangleSize.height),
        strokeWidth.toPx(), lineCap
    )

}
