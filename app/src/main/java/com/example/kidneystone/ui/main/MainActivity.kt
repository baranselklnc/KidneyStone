package com.example.kidneystone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.kidneystone.listener.DialogFragmentListener
import com.example.kidneystone.network.ApiService
import com.example.kidneystone.network.GPTApiClient
import com.example.kidneystone.network.GPTRequest
import com.example.kidneystone.network.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), DialogFragmentListener {

    private lateinit var codeScanner: CodeScanner
    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun onDialogClosed() {
        codeScanner.startPreview() // Kamerayı yeniden başlat
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
        val isOnboardingCompleted = sharedPref.getBoolean("isOnboardingCompleted", false)
        if (!isOnboardingCompleted) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val helpIcon = findViewById<ImageView>(R.id.help_icon)
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

        requestCameraPermission()
    }

    private fun evaluateRisk(product: Product): Boolean {
        val sodium = product.nutriments?.sodium_100g
        val calcium = product.nutriments?.calcium_100g

        if ((sodium != null && sodium > 0.07) || (calcium != null && calcium > 0.08)) {
            return true
        }

        val ingredients = product.ingredients_text
        if (ingredients != null && ingredients.contains("oxalate", ignoreCase = true)) {
            return true
        }

        return false
    }

    private fun navigateToResultScreen(risk: Boolean? = null, riskMessage: String? = null, notFound: Boolean = false) {
        val dialog = ResultDialogFragment.newInstance(risk, riskMessage)
        dialog.show(supportFragmentManager, "ResultDialog")
    }
    private fun evaluateRiskMessage(sodium: Double?, calcium: Double?, oxalate: String?): String {
        val riskReasons = mutableListOf<String>()

        if (sodium != null && sodium > 0.5) riskReasons.add(getString(R.string.high_sodium))
        if (calcium != null && calcium > 0.5) riskReasons.add(getString(R.string.high_calcium))
        if (oxalate != null && oxalate.contains("oxalate", ignoreCase = true)) riskReasons.add(R.string.contains_ox.toString())

        return if (riskReasons.isNotEmpty()) {
            "${R.string.risk_factors} : ${riskReasons.joinToString(", ")}"
        } else {
            "${R.string.doctor}"
        }
    }

    private fun fetchProductInfo(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.api.getProductInfo(barcode)

                if (response.status == 1 && response.product != null) {
                    // Ürün bulundu
                    val riskMessage = evaluateRisk(response.product)
                    withContext(Dispatchers.Main) {
                        navigateToResultScreen(false, riskMessage.toString())
                    }
                } else {
                    // Ürün bulunamadı, AI analizi yap
                    Log.d("OpenFoodFacts", "Product not found. Status: ${response.status}")
                    fetchAIAnalysis(barcode)
                }
            } catch (e: Exception) {
                Log.e("Product_Error", "Failed to fetch product: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, getString(R.string.unexpected), Toast.LENGTH_SHORT).show()
                }
                codeScanner.startPreview()
            }
        }
    }

    private fun fetchAIAnalysis(barcode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("AI_Analysis", "Fetching AI analysis for barcode: $barcode")


                val request = GPTRequest(
                    prompt = "Barcode $barcode: Is this product safe for kidney stone patients? Respond with 'safe' or 'unsafe'.",
                    max_tokens = 10
                )
                val response = GPTApiClient.instance.getGPTResponse(request)

                val aiResult = response.choices.firstOrNull()?.text?.trim() ?: "unknown"
                Log.d("AI_Response", "AI result: $aiResult")

                val isSafe = aiResult.equals("safe", ignoreCase = true)
                val riskMessage = if (isSafe) {
                    getString(R.string.success_message)
                } else {
                    getString(R.string.be_risk)
                }

                withContext(Dispatchers.Main) {
                    navigateToResultScreen(!isSafe, riskMessage)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("AI_Error", "Failed to fetch AI analysis: ${e.message}")



                    Toast.makeText(this@MainActivity, getString(R.string.unexpected), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestCameraPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
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
