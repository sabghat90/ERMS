package com.kust.erms_company.ui.employee

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kust.erms_company.R
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.databinding.FragmentAddEmployeeBinding
import com.kust.erms_company.utils.Role
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEmployeeFragment : Fragment() {

    private var _binding : FragmentAddEmployeeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth : FirebaseAuth

    private lateinit var viewModel: EmployeeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEmployeeBinding.inflate(inflater, container, false)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.toolbar.title = "Add Employee"

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[EmployeeViewModel::class.java]

        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()){
                val email = binding.etEmail.text.toString()
                val password = "123456"
                val employeeModel = getObject()
                viewModel.registerEmployee(email, password, employeeModel)
            }
        }

    }

    private fun observer() {
        viewModel.registerEmployee.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.text = ""
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    findNavController().navigate(R.id.action_addEmployeeFragment_to_featuresFragment)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(it.error)
                }
            }
        }
    }

    private fun getObject() : EmployeeModel {
        return EmployeeModel(
            id = "",
            name = binding.etName.text.toString(),
            employeeId = binding.etEmployeeId.text.toString(),
            email = binding.etEmail.text.toString(),
            phone = binding.etPhone.text.toString(),
            gender = "-",
            dob = "-",
            address = "-",
            city = "-",
            state = "-",
            country = "-",
            department = binding.etDepartment.text.toString(),
            companyId = auth.currentUser?.uid.toString(),
            jobTitle = binding.etDesignation.text.toString(),
            salary = binding.etBasicPay.text.toString(),
            points = "0",
            role = Role.EMPLOYEE,
            profilePicture = ""
        )
    }

    private fun validation() : Boolean {
        if (binding.etName.text.toString().isEmpty()) {
            binding.etName.error = "Name is required"
            binding.etName.requestFocus()
            return false
        }
        else if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        }
        else if (binding.etPhone.text.toString().isEmpty()) {
            binding.etPhone.error = "Phone is required"
            binding.etPhone.requestFocus()
            return false
        }
        else if (binding.etDesignation.text.toString().isEmpty()) {
            binding.etDesignation.error = "Position is required"
            binding.etDesignation.requestFocus()
            return false
        }
        else if (binding.etBasicPay.text.toString().isEmpty()) {
            binding.etBasicPay.error = "Salary is required"
            binding.etBasicPay.requestFocus()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}