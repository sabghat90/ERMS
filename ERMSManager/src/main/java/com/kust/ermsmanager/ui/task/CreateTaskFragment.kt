package com.kust.ermsmanager.ui.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.TaskStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hideKeyboard
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.databinding.FragmentCreateTaskBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.kust.ermslibrary.R as LibraryR
import com.kust.ermsmanager.R as ManagerR


@AndroidEntryPoint
class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()
    private val employee by lazy {
        arguments?.getParcelable<Employee>("employee")
    }
    private var selectedDateTimestamp: Date? = null

    @Inject
    lateinit var notificationService: NotificationService
    private lateinit var manager: Employee
    private val authViewModel: AuthViewModel by viewModels()

    private val taskObj by lazy {
        arguments?.getParcelable<Task>("task")
    }
    private val isEdit by lazy {
        arguments?.getBoolean("isEdit") ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get employee from shared preferences
        authViewModel.getSession {
            if (it != null) {
                manager = it
            }
        }
        observer()
        updateUi()

        binding.taskDueDateButton.setOnClickListener {
            hideKeyboard()
            getDueDateAndTime()
        }

        if (isEdit) {
            binding.btnCreateTask.setOnClickListener {
                hideKeyboard()
                if (validateInput()) {
                    val task = taskObj()
                    lifecycleScope.launch {
                        taskViewModel.updateTask(task)
                    }
                }
            }
        } else {
            binding.btnCreateTask.setOnClickListener {
                hideKeyboard()
                if (validateInput()) {
                    val task = taskObj()
                    lifecycleScope.launch {
                        taskViewModel.createTask(task)
                    }
                }
            }
        }

    }

    private fun updateUi() {
        binding.tvEmployeeInfo.text = employee?.name ?: taskObj?.assigneeName
        binding.taskNameInput.setText(taskObj?.title ?: "")
        binding.taskDescriptionInput.setText(taskObj?.description ?: "")
        binding.taskDueDateButton.text = taskObj?.deadline ?: "Select Due Date"

        if (isEdit) {
            binding.btnCreateTask.text = getString(LibraryR.string.update_task)
        } else {
            binding.btnCreateTask.text = getString(LibraryR.string.create_task)
        }
    }

    private fun taskObj(): Task {

        val assigneeName = employee?.name ?: taskObj?.assigneeName
        val assigneeEmail = employee?.email ?: taskObj?.assigneeEmail
        val assigneeId = employee?.id ?: taskObj?.assigneeId
        val assigneeFCMToken = employee?.fcmToken ?: taskObj?.assigneeFCMToken

        return Task(
            id = taskObj?.id ?: "",
            title = binding.taskNameInput.text.toString(),
            description = binding.taskDescriptionInput.text.toString(),
            status = TaskStatus.PENDING,
            deadline = selectedDateTimestamp.toString(),
            createdDate = Timestamp.now().toDate().toString(),
            assigneeName = assigneeName!!,
            assigneeEmail = assigneeEmail!!,
            assigneeId = assigneeId!!,
            companyId = manager.companyId,
            managerId = FirebaseAuth.getInstance().currentUser!!.uid,
            managerName = manager.name,
            managerFCMToken = manager.fcmToken,
            assigneeFCMToken = assigneeFCMToken!!
        )
    }

    private fun observer() {
        taskViewModel.createTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnCreateTask.text = ""
                }

                is UiState.Success -> {
                    binding.btnCreateTask.text = getString(LibraryR.string.create_task)
                    binding.progressBar.hide()
                    sendNotification(employee!!.fcmToken)
                    toast("Task created successfully")
                    findNavController().navigate(ManagerR.id.action_createTaskFragment_to_taskListingFragment)
                }

                is UiState.Error -> {
                    binding.btnCreateTask.text = getString(LibraryR.string.create_task)
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }
        taskViewModel.updateTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnCreateTask.text = ""
                }

                is UiState.Success -> {
                    binding.btnCreateTask.text = getString(LibraryR.string.update_task)
                    binding.progressBar.hide()
                    sendNotification(employee!!.fcmToken)
                    toast("Task updated successfully")
                    findNavController().navigate(ManagerR.id.action_createTaskFragment_to_taskListingFragment)
                }

                is UiState.Error -> {
                    binding.btnCreateTask.text = getString(LibraryR.string.update_task)
                    binding.progressBar.hide()
                    Log.d("TAG", "observer: ${it.error}")
                    toast(it.error)
                }
            }
        }
    }

    private fun sendNotification(token: String) {

        if (isEdit) {
            val title = "Task Updated"
            val message = "Dear ${employee!!.name}, your task has been updated"
            PushNotification(
                NotificationData(
                    title,
                    message
                ),
                to = token
            )
                .also {
                    notificationService.sendNotification(it)
                }
        } else {
            val title = "New Task"
            val message = "Dear ${employee!!.name}, you have a new task"
            PushNotification(
                NotificationData(
                    title,
                    message
                ),
                to = token
            )
                .also {
                    notificationService.sendNotification(it)
                }
        }

    }

    private fun getDueDateAndTime() {
        // hide the keyboard
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)

        // Get the current date and time
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Create a DatePickerDialog to pick the date
        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            // Create a TimePickerDialog to pick the time
            val timePickerDialog = TimePickerDialog(context, { _, hourOfDay, minute ->
                // Create the Date object using the selected date and time
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
                val selectedDate = selectedCalendar.time

                // Format the date and time using SimpleDateFormat
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm a", Locale.getDefault())

                val formattedDate = dateFormat.format(selectedDate)
                val formattedTime = timeFormat.format(selectedDate)

                binding.taskDueDateButton.text =
                    getString(LibraryR.string.date_time, formattedDate, formattedTime)

                selectedDateTimestamp = Timestamp(selectedDate).toDate()

            }, currentHour, currentMinute, false)

            timePickerDialog.show()
        }, currentYear, currentMonth, currentDay)

        datePickerDialog.datePicker.minDate = Date().time
        datePickerDialog.show()
    }

    private fun validateInput(): Boolean {
        var isValid = true
        val taskName = binding.taskNameInput.text.toString()
        val taskDescription = binding.taskDescriptionInput.text.toString()
        val taskDeadline = binding.taskDueDateButton.text.toString()

        if (taskName.isEmpty()) {
            binding.taskNameInput.error = "Task name cannot be empty"
            binding.taskNameInput.requestFocus()
            isValid = false
        }

        if (taskDescription.isEmpty()) {
            binding.taskDescriptionInput.error = "Task description cannot be empty"
            binding.taskDescriptionInput.requestFocus()
            isValid = false
        }

        if (taskDeadline.isEmpty()) {
            binding.taskDueDateButton.error = "Task deadline cannot be empty"
            binding.taskDueDateButton.requestFocus()
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}