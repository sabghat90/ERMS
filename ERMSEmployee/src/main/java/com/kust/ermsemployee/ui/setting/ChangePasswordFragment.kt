package com.kust.ermsemployee.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentChangePasswordBinding
import com.kust.ermsemployee.ui.auth.AuthViewModel
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var _binding : FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding  = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnSubmit.setOnClickListener {
            if (validation()) {
                val newPassword = binding.newPasswordEditText.text.toString()
                authViewModel.changePassword(newPassword)
            }
        }
    }

    private fun observer() {
        authViewModel.changePassword.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmit.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.text = getString(R.string.submit)
                    toast(it.data)
                    findNavController().navigate(R.id.action_changePasswordFragment_to_settingFragment)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmit.text = getString(R.string.submit)
                    toast(it.error)
                }
            }
        }
    }

    private fun validation() : Boolean {
        var isValid = true

            val newPassword = binding.newPasswordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (newPassword.isEmpty()) {
                binding.newPasswordEditText.error = getString(R.string.required)
                isValid = false
            }

            else if (confirmPassword.isEmpty()) {
                binding.confirmPasswordEditText.error = getString(R.string.required)
                isValid = false
            }

            else if (newPassword != confirmPassword) {
                binding.confirmPasswordEditText.error = getString(R.string.password_not_match)
                isValid = false
            }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}