package com.kust.ermsmanager.ui.attendance

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermslibrary.models.Attendance
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.AttendancePoints
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hideKeyboard
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.databinding.FragmentAttendanceSheetBinding
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates
import com.kust.ermslibrary.R as ERMSLibraryR
import com.kust.ermsmanager.R as ManagerR

@AndroidEntryPoint
class AttendanceSheetFragment : Fragment() {

    private var _binding: FragmentAttendanceSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var employeeObj: Employee
    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private val employeeViewModel: EmployeeViewModel by viewModels()

    // notification service instance
    private val notificationService = NotificationService()

    private lateinit var date : String
    private var points by Delegates.notNull<Double>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAttendanceSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        observer()

        binding.btnSubmitAttendance.setOnClickListener {
            hideKeyboard()
            attendanceViewModel.markAttendance(getAttendanceObj())
            employeeViewModel.addPoints(employeeObj.id, points)
        }

        binding.datePickerLayout.setOnClickListener {
            hideKeyboard()
            selectDateFromDatePicker()
        }
    }

    private fun selectDateFromDatePicker() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a date picker dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Handle the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                binding.tvDayName.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(selectedDate.time)
                binding.tvDate.text = SimpleDateFormat("MMM d, y", Locale.getDefault()).format(selectedDate.time)
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set the maximum date to today
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // Show the date picker dialog
        datePickerDialog.show()

    }

    private fun observer() {
        attendanceViewModel.markAttendance.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSubmitAttendance.text = ""
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    toast(state.data)
                    binding.btnSubmitAttendance.text = getString(ERMSLibraryR.string.submit_attendance)
                    sendNotification()
                    findNavController().navigate(ManagerR.id.action_attendanceSheetFragment_to_employeeListForAttendanceFragment)
                }

                is UiState.Error -> {
                    toast(state.error)
                    binding.progressBar.visibility = View.GONE
                    binding.btnSubmitAttendance.text = getString(ERMSLibraryR.string.submit_attendance)
                }
            }
        }
    }

    private fun updateUI() {

        employeeObj = arguments?.getParcelable("employeeObj")!!
        val todayDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
        date = todayDate

        with(binding) {
            tvEmployeeName.text = employeeObj.name
            tvDepartment.text = employeeObj.department
            tvEmployeeId.text = employeeObj.employeeId
            tvDayName.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
            tvDate.text = SimpleDateFormat("MMM d, y", Locale.getDefault()).format(Date())
        }
    }

    private fun getAttendanceObj(): Attendance {
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val extraBonus = {
            if (binding.etExtraBonus.text.toString().isEmpty()) {
                0.0
            } else {
                binding.etExtraBonus.text.toString().toDouble()
            }
        }

        val advanceOrLoan = {
            if (binding.etAdvanceLoan.text.toString().isEmpty()) {
                0.0
            } else {
                binding.etAdvanceLoan.text.toString().toDouble()
            }
        }

        return Attendance(
            employeeId = employeeObj.id,
            employeeName = employeeObj.name,
            date = date,
            status = attendanceStatus(),
            time = time,
            extraBonus = extraBonus(),
            advanceOrLoan = advanceOrLoan()
        )
    }

    private fun attendanceStatus(): String {
        with(binding) {
            when {
                rbtnPresent.isChecked -> {
                    points = AttendancePoints.PRESENT
                    return Attendance.STATUS_PRESENT
                }

                rbtnAbsent.isChecked -> {
                    points = AttendancePoints.ABSENT
                    return Attendance.STATUS_ABSENT
                }

                rbtnLeave.isChecked -> {
                    points = AttendancePoints.LEAVE
                    return Attendance.STATUS_LEAVE
                }

                rbtnHoliday.isChecked -> {
                    points = AttendancePoints.HOLIDAY
                    return Attendance.STATUS_HOLIDAY
                }

                rbtnHalfDay.isChecked -> {
                    points = AttendancePoints.HALF_DAY
                    return Attendance.STATUS_HALF_DAY
                }

                rbtnOvertime.isChecked -> {
                    points = AttendancePoints.OVERTIME
                    return Attendance.STATUS_OVERTIME
                }

                else -> {
                    return ""
                }
            }
        }
    }

    private fun sendNotification() {
        PushNotification(
            NotificationData(
                title = "Attendance Marked",
                body = "Your attendance has been marked for ${binding.tvDate.text}"
            ),
            to = employeeObj.fcmToken
        ).also {
            notificationService.sendNotification(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}