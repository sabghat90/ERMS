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
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.FragmentViewAttendaceBinding
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ViewAttendanceFragment : Fragment() {
    private var _binding: FragmentViewAttendaceBinding? = null
    private val binding get() = _binding!!

    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private var attendanceToday: ArrayList<Attendance> = ArrayList()
    private val adapter = AttendanceListingAdapter()
    private var attendanceList: ArrayList<Attendance> = ArrayList()
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

        attendanceViewModel.getAttendanceList()
        attendanceViewModel.getAttendanceForToday()

        binding.rvAttendance.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAttendance.adapter = adapter
    }

    private fun setForToday(attendance: Attendance? = null) {
        val day = SimpleDateFormat("dd").format(Date())
        if (attendanceList.isNotEmpty()) {
            with (binding) {
                tvStatus.text = attendance?.status ?: "Absent"
                tvSubmissionTime.text = attendance?.time ?: "N/A"
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
                    for (attendance in it.data as ArrayList<Attendance>) {
                        if (attendance.employeeId == FirebaseAuth.getInstance().currentUser?.uid) {
                            attendanceToday.add(attendance)
                            setForToday(attendanceToday[0])
                        }
                    }
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