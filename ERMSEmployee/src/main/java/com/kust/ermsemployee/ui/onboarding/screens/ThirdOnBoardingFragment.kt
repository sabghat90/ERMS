package com.kust.ermsemployee.ui.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kust.ermsemployee.R
import com.kust.ermsemployee.ui.auth.AuthActivity

class ThirdOnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_on_boarding, container, false)

        view.findViewById<View>(R.id.finish).setOnClickListener {
            val intent = Intent(requireContext(), AuthActivity::class.java)
            onBoardingFinished()
            activity?.finish()
            startActivity(intent)
        }

        return view
    }

    private fun onBoardingFinished() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}