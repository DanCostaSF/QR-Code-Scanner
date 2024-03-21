package com.example.qrcodescanner.qrcode

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ExperimentalGetImage

@ExperimentalGetImage
class QRCodeActivity : ComponentActivity() {

    val viewModel: QRCodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TesteBarcode() {
                if (!it.isNullOrEmpty()) {
                    val intent = Intent(this@QRCodeActivity, FinishActivity::class.java).run {
                        this.putExtra("barcode", it)
                    }

                    startActivity(intent)
                }

            }
        }
    }
}