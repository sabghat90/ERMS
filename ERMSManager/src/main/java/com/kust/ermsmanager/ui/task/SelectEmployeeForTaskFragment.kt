package com.kust.ermsmanager.ui.task

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentSelectEmployeeForTaskBinding
import com.kust.ermsmanager.ui.employee.EmployeeListingAdapter
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectEmployeeForTaskFragment : Fragment() {

    private var _binding: FragmentSelectEmployeeForTaskBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()

    private val adapter by lazy {
        EmployeeListingAdapter(
            onItemClicked = { _, employee ->
                val bundle = Bundle()
                bundle.putParcelable("employee", employee)
                bundle.putBoolean("isEdit", false)
                findNavController().navigate(
                    R.id.action_selectEmployeeForTaskFragment_to_createTaskFragment,
                    bundle
                )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSelectEmployeeForTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        employeeViewModel.getEmployeeList()

        binding.rvEmpList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpList.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
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