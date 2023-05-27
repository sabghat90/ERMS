package com.kust.ermsemployee.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.databinding.FragmentEmployeeRankListingBinding
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeRankListingFragment : Fragment() {
    private var _binding: FragmentEmployeeRankListingBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel : EmployeeViewModel by viewModels()

    private val adapter by lazy {
        EmployeeRankingAdapter { _, _ -> }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeRankListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        employeeViewModel.getEmployeeRank()

        binding.rvEmployeeRankListing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployeeRankListing.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeRank.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    adapter.employeeList = it.data.toMutableList()
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