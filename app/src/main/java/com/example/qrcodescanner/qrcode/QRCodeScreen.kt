package com.example.qrcodescanner.qrcode

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.qrcodescanner.ui.theme.GreenQRCode
import kotlin.math.min

private const val LINE_LENGHT_FACTOR = 12
private const val SQUARE_SIZE = 0.8f

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun QRCodeScreen(
    modifier: Modifier = Modifier,
    hasCameraPermission: Boolean,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    onFailure: ((Exception) -> Unit)? = null,
    onResult: (String) -> Unit,
) {

    val camera = remember {
        UDSQRCodeCamera()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box {
            Box(
                modifier =
                Modifier
                    .padding(paddingValues)
                    .drawWithContent {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val size = min(canvasWidth, canvasHeight) * SQUARE_SIZE

                        val topLeftX = (canvasWidth - size) / 2
                        val topLeftY = (canvasHeight - size) / 2

                        drawContent()

                        val squareTopLeft = Offset(topLeftX, topLeftY)
                        val squareSize = Size(size, size)

                        drawRect(Color(0x99000000))

                        // Draws the rectangle in the middle
                        drawRoundRect(
                            topLeft = squareTopLeft,
                            size = squareSize,
                            color = Color.Transparent,
                            blendMode = BlendMode.SrcIn,
                            cornerRadius = CornerRadius.Zero,
                        )

                        drawQrBorderCanvas(
                            rectangleTopLeft = squareTopLeft,
                            rectangleSize = squareSize,
                        )
                    },
            ) {
                if (hasCameraPermission) {
                    camera.CameraPreview(
                        onBarcodeScanned = onResult
                    )
                }
            }
            buttonText?.let {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = 16.dp,
                            start = 36.dp,
                            end = 36.dp
                        )
                ) {
                    Button(
                        onClick = { onButtonClick?.invoke() }
                    ) {
                        Text(text = "Ler QRCode")
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawQrBorderCanvas(
    borderColor: Color = GreenQRCode,
    rectangleTopLeft: Offset,
    rectangleSize: Size,
) {
    val strokeWidthPx = 2.dp.toPx()

    // Coordenadas dos cantos do quadrado
    val topRightCorner = Offset(rectangleTopLeft.x + rectangleSize.width, rectangleTopLeft.y)
    val bottomLeftCorner = Offset(rectangleTopLeft.x, rectangleTopLeft.y + rectangleSize.height)
    val bottomRightCorner =
        Offset(rectangleTopLeft.x + rectangleSize.width, rectangleTopLeft.y + rectangleSize.height)

    // Define um fator de aumento para o tamanho da linha
    val lineLengthFactor = LINE_LENGHT_FACTOR

    // Desenha as linhas da borda
    drawLine(
        color = borderColor,
        start = Offset(rectangleTopLeft.x - strokeWidthPx, rectangleTopLeft.y - strokeWidthPx),
        end = Offset(
            rectangleTopLeft.x - strokeWidthPx,
            rectangleTopLeft.y + strokeWidthPx * lineLengthFactor,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(rectangleTopLeft.x - strokeWidthPx, rectangleTopLeft.y - strokeWidthPx),
        end = Offset(
            rectangleTopLeft.x + strokeWidthPx * lineLengthFactor,
            rectangleTopLeft.y - strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(topRightCorner.x + strokeWidthPx, topRightCorner.y - strokeWidthPx),
        end = Offset(
            topRightCorner.x + strokeWidthPx,
            topRightCorner.y + strokeWidthPx * lineLengthFactor,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(topRightCorner.x + strokeWidthPx, topRightCorner.y - strokeWidthPx),
        end = Offset(
            topRightCorner.x - strokeWidthPx * lineLengthFactor,
            topRightCorner.y - strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(bottomLeftCorner.x - strokeWidthPx, bottomLeftCorner.y + strokeWidthPx),
        end = Offset(
            bottomLeftCorner.x - strokeWidthPx,
            bottomLeftCorner.y - strokeWidthPx * lineLengthFactor,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(bottomLeftCorner.x - strokeWidthPx, bottomLeftCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomLeftCorner.x + strokeWidthPx * lineLengthFactor,
            bottomLeftCorner.y + strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(bottomRightCorner.x + strokeWidthPx, bottomRightCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomRightCorner.x + strokeWidthPx,
            bottomRightCorner.y - strokeWidthPx * lineLengthFactor,
        ),
        strokeWidth = strokeWidthPx,
    )

    drawLine(
        color = borderColor,
        start = Offset(bottomRightCorner.x + strokeWidthPx, bottomRightCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomRightCorner.x - strokeWidthPx * lineLengthFactor,
            bottomRightCorner.y + strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )
}
