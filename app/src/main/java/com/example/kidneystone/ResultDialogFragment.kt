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

        val risk = arguments?.getBoolean("risk") ?: false
        val riskMessage = arguments?.getString("riskMessage") ?: ""

        if (risk) {
            animationView.setAnimation(R.raw.unhealthy) // Olumsuz animasyon
            messageView.text = "Bu ürün böbrek taşı riski taşıyabilir!\n$riskMessage"
        } else {
            animationView.setAnimation(R.raw.healthy) // Olumlu animasyon
            messageView.text =
                "Harika! Bu ürün böbrek taşı oluşturma riskine sahip değil güvenle tüketebilirsin."
        }

        animationView.playAnimation()

        closeButton.setOnClickListener {
            dismiss()
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

        fun newInstance(risk: Boolean, riskMessage: String): ResultDialogFragment {
            val fragment = ResultDialogFragment()
            val args = Bundle().apply {
                putBoolean(ARG_RISK, risk)
                putString(ARG_RISK_MESSAGE, riskMessage)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
