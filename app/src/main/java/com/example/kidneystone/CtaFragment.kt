package com.example.kidneystone

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
    )
    : View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_cta, container, false)

        val startAppButton=view.findViewById<Button>(R.id.btnStartApp)
        startAppButton.setOnClickListener {
            val intent=Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)

            requireActivity().finish()
        }


        return view

}
    }