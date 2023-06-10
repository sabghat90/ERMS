package com.kust.ermsmanager.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.show
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentEmployeeListingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmployeeListingFragment : Fragment() {

    private var _binding: FragmentEmployeeListingBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()

    private val adapter by lazy {
        EmployeeListingAdapter(
            onItemClicked = { _, employee ->
                findNavController().navigate(
                    R.id.action_employeeListingFragment_to_employeeProfileFragment,
                    Bundle().apply {
                        putParcelable("employee", employee)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observer()
        lifecycleScope.launch {
            employeeViewModel.getEmployeeList()
        }
        binding.rvEmployee.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployee.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }

                is UiState.Success -> {
                    if (it.data.isEmpty()) {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.rvEmployee.hide()
                        binding.imgDataStatus.show()
                        binding.tvDataStatus.show()
                    } else {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.hide()
                        binding.rvEmployee.show()
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