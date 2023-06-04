package com.kust.ermsemployee.ui.complaints

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentComplaintListingBinding
import com.kust.ermslibrary.models.Complaint
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComplaintListingFragment : Fragment() {

    private var _binding: FragmentComplaintListingBinding? = null
    private val binding get() = _binding!!

    private val complaintViewModel: ComplaintViewModel by viewModels()
    private lateinit var progressDialog: Dialog

    private val adapter by lazy {
        ComplaintListingAdapter(requireContext()) { _, complaint ->
            findNavController().navigate(R.id.action_complaintListingFragment_to_complaintDetailFragment, Bundle().apply {
                putParcelable("complaint", complaint)
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentComplaintListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(R.layout.custom_progress_dialog)

        observer()
        lifecycleScope.launch {
            complaintViewModel.getComplaints()
        }

        binding.rvComplaints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComplaints.adapter = adapter

        binding.createComplaintFab.setOnClickListener {
            findNavController().navigate(R.id.action_complaintListingFragment_to_createComplaintFragment)
        }
    }

    private fun observer() {
        complaintViewModel.getComplaints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
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