package com.kust.ermsmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.FragmentManagerSignUpBinding
import com.kust.ermsmanager.ui.dashboard.DashboardActivity
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast

class ManagerSignUpFragment : Fragment() {

    private var _binding : FragmentManagerSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManagerSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString()
                val employee = getEmployeeObj()
                authViewModel.signUp(email, password, employee)
            }
        }
    }

    private fun observer() {
        authViewModel.signUp.observe(viewLifecycleOwner) {state ->
            when (state) {
                is UiState.Error -> {
                    binding.progressBar.show()
                    binding.btnRegister.text = getString(R.string.register)
                    toast(state.error)
                }
                UiState.Loading -> {
                    binding.btnRegister.text = ""
                    binding.progressBar.show()
                }
                is UiState.Success -> {
                    binding.btnRegister.text = getString(R.string.register)
                    binding.progressBar.hide()
                    toast(state.data)
                    val intent = Intent(requireContext(), DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun getEmployeeObj() : EmployeeModel {
        return EmployeeModel(
            id = "",
            name = binding.etName.text.toString(),
            employeeId = "-",
            email = binding.etEmail.text.toString(),
            phone = binding.etPhone.text.toString(),
            gender = "-",
            dob = "-",
            address = "-",
            city = "-",
            state = "-",
            country = "-",
            department = binding.etDepartment.text.toString(),
            companyId = "",
            jobTitle = binding.etDesignation.text.toString(),
            salary = "-",
            points = "-",
            role = "",
            profilePicture = ""
        )
    }

    private fun validation(): Boolean {
        var valid = true
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val name = binding.etName.text.toString()
        val phone = binding.etPhone.text.toString()
        val jobTile = binding.etDesignation.text.toString()
        val department = binding.etDesignation.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Please enter email"
            valid = false
            binding.etEmail.requestFocus()
        } else {
            binding.etEmail.error = null
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Please enter password"
            valid = false
            binding.etPassword.requestFocus()
        } else {
            binding.etPassword.error = null
        }

        if (name.isEmpty()) {
            binding.etName.error = "Please enter name"
            valid = false
            binding.etName.requestFocus()
        } else {
            binding.etEmail.error = null
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Please enter phone number"
            valid = false
            binding.etPhone.requestFocus()
        } else {
            binding.etPhone.error = null
        }

        if (jobTile.isEmpty()) {
            binding.etDesignation.error = "Please enter job"
            valid = false
            binding.etDesignation.requestFocus()
        } else {
            binding.etDesignation.error = null
        }

        if (department.isEmpty()) {
            binding.etDepartment.error = "Please enter department"
            valid = false
            binding.etDepartment.requestFocus()
        } else {
            binding.etDepartment.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}