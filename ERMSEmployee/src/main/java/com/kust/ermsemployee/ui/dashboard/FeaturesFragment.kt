package com.kust.ermsemployee.ui.dashboard

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kust.ermsemployee.R as EmployeeR
import com.kust.ermslibrary.R as LibraryR
import com.kust.ermsemployee.databinding.FragmentFeatureBinding
import com.kust.ermsemployee.ui.auth.AuthActivity
import com.kust.ermsemployee.ui.auth.AuthViewModel
import com.kust.ermsemployee.ui.ranking.EmployeeViewModel
import com.kust.ermslibrary.models.Feature
import com.kust.ermslibrary.utils.Role
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class FeaturesFragment : Fragment() {

    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val employeeViewModel: EmployeeViewModel by viewModels()

    private val adapter by lazy { FeatureListingAdapter() }

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
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(LibraryR.layout.custom_progress_dialog)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        observer()

        binding.rvFeatures.visibility = View.VISIBLE

        val features = mutableListOf<Feature>()

        features.add(Feature("View Employees", LibraryR.drawable.avatar2))
        features.add(Feature("Employee Ranking", LibraryR.drawable.avatar2))
        features.add(Feature("View Attendance", LibraryR.drawable.avatar2))
        features.add(Feature("Task", LibraryR.drawable.avatar2))
        features.add(Feature("Events", LibraryR.drawable.avatar2))
        features.add(Feature("Complaints", LibraryR.drawable.avatar2))
        features.add(Feature("Company Profile", LibraryR.drawable.avatar2))
        features.add(Feature("Logout", LibraryR.drawable.avatar2))

        adapter.features = features

        val layout = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.rvFeatures.layoutManager = layout

        binding.rvFeatures.adapter = adapter

        lifecycleScope.launch {
            employeeViewModel.getEmployee()
        }

        adapter.setOnItemClickListener(object : FeatureListingAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_employeeListingFragment)
                    }
                    1 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_employeeRankListingFragment)
                    }
                    2 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_viewAttendanceFragment)
                    }
                    3 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_taskListingFragment)
                    }
                    4 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_eventListingFragment)
                    }
                    5 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_complaintListingFragment)
                    }
                    6 -> {
                        findNavController().navigate(EmployeeR.id.action_featureFragment_to_companyProfileFragment)
                    }
                    7 -> {
                        Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                        // Logout
                        authViewModel.logout {
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
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
                    if (it.data.role != Role.EMPLOYEE) {
                        toast("You are no longer an employee")
                        authViewModel.logout {
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
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