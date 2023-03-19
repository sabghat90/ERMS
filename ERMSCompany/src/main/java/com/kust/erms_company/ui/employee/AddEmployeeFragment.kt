package com.kust.erms_company.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.erms_company.R
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.databinding.FragmentAddEmployeeBinding
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEmployeeFragment : Fragment() {

    private var _binding: FragmentAddEmployeeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmployeeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEmployeeBinding.inflate(inflater, container, false)

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                val email = binding.etEmail.text.toString()
                val employeeModel = getObject()
                viewModel.registerEmployee(email, employeeModel)
            }
        }

    }

    private fun observer() {
        viewModel.registerEmployee.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(it.data.toString())
                    findNavController().navigate(R.id.action_addEmployeeFragment_to_featuresFragment)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(it.error)
                }
            }
        }
    }

    private fun getObject(): EmployeeModel {
        return EmployeeModel(
            email = binding.etEmail.text.toString(),
            department = binding.etDepartment.text.toString(),
            salary = binding.etBasicPay.text.toString()
        )
    }

    private fun validation(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        } else if (binding.etBasicPay.text.toString().isEmpty()) {
            binding.etBasicPay.error = "Salary is required"
            binding.etBasicPay.requestFocus()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}