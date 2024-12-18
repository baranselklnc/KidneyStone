package com.example.kidneystone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class CtaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cta, container, false)

        val startAppButton = view.findViewById<Button>(R.id.btnStartApp)
        startAppButton.setOnClickListener {
            // Onboarding tamamlandı bilgisini kaydet
            val sharedPref = requireActivity().getSharedPreferences("Onboarding", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("isOnboardingCompleted", true).apply()

            // Ana ekrana yönlendir
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)

            // OnboardingActivity'yi kapat
            requireActivity().finish()
        }

        return view
    }
}
