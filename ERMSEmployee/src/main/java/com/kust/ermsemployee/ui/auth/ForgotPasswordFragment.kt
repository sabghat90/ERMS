package com.kust.ermsemployee.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermsemployee.R as EmployeeR
import com.kust.ermslibrary.R as LibraryR
import com.kust.ermsemployee.databinding.FragmentForgotPasswordBinding
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnContinue.setOnClickListener {
            if (validation()) {
                val email = binding.etEmail.text?.trim().toString()
                authViewModel.forgotPassword(email)
            }
        }
        binding.btnLoginAgain.setOnClickListener {
            findNavController().navigate(EmployeeR.id.action_forgotPasswordFragment_to_loginFragment)
        }
    }

    private fun observer() {
        authViewModel.forgotPassword.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnContinue.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnContinue.text = getString(LibraryR.string.continue_)
                    toast(it.data)
                    findNavController().navigate(EmployeeR.id.action_forgotPasswordFragment_to_loginFragment)
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnContinue.text = getString(LibraryR.string.continue_)
                    toast(it.error)
                }
            }
        }
    }

    private fun validation(): Boolean {
        var valid = true
        val email = binding.etEmail.text.toString()
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            valid = false
        }
        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}