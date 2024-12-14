package com.example.kidneystone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimation)
        val messageView = findViewById<TextView>(R.id.tvMessage)

        val risk = intent.getBooleanExtra("risk", false)
        val riskMessage = intent.getStringExtra("riskMessage") ?: ""

        if (risk) {
            // Risk varsa olumsuz animasyon
            animationView.setAnimation(R.raw.unhealthy) // Olumsuz Lottie animasyonu
            messageView.text = "Bu ürün böbrek taşı riski taşıyabilir!\n$riskMessage"
        } else {
            // Risk yoksa olumlu animasyon
            animationView.setAnimation(R.raw.healthy) // Olumlu Lottie animasyonu
            messageView.text = "Harika! Bu ürün böbrek taşı oluşturma riskine sahip değil güvenle tüketebilirsin."
        }

        animationView.playAnimation()
    }
}
