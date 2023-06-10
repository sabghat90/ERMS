package com.kust.erms_company.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.erms_company.R as CompanyR
import com.kust.ermslibrary.R as LibraryR
import com.kust.erms_company.databinding.FragmentCompanyRegistrationBinding
import com.kust.ermslibrary.models.Company
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CompanyRegistrationFragment : Fragment() {

    private var _binding: FragmentCompanyRegistrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCompanyRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                val email = binding.editTextEmail.text.toString()
                val password = binding.editTextPassword.text.toString()
                val company = getCompanyObj()
                viewModel.register(email, password, company)
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    binding.btnRegister.text = getString(LibraryR.string.register)
                    binding.progressBar.visibility = View.GONE
                    toast(state.data)
                    findNavController().navigate(CompanyR.id.action_companyRegistrationFragment_to_companyLoginFragment)

                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(LibraryR.string.register)
                    toast(state.error)
                }
                is UiState.Loading -> {
                    binding.btnRegister.text = ""
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getCompanyObj(): Company {
        return Company(
            id = "",
            name = binding.editTextCompanyName.text.toString(),
            address = "",
            city = "",
            country = "",
            email = binding.editTextEmail.text.toString(),
            phone = binding.editTextPhone.text.toString(),
            website = "",
            role = Role.COMPANY,
            profilePicture = ""
        )
    }

    private fun validation(): Boolean {
        var valid = true
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.isEmpty()) {
            binding.editTextEmail.error = "Please enter email"
            valid = false
        } else {
            binding.editTextEmail.error = null
        }

        if (password.isEmpty()) {
            binding.editTextPassword.error = "Please enter password"
            valid = false
        } else {
            binding.editTextPassword.error = null
        }
        return valid
    }
}