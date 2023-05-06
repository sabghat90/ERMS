package com.kust.erms_company.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kust.erms_company.databinding.FragmentProjectProposalBinding

class ProjectProposalFragment : Fragment() {

    private var _binding : FragmentProjectProposalBinding? = null
    private val binding get() = _binding!!

    private val FILE_NAME = "project_proposal.html"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProjectProposalBinding.inflate(inflater, container, false)

        binding.projectProposalWebview.loadUrl("file:///android_asset/$FILE_NAME")

        return binding.root
    }

}