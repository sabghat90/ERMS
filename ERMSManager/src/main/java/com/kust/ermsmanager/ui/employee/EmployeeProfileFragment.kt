package com.kust.ermsmanager.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentEmployeeProfileBinding
import javax.inject.Inject


class EmployeeProfileFragment : Fragment() {
    private var _binding: FragmentEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var employeeObj: Employee

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

        employeeObj = arguments?.getParcelable("employee")!!

        updateUi()

        binding.btnUpdateProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("employee", employeeObj)
            findNavController().navigate(R.id.action_employeeProfileFragment_to_updateEmployeeProfileFragment, bundle)
        }
    }

    private fun updateUi() {
        val date = ConvertDateAndTimeFormat().formatDate(employeeObj.joiningDate)
        binding.profileLayout.apply {
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
            tvJoiningDate.text = date

            Glide.with(requireContext())
                .load(employeeObj.profilePicture)
                .placeholder(com.kust.ermslibrary.R.drawable.avatar1)
                .into(imgLogo)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}