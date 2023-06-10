package com.kust.ermsemployee.ui.ranking

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.databinding.FragmentEmployeeRankListingBinding
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import com.kust.ermslibrary.R as LibraryR

@AndroidEntryPoint
class EmployeeRankListingFragment : Fragment() {
    private var _binding: FragmentEmployeeRankListingBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel : EmployeeViewModel by viewModels()
    private lateinit var progressDialog: Dialog

    private val adapter by lazy {
        EmployeeRankingAdapter { _, _ -> }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeRankListingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(LibraryR.layout.custom_progress_dialog)

        observer()

        employeeViewModel.getEmployeeRank()

        binding.rvEmployeeRankListing.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployeeRankListing.adapter = adapter
    }

    private fun observer() {
        employeeViewModel.getEmployeeRank.observe(viewLifecycleOwner) {
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