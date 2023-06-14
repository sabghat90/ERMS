package com.kust.ermsmanager.ui.dashboard

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.ermslibrary.models.Feature
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.R as ManagerR
import com.kust.ermslibrary.R as LibraryR
import com.kust.ermsmanager.databinding.FragmentFeatureBinding
import com.kust.ermsmanager.ui.auth.AuthActivity
import com.kust.ermsmanager.ui.auth.AuthViewModel
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val authViewModel : AuthViewModel by viewModels()
    private val employeeViewModel : EmployeeViewModel by viewModels()
    private val adapter by lazy { FeaturesListingAdapter() }

    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(LibraryR.layout.custom_progress_dialog)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        observer()

        val features = mutableListOf<Feature>()
        features.add(Feature("View Employees", LibraryR.drawable.ic_employee))
        features.add(Feature("Manage Employee", LibraryR.drawable.ic_manage_emp))
        features.add(Feature("Mark Attendance", LibraryR.drawable.ic_attendance))
        features.add(Feature("Task", LibraryR.drawable.ic_task))
        features.add(Feature("Events", LibraryR.drawable.ic_events))
        features.add(Feature("Complaints", LibraryR.drawable.ic_report))
        features.add(Feature("Company Profile", LibraryR.drawable.ic_profile))

        adapter.features = features
        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvFeature.layoutManager = layout
        binding.rvFeature.adapter = adapter

        employeeViewModel.getEmployee()

        adapter.setOnItemClickListener(object : FeaturesListingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_employeeListingFragment)
                    }
                    1 -> Toast.makeText(requireContext(), "Manage Employee", Toast.LENGTH_SHORT).show()
                    2 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_employeeListForAttendanceFragment)
                    }
                    3 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_taskListingFragment)
                    }
                    4 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_eventListingFragment)
                    }
                    5 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_complaintListingFragment)
                    }
                    6 -> {
                        findNavController().navigate(ManagerR.id.action_featureFragment_to_companyProfileFragment)
                    }
                }
            }
        })
    }

    private fun observer() {
        employeeViewModel.getEmployee.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }

                is UiState.Success -> {
                    progressDialog.dismiss()
                    if (it.data.role != Role.MANAGER) {
                        toast("You are no longer a manager")
                        authViewModel.logout {
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }

                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}