package com.kust.ermsemployee.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.kust.ermsemployee.databinding.FragmentCreateComplaintBinding
import com.kust.ermsemployee.ui.auth.AuthViewModel
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.ComplaintStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateComplaintFragment : Fragment() {
    private var _binding: FragmentCreateComplaintBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()
    private val complaintViewModel: ComplaintViewModel by viewModels()
    private lateinit var employee: Employee

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
                complaintViewModel.createComplaint(
                    getComplaintObj(), ComplaintHistory(
                        message = "Complaint created, forward to company for review",
                        date = Timestamp.now().toDate().toString()
                    )
                )
            }
        }
    }

    private fun observer() {
        complaintViewModel.createComplaint.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.btnCreateComplaint.text = ""
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

    private fun getComplaintObj(): Complaint {
        return Complaint(
            title = binding.etComplaintTitle.text.toString(),
            description = binding.etComplaintDescription.text.toString(),
            status = ComplaintStatus.PENDING,
            dateCreated = Timestamp.now().toDate().toString(),
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