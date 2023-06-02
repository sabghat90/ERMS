package com.kust.ermsmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hideKeyboard
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentLoginBinding
import com.kust.ermsmanager.ui.setting.BiometricActivity
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {
    // create binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        if (authViewModel.isUserLoggedIn.value == true) {
            val intent = Intent(requireContext(), BiometricActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            if (validation()) {
                authViewModel.login(
                    email = binding.editTextEmail.text.toString().trim(),
                    password = binding.editTextPassword.text.toString()
                )
            }
        }

        binding.btnForgetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun observer() {
        authViewModel.login.observe(viewLifecycleOwner) {uiState ->
            when (uiState) {
                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    toast(uiState.error)
                }
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnLogin.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnLogin.text = getString(R.string.login)
                    toast(uiState.data)
                    val intent = Intent(requireContext(), BiometricActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.editTextEmail.text.toString().isEmpty()) {
            binding.editTextEmail.error = "Email is required"
            binding.editTextEmail.requestFocus()
            return false
        }
        if (binding.editTextPassword.text.toString().isEmpty()) {
            binding.editTextPassword.error = "Password is required"
            binding.editTextPassword.requestFocus()
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}