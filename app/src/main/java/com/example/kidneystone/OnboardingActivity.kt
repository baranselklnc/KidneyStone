package com.example.kidneystone

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsIndicator: DotsIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        dotsIndicator = findViewById(R.id.dotsIndicator)

        val fragments = listOf(
            WelcomeFragment(),
            FeatureFragment(),
            CtaFragment()
        )
        val adapter = OnboardingPagerAdapter(this, fragments)
        viewPager.adapter = adapter

        // Dots göstergesi ile ViewPager'ı bağla
        dotsIndicator.attachTo(viewPager)
        val prefs = getSharedPreferences("onboarding", MODE_PRIVATE)
        if (prefs.getBoolean("isFirstRun", true)) {
            // İlk çalıştırma, onboarding göster
            startActivity(Intent(this, OnboardingActivity::class.java))
            prefs.edit().putBoolean("isFirstRun", false).apply()
        }
    }
}
