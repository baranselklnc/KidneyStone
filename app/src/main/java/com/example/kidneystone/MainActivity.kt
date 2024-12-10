package com.example.kidneystone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.example.kidneystone.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scannerView = findViewById<CodeScannerView>(R.id.scannerView)
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                fetchProductInfo(it.text)
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        requestCameraPermission()
    }

    private fun fetchProductInfo(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.api.getProductInfo(barcode)
                withContext(Dispatchers.Main) {
                    Log.d("API_RESPONSE", "Response: $response")

                    if (response.status == 1 && response.product != null) {
                        val productName = response.product.product_name ?: "Ürün adı bulunamadı"
                        val ingredients = response.product.ingredients_text ?: "İçerik bilgisi bulunamadı"

                        Log.d("PRODUCT_DATA", "Ürün adı: $productName, İçerikler: $ingredients")

                        Toast.makeText(
                            this@MainActivity,
                            "Ürün: $productName\nİçerik: $ingredients",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.d("API_ERROR", "Ürün bulunamadı: ${response.status}")
                        Toast.makeText(
                            this@MainActivity,
                            "Ürün bulunamadı.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_EXCEPTION", "Hata: ${e.message}")
                    Toast.makeText(this@MainActivity, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                codeScanner.startPreview()
            } else {
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show()
            }
        }

        when {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                codeScanner.startPreview()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "Kamera izni gerekiyor.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}
