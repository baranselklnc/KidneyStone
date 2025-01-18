package com.example.kidneystone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieAnimationView
import com.example.kidneystone.listener.DialogFragmentListener

class ResultDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationView = view.findViewById<LottieAnimationView>(R.id.lottieAnimation)
        val messageView = view.findViewById<TextView>(R.id.tvMessage)
        val closeButton = view.findViewById<Button>(R.id.btnClose)

        // Argümanları alıyoruz.
        val risk = arguments?.getBoolean(ARG_RISK, false) ?: false
        val riskMessage = arguments?.getString(ARG_RISK_MESSAGE, "")
        val notFound = arguments?.getBoolean(ARG_NOT_FOUND, false) ?: false

        // Durumları kontrol ediyoruz ve uygun animasyonları ve mesajları yerleştiriyoruz.
        when {
            notFound -> { // Ürün bulunamadı durumu
                animationView.setAnimation(R.raw.notfound) // Ürün bulunamadı animasyonu
                messageView.text = getString(R.string.not_found) // Mesaj
            }
            risk -> { // Risk durumu
                animationView.setAnimation(R.raw.unhealthy) // Riskli animasyon
                messageView.text = getString(R.string.be_risk)
            }
            else -> { // Güvenli ürün durumu
                animationView.setAnimation(R.raw.healthy) // Güvenli animasyon
                messageView.text = getString(R.string.success_message) // Güvenli mesaj
            }
        }

        // Animasyonu başlatıyoruz.
        animationView.playAnimation()

        closeButton.setOnClickListener {
            dismiss() // Dialog kapanınca
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDetach() {
        super.onDetach()
        (activity as? DialogFragmentListener)?.onDialogClosed()
    }

    companion object {
        private const val ARG_RISK = "risk"
        private const val ARG_RISK_MESSAGE = "riskMessage"
        private const val ARG_NOT_FOUND = "notFound"

        fun newInstance(risk: Boolean? = null, riskMessage: String? = null, notFound: Boolean = false): ResultDialogFragment {
            val fragment = ResultDialogFragment()
            val args = Bundle().apply {
                risk?.let { putBoolean(ARG_RISK, it) }
                riskMessage?.let { putString(ARG_RISK_MESSAGE, it) }
                putBoolean(ARG_NOT_FOUND, notFound)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
