package com.kust.ermsemployee.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.databinding.FragmentProfileBinding
import com.kust.ermsemployee.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var employeeObj: EmployeeModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get employee object from user preferences and update UI

        observer()
        updateUi()
    }

    private fun observer() {
        authViewModel.getSession {
            if (it != null) {
                employeeObj = it
            }
        }
    }

    private fun updateUi() {
        binding.profileView.apply {
            name.text = employeeObj.name
            tvEmail.text = employeeObj.email
            tvPhone.text = employeeObj.phone
            tvWebsite.text = employeeObj.website
            tvCountry.text = employeeObj.country
            tvState.text = employeeObj.state
            tvFullAddress.text = employeeObj.address
            tvDepartment.text = employeeObj.department
            tvJobTitle.text = employeeObj.jobTitle
            tvSalary.text = employeeObj.salary.toString()
            tvJoiningDate.text = employeeObj.joiningDate
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}