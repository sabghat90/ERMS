package com.kust.ermsemployee.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.databinding.FragmentSignUpBinding
import com.kust.ermsemployee.ui.dashboard.DashboardActivity
import com.kust.ermsemployee.utils.Role
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding : FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                authViewModel.signUp(
                    email = binding.editTextEmail.text.toString().trim(),
                    password = binding.editTextPassword.text.toString(),
                    employeeModel = getEmployeeObj()
                )
            }
        }
    }

    private fun observer() {
        authViewModel.signUp.observe(viewLifecycleOwner) {uiState ->
            when(uiState) {
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(uiState.error)
                }
                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(uiState.data)
                    val intent = Intent (requireContext(), DashboardActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        }
    }

    private fun validation() : Boolean {
        var isValid = true

        with(binding) {
            if (editTextEmail.text.toString().isEmpty()) {
                editTextEmail.error = "Please Enter Email"
                editTextEmail.requestFocus()
                isValid = false
            }
            if (editTextPassword.text.toString().isEmpty()) {
                editTextPassword.error = "Please Enter Password"
                editTextPassword.requestFocus()
                isValid = false
            }
            if (editTextName.text.toString().isEmpty()) {
                editTextName.error = "Please Enter Name"
                editTextName.requestFocus()
                isValid = false
            }
            if (editTextPhone.text.toString().isEmpty()) {
                editTextPhone.error = "Please Enter Phone"
                editTextPhone.requestFocus()
                isValid = false
            }
            if (editTextGender.text.toString().isEmpty()) {
                editTextGender.error = "Please Enter Gender"
                editTextGender.requestFocus()
                isValid = false
            }
        }
        return isValid
    }

    private fun getEmployeeObj(): EmployeeModel {
        return EmployeeModel(
            id = "",
            name = binding.editTextName.text.toString(),
            email = binding.editTextEmail.text.toString().trim(),
            phone = binding.editTextPhone.text.toString(),
            address = "-",
            city = "-",
            country = "-",
            companyName = "-",
            companyId = "",
            designation = "-",
            salary = "0.00",
            points = "0",
            image = 0,
            role = Role.EMPLOYEE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}