package com.kust.ermsemployee.ui.attendance

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.ermsemployee.R
import com.kust.ermsemployee.data.model.AttendanceModel
import com.kust.ermsemployee.databinding.FragmentViewAttendaceBinding
import com.kust.ermsemployee.utils.UiState
import com.kust.ermsemployee.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ViewAttendanceFragment : Fragment() {
    private var _binding: FragmentViewAttendaceBinding? = null
    private val binding get() = _binding!!

    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private val adapter = AttendanceListingAdapter()
    private var attendanceList: ArrayList<AttendanceModel> = ArrayList()
    private lateinit var progressDialog: Dialog

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

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setContentView(R.layout.custom_progress_dialog)

        observer()
        setForToday()

        binding.rvAttendance.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAttendance.adapter = adapter

    }

    private fun setForToday() {
        val day = SimpleDateFormat("dd").format(Date())
        if (attendanceList.isNotEmpty()) {
            if (attendanceList[0].day == day) {
                binding.tvStatus.text = attendanceList[0].status
                binding.tvSubmissionTime.text = attendanceList[0].time
            }
        } else {
            toast("No attendance found for today")
        }
    }

    private fun observer() {
        attendanceViewModel.getAttendanceList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    toast("Success ${it.data}")
                    adapter.attendanceList = it.data as ArrayList<AttendanceModel>
                    adapter.submitList(it.data)
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(it.error)
                }
            }
        }
        attendanceViewModel.getAttendanceForToday.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    progressDialog.dismiss()
                    attendanceList = it.data as ArrayList<AttendanceModel>
                    setForToday()
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