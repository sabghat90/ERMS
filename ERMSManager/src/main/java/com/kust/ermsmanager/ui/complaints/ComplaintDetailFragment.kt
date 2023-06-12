package com.kust.ermsmanager.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.utils.ComplaintStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.databinding.FragmentComplaintDetailBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ComplaintDetailFragment : Fragment() {
    private var _binding: FragmentComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var complaint: Complaint
    private val historyAdapter by lazy { ComplaintHistoryAdapter() }
    @Inject
    lateinit var managerObj: Employee

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentComplaintDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        complaint = arguments?.getParcelable("complaint")!!
        authViewModel.getSession {
            if (it != null) {
                managerObj = it
            }
        }
        updateUi()
        observer()

        lifecycleScope.launch {
            toast(complaint.id)
            complaintViewModel.getComplaintHistory(complaint.id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = historyAdapter
    }

    private fun observer() {
        complaintViewModel.getComplaintHistory.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    toast("Loading history...")
                }

                is UiState.Success -> {
                    historyAdapter.submitList(it.data)
                }

                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }

        complaintViewModel.updateComplaint.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnResolve.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnResolve.text = getString(com.kust.ermslibrary.R.string.resolve)
                    toast("Complaint updated")
                    findNavController().navigate(com.kust.ermsmanager.R.id.action_complaintDetailFragment_to_complaintListingFragment)
                }

                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnResolve.text = getString(com.kust.ermslibrary.R.string.resolve)
                    toast(it.error)
                }
            }
        }
    }

    private fun updateUi() {
        // get from bundle
        with(binding) {
            tvComplaintTitle.text = complaint.title
            tvComplaintDescription.text = complaint.description
            tvDateCreated.text = complaint.dateCreated
        }

        when (complaint.status) {
            ComplaintStatus.IN_PROGRESS -> {
                binding.btnResolve.setOnClickListener {
                    complaint.status = ComplaintStatus.RESOLVED
                    complaint.dateUpdated = Timestamp.now().toDate().toString()
                    lifecycleScope.launch {
                        complaintViewModel.updateComplaint(complaint, updateHistory())
                    }
                }
            }
            ComplaintStatus.CLOSED -> {
                binding.btnResolve.hide()
                binding.etRemarks.hide()
            }
            else -> {
                binding.btnResolve.hide()
                binding.etRemarks.hide()
            }
        }

    }

    private fun updateHistory(): ComplaintHistory {
        return ComplaintHistory(
            message = "From Manager:\nComplaint resolved by ${managerObj.name} and referred to company\nRemarks: ${binding.etRemarks.text}",
            date = Timestamp.now().toString(),
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}