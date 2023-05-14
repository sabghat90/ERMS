package com.kust.ermsmanager.ui.attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.databinding.FragmentAttendanceSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AttendanceSheetFragment : Fragment() {

    private var _binding: FragmentAttendanceSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var employeeObj : EmployeeModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAttendanceSheetBinding.inflate(inflater, container, false)

        updateUI()

        return binding.root
    }

    private fun updateUI() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayName = DateFormatSymbols().shortWeekdays[dayOfWeek]

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val date = dateFormat.format(calendar.time)



        employeeObj = arguments?.getParcelable("employeeObj")!!

        with(binding) {
            tvEmployeeName.text = employeeObj.name
            tvDepartment.text = employeeObj.department
            tvEmployeeId.text = employeeObj.employeeId
            tvDayName.text = dayName
            tvDate.text = date
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}