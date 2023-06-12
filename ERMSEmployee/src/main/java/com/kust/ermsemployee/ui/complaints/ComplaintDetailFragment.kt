package com.kust.ermsemployee.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.kust.ermsemployee.databinding.FragmentComplaintDetailBinding
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.models.ComplaintHistory
import com.kust.ermslibrary.utils.ComplaintStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.show
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComplaintDetailFragment : Fragment() {
    private var _binding: FragmentComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()
    private lateinit var complaint: Complaint
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
            complaintViewModel.getComplaintHistory(complaint.id)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = historyAdapter
    }

    private fun observer() {
        complaintViewModel.getComplaintHistory.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    toast("Loading history")
                }

                is UiState.Success -> {
                    historyAdapter.submitList(it.data)
                }

                is UiState.Error -> {
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
                binding.btnFeedback.show()
                binding.textInputLayout8.show()
                complaint.employeeFeedBack = binding.etFeedback.text.toString()
                binding.btnFeedback.setOnClickListener {
                    complaint.status = ComplaintStatus.CLOSED
                    lifecycleScope.launch {
                        complaintViewModel.updateComplaint(complaint, ComplaintHistory(
                            message = "From Employee\nFeedback: ${binding.etFeedback.text}",
                            date = Timestamp.now().toDate().toString(),
                        ))
                    }
                }
            }
            ComplaintStatus.CLOSED -> {
                binding.btnFeedback.hide()
                binding.textInputLayout8.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}