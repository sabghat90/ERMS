package com.kust.ermsmanager.ui.attendance

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.FragmentEmployeeListForAttendanceBinding
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeListForAttendanceFragment : Fragment() {

    private var _binding: FragmentEmployeeListForAttendanceBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()
    private val attendanceViewModel: AttendanceViewModel by viewModels()

    private val adapter by lazy {
        AttendanceListingAdapter(
            onItemClicked = { _, employee ->
                findNavController().navigate(R.id.action_employeeListForAttendanceFragment_to_attendanceSheetFragment, Bundle().apply {
                    putParcelable("employeeObj", employee)
                })
            }
        )
    }

    private val dialog: ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentEmployeeListForAttendanceBinding.inflate(inflater, container, false)

        dialog.setMessage("Loading...")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.rvEmpList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpList.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    dialog.show()
                }
                is UiState.Success -> {
                    dialog.dismiss()
                    adapter.employeeList = it.data as MutableList<EmployeeModel>
                    adapter.submitList(it.data)
                }
                is UiState.Error -> {
                    dialog.dismiss()
                    toast(it.error)
                }
            }
        }

        attendanceViewModel.getAttendance.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    dialog.show()
                }
                is UiState.Success -> {
                    dialog.dismiss()
                }
                is UiState.Error -> {
                    dialog.dismiss()
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