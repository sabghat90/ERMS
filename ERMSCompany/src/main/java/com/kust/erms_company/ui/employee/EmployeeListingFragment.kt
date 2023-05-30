package com.kust.erms_company.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentEmployeeListingBinding
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeListingFragment : Fragment() {

    private var _binding : FragmentEmployeeListingBinding? = null
    private val binding get() = _binding!!

    private val viewModel : EmployeeViewModel by viewModels()

    private val adapter by lazy { EmployeeListingAdapter(
        onItemClicked = { _, employee ->
            findNavController().navigate(R.id.action_manageEmployeeFragment_to_profileFragment, Bundle().apply {
                putParcelable("employee", employee)
            })
        }
    ) }

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

        binding.rvEmployee.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployee.adapter = adapter
    }

    private fun observer() {
        viewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.shimmerLayout.startShimmer()
                }
                is UiState.Success -> {
                    adapter.employees = it.data as MutableList<Employee>
                    adapter.updateList(it.data.toMutableList())
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.rvEmployee.visibility = View.VISIBLE
                }
                is UiState.Error -> {
                    binding.shimmerLayout.stopShimmer()
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