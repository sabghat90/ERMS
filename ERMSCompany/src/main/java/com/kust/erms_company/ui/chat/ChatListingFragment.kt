package com.kust.erms_company.ui.chat

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.databinding.FragmentChatListingBinding
import com.kust.erms_company.ui.employee.EmployeeListingAdapter
import com.kust.erms_company.ui.employee.EmployeeViewModel
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListingFragment : Fragment() {
    private var _binding: FragmentChatListingBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()

    private lateinit var progressDialog: Dialog

    private val adapter: EmployeeListingAdapter by lazy {
        EmployeeListingAdapter(
            onItemClicked = { _, employee ->
                findNavController().navigate(R.id.action_chatListingFragment_to_chatFragment, Bundle().apply {
                    putParcelable("employee", employee)
                })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(R.layout.custom_progress_dialog)

        binding.rvChatListing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChatListing.adapter = adapter

        observer()
        employeeViewModel.getEmployeeList()
    }

    private fun observer() {
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    adapter.employeeList = state.data as MutableList<Employee>
                    adapter.submitList(state.data)
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(state.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}