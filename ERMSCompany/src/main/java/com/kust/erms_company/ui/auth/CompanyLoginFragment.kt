package com.kust.erms_company.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentCompanyLoginBinding
import com.kust.erms_company.ui.dashboard.DashBoardActivity
import com.kust.erms_company.ui.setting.BiometricActivity
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CompanyLoginFragment : Fragment() {

    private var _binding: FragmentCompanyLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyLoginBinding.inflate(inflater, container, false)

        if (viewModel.isUserLoggedIn.value == true) {
            val loadBiometricActivity = Intent(requireContext(), BiometricActivity::class.java)
            startActivity(loadBiometricActivity)
            requireActivity().finish()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnLogin.setOnClickListener {
            if (validation()) {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_companyLoginFragment_to_companyRegistrationFragment)
        }
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.btnLogin.text = getString(R.string.login)
                    binding.progressBar.visibility = View.GONE
                    toast(state.data)
                    val intent = Intent(requireContext(), DashBoardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.text = getString(R.string.login)
                    toast(state.error)
                }
                is UiState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun validation(): Boolean {
        var valid = true
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.isEmpty()) {
            binding.editTextEmail.error = "Please enter email"
            valid = false
            binding.editTextEmail.requestFocus()
        } else {
            binding.editTextEmail.error = null
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "Please enter password"
            valid = false
            binding.editTextPassword.requestFocus()
        } else {
            binding.editTextPassword.error = null
        }

        return valid
    }

    fun isValidPassword(password: String): Boolean {
        // Set password requirements
        val minLength = 8
        val maxLength = 20
        val requiresLowercase = true
        val requiresUppercase = true
        val requiresDigit = true
        val requiresSpecialChar = true

        // Check password length
        if (password.length < minLength || password.length > maxLength) {
            binding.editTextPassword.error = "Length should be between 8-20"
            return false
        }

        // Check for required character types
        var hasLowercase = false
        var hasUppercase = false
        var hasDigit = false
        var hasSpecialChar = false

        for (c in password) {
            if (c.isLowerCase()) {
                hasLowercase = true
            }
            if (c.isUpperCase()) {
                hasUppercase = true
            }
            if (c.isDigit()) {
                hasDigit = true
            }
            if (c in "!@#$%^&*()_+-=[]{}|\\;:'\"<>,.?/~`") {
                hasSpecialChar = true
            }
        }
        if (requiresLowercase && !hasLowercase) {
            return false
        }
        if (requiresUppercase && !hasUppercase) {
            return false
        }
        if (requiresDigit && !hasDigit) {
            return false
        }
        if (requiresSpecialChar && !hasSpecialChar) {
            return false
        }

        // If all checks pass, password is valid
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}