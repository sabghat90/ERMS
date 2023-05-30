package com.kust.ermsemployee.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentSignUpBinding
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
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

        val spinnerData = resources.getStringArray(R.array.gender)
        val arrayAdapter : ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.dropdown_menu_item,
            spinnerData
        )
        binding.ddmGender.setAdapter(arrayAdapter)

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                authViewModel.signUp(
                    email = binding.editTextEmail.text.toString().trim(),
                    password = binding.editTextPassword.text.toString(),
                    employee = getEmployeeObj()
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
                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
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
            if (ddmGender.text.toString().isEmpty()) {
                ddmGender.error = "Please Enter Gender"
                ddmGender.requestFocus()
                isValid = false
            }
        }
        return isValid
    }

    private fun getEmployeeObj(): Employee {
        return Employee(
            name = binding.editTextName.text.toString(),
            employeeId = binding.editTextName.text.toString(),
            email = binding.editTextEmail.text.toString().trim(),
            phone = binding.editTextPhone.text.toString(),
            gender = binding.ddmGender.text.toString(),
            role = Role.EMPLOYEE,
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}