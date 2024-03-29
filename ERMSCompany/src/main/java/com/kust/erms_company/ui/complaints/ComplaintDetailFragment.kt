package com.kust.erms_company.ui.complaints

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
import com.kust.erms_company.databinding.FragmentComplaintDetailBinding
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.ComplaintStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ComplaintDetailFragment : Fragment() {
    private var _binding: FragmentComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()

    @Inject
    lateinit var complaint: Complaint
    private val historyAdapter by lazy { ComplaintHistoryAdapter() }

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
                is UiState.Success -> {
                    historyAdapter.submitList(it.data)
                }

                else -> {
                    toast("History not found")
                }
            }
        }
        complaintViewModel.updateComplaint.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnForward.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.hide()
                    binding.btnForward.text =
                        getString(com.kust.ermslibrary.R.string.forward_to_management)
                    toast("Complaint is referred to manager")
                    findNavController().navigate(com.kust.erms_company.R.id.action_complaintDetailFragment_to_complaintListingFragment)
                }

                is UiState.Error -> {
                    binding.progressBar.hide()
                    binding.btnForward.text =
                        getString(com.kust.ermslibrary.R.string.forward_to_management)
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
            ComplaintStatus.RESOLVED -> {
                binding.btnForward.text = getString(com.kust.ermslibrary.R.string.feedback)
                complaint.employeeFeedBack = binding.etFeedback.text.toString()
                binding.btnForward.setOnClickListener {
                    lifecycleScope.launch {
                        complaintViewModel.updateComplaint(
                            complaint, ComplaintHistory(
                                message = "From Company:\nComplaint is closed by company with\nFeedback: ${binding.etFeedback.text.toString()}",
                                date = Timestamp.now().toDate().toString()
                            )
                        )
                    }
                }
            }
            ComplaintStatus.PENDING -> {
                binding.btnForward.text = getString(com.kust.ermslibrary.R.string.forward_to_management)
                binding.etFeedback.hide()
                binding.btnForward.setOnClickListener {
                    lifecycleScope.launch {
                        complaint.isReferToManager = true
                        complaint.status = ComplaintStatus.IN_PROGRESS
                        complaintViewModel.updateComplaint(
                            complaint, ComplaintHistory(
                                message = "From Company:\nComplaint is referred to manager",
                                date = Timestamp.now().toDate().toString()
                            )
                        )
                    }
                }
            }
            ComplaintStatus.CLOSED -> {
                binding.btnForward.hide()
                binding.etFeedback.hide()
            }
            ComplaintStatus.IN_PROGRESS -> {
                binding.btnForward.hide()
                binding.etFeedback.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}