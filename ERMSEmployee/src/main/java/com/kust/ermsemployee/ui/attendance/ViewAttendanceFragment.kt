package com.kust.ermsemployee.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.databinding.FragmentViewAttendaceBinding
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewAttendanceFragment : Fragment() {
    private var _binding: FragmentViewAttendaceBinding? = null
    private val binding get() = _binding!!

    private val attendanceViewModel: AttendanceViewModel by viewModels()

    private val adapter = AttendanceListingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentViewAttendaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()

        binding.rvAttendance.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAttendance.adapter = adapter
    }

    private fun observer() {
        attendanceViewModel.getAttendanceList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Success -> {
                    adapter.attendanceList = it.data as ArrayList<AttendanceModel>
                    adapter.submitList(it.data)
                }
                is UiState.Error -> {
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