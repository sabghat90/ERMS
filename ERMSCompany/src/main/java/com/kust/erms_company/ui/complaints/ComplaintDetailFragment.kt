package com.kust.erms_company.ui.complaints

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.data.model.ComplaintHistoryModel
import com.kust.erms_company.data.model.ComplaintModel
import com.kust.erms_company.databinding.FragmentComplaintDetailBinding
import com.kust.erms_company.utils.ComplaintStatus
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComplaintDetailFragment : Fragment() {
    private var _binding: FragmentComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()
    private lateinit var complaint : ComplaintModel

    private val historyAdapter by lazy { ComplaintHistoryAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentComplaintDetailBinding.inflate(layoutInflater,container,false)
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
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    toast(it.data.toString())
                    historyAdapter.historyList = it.data as ArrayList<ComplaintHistoryModel>
                    historyAdapter.submitList(it.data)
                }

                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    private fun updateComplaintHistory(): ComplaintHistoryModel {
        val message = "Complaint is referred to manager"
        return ComplaintHistoryModel(
            message = message
        )
    }

    private fun updateUi() {
        // get from bundle
        with (binding) {
            tvComplaintTitle.text = complaint.title
            tvComplaintDescription.text = complaint.description
            tvDateCreated.text = complaint.dateCreated
        }

        if (!complaint.isReferToManager) {
            lifecycleScope.launch {
                complaint.isReferToManager = true
                complaint.status = ComplaintStatus.IN_PROGRESS
                complaintViewModel.updateComplaint(complaint, updateComplaintHistory())
            }
        } else {
            binding.btnForward.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}