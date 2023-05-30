package com.kust.ermsmanager.ui.employee

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.FragmentEmployeeListingBinding
import com.kust.ermsmanager.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeListingFragment : Fragment() {

    private var _binding: FragmentEmployeeListingBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()

    private val adapter by lazy {
        EmployeeListingAdapter(
            onItemClicked = { _, employee ->
                toast("Clicked on ${employee.name}")
            }
        )
    }

    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeListingBinding.inflate(inflater, container, false)

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

        binding.rvEmpolyee.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmpolyee.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    adapter.employeeList = it.data as MutableList<EmployeeModel>
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