package com.kust.ermsemployee.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kust.ermsemployee.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment() {

    // create binding
    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    private val FILENAME = "privacy_policy.html"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentPrivacyPolicyBinding.inflate(inflater, container, false)

        // load html file
        binding.privacyPolicyWebView.loadUrl("file:///android_asset/$FILENAME")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}