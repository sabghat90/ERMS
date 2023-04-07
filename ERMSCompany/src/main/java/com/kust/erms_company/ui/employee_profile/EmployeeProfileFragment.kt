package com.kust.erms_company.ui.employee_profile

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.databinding.FragmentEmployeeProfileBinding
import com.kust.erms_company.ui.employee.EmployeeViewModel
import com.kust.erms_company.utils.Role
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EmployeeProfileFragment : Fragment() {

    val TAG = "EmployeeProfileFragment"

    private var _binding: FragmentEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var employeeObj : EmployeeModel

    private lateinit var viewModel: ProfileViewModel
    private lateinit var employeeViewModel : EmployeeViewModel

    private val progressDialog : ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        employeeViewModel = ViewModelProvider(this)[EmployeeViewModel::class.java]

        updateUi()
        observer()

        binding.btnSelectManager.setOnClickListener {
            employeeObj.role = Role.MANAGER

            employeeViewModel.updateEmployee(employeeObj)
        }
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    employeeObj = state.data[0]
                    isMakeEnableUI(false)
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(state.error)
                }
            }
        }

        employeeViewModel.updateEmployee.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    toast("Update successfully")
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(state.error)
                }
            }
        }
    }

    private fun updateUi() {
        employeeObj = arguments?.getParcelable("employee")!!
        binding.profileView.apply {
            companyName.text = employeeObj.name
            tvEmail.text = employeeObj.email
            tvPhone.text = employeeObj.phone
            tvCountry.text = employeeObj.country
            tvState.text = employeeObj.state
            tvFullAddress.text = employeeObj.address
            isMakeEnableUI(false)
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.profileView.apply {
            if (isDisable) {
                companyName.isEnabled = false
                tvEmail.isEnabled = false
                tvPhone.isEnabled = false
                tvCountry.isEnabled = false
                tvState.isEnabled = false
                tvFullAddress.isEnabled = false
            } else {
                companyName.isEnabled = true
                tvEmail.isEnabled = true
                tvPhone.isEnabled = true
                tvCountry.isEnabled = true
                tvState.isEnabled = true
                tvFullAddress.isEnabled = true
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}