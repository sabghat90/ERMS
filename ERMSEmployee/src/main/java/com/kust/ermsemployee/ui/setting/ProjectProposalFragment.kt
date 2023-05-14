package com.kust.ermsemployee.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kust.ermsemployee.databinding.FragmentProjectProposalBinding

class ProjectProposalFragment : Fragment() {

    private var _binding : FragmentProjectProposalBinding? = null
    private val binding get() = _binding!!

    private val FILENAME = "project_proposal.html"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProjectProposalBinding.inflate(inflater, container, false)

        binding.projectProposalWebview.loadUrl("file:///android_asset/$FILENAME")

        return binding.root
    }

}