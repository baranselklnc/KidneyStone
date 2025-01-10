package com.example.kidneystone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView

class WelcomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)



    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val animationView= view.findViewById<LottieAnimationView>(R.id.supermarket_anim)
        animationView.setAnimation(R.raw.supermarket)
        animationView.playAnimation()
        val animationFruit=view.findViewById<LottieAnimationView>(R.id.fruits)
        animationFruit.setAnimation(R.raw.fruits)
        animationFruit.playAnimation()


    }

}