package com.kust.ermsmanager.ui.event

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.data.models.NotificationModel
import com.kust.ermsmanager.data.models.PushNotification
import com.kust.ermsmanager.databinding.FragmentCreateEventBinding
import com.kust.ermsmanager.services.NotificationService
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CreateEventFragment : Fragment() {

    private var _binding: FragmentCreateEventBinding? = null
    private val binding get() = _binding!!

    private val employeeViewModel: EmployeeViewModel by viewModels()
    private val eventViewModel: EventViewModel by viewModels()

    // create employee list to store all employees from database
    private var employeeList = listOf<EmployeeModel>()

    private val notificationService = NotificationService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnCreateEvent.setOnClickListener {
            if (validation()) {
                eventViewModel.createEvent(getEventObj())
            }
        }

        binding.btnDate.setOnClickListener {
            selectDateFromDatePicker()
        }

        binding.btnTime.setOnClickListener {
            selectTimeFromTimePicker()
        }

    }

    private fun selectTimeFromTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                binding.btnTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedTime.time)
            },
            12,
            0,
            false
        )
        timePickerDialog.show()
    }

    private fun selectDateFromDatePicker() {
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
                binding.btnDate.text = SimpleDateFormat("MMM d, y", Locale.getDefault()).format(selectedDate.time)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set the maximum date to today
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun observer() {
        eventViewModel.createEvent.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnCreateEvent.text = ""
                }
                is UiState.Success -> {
                    binding.btnCreateEvent.text = getString(R.string.create_an_event)
                    binding.progressBar.visibility = View.GONE
                    toast("Event created successfully")
                    sendNotification()
                }
                is UiState.Error -> {
                    binding.btnCreateEvent.text = getString(R.string.create_an_event)
                    binding.progressBar.visibility = View.GONE
                    toast(it.error)
                }
            }
        }
        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    employeeList = it.data
                }
                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    private fun sendNotification() {
        // get all employee tokens from employee list and store in a list of string
        val tokens = mutableListOf<String>()
        employeeList.forEach { employee ->
            tokens.add(employee.fcmToken)
        }
        // send notification to all employees
        tokens.forEach { token ->
            PushNotification(
                NotificationModel(
                    title = "New Event",
                    body = "A new event has been created"
                ),
                to = token
            ).also { notification ->
                notificationService.sendNotification(notification)
            }
        }
    }

    private fun getEventObj(): EventModel {
        val title = binding.etEventTitle.text.toString()
        val description = binding.etEventDescription.text.toString()
        val dateCreated = SimpleDateFormat("MMM d, y", Locale.getDefault()).format(Date())
        val timeCreated = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val date = binding.btnDate.text.toString()
        val time = binding.btnTime.text.toString()
        val location = binding.etEventLocation.text.toString()
        val type = binding.etEventType.text.toString()
        return EventModel(
            id = "",
            title = title,
            description = description,
            dateCreated = dateCreated,
            timeCreated = timeCreated,
            date = date,
            time = time,
            location = location,
            type = type,
        )
    }

    private fun validation(): Boolean {
        val title = binding.etEventTitle.text.toString()
        val description = binding.etEventDescription.text.toString()
        val date = binding.btnDate.text.toString()
        val time = binding.btnTime.text.toString()
        val location = binding.etEventLocation.text.toString()
        val type = binding.etEventType.text.toString()
        return when {
            title.isEmpty() -> {
                binding.etEventTitle.error = "Title is required"
                binding.etEventTitle.requestFocus()
                false
            }
            description.isEmpty() -> {
                binding.etEventDescription.error = "Description is required"
                binding.etEventDescription.requestFocus()
                false
            }
            date.isEmpty() -> {
                binding.btnDate.error = "Date is required"
                binding.btnDate.requestFocus()
                false
            }
            time.isEmpty() -> {
                binding.btnTime.error = "Time is required"
                binding.btnTime.requestFocus()
                false
            }
            location.isEmpty() -> {
                binding.etEventLocation.error = "Location is required"
                binding.etEventLocation.requestFocus()
                false
            }
            type.isEmpty() -> {
                binding.etEventType.error = "Type is required"
                binding.etEventType.requestFocus()
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}