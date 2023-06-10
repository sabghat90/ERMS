package com.kust.erms_company.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentComplaintListingBinding
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.show
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComplaintListingFragment : Fragment() {

    private var _binding: FragmentComplaintListingBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()

    private val adapter by lazy {
        ComplaintListingAdapter(requireContext()) { _, complaint ->
            findNavController().navigate(
                R.id.action_complaintListingFragment_to_complaintDetailFragment,
                Bundle().apply {
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
                    binding.shimmerLayout.startShimmer()
                }

                is UiState.Success -> {
                    if (it.data.isEmpty()) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.imgDataStatus.show()
                        binding.shimmerLayout.hide()
                        binding.tvDataStatus.show()
                    } else {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.rvComplaints.visibility = View.VISIBLE
                        adapter.submitList(it.data)
                    }
                }

                is UiState.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.hide()
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