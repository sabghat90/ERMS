package com.kust.erms_company.ui.employee

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.kust.erms_company.R as CompanyR
import com.kust.ermslibrary.R as LibraryR
import com.kust.erms_company.databinding.FragmentEmployeeProfileBinding
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeProfileFragment : Fragment() {

    private var _binding: FragmentEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var employeeObj: Employee
    private val employeeViewModel: EmployeeViewModel by viewModels()

    @Inject
    lateinit var notificationService: NotificationService


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
            if (validation()) {
                // check if employee is already a manager
                if (employeeObj.role == Role.MANAGER) {
                    val dialog = Dialog(requireContext())
                    dialog.setCancelable(false)
                    dialog.setContentView(CompanyR.layout.custom_dialog_layout)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.window!!.attributes.windowAnimations = LibraryR.style.DialogAnimation
                    val btnYes = dialog.findViewById<View>(LibraryR.id.btn_yes)
                    val btnNo = dialog.findViewById<View>(LibraryR.id.btn_cancel)
                    dialog.show()

                    btnYes.setOnClickListener {
                        dialog.dismiss()
                        employeeObj.role = Role.EMPLOYEE
                        employeeViewModel.updateEmployee(employeeObj)
                    }

                    btnNo.setOnClickListener {
                        dialog.dismiss()
                    }
                } else {

                    val dialog = Dialog(requireContext())
                    dialog.setCancelable(false)
                    dialog.setContentView(CompanyR.layout.custom_dialog_layout)
                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.window!!.attributes.windowAnimations = LibraryR.style.DialogAnimation
                    val btnYes = dialog.findViewById<View>(LibraryR.id.btn_yes)
                    val btnNo = dialog.findViewById<View>(LibraryR.id.btn_cancel)
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
            } else {
                return@setOnClickListener
            }
        }

        binding.btnRemoveEmployee.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setCancelable(false)
            dialog.setContentView(CompanyR.layout.custom_dialog_layout)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.attributes.windowAnimations = LibraryR.style.DialogAnimation
            val btnYes = dialog.findViewById<View>(LibraryR.id.btn_yes)
            val btnNo = dialog.findViewById<View>(LibraryR.id.btn_cancel)
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
                    binding.btnSelectManager.text = ""
                }

                is UiState.Success -> {
                    binding.btnSelectManager.text = getString(LibraryR.string.select_manager)
                    binding.progressBar1.visibility = View.GONE
                    sendNotification()
                    toast("Update successfully")
                }

                is UiState.Error -> {
                    binding.btnSelectManager.text = getString(LibraryR.string.select_manager)
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
                    binding.btnRemoveEmployee.text = getString(LibraryR.string.remove)
                    toast("Employee removed successfully")
                }

                is UiState.Error -> {
                    binding.btnRemoveEmployee.text = getString(LibraryR.string.remove)
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
        val date = employeeObj.joiningDate
        val formattedDate = ConvertDateAndTimeFormat().formatDate(date)
        binding.profileView.apply {
            name.text = employeeObj.name
            tvEmail.text = employeeObj.email
            tvPhone.text = employeeObj.phone
            tvCountry.text = employeeObj.country
            tvState.text = employeeObj.state
            tvFullAddress.text = employeeObj.address
            tvDepartment.text = employeeObj.department
            tvJobTitle.text = employeeObj.jobTitle
            tvSalary.text = employeeObj.salary.toString()
            tvJoiningDate.text = formattedDate
        }

        Glide.with(requireContext())
            .load(employeeObj.profilePicture)
            .placeholder(LibraryR.drawable.avatar1)
            .into(binding.profileView.imgLogo)

        if (employeeObj.role == Role.MANAGER) {
            binding.btnSelectManager.text = getString(LibraryR.string.unselect)
        } else {
            binding.btnSelectManager.text = getString(LibraryR.string.select_manager)
        }
    }

    private fun validation() : Boolean {
        val isValid = true

        if (employeeObj.department.isEmpty() && employeeObj.jobTitle.isEmpty() && employeeObj.salary == 0.00) {
            toast("Please update department, job title and salary of the employee")
            return false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}