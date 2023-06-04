package com.kust.erms_company.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentAddEmployeeBinding
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEmployeeFragment : Fragment() {

    private var _binding: FragmentAddEmployeeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmployeeViewModel by viewModels()

    private val notificationService = NotificationService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEmployeeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.btnRegister.setOnClickListener {
            if (validation()) {
                val email = binding.etEmail.text.toString()
                val employeeModel = getObject()
                viewModel.registerEmployee(email, employeeModel)
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
                    sendNotification(it.data)
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    toast(it.data.toString())
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

    private fun sendNotification(data: Pair<Employee, String>) {
        val title = "New Employee"
        val message = "New employee ${data.first.email} has been registered"
        PushNotification(
            NotificationData(
                title,
                message
            ),
            to = data.first.fcmToken
        ).also {
            notificationService.sendNotification(it)
        }
    }

    private fun getObject(): Employee {
        val joiningDate = Timestamp.now().toDate().toString()
        return Employee(
            email = binding.etEmail.text.toString(),
            joiningDate = joiningDate
        )
    }


    private fun validation(): Boolean {
        if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}