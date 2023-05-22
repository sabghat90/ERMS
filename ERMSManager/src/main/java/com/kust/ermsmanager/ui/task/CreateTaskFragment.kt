package com.kust.ermsmanager.ui.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentCreateTaskBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermsmanager.utils.TaskStatus
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val employeeViewModel: EmployeeViewModel by viewModels()

    // employee list for spinner
    private val employeeList = mutableListOf<String>()

    // employee object for storing selected employee from spinner not lateinit var
    private var selectedEmployee = EmployeeModel()

    // company id from auth view model getSession function not lateinit var
    private lateinit var companyId: String

    private var selectedDateTimestamp: Date? = null

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

        authViewModel.getSession {
            if (it != null) companyId = it.companyId
        }

        observer()

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, employeeList)
        binding.ddmEmployeeList.setAdapter(arrayAdapter)

        // q: how to store the selected employee from spinner to employee object?
        binding.ddmEmployeeList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItem = parent?.getItemAtPosition(position)
                if (selectedItem is EmployeeModel) {
                    selectedEmployee = selectedItem
                    // Do something with the selected employee
                    val id = selectedItem.id
                    val email = selectedItem.email

                    selectedEmployee = EmployeeModel(id, email)
                }
            }


        binding.taskDueDateButton.setOnClickListener { getDueDateAndTime() }

        binding.btnCreateTask.setOnClickListener {
            if (validateInput()) {
                val task = taskObj()
                CoroutineScope(Dispatchers.IO).launch {
                    taskViewModel.createTask(task)
                }
            }
        }
    }

    private fun taskObj(): TaskModel {

        return TaskModel(
            name = binding.taskNameInput.text.toString(),
            description = binding.taskDescriptionInput.text.toString(),
            status = TaskStatus.PENDING,
            deadline = selectedDateTimestamp.toString(),
            createdDate = Timestamp.now().toDate().toString(),
            createdBy = FirebaseAuth.getInstance().currentUser?.email.toString(),
            assigneeName = binding.ddmEmployeeList.text.toString(),
            assigneeEmail = selectedEmployee.email,
            assigneeId = selectedEmployee.id,
            companyId = companyId
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
                    binding.btnCreateTask.text = getString(R.string.create_task)
                    binding.progressBar.hide()
                    toast("Task created successfully")
                    findNavController().navigate(R.id.action_createTaskFragment_to_taskListingFragment)
                }

                is UiState.Error -> {
                    binding.btnCreateTask.text = getString(R.string.create_task)
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }

        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Success -> {
                    it.data.let { employees ->
                        employees.forEach { employee ->
                            employeeList.add(employee.name)
                        }
                    }
                }
                else -> {
                    toast("Error fetching employee list")
                }
            }
        }
    }

    private fun getDueDateAndTime() {
        // hide the keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    getString(R.string.date_time, formattedDate, formattedTime)

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