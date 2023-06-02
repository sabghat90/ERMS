package com.kust.erms_company.ui.employee_profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentEmployeeProfileBinding
import com.kust.erms_company.ui.employee.EmployeeViewModel
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeProfileFragment : Fragment() {

    private var _binding: FragmentEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var employeeObj: Employee

    private val employeeViewModel: EmployeeViewModel by viewModels()

    // notification service object
    private val notificationService = NotificationService()


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

        updateUi()
        observer()

        binding.btnSelectManager.setOnClickListener {
            // check if employee is already a manager
            if (employeeObj.role == Role.MANAGER) {
                toast("Employee is already a manager")
                return@setOnClickListener

            } else {

                val dialog = Dialog(requireContext())
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.custom_dialog_layout)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
                val btnYes = dialog.findViewById<View>(R.id.btn_yes)
                val btnNo = dialog.findViewById<View>(R.id.btn_cancel)
                dialog.show()

                btnYes.setOnClickListener {
                    dialog.dismiss()
                    employeeObj.role = Role.MANAGER
                    employeeViewModel.updateEmployee(employeeObj)
                }

                btnNo.setOnClickListener {
                    dialog.dismiss()
                }
            }

        }

        binding.btnRemoveEmployee.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            val btnYes = dialog.findViewById<View>(R.id.btn_yes)
            val btnNo = dialog.findViewById<View>(R.id.btn_cancel)
            dialog.show()

            btnYes.setOnClickListener {
                dialog.dismiss()
                employeeViewModel.removeEmployee(employeeObj)
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun observer() {

        employeeViewModel.updateEmployee.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar1.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar1.visibility = View.GONE
                    sendNotification()
                    toast("Update successfully")
                }

                is UiState.Error -> {
                    binding.progressBar1.visibility = View.GONE
                    toast(state.error)
                }
            }
        }

        employeeViewModel.removeEmployee.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.btnRemoveEmployee.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar2.visibility = View.GONE
                    binding.btnRemoveEmployee.text = getString(R.string.remove)
                    toast("Employee removed successfully")
                }

                is UiState.Error -> {
                    binding.progressBar2.visibility = View.GONE
                    toast(it.error)
                }
            }
        }
    }

    private fun sendNotification() {
        val title = "Manager Update"
        val body = let {
            if (employeeObj.gender == "Male") {
                "Mr ${employeeObj.name} You are now a manager"
            } else {
                "Ms ${employeeObj.name} You are now a manager"
            }
        }

        PushNotification(
            NotificationData(
                title = title,
                body = body,
            ),
            to = employeeObj.fcmToken
        ).also {
            notificationService.sendNotification(it)
        }
    }

    private fun updateUi() {
        employeeObj = arguments?.getParcelable("employee")!!
        binding.profileView.apply {
            name.text = employeeObj.name
            tvEmail.text = employeeObj.email
            tvPhone.text = employeeObj.phone
            tvCountry.text = employeeObj.country
            tvState.text = employeeObj.state
            tvFullAddress.text = employeeObj.address
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}