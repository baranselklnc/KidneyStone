package com.example.kidneystone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
//import com.budiyev.android.codescanner.ErrorCallback
import com.example.kidneystone.listener.DialogFragmentListener
import com.example.kidneystone.network.ApiService
import com.example.kidneystone.network.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(),DialogFragmentListener {

    private lateinit var codeScanner: CodeScanner
    override fun onDialogClosed() {
        codeScanner.startPreview() // Kamerayı yeniden başlat
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
        val isOnboardingCompleted = sharedPref.getBoolean("isOnboardingCompleted", false)
        if (!isOnboardingCompleted) {
            // Eğer onboarding tamamlanmadıysa, onboarding ekranına yönlendir
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish() // MainActivity'yi kapat
            return
        }
        setContentView(R.layout.activity_main)


        val helpIcon=findViewById<ImageView>(R.id.help_icon)
        helpIcon.setOnClickListener {
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)

        }


        val scannerView = findViewById<CodeScannerView>(R.id.scannerView)
        codeScanner = CodeScanner(this, scannerView)

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                fetchProductInfo(it.text)
            }
        }

        /*
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        */

        requestCameraPermission()
    }

    private fun evaluateRisk(product: Product): Boolean {
        // Nutriments üzerinden risk değerlendirmesi
        val sodium = product.nutriments?.sodium_100g
        val calcium = product.nutriments?.calcium_100g

        // Sodyum veya kalsiyum yüksekse risk var
        if ((sodium != null && sodium > 0.07) || (calcium != null && calcium > 0.08)) {
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
                    } else if (response.status == 0 || response.status == 404) {
                        Toast.makeText(
                            this@MainActivity,
                            "${R.string.not_found}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "${R.string.unexpected}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: java.net.SocketTimeoutException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "${R.string.timeout}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val dialog = ResultDialogFragment.newInstance(notFound = true)
                    dialog.show(supportFragmentManager, "ResultDialog")
                }
            }
        }
    }

    private fun evaluateRiskMessage(sodium: Double?, calcium: Double?, oxalate: String?): String {
        val riskReasons = mutableListOf<String>()

        if (sodium != null && sodium > 0.5) riskReasons.add(R.string.high_sodium.toString())
        if (calcium != null && calcium > 0.5) riskReasons.add(R.string.high_calcium.toString())
        if (oxalate != null && oxalate.contains("oxalate", ignoreCase = true)) riskReasons.add(R.string.contains_ox.toString())

        return if (riskReasons.isNotEmpty()) {
            "${R.string.risk_factors} : ${riskReasons.joinToString(", ")}"
        } else {
            "${R.string.doctor}"
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                codeScanner.startPreview()
            } else {
                Toast.makeText(this, "${R.string.denied_camera}", Toast.LENGTH_SHORT).show()
            }
        }

        when {
            checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                codeScanner.startPreview()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, "${R.string.required_camera}", Toast.LENGTH_SHORT).show()
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
