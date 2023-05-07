package com.kust.ermsemployee.ui.auth

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
import java.text.DateFormat
import java.util.Calendar

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

        binding.joiningDate.setOnClickListener {
            getJoiningDate()
        }

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
            if (ddmGender.text.toString().isEmpty()) {
                ddmGender.error = "Please Enter Gender"
                ddmGender.requestFocus()
                isValid = false
            }
        }
        return isValid
    }

    private fun getEmployeeObj(): EmployeeModel {
        return EmployeeModel(
            id = "",
            name = binding.editTextName.text.toString(),
            employeeId = binding.editTextName.text.toString(),
            email = binding.editTextEmail.text.toString().trim(),
            phone = binding.editTextPhone.text.toString(),
            gender = binding.ddmGender.text.toString(),
            dob = "",
            address = "",
            city = "",
            state = "",
            country = "",
            department = "",
            companyId = "",
            jobTitle = "",
            salary = 0.00,
            joiningDate = "",
            points = "0",
            role = Role.EMPLOYEE,
            profilePicture = ""
        )
    }

    // get joining date from date picker, date picker should be onward from today date
    private fun getJoiningDate() {
        // hide keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)


        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        binding.joiningDate.hint = DateFormat.getDateTimeInstance().format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}