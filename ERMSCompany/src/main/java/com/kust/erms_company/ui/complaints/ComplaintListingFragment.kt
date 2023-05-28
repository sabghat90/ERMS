package com.kust.erms_company.ui.complaints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.data.model.ComplaintModel
import com.kust.erms_company.databinding.FragmentComplaintListingBinding
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComplaintListingFragment : Fragment() {

    private var _binding: FragmentComplaintListingBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()

    private val adapter by lazy {
        ComplaintListingAdapter(requireContext()) { _, complaint ->
            findNavController().navigate(R.id.action_complaintListingFragment_to_complaintDetailFragment, Bundle().apply {
                putParcelable("complaint", complaint)
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentComplaintListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        lifecycleScope.launch {
            complaintViewModel.getComplaints()
        }

        binding.rvComplaints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComplaints.adapter = adapter

    }

    private fun observer() {
        complaintViewModel.getComplaints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    adapter.complaintList = it.data as MutableList<ComplaintModel>
                    adapter.submitList(it.data)
                }
                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}