package com.kust.ermsemployee.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kust.ermsemployee.data.model.ComplaintModel
import com.kust.ermsemployee.data.model.EmployeeModel
import com.kust.ermsemployee.databinding.FragmentCreateComplaintBinding
import com.kust.ermsemployee.ui.auth.AuthViewModel
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import kotlinx.coroutines.launch

class CreateComplaintFragment : Fragment() {
    private var _binding: FragmentCreateComplaintBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val complaintViewModel: ComplaintViewModel by viewModels()
    private lateinit var employee: EmployeeModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateComplaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.getSession {
            employee = it!!
        }
        observer()

        binding.btnCreateComplaint.setOnClickListener {
            lifecycleScope.launch {
                complaintViewModel.createComplaint(getComplaintObj())
            }
        }
    }

    private fun observer() {
        complaintViewModel.createComplaint.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnCreateComplaint.text = "Create"
                    toast("Complaint created successfully")
                }

                is UiState.Error -> {
                    binding.btnCreateComplaint.text = "Create"
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun getComplaintObj(): ComplaintModel {
        return ComplaintModel(
            title = binding.etComplaintTitle.text.toString(),
            description = binding.etComplaintDescription.text.toString(),
            status = "Pending",
            companyId = employee.companyId,
            employeeId = employee.id,
            employeeName = employee.name,
            employeeFCMToken = employee.fcmToken
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}