package com.example.qrcodescanner.qrcode

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.qrcodescanner.R
import com.example.qrcodescanner.ui.theme.GreenQRCode
import kotlin.math.min

private const val LINE_LENGTH_FACTOR = 12
private const val SQUARE_SIZE = 0.8f

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalGetImage
@Composable
fun QRCodeReader(
    modifier: Modifier = Modifier,
    hasCameraPermission: Boolean,
    buttonText: String? = null,
    titleText: String? = "Escaneie seu QR Code",
    onButtonClick: (() -> Unit)? = null,
    onFailure: ((Exception) -> Unit)? = null,
    onResult: (String) -> Unit,
) {
    val camera = remember {
        UDSQRCodeCamera()
    }

    val context = LocalContext.current
    if (hasCameraPermission) {
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

                            // Draws the square in the middle
                            drawRoundRect(
                                topLeft = squareTopLeft,
                                size = squareSize,
                                color = Color.Transparent,
                                blendMode = BlendMode.SrcIn,
                                cornerRadius = CornerRadius.Zero,
                            )

                            drawQrBorderCanvas(
                                squareTopLeft = squareTopLeft,
                                squareSize = squareSize,
                            )

                            drawTextTitleQrCode(titleText, canvasWidth, squareTopLeft, context)
                        },
                ) {
                    camera.CameraPreview(
                        onBarcodeScanned = onResult,
                        onFailure = onFailure
                    )
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
}

private fun ContentDrawScope.drawTextTitleQrCode(
    titleText: String?,
    canvasWidth: Float,
    squareTopLeft: Offset,
    context: Context
) {
    titleText?.let {
        val paint = Paint().apply {
            color = Color.White.toArgb()
            textSize = 60f
            textAlign = Paint.Align.LEFT
        }

        val textBounds = Rect()
        paint.getTextBounds(it, 0, it.length, textBounds)

        val textWidth = paint.measureText(it)
        val textHeight = textBounds.height()

        val textLeft = (canvasWidth - textWidth) / 2
        val textTop = squareTopLeft.y - textHeight

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.drawText(it, textLeft - 32, textTop, paint)

            val drawable = ContextCompat.getDrawable(context, R.drawable.qrcode_icon)
            drawable?.let { qrCodeIcon ->
                val imageBitmap = qrCodeIcon.toBitmap().asImageBitmap()

                val imageLeft = textLeft + textWidth + 8

                val imageTop = textTop - textHeight - ((imageBitmap.height - textHeight) / 3)
                canvas.nativeCanvas.drawBitmap(
                    imageBitmap.asAndroidBitmap(),
                    imageLeft,
                    imageTop,
                    null
                )
            }
        }
    }
}

@Suppress("LongMethod")
private fun DrawScope.drawQrBorderCanvas(
    borderColor: Color = GreenQRCode,
    squareTopLeft: Offset,
    squareSize: Size,
) {
    val strokeWidthPx = 2.dp.toPx()

    val topRightCorner = Offset(squareTopLeft.x + squareSize.width, squareTopLeft.y)
    val bottomLeftCorner = Offset(squareTopLeft.x, squareTopLeft.y + squareSize.height)
    val bottomRightCorner =
        Offset(squareTopLeft.x + squareSize.width, squareTopLeft.y + squareSize.height)

    // Top left vertical line
    drawLine(
        color = borderColor,
        start = Offset(squareTopLeft.x - strokeWidthPx, squareTopLeft.y - strokeWidthPx),
        end =
        Offset(
            squareTopLeft.x - strokeWidthPx,
            squareTopLeft.y + strokeWidthPx * LINE_LENGTH_FACTOR,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Top left horizontal line
    drawLine(
        color = borderColor,
        start = Offset(squareTopLeft.x - strokeWidthPx, squareTopLeft.y - strokeWidthPx),
        end =
        Offset(
            squareTopLeft.x + strokeWidthPx * LINE_LENGTH_FACTOR,
            squareTopLeft.y - strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Top right vertical line
    drawLine(
        color = borderColor,
        start = Offset(topRightCorner.x + strokeWidthPx, topRightCorner.y - strokeWidthPx),
        end =
        Offset(
            topRightCorner.x + strokeWidthPx,
            topRightCorner.y + strokeWidthPx * LINE_LENGTH_FACTOR,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Top left horizontal line
    drawLine(
        color = borderColor,
        start = Offset(topRightCorner.x + strokeWidthPx, topRightCorner.y - strokeWidthPx),
        end =
        Offset(
            topRightCorner.x - strokeWidthPx * LINE_LENGTH_FACTOR,
            topRightCorner.y - strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Bottom left vertical line
    drawLine(
        color = borderColor,
        start = Offset(bottomLeftCorner.x - strokeWidthPx, bottomLeftCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomLeftCorner.x - strokeWidthPx,
            bottomLeftCorner.y - strokeWidthPx * LINE_LENGTH_FACTOR,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Bottom left horizontal line
    drawLine(
        color = borderColor,
        start = Offset(bottomLeftCorner.x - strokeWidthPx, bottomLeftCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomLeftCorner.x + strokeWidthPx * LINE_LENGTH_FACTOR,
            bottomLeftCorner.y + strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Bottom right vertical line
    drawLine(
        color = borderColor,
        start = Offset(bottomRightCorner.x + strokeWidthPx, bottomRightCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomRightCorner.x + strokeWidthPx,
            bottomRightCorner.y - strokeWidthPx * LINE_LENGTH_FACTOR,
        ),
        strokeWidth = strokeWidthPx,
    )

    // Bottom right horizontal line
    drawLine(
        color = borderColor,
        start = Offset(bottomRightCorner.x + strokeWidthPx, bottomRightCorner.y + strokeWidthPx),
        end =
        Offset(
            bottomRightCorner.x - strokeWidthPx * LINE_LENGTH_FACTOR,
            bottomRightCorner.y + strokeWidthPx,
        ),
        strokeWidth = strokeWidthPx,
    )
}

