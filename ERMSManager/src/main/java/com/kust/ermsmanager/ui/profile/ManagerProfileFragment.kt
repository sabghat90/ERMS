package com.kust.ermsmanager.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.FragmentManagerProfileBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    private var _binding : FragmentManagerProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    // create employee model object to store employee data from session
    private val employeeModel = EmployeeModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManagerProfileBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
    }

    private fun observer() {
        authViewModel.getSession {
            employeeModel.apply {
                id = it?.id.toString()
                name = it?.name.toString()
                email = it?.email.toString()
                phone = it?.phone.toString()
                address = it?.address.toString()
                role = it?.role.toString()
            }
            binding.profileLayout.name.text = employeeModel.name
            binding.profileLayout.tvEmail.text = employeeModel.email
            binding.profileLayout.tvPhone.text = employeeModel.phone
            binding.profileLayout.tvFullAddress.text = employeeModel.address
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}