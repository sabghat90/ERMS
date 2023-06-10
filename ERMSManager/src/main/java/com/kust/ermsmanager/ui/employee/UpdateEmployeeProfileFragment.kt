package com.kust.ermsmanager.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hideKeyboard
import com.kust.ermsmanager.databinding.FragmentUpdateEmployeeProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UpdateEmployeeProfileFragment : Fragment() {
    private var _binding: FragmentUpdateEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()
    @Inject
    lateinit var employeeObj: Employee

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateEmployeeProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        employeeObj = arguments?.getParcelable("employee")!!
        // update tool bar name with employee name
        requireActivity().title = employeeObj.name
        updateUi()
        observer()

        binding.btnUpdate.setOnClickListener {
            hideKeyboard()
            lifecycleScope.launch {
                if (validation()) {
                    employeeObj.department = binding.etDepartment.text.toString()
                    employeeObj.jobTitle = binding.etJobTitle.text.toString()
                    employeeObj.salary = binding.etSalary.text.toString().toDouble()
                    employeeViewModel.updateEmployeeProfile(employeeObj)
                }
            }
        }
    }

    private fun observer() {
        employeeViewModel.updateEmployeeProfile.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnUpdate.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnUpdate.text = getString(com.kust.ermslibrary.R.string.update)
                    findNavController().navigate(com.kust.ermsmanager.R.id.action_updateEmployeeProfileFragment_to_employeeListingFragment)
                }
                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnUpdate.text = getString(com.kust.ermslibrary.R.string.update)
                }
            }
        }
    }

    private fun updateUi() {
        with(binding) {
            etDepartment.setText(employeeObj.department)
            etJobTitle.setText(employeeObj.jobTitle)
            etSalary.setText(employeeObj.salary.toString())
        }
    }

    private fun validation() : Boolean {
        val isValid = true

        if (binding.etDepartment.text.toString().isEmpty()) {
            binding.etDepartment.error = "Department is required"
            binding.etDepartment.requestFocus()
            return false
        }

        if (binding.etJobTitle.text.toString().isEmpty()) {
            binding.etJobTitle.error = "Job title is required"
            binding.etJobTitle.requestFocus()
            return false
        }

        if (binding.etSalary.text.toString().isEmpty()) {
            binding.etSalary.error = "Salary is required"
            binding.etSalary.requestFocus()
            return false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}