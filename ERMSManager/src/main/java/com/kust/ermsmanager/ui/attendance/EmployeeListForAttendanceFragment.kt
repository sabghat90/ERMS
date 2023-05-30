package com.kust.ermsmanager.ui.attendance

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentEmployeeListForAttendanceBinding
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeListForAttendanceFragment : Fragment() {

    private var _binding: FragmentEmployeeListForAttendanceBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()

    private lateinit var progressDialog: Dialog

    private val adapter by lazy {
        AttendanceListingAdapter(
            onItemClicked = { _, employee ->
                findNavController().navigate(R.id.action_employeeListForAttendanceFragment_to_attendanceSheetFragment, Bundle().apply {
                    putParcelable("employeeObj", employee)
                })
            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentEmployeeListForAttendanceBinding.inflate(inflater, container, false)

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(R.layout.custom_progress_dialog)

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
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    adapter.employeeList = it.data as MutableList<Employee>
                    adapter.submitList(it.data)
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