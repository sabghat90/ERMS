package com.kust.ermsmanager.ui.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.kust.ermsmanager.R

class SecondOnBoardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_on_boarding, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        view.findViewById<View>(R.id.next).setOnClickListener {
            viewPager?.currentItem = 2
        }
        return view
    }
}