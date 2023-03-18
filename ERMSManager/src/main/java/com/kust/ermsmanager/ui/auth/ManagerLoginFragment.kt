package com.kust.ermsmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsmanager.databinding.FragmentManagerLoginBinding
import com.kust.ermsmanager.ui.dashboard.DashboardActivity
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast


class ManagerLoginFragment : Fragment() {

    private val TAG = "LoginActivity"
    private var _binding: FragmentManagerLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentManagerLoginBinding.inflate(inflater, container, false)

        if (viewModel.isUserLoggedIn.value == true) {
            val intent = Intent(requireContext(), DashboardActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnLogin.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            }
        }
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.progressBar.show()
                }
                is UiState.Error -> {
                    binding.btnLogin.text = "Login"
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnLogin.text = "Login"
                    binding.progressBar.hide()
                    toast(state.data)
                    val intent = Intent(requireContext(), DashboardActivity::class.java)
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
}