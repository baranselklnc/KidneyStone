package com.example.kidneystone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.example.kidneystone.network.ApiService
import com.example.kidneystone.network.Product
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
    private fun evaluateRisk(product: Product): Boolean {
        // Nutriments üzerinden risk değerlendirmesi
        val sodium = product.nutriments?.sodium_100g
        val calcium = product.nutriments?.calcium_100g

        // Sodyum veya kalsiyum yüksekse risk var
        if ((sodium != null && sodium > 1500.0) || (calcium != null && calcium > 1000.0)) {
            return true
        }

        // İçeriklerde oksalat varsa risk var
        val ingredients = product.ingredients_text
        if (ingredients != null && ingredients.contains("oxalate", ignoreCase = true)) {
            return true
        }

        // Risk bulunmadı
        return false
    }
    private fun navigateToResultScreen(risk: Boolean, riskMessage: String) {
        val dialog = ResultDialogFragment.newInstance(risk, riskMessage)
        dialog.show(supportFragmentManager, "ResultDialog")
    }



    private fun fetchProductInfo(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.api.getProductInfo(barcode)
                withContext(Dispatchers.Main) {
                    if (response.status == 1 && response.product != null) {
                        val product = response.product
                        val risk = evaluateRisk(product)
                        val riskMessage = evaluateRiskMessage(
                            product.nutriments?.sodium_100g,
                            product.nutriments?.calcium_100g,
                            product.ingredients_text
                        )
                        navigateToResultScreen(risk, riskMessage)
                    } else if (response.status == 0) {
                        Toast.makeText(
                            this@MainActivity,
                            "Üzgünüz, bu barkoda ait ürün veritabanımızda bulunamadı.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Beklenmedik bir hata oluştu. Lütfen tekrar deneyin.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Bir hata oluştu: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun evaluateRiskMessage(sodium: Double?, calcium: Double?, oxalate: String?): String {
        val riskReasons = mutableListOf<String>()

        if (sodium != null && sodium > 1500.0) riskReasons.add("Yüksek sodyum")
        if (calcium != null && calcium > 1000.0) riskReasons.add("Yüksek kalsiyum")
        if (oxalate != null && oxalate.contains("oxalate", ignoreCase = true)) riskReasons.add("Oksalat içeriyor")

        return if (riskReasons.isNotEmpty()) {
            "Risk faktörleri: ${riskReasons.joinToString(", ")}"
        } else {
            "Böbrek taşı riski bulunmuyor."
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
